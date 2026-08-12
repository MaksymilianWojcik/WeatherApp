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
| Networking | Retrofit, OkHttp, kotlinx.serialization |
| Weather API | OpenWeatherMap (One Call 3.0 + Geocoding) |
| Location | Play Services Location (`FusedLocationProvider`) |
| Build | Gradle (Kotlin DSL) + version catalog |
| Testing | JUnit 5, MockK, Kluent, Turbine, `kotlinx-coroutines-test` |

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
└── :core:location       Device location via FusedLocationProvider
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
./gradlew test                 # JVM unit tests (domain, data, mapping, ViewModels)
./gradlew connectedAndroidTest # instrumented / Compose UI tests
```

## Permissions

- `ACCESS_COARSE_LOCATION` — used to determine the device's location for the initial forecast.
  Coarse precision is sufficient for city-level weather. The app works without it: you can search for a
  city manually.
- `INTERNET` — required to reach the weather API.

## Project conventions

The codebase follows multi-module Clean Architecture with MVI, unidirectional state, consistent error
handling, and a shared design system. Development follows **git-flow** (`feature/*` branches off
`develop`).
