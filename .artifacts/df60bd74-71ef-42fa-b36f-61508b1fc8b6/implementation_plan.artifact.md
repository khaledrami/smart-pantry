# Fix Hilt and AGP Compatibility Issue

The project is currently using **Hilt 2.60.1**, which requires **Android Gradle Plugin (AGP) 9.0.0** or higher. However, the project is configured with **AGP 8.13.2**. This discrepancy causes a build failure.

## Proposed Changes

To resolve this, we will downgrade Hilt to version **2.58**, which is the latest version compatible with AGP 8.x. Upgrading to AGP 9.0.0 would require a major migration of the entire build system, including Gradle itself (to version 9.1+), which is typically a more complex undertaking.

### Build Configuration

#### [MODIFY] [build.gradle.kts](file:///C:/Users/david/Projects/smart-pantry/build.gradle.kts)
- Downgrade `com.google.dagger.hilt.android` plugin to `2.58`.

#### [MODIFY] [settings.gradle.kts](file:///C:/Users/david/Projects/smart-pantry/settings.gradle.kts)
- Downgrade `com.google.dagger.hilt.android` plugin in `pluginManagement` to `2.58`.

#### [MODIFY] [app/build.gradle.kts](file:///C:/Users/david/Projects/smart-pantry/app/build.gradle.kts)
- Update `hiltVersion` variable to `2.58`.

#### [MODIFY] [inventory/build.gradle.kts](file:///C:/Users/david/Projects/smart-pantry/inventory/build.gradle.kts)
- Update `hilt-android` and `hilt-compiler` dependencies to `2.58`.

## Verification Plan

### Automated Tests
- Run `./gradlew assembleDebug` to ensure the project builds successfully.
- Run unit tests in `:app` and `:inventory` to ensure no regressions in DI setup.

### Manual Verification
- Verify that Hilt code generation works by checking for successful build completion.
