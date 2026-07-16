# Smart Pantry

An Android-first mobile application for intelligent household inventory management.

## Overview

Smart Pantry transforms your pantry, fridge, and freezer into an intelligent inventory system that tracks:
- What food you have
- Where it's stored (3-level location hierarchy)
- When it expires
- Quantity and units
- Complete audit trail of all changes

## Architecture

### Modules

| Module | Description |
|--------|-------------|
| `app` | Application entry point, Hilt setup, navigation host |
| `inventory` | Feature module: domain, data, presentation layers |

### Inventory Module Structure

```
inventory/
├── domain/
│   ├── model/           # Product, Location, Movement, Category, Status, MovementType
│   ├── repository/      # Repository interfaces (ProductRepository, BarcodeScannerRepository, ProductLookupRepository)
│   └── usecase/         # GetProducts, GetProduct, AddProduct, UpdateProduct, DeleteProduct, ScanBarcode, LookupProduct
├── data/
│   ├── entity/          # Room entities: ProductEntity, MovementEntity
│   ├── dao/             # ProductDao, MovementDao with Flow queries
│   ├── repository/      # ProductRepositoryImpl, BarcodeScannerRepositoryImpl, MockProductLookupRepository
│   ├── mapper/          # ProductMapper, MovementMapper (with JSON serialization)
│   └── AppDatabase.kt   # Room database v1
├── presentation/
│   ├── viewmodel/       # ProductListViewModel, ProductDetailViewModel, AddEditProductViewModel, BarcodeScannerViewModel
│   ├── screen/          # ProductListScreen, ProductDetailScreen, AddEditProductScreen, BarcodeScannerScreen
│   └── navigation/      # InventoryNavHost (Compose Navigation)
└── di/
    └── InventoryModule.kt  # Hilt bindings
```

## Tech Stack

| Layer | Technology |
|-------|------------|
| Language | Kotlin 1.9+ |
| UI | Jetpack Compose (Material 3) |
| Architecture | Clean Architecture + Feature Modules |
| DI | Hilt |
| Database | Room (SQLite) with Flow |
| Async | Coroutines + Flow |
| Barcode | ML Kit Barcode Scanning |
| Camera | CameraX |
| Testing | JUnit5, MockK, Turbine, Robolectric, Compose Test |

## Features Implemented

### Phase 1: Foundation (Domain + Data)
- ✅ Product entity with 17 fields (name, brand, category, barcode, quantity, unit, price, dates, location, status, notes, tags)
- ✅ 15 Categories, 7 Statuses, MovementType sealed class (8 variants)
- ✅ Room schema with indices for location, expiry, category, status, barcode
- ✅ Movement audit log with JSON-serialized payload
- ✅ Repository pattern with Flow-based reactive queries
- ✅ ML Kit barcode scanning integration
- ✅ Mock product lookup (53 products in JSON asset)

### Phase 2: Presentation
- ✅ ProductListScreen: LazyColumn grouped by location, pull-to-refresh, expiry chips
- ✅ ProductDetailScreen: Details + Movement history tab
- ✅ AddEditProductScreen: Full form with dropdowns for category/status/location, barcode scan button
- ✅ BarcodeScannerScreen: CameraX preview + ML Kit analyzer
- ✅ Navigation graph with 5 routes
- ✅ ViewModels with sealed UiState (Loading/Success/Error/Empty)
- ✅ Compose UI tests + ViewModel Turbine tests

### Phase 3: App Integration
- ✅ Hilt DI wiring (AppModule + InventoryModule)
- ✅ Camera permission with Play Store compliant rationale
- ✅ CI/CD workflow (test, lint, build)

## Getting Started

### Prerequisites
- Android Studio Koala+ (2024.1.2+)
- JDK 17
- Android SDK 34

### Build
```bash
./gradlew assembleDebug
```

### Run Tests
```bash
# Unit tests
./gradlew testDebugUnitTest

# Instrumented tests (requires emulator/device)
./gradlew connectedDebugAndroidTest

# Lint
./gradlew lintDebug
```

## Project Structure

```
SmartPantry/
├── app/                      # Application module
│   ├── src/main/
│   │   ├── java/com/smartpantry/app/
│   │   │   ├── SmartPantryApplication.kt
│   │   │   ├── MainActivity.kt
│   │   │   ├── di/AppModule.kt
│   │   │   ├── navigation/AppNavHost.kt
│   │   │   └── ui/theme/
│   │   ├── AndroidManifest.xml
│   │   └── res/values/strings.xml
│   └── build.gradle.kts
├── inventory/                # Feature module
│   ├── src/main/
│   │   ├── assets/mock_products.json
│   │   └── java/com/smartpantry/inventory/
│   │       ├── domain/
│   │       ├── data/
│   │       ├── presentation/
│   │       └── di/InventoryModule.kt
│   └── build.gradle.kts
├── .github/workflows/ci.yml
├── build.gradle.kts
├── settings.gradle.kts
└── gradlew
```

## Next Steps (Roadmap)

- [ ] Shopping List module
- [ ] AI Recipe Suggestions (OpenAI integration)
- [ ] Statistics Dashboard
- [ ] Cloud Sync / Backup
- [ ] Multi-user / Multi-home support
- [ ] Wear OS companion
- [ ] NFC tag support
- [ ] Bluetooth scale integration