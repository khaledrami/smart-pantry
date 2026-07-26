# Implementation Plan - Migrate to compilerOptions DSL

The project is currently using the deprecated `kotlinOptions` DSL to set the `jvmTarget` in both `:app` and `:inventory` modules. With Kotlin 2.0+, this has been replaced by the `compilerOptions` DSL.

## User Review Required

> [!IMPORTANT]
> This change migrates `kotlinOptions` to `compilerOptions` to comply with Kotlin 2.0+ requirements. This should resolve the Gradle sync error.

## Proposed Changes

### Build Configuration

#### [MODIFY] [app/build.gradle.kts](file:///C:/Users/david/Projects/smart-pantry/app/build.gradle.kts)
- Replace `kotlinOptions` block with `compilerOptions`.

#### [MODIFY] [inventory/build.gradle.kts](file:///C:/Users/david/Projects/smart-pantry/inventory/build.gradle.kts)
- Replace `kotlinOptions` block with `compilerOptions`.

## Verification Plan

### Automated Tests
- Run Gradle sync to ensure the error is resolved.
- Run `./gradlew assembleDebug` to verify the build succeeds.

### Manual Verification
- None required beyond successful build and sync.
