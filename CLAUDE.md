# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

**DosageCalc** — An Android app for dermatological drug dosage calculation. Targets medical professionals; clinical accuracy and safety constraints are paramount.

- Package: `com.example.dosagecalc`
- minSdk 26 (Android 8.0), compileSdk 36, targetSdk 35
- Kotlin 2.3.20, AGP 9.1.0, KSP 2.3.6 (not kapt; KSP2 independent versioning)

## Build Commands

```bash
# Build debug APK
./gradlew :app:assembleDebug

# Run unit tests (JUnit 5)
./gradlew :app:test

# Run a single test class
./gradlew :app:test --tests "com.example.dosagecalc.domain.usecase.CalculateDosageUseCaseTest"

# Run instrumented tests (requires emulator/device)
./gradlew :app:connectedAndroidTest

# Lint
./gradlew :app:lint
```

## Architecture

Clean Architecture with three layers, enforced by package structure:

```
domain/         ← Pure Kotlin, no Android/framework dependencies
  model/        ← Drug, PatientData, DosageResult, FormulaType
  repository/   ← DrugRepository interface
  usecase/      ← CalculateDosageUseCase, CalculateBsaUseCase, ValidateInputUseCase

data/           ← Android-aware implementations
  model/        ← DrugDto (@Serializable, mirrors drugs.json fields)
  datasource/   ← LocalDrugDataSource (reads assets/drugs.json via kotlinx.serialization)
  repository/   ← DrugRepositoryImpl
  di/           ← DataModule (Hilt bindings)

presentation/   ← Jetpack Compose + MVVM
  calculator/   ← CalculatorViewModel, CalculatorUiState
  calculator/screen/ ← DrugSelectionScreen, PatientInputScreen, DosageResultScreen
  navigation/   ← AppNavigation (sealed AppRoute, shared ViewModel pattern)
  ui/theme/     ← Material 3 theme
```

## Key Design Decisions

**Shared ViewModel across screens**: `CalculatorViewModel` is instantiated once at `AppNavigation` level and passed down to all three screens. This preserves state (selected drug, patient inputs, result) across the `DrugSelection → PatientInput → DosageResult` navigation flow.

**DosageResult sealed class**: The use case returns `DosageResult.Success`, `DosageResult.ValidationError`, or `DosageResult.Error` — never throws. The UI should handle all three cases.

**Dose ceiling safety**: `CalculateDosageUseCase.applyCeiling()` truncates calculated doses to `maxSingleDoseMcg` when exceeded and sets `cappedToMaxDose = true` in the result. Always surface this to the user.

**FormulaType.BY_RANGE**: Not yet implemented — returns `DosageResult.Error` explicitly.

## Drug Data

`app/src/main/assets/drugs.json` is the sole drug catalog. To add a drug, add an entry following the existing schema. `formulaType` values: `"per_kg"`, `"per_m2"`, `"fixed"`, `"by_range"`.

Sources must be from RCP, AIFA, or WHO — do not invent dosing data.

## Dependencies (Version Catalog)

All versions are in `gradle/libs.versions.toml`. Update versions there, not in individual `build.gradle.kts` files. Key libs: Hilt 2.59.2, Compose BOM 2026.03.00, kotlinx.serialization 1.10.0, JUnit 5 + MockK + Turbine for tests.
