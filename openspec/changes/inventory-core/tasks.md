# Tasks: inventory-core

## Review Workload Forecast

| Field | Value |
|-------|-------|
| Estimated changed lines | 1,200–1,500 |
| 400-line budget risk | High |
| Chained PRs recommended | Yes |
| Suggested split | PR 1: Foundation (domain + data) → PR 2: Presentation (ViewModels + Compose) → PR 3: App Integration + Tests |
| Delivery strategy | ask-on-risk |
| Chain strategy | feature-branch-chain |

Decision needed before apply: Yes
Chained PRs recommended: Yes
Chain strategy: feature-branch-chain
400-line budget risk: High

### Suggested Work Units

| Unit | Goal | Likely PR | Focused test command | Runtime harness | Rollback boundary |
|------|------|-----------|----------------------|-----------------|-------------------|
| 1 | Domain + Data layer (entities, DAOs, repository, use cases) | PR 1 | `./gradlew :inventory:testDebugUnitTest` | Room in-memory DB + JUnit5 | Revert `inventory/domain/`, `inventory/data/` |
| 2 | Presentation layer (ViewModels, Compose screens, navigation) | PR 2 | `./gradlew :inventory:connectedDebugAndroidTest` | Robolectric + ComposeTestRule | Revert `inventory/presentation/` |
| 3 | App wiring + E2E tests | PR 3 | `./gradlew connectedDebugAndroidTest` | Emulator + real ML Kit | Revert `app/` module changes |

## Phase 1: Foundation (Domain + Data)

- [x] 1.1 Create Gradle module `inventory/` with `build.gradle.kts` (compose, room, hilt, ML Kit deps)
- [x] 1.2 Define domain entities: `Product`, `Location`, `Movement`, `Category`, `Status`, `MovementType` sealed class
- [x] 1.3 Define repository interfaces: `ProductRepository`, `BarcodeScannerRepository`, `ProductLookupRepository`
- [x] 1.4 Create use cases: `GetProductsUseCase`, `GetProductUseCase`, `AddProductUseCase`, `UpdateProductUseCase`, `DeleteProductUseCase`, `ScanBarcodeUseCase`, `LookupProductUseCase`
- [x] 1.5 Define Room entities: `ProductEntity`, `MovementEntity` with indices on `productId`, `timestamp`, `location`
- [x] 1.6 Create DAOs: `ProductDao` (Flow queries: allByLocation, byExpiry, byCategory, byStatus), `MovementDao` (byProductIdDesc)
- [x] 1.7 Implement mappers: `ProductMapper`, `MovementMapper`
- [x] 1.8 Implement `ProductRepositoryImpl` with Room DAOs + mappers
- [x] 1.9 Implement `BarcodeScannerRepositoryImpl` wrapping ML Kit `BarcodeScanning`
- [x] 1.10 Implement `MockProductLookupRepository` reading `assets/mock_products.json`
- [x] 1.11 Write unit tests for use cases (Turbine + MockK) — covers spec scenarios: Product CRUD, Location hierarchy, Category/Status enums, Movement audit
- [x] 1.12 Write DAO tests with Room in-memory database

## Phase 2: Presentation (ViewModels + Compose)

- [x] 2.1 Create `ProductListViewModel` with `StateFlow<UiState>` (Loading, Success, Error, Empty)
- [x] 2.2 Create `ProductDetailViewModel` with history tab (movements Flow)
- [x] 2.3 Create `AddEditProductViewModel` with form validation + barcode scan integration
- [x] 2.4 Create `BarcodeScannerViewModel` handling permission rationale + ML Kit result
- [x] 2.5 Build `ProductListScreen` (LazyColumn grouped by location, pull-to-refresh, FAB → AddEdit)
- [x] 2.6 Build `ProductDetailScreen` (info tabs: Details / History movements)
- [x] 2.7 Build `AddEditProductScreen` (form with category/status/location pickers, barcode scan button)
- [x] 2.8 Build `BarcodeScannerScreen` (CameraX preview, ML Kit overlay, retry/cancel/manual)
- [x] 2.9 Define navigation graph `InventoryNavHost` with routes: list, detail, add, edit, scan
- [x] 2.10 Write Compose UI tests: list renders, navigation, form validation, scanner permission flow
- [x] 2.11 Write ViewModel tests (Turbine): state transitions for all spec scenarios

## Phase 3: App Integration + E2E

- [x] 3.1 Add `inventory` module to `settings.gradle.kts`
- [x] 3.2 Add `inventory` dependency to `app/build.gradle.kts`
- [x] 3.3 Create `InventoryModule` (Hilt) binding repository interfaces → impls
- [x] 3.4 Install `InventoryModule` in `AppModule`
- [x] 3.5 Add `InventoryNavHost` to `AppNavHost` (bottom nav tab)
- [x] 3.6 Add camera permission to `AndroidManifest.xml` + rationale string
- [x] 3.7 Create `assets/mock_products.json` with 50+ products
- [x] 3.8 Write E2E test: add product → scan barcode → verify auto-fill → save → appears in list
- [x] 3.9 Write E2E test: edit quantity → movement log created → history tab shows entry
- [x] 3.10 Verify CI: `./gradlew test connectedCheck` passes on GitHub Actions

## Phase 4: Cleanup

- [x] 4.1 Add KDoc to public APIs
- [x] 4.2 Remove unused imports / dead code
- [x] 4.3 Update `README.md` with inventory module overview