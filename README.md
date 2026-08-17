# WeatherApp

An Android weather app built with Kotlin, Jetpack Compose, and a multi-module MVI architecture.
It shows the current day's weather, an hourly strip, and a weekly forecast for your current location,
and lets you search for and switch to any other city.

## Features

- Current weather for the device's location (with runtime location permission).
- Hourly forecast for the day.
- Daily / weekly forecast.
- City search and selection; view the forecast for any chosen city.
- Pull-to-refresh on the forecast screen.

## Tech stack

| Area | Tools |
|---|---|
| Language | Kotlin, Coroutines & Flow |
| UI | Jetpack Compose, Material 3, Navigation Compose |
| Architecture | Multi-module, feature-based Clean Architecture + MVI |
| Dependency injection | Hilt |
| Networking | Retrofit, OkHttp (logging), kotlinx.serialization |
| Weather API | OpenWeatherMap — Current Weather + 5-day / 3-hour Forecast (`data/2.5`) + Geocoding |
| Location & permissions | Play Services Location (`FusedLocationProvider`), Accompanist Permissions |
| Build | Gradle (Kotlin DSL) + version catalog |
| Testing | JUnit 5, MockK, Kluent, Turbine, `kotlinx-coroutines-test`, shared `:core:testing` |

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
card-free `data/2.5` endpoints instead (this choice is isolated inside `:feature:forecast:data`).

## Module structure

Every feature is a vertical slice that owns its own `domain`, `data`, and `presentation` modules.
`:core` holds only what is genuinely shared across features, split along the same three layers.

```
:app                                Single Activity, NavHost, DI wiring, cross-feature navigation

:feature
├── :feature:forecast               Current + hourly + daily weather (main screen)
│   ├── :domain                     Forecast models, WeatherRepository interface, use cases (pure Kotlin)
│   ├── :data                       Weather DTOs, WeatherApi, mappers, repository impl
│   └── :presentation               Contract / ViewModel / Route / Screen / UI models
└── :feature:search                 City search & selection
    ├── :domain                     City model, GeocodingRepository interface, use case (pure Kotlin)
    ├── :data                       Geocoding DTO, GeocodingApi, mapper, repository impl
    └── :presentation               Contract / ViewModel / Route / Screen / UI models

:core
├── :core:common                    Result/AppError, DispatcherProvider, Logger (pure Kotlin)
├── :core:domain                    Cross-feature domain: Coordinates, LocationProvider (pure Kotlin)
├── :core:data                      Shared networking: OkHttp, Json, Retrofit, API-key interceptor
├── :core:presentation              MviViewModel base + State/Action/Effect contracts
├── :core:designsystem              Compose theme, tokens, icons, reusable composables
├── :core:location                  Device location via FusedLocationProvider
└── :core:testing                   Shared test utilities (MainDispatcherExtension, DefaultLocaleExtension)
```

### Layering & dependency rules

- Inside a feature the layers flow one way: **`presentation → domain ← data`**. Every `domain` module
  (feature and core alike) is a **pure Kotlin/JVM library**, so "domain never touches Android" is
  enforced by the compiler rather than by discipline.
- **`:feature:x:presentation` depends on `:feature:x:domain`** plus `:core:{domain, common,
  presentation, designsystem}` — never on any `data` module. It therefore never sees Retrofit, a DTO,
  or a repository implementation.
- **`:feature:x:data` implements the interfaces declared in `:feature:x:domain`** and binds them with
  Hilt; `:app` depends on the `data` modules purely to aggregate those bindings into the graph
  (dependency inversion).
- **`:core` holds only what more than one feature needs**: `Coordinates` and `LocationProvider` (shared
  by forecast and search), the configured `Retrofit`/OkHttp/JSON stack, the MVI base, the design system.
  Feature-specific types live in the feature that owns them — `WeatherCondition` is not core, `City` is
  not core.
- **No feature depends on another feature**, at any layer. Cross-feature navigation is mediated by
  `:app`; features exchange primitives (e.g. `lat`, `lon`, `name`), not objects.

### Why this shape

The organising principle is **feature-first, layer-second**: a feature is a vertical slice that owns
its whole stack, and `:core` is deliberately thin — it earns a type only when a second feature needs it.

That buys three things:

- **Cohesion.** Everything about "forecast" — its models, its endpoint, its mapper, its screen — is in
  one place. Changing how a forecast is fetched touches `:feature:forecast:*` and nothing else.
- **Compiler-enforced purity, per feature.** Each feature's `domain` is a plain Kotlin/JVM module. It
  *cannot* import Android, Retrofit, or Compose, because they're not on its classpath.
- **Real isolation between features.** A feature's presentation module can't reach another feature's
  data, or even its own — the module graph forbids it. Ownership boundaries are a build-file fact
  rather than a code-review convention.

The cost is module count and some repetition in build files. That's the honest trade: more Gradle
modules, more DI modules, and a version catalog doing a lot of work to keep them consistent. In
exchange, the boundaries can't drift — the ones that matter are enforced by the build rather than by
whoever reviews the PR.

`:core` stayed small on purpose. `Coordinates` is there because both features speak it, and
`LocationProvider` is there because `:core:location` implements it and core must never depend on a
feature. Networking is there because a single configured `Retrofit` (one OkHttp client, one API-key
interceptor, one JSON config) should exist once — each feature's data module then creates its own
service interface from it, so the endpoints stay with the feature that calls them.

