# WeatherApp

An Android weather app built with Kotlin, Jetpack Compose, and a multi-module MVI architecture.
It shows the current day's weather, an hourly strip, and a weekly forecast for your current location,
and lets you search for and switch to any other city.

## Features

- Current weather for the device's location (with runtime location permission).
- Hourly forecast for the day.
- Daily / weekly forecast.
- City search and selection; view the forecast for any chosen city.

## Tech stack

| Area | Tools |
|---|---|
| Language | Kotlin, Coroutines & Flow |
| UI | Jetpack Compose, Material 3, Navigation Compose |
| Architecture | Multi-module Clean Architecture + MVI |
| Dependency injection | Hilt |
| Networking | Retrofit, OkHttp (logging), kotlinx.serialization |
| Weather API | OpenWeatherMap — Current Weather + 5-day / 3-hour Forecast (`data/2.5`) + Geocoding |
| Location & permissions | Play Services Location (`FusedLocationProvider`), Accompanist Permissions |
| Build | Gradle (Kotlin DSL) + version catalog |
| Testing | JUnit 5, MockK, Kluent, Turbine, `kotlinx-coroutines-test`, shared `:core:testing` (`MainDispatcherExtension`) |

### Libraries

- **Kotlin & async** — Kotlin, Coroutines & Flow (`kotlinx-coroutines-core`, `-play-services`)
- **UI** — Jetpack Compose (BOM), Material 3, Compose Foundation, Activity Compose, Lifecycle
  Runtime/ViewModel Compose, Navigation Compose
- **DI** — Hilt (`hilt-android`, `hilt-navigation-compose`) with KSP
- **Networking** — Retrofit, Retrofit ↔ kotlinx.serialization converter, OkHttp + logging interceptor,
  kotlinx.serialization JSON
- **Location & permissions** — Play Services Location (`FusedLocationProviderClient`),
  Accompanist Permissions
- **Testing** — JUnit 5 (Jupiter), MockK, Kluent, Turbine, `kotlinx-coroutines-test`,
  JUnit Platform Launcher
- **Build** — Android Gradle Plugin, Gradle Kotlin DSL, version catalog, KSP

### APIs (OpenWeatherMap)

Free tier, base URL `https://api.openweathermap.org`:

- **Current weather** — `GET data/2.5/weather` —
  [docs](https://openweathermap.org/api/current?collection=current_forecast)
- **5-day / 3-hour forecast** — `GET data/2.5/forecast` —
  [docs](https://openweathermap.org/api/forecast5?collection=current_forecast)
- **Geocoding (city search)** — `GET geo/1.0/direct` —
  [docs](https://openweathermap.org/api/geocoding-api?collection=other)

The One Call 3.0 endpoint is also free but requires a payment card on file, so the app uses the
card-free `data/2.5` endpoints instead (this choice is isolated inside `:core:data`).

## Module structure

```
:app                     Single Activity, NavHost, DI wiring, cross-feature navigation

:feature
├── :feature:forecast    Current + hourly + daily weather (main screen)
└── :feature:search      City search & selection

:core
├── :core:common         Result/AppError result types, DispatcherProvider (pure Kotlin)
├── :core:mvi            MviViewModel base + State/Action/Effect contracts
├── :core:designsystem   Compose theme, typography, reusable composables
├── :core:domain         Models, repository/provider interfaces, use cases (pure Kotlin)
├── :core:data           DTOs, Retrofit services, mappers, repository implementations
├── :core:location       Device location via FusedLocationProvider
└── :core:testing        Shared test utilities (MainDispatcherExtension)
```

### Layering & dependency rules

- Each layer flows one way: **`presentation → domain ← data`**. `:core:domain` is pure Kotlin and
  depends on nothing Android.
- **Feature modules depend on `:core:domain` interfaces**, `:core:mvi`, `:core:designsystem`,
  `:core:common` — never on `:core:data` or `:core:location` directly.
- `:core:data` and `:core:location` **implement** the domain interfaces; `:app` wires those
  implementations in via Hilt (dependency inversion).
- **No feature depends on another feature.** Cross-feature navigation is mediated by `:app`; features
  exchange primitives (e.g. `lat`, `lon`, `name`), not objects.

### Why this shape (and how it scales)

I went with the **Now-in-Android** layout: features are single, presentation-only modules sitting on a
shared, pure-Kotlin `:core:domain` (so "domain never touches Android" is enforced by the compiler, not by
willpower), with the implementations in `:core:data`.

I did think about going further. The "proper" clean-architecture-at-scale option is per-feature
submodules — `:feature:x:{domain,data,presentation}` — and honestly that's the more scalable one: each
feature stays cohesive *and* keeps its domain purity enforced. But when I sketched it out for what I
actually have here — two small features — it came to 10+ modules, each with its own build file and DI
wiring, and I couldn't point at a problem it was solving. The payoffs (parallel builds, clean ownership
when several people work in parallel) only really show up once there are lots of features and a team
splitting them up, and I have neither.

I also looked at the middle option — each feature owning its own `data`/`domain` as *packages* instead of
core. I actually built it on a throwaway branch just to feel it out, and it mostly confirmed the trade:
I'd lose the compiler-enforced purity, Retrofit would leak into the presentation modules, and I'd *still*
need shared core modules for networking and `Coordinates` anyway.

So I stopped at what felt proportionate. The boundaries are already clean, so if a feature ever grows big
or a team forms around it, promoting it into submodules is a mechanical afternoon's refactor — I lose
nothing by waiting. I'd rather modularize when something actually hurts (slow builds, people stepping on
each other, fuzzy ownership) than pay upfront for structure I don't need yet. For a sample this size this
is enough, and the door to the bigger layout stays wide open.

### Navigation

I kept navigation deliberately plain — two string-route destinations (`forecast`, `search`) wired with
Navigation Compose, and the chosen city handed back through the destination's `SavedStateHandle`. With
only two screens that's the least ceremony. If the graph grew I'd move to **type-safe routes**
(Navigation 2.8+, serializable route objects) or **Navigation 3** — but for a couple of destinations that
would be structure for its own sake.

### Design system

Shared visual language lives in `:core:designsystem`, and features **consume tokens rather than invent
them**: the shape scale (`MaterialTheme.shapes`), the color scheme, and the hero gradient
(`colorScheme.heroGradient`) are defined once in the theme, so a feature never hard-codes a corner radius
or a brush. Design *tokens* live here from day one (you can't predict reuse, and consistency is the
point); reusable *components* move here only once a second screen needs them.

## Architecture at a glance (MVI)

Each screen defines a `Contract` with three parts:

- **State** — an immutable data class describing everything on screen (`isLoading`, `content`, `error`…).
- **Action** — everything the user/UI can trigger (`Retry`, `RefreshRequested`, `CitySelected`…).
- **Effect** — one-shot events (navigation, messages) that fire once and aren't replayed.

The flow:

```
Screen (stateless) ──onAction(Action)──▶ ViewModel ──▶ UseCase ──▶ Repository ──▶ API/Location
                                            │                                         │
   Compose ◀── collectAsStateWithLifecycle ─┴── StateFlow<State> ◀── domain model ◀──┘
```

- `XxxRoute` is the stateful entry (owns the ViewModel, collects state & effects).
- `XxxScreen` is stateless and previewable — no ViewModel, no business logic.
- DTOs are mapped to domain models in `:core:data`; domain models are mapped to UI state in the feature.

## Getting started

### Prerequisites

- Android Studio (latest stable) with JDK as configured by the project.
- Android SDK 36; minimum supported device API is 28.
- An [OpenWeatherMap](https://openweathermap.org/api) API key.

### Configure your API key

The API key is read from `local.properties` (git-ignored) and exposed through `BuildConfig`. Add:

```properties
OPEN_WEATHER_API_KEY=your_key_here
```

Do **not** commit your key.

### Build & run

```bash
./gradlew assembleDebug
```

Then run the `app` configuration from Android Studio, or:

```bash
./gradlew installDebug
```

### Test

```bash
./gradlew test   # JVM unit tests (domain, data, mapping, ViewModels)
```

Unit tests only by design — no instrumented / Compose UI tests.

## Permissions

- `ACCESS_COARSE_LOCATION` — used to determine the device's location for the initial forecast.
  Coarse precision is sufficient for city-level weather. The app works without it: you can search for a
  city manually.
- `INTERNET` — required to reach the weather API.

## Project conventions

The codebase follows multi-module Clean Architecture with MVI, unidirectional state, consistent error
handling, and a shared design system. Development follows **git-flow** (`feature/*` branches off
`develop`).
