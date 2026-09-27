# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

MyDevotional is an Android app for daily Bible reading, built with Kotlin and Jetpack Compose. The UI strings are in Brazilian Portuguese, and so are the Bible book names in `BibleBooks.kt`.

## Commands

```bash
./gradlew assembleDebug                        # build debug APK
./gradlew installDebug                         # install on connected device/emulator
./gradlew testDebugUnitTest                    # JVM unit tests, all modules
./gradlew :app:testDebugUnitTest --tests "com.example.mydevotional.ExampleUnitTest"   # single test class
./gradlew connectedDebugAndroidTest            # instrumented tests (needs device)
./gradlew lint
```

A full clean build takes several minutes (KSP for Hilt and Room).

Known test issue: `app/src/test/.../UserRepositoryTest.kt` references a `FakeUserRemoteDataSource` that doesn't exist. It also uses Room with `ApplicationProvider` on the JVM without Robolectric. `:app` unit tests won't compile until that is fixed.

## Required local config (not committed)

- `app/google-services.json`: the Firebase config. The google-services plugin needs it. It must include the debug keystore's SHA-1 and a Web OAuth client, because `MainActivity` passes the generated `R.string.default_web_client_id` to `:loginlib` for Google Sign-In.

## Architecture

There is one Gradle module, `:app`. Dependency and plugin versions are centralized in `gradle/libs.versions.toml`.

- Auth (email/password, Google Sign-In through Credential Manager, phone verification, registration screens) comes from `loginlib`, published from the Mylogin repo (github.com/Joaovts07/Mylogin) through JitPack as `com.github.Joaovts07.Mylogin:loginlib`. Bump the `loginlib` version in `libs.versions.toml` to a new Mylogin tag to upgrade. To test unreleased lib changes, check Mylogin out at `../Mylogin` and build with `-PlocalLoginlib`, which swaps in its `:loginlib` project through `includeBuild`. It exposes `AuthViewModel` (`loginState`, `logout()`) and `LoginNavigation(navController, serverClientId)`. Its ViewModels aren't Hilt ViewModels, so get them with `viewModel { AuthViewModel() }`.
- **`:app`** (`com.example.mydevotional`). `MainActivity.InitNavigation` observes `AuthViewModel.loginState` and swaps between `LoginNavigation` and the app's `AppNavigation`. Auth gating happens here, not per screen.

Layering inside `:app` is Compose screen → `@HiltViewModel` → use case (`usecase/`) → repository (`repositorie/`, note the spelling) → data source. Most bindings are explicit `@Provides` in `di/AppModule.kt` rather than `@Binds`, so new repositories and use cases usually need a provider added there.

Data sources:
- **bible-api.com over Ktor** (`BibleRepositoryImpl`) supplies verse text. The selected translation's `apiCode` is appended to the URL.
- **Firestore** holds `readings/{yyyy-MM-dd}` documents (the passages for each day) and `users/{uid}`.
- **Preferences DataStore** holds favorites and completed readings. Both share `Context.dataStore` (`favorites_store`) from `repositorie/DataStoreManager.kt`. The selected translation has its own store, `bible_translation_prefs`, in `TranslationPreferenceRepository`.
- **Room** (`local/AppDatabase`, `my_devotional_db`) caches the user, synced from Firestore through `UserRepository`.
- **ML Kit text recognition** (`SaveReadingsFromImageUseCase`) OCRs a photo of a reading plan, parses the passages per date, and writes them to Firestore `readings`.

Navigation: routes are defined in `navigation/AppDestination.kt`. `AppNavigation` wraps each destination in `MyDevocionalScaffold`, which provides the top bar and bottom bar. `VersesViewModel` and `AccountViewModel` are created once in `AppNavigation` and shared across the Bible books, chapters, verses, and favorites screens.

Firebase Analytics and Crashlytics are enabled in `:app`.

## Workflow

Work happens on feature branches named `feature/MYD-<n>`, with commit messages prefixed `MYD-<n>:`. Branches merge into `develop` through PRs. `main` is the release branch.
