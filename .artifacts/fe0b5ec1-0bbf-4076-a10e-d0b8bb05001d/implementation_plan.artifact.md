# Fix Kotlin Serialization Plugin and Build Script Errors

The project fails to sync because the Kotlin Serialization plugin is being referenced with an incorrect ID (`kotlinx-serialization`) and without a version in the module-level `build.gradle.kts` files. Additionally, there are syntax errors in the `inventory/build.gradle.kts` file (using Groovy's `def` instead of Kotlin's `val`).

## Proposed Changes

### Root Project

#### [MODIFY] [build.gradle.kts](file:///C:/Users/david/Projects/smart-pantry/build.gradle.kts)
- Add the Kotlin Serialization plugin to the `plugins` block with `apply false` to centralize the version management.

### App Module

#### [MODIFY] [app/build.gradle.kts](file:///C:/Users/david/Projects/smart-pantry/app/build.gradle.kts)
- Replace the invalid `id("kotlinx-serialization")` with the idiomatic `kotlin("plugin.serialization")`.
- Correct the Hilt plugin application (remove `version` and `apply false` if it's already defined in the root).

### Inventory Module

#### [MODIFY] [inventory/build.gradle.kts](file:///C:/Users/david/Projects/smart-pantry/inventory/build.gradle.kts)
- Replace the invalid `id("kotlinx-serialization")` with `kotlin("plugin.serialization")`.
- Fix the Groovy-style `def` keyword for `cameraxVersion`.
- Correct the Hilt plugin application.

## Verification Plan

### Automated Tests
- Run `gradlew sync` (or trigger it via the IDE) to ensure the project builds and synchronizes correctly.
- Run `gradlew assembleDebug` to verify the build completes.