### Navigation

I kept navigation deliberately plain — two string-route destinations (`forecast`, `search`) wired with
Navigation Compose, and the chosen city handed back through the destination's `SavedStateHandle`. With
only two screens that's the least ceremony. If the graph grew I'd move to **type-safe routes**
(Navigation 2.8+, serializable route objects) or **Navigation 3** — but for a couple of destinations that
would be structure for its own sake.

### Design system

Shared visual language lives in `:core:designsystem`, and features **consume tokens rather than invent
them**: the shape scale (`MaterialTheme.shapes`), the colour scheme, and the app-specific extras that
Material 3 has no role for — the hero gradient and the list divider — which are provided as
`WeatherTheme.colors` through a `CompositionLocal`. A feature never hard-codes a corner radius or a
brush. Design *tokens* live here from day one (you can't predict reuse, and consistency is the point);
reusable *components* move here only once a second screen needs them.

The weather icons are an **11-piece two-tone set** shipped as vector drawables and addressed through a
`WeatherIcon` enum, so a feature names a condition rather than a resource id. The sun, lightning, rain and
snow keep fixed brand colours; the cloud mass is a single theme-aware colour resource
(`ds_weather_icon_neutral`, with a `values-night` variant), which is why the icons read correctly on both
themes without a second drawable set. On the coloured hero the whole glyph is tinted white instead, so it
stays legible against the gradient.

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
- DTOs are mapped to domain models in the feature's `data` module; domain models are mapped to UI state
  in its `presentation` module. Neither mapping skips a layer: a ViewModel never sees a DTO.

## Getting started

### Prerequisites

- Android Studio (latest stable) with JDK as configured by the project.
- Android SDK 37; minimum supported device API is 28.
- An [OpenWeatherMap](https://openweathermap.org/api) API key.

### Configure your API key

The API key is read from `local.properties` (git-ignored) and exposed through `BuildConfig`. Add:

```properties
OPEN_WEATHER_API_KEY=your_key_here
```

Do **not** commit your key.

### Test

```bash
./gradlew test   # JVM unit tests (use cases, repositories, mappers, ViewModels)
```

Unit tests only by design — no instrumented / Compose UI tests.

## Permissions

- `ACCESS_COARSE_LOCATION` — used to determine the device's location for the initial forecast.
  Coarse precision is sufficient for city-level weather. The app works without it: you can search for a
  city manually.
- `INTERNET` — required to reach the weather API.

## Project conventions

The codebase follows feature-based multi-module Clean Architecture with MVI, unidirectional state,
consistent error handling, and a shared design system. For development, I followed **git-flow** — `feature/*` branches off
`develop`, merged back with `--no-ff`.

## How I used AI

I used AI (Claude) throughout, the way I use it in day-to-day work: as a fast second brain, a sounding
board, and an extra reviewer — not as an autopilot. I designed the architecture, made the engineering
decisions, and wrote and reviewed the code. Every AI-assisted change was read, understood, and adjusted
by me before it went into a commit; nothing landed that I couldn't explain and defend line by line.

- **Architecture & conventions** — I decided the architecture up front (multi-module Clean Architecture
  with MVI, a pure-Kotlin domain, dependency inversion) and used AI as a second brain to pressure-test it
  and catch anything I'd glossed over. As the conventions firmed up I captured them as project rules;
  when I introduced something like the shared `MviViewModel` base I updated those rules so the guidance
  stayed in sync with the code.
- **Planning & implementation** — before a feature I used AI to help think through the slice (the
  domain/data/presentation breakdown and the order to build and review it in), then implemented it layer
  by layer. For mechanical bits — small helpers, boilerplate, trivial refactors — I let it draft a first
  pass and shaped it to fit.
- **Testing** — I used AI to brainstorm the cases worth covering (edge cases, failure paths, the states
  a ViewModel should end up in), which made the suites more thorough. I owned the assertions and the test
  design.
- **Code review** — before each commit I reviewed my own diff and also asked AI to review it as a second
  pair of eyes, then took the useful feedback and pushed back on the rest.
- **Sanity-checking coverage** — whenever I had a nagging feeling something was missing, I confirmed with
  AI rather than assuming. Things like *"did we handle every permission case — I feel like we skipped the
  permanently-denied one?"* — a quick check that caught gaps early.
- **UI / UX** — I built the first working UI and layout myself, then took it further with **Claude
  Design**: I briefed it with the screens and states I actually had (and the data I *didn't* have, so it
  couldn't invent features), and it came back with mockups for every state in light and dark, a token
  spec, and the weather icon set as SVGs. I implemented that in the same layered way I build features —
  design-system tokens first (colour roles, shapes, the hero gradient), then the shared components
  (error card, skeletons, the icon set as vector drawables), and only then migrating the two screens onto
  them. Where the design and the platform disagreed I went with the platform: the icons ship as drawables
  with a night-qualified tint rather than the per-path tinting the mockups assumed. I also leaned on AI
  for the Compose `@Preview` fixtures, which are repetitive to hand-write.
- **Docs & writing** — I used AI to polish this README and the occasional doc comment, so the prose stays
  clear and consistent.

Throughout, AI never committed on my behalf and never chose the architecture — those stayed my calls. The
goal was to move faster and think more sharply while keeping full ownership of the product logic and the
implementation.
