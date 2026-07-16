# Design: inventory-core

## Technical Approach

Clean Architecture feature module `inventory/` with three layers (domain, data, presentation). Room for persistence, Hilt for DI, ML Kit for barcode scanning, Compose for UI. Strict TDD: domain/use cases → data/repository → presentation/ViewModels → Compose screens.

## Architecture Decisions

### Decision: Feature Module Structure

**Choice**: `inventory/` as separate Gradle module with domain/data/presentation subpackages
**Alternatives**: Monolithic app module, or domain/data/presentation as separate modules
**Rationale**: Balances build speed (single module) with architectural boundaries. Enables future KMP extraction of domain/data.

### Decision: Room Entity Design

**Choice**: Single `ProductEntity` with all 17 fields + `LocationEntity`, `CategoryEntity` (enum), `StatusEntity` (enum), `MovementEntity`
**Alternatives**: Separate tables for each enum, embedded value objects
**Rationale**: Flat schema minimizes joins; enums as TEXT with CHECK constraints for integrity; Location as TEXT path ("Freezer/Upper Drawer/Left Slot") for simple grouping queries.

### Decision: Barcode Scanner Integration

**Choice**: ML Kit Barcode Scanning via `BarcodeScanner` in `data/barcode/`, wrapped in `BarcodeScannerRepository` interface in domain
**Alternatives**: ZXing, CameraX + custom ML, Google Vision (deprecated)
**Rationale**: ML Kit is on-device, fast, maintained by Google, supports all required formats (EAN-13, UPC, QR, Code128). No API key needed.

### Decision: Movement Log as Separate Entity

**Choice**: `MovementEntity` table with FK to `ProductEntity`, indexed by productId + timestamp DESC
**Alternatives**: JSON column in Product, event sourcing
**Rationale**: Query performance for history tab; immutable audit trail; enables future analytics without schema changes.

### Decision: StateFlow for Reactive UI

**Choice**: Repository returns `Flow<List<Product>>`; ViewModels expose `StateFlow<UiState>`; Compose collects via `collectAsStateWithLifecycle()`
**Alternatives**: LiveData, RxJava, plain callbacks
**Rationale**: Flow is native Kotlin, integrates with coroutines, cold streams = no leaks, Compose has first-class support.

## Data Flow

```
User Action (Compose)
       │
       ▼
ViewModel (StateFlow<UiState>)
       │
       ▼
Use Case (suspend fun)
       │
       ▼
Repository Interface (Domain)
       │
       ▼
Room DAO (Data) ──→ SQLite
       │
       ▼
Mapper (Domain ↔ Entity)
       │
       ▼
Flow emits → ViewModel updates UiState → Compose recomposes
```

Barcode scan flow:
```
Scan Button → Permission Check → ML Kit Scanner (CameraX) → Barcode String
                                                      │
                                                      ▼
                                    MockLookupService (Domain) → ProductData
                                                      │
                                                      ▼
                                    AddEditProductScreen (pre-filled form)
```

## File Changes

| File | Action | Description |
|------|--------|-------------|
| `inventory/build.gradle.kts` | Create | Module config: Compose, Room, Hilt, ML Kit, Test deps |
| `inventory/src/main/AndroidManifest.xml` | Create | CAMERA permission, ML Kit metadata |
| `inventory/domain/model/Product.kt` | Create | Domain entity (data class, 17 fields) |
| `inventory/domain/model/Location.kt` | Create | Value object with path parsing |
| `inventory/domain/model/Category.kt` | Create | Enum (15 values) |
| `inventory/domain/model/Status.kt` | Create | Enum (7 values) |
| `inventory/domain/model/Movement.kt` | Create | Domain entity with type enum |
| `inventory/domain/repository/ProductRepository.kt` | Create | Interface: CRUD + Flow queries + barcode |
| `inventory/domain/repository/BarcodeScannerRepository.kt` | Create | Interface: scan(), lookup(barcode) |
| `inventory/domain/usecase/GetProductsUseCase.kt` | Create | Returns Flow<List<Product>> grouped by location |
| `inventory/domain/usecase/GetProductUseCase.kt` | Create | Returns Flow<Product> by id |
| `inventory/domain/usecase/AddProductUseCase.kt` | Create | Insert + Movement log |
| `inventory/domain/usecase/UpdateProductUseCase.kt` | Create | Update + Movement log |
| `inventory/domain/usecase/DeleteProductUseCase.kt` | Create | Soft delete (status=CONSUMED) + Movement log |
| `inventory/domain/usecase/ScanBarcodeUseCase.kt` | Create | Permission → scan → lookup |
| `inventory/domain/usecase/MoveProductUseCase.kt` | Create | Location change + Movement log |
| `inventory/data/entity/ProductEntity.kt` | Create | @Entity with all fields, indices |
| `inventory/data/entity/LocationEntity.kt` | Create | @Entity (optional, or embedded) |
| `inventory/data/entity/MovementEntity.kt` | Create | @Entity with FK, indices |
| `inventory/data/dao/ProductDao.kt` | Create | @Dao with Flow queries |
| `inventory/data/dao/MovementDao.kt` | Create | @Dao with insert + query by productId |
| `inventory/data/repository/ProductRepositoryImpl.kt` | Create | Implements ProductRepository |
| `inventory/data/repository/BarcodeScannerRepositoryImpl.kt` | Create | Implements BarcodeScannerRepository (ML Kit) |
| `inventory/data/mapper/ProductMapper.kt` | Create | Entity ↔ Domain |
| `inventory/data/mapper/MovementMapper.kt` | Create | Entity ↔ Domain |
| `inventory/data/barcode/MockProductLookupService.kt` | Create | Mock OpenFoodFacts for MVP |
| `inventory/presentation/viewmodel/ProductListViewModel.kt` | Create | StateFlow<UiState>, collects GetProductsUseCase |
| `inventory/presentation/viewmodel/ProductDetailViewModel.kt` | Create | StateFlow<UiState>, GetProduct + movements |
| `inventory/presentation/viewmodel/AddEditProductViewModel.kt` | Create | Form state, validation, save/delete |
| `inventory/presentation/viewmodel/BarcodeScanViewModel.kt` | Create | Scan flow, permission, lookup |
| `inventory/presentation/screen/ProductListScreen.kt` | Create | LazyColumn grouped by location, expiry chips |
| `inventory/presentation/screen/ProductDetailScreen.kt` | Create | Detail + history tab (movements) |
| `inventory/presentation/screen/AddEditProductScreen.kt` | Create | Form with category/status/location pickers |
| `inventory/presentation/screen/BarcodeScanScreen.kt` | Create | CameraX preview + ML Kit overlay |
| `inventory/presentation/navigation/InventoryNavGraph.kt` | Create | Compose navigation routes |
| `inventory/di/InventoryModule.kt` | Create | Hilt @Module bindings |
| `app/build.gradle.kts` | Modify | Add `inventory` module dependency |
| `app/src/main/java/.../AppModule.kt` | Modify | Install InventoryModule |
| `app/src/main/java/.../AppNavHost.kt` | Modify | Include InventoryNavGraph |
| `settings.gradle.kts` | Modify | Include `:inventory` |

## Interfaces / Contracts

```kotlin
// Domain: ProductRepository
interface ProductRepository {
    fun getAllProducts(): Flow<List<Product>>
    fun getProduct(id: Long): Flow<Product>
    suspend fun addProduct(product: Product): Long
    suspend fun updateProduct(product: Product)
    suspend fun deleteProduct(id: Long)
    suspend fun moveProduct(id: Long, newLocation: Location)
}

// Domain: BarcodeScannerRepository
interface BarcodeScannerRepository {
    suspend fun scan(): Result<BarcodeScanResult>
    suspend fun lookupProduct(barcode: String): Result<ProductData>
}

// Data: ProductDao
@Dao
interface ProductDao {
    @Query("SELECT * FROM products ORDER BY location, expiryDate")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :id")
    fun getProduct(id: Long): Flow<ProductEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(entity: ProductEntity): Long

    @Update
    suspend fun updateProduct(entity: ProductEntity)

    @Query("UPDATE products SET status = 'CONSUMED' WHERE id = :id")
    suspend fun softDelete(id: Long)
}

// Data: MovementDao
@Dao
interface MovementDao {
    @Insert
    suspend fun insertMovement(entity: MovementEntity)

    @Query("SELECT * FROM movements WHERE productId = :productId ORDER BY timestamp DESC")
    fun getMovements(productId: Long): Flow<List<MovementEntity>>
}
```

## Testing Strategy

| Layer | What to Test | Approach |
|-------|-------------|----------|
| Unit (Domain) | Use cases, mappers, entities | JUnit5 + MockK + Turbine (Flow testing) |
| Unit (Data) | DAOs, RepositoryImpl, Mappers | Room in-memory DB + JUnit5 |
| Unit (Presentation) | ViewModels, UiState transitions | JUnit5 + MockK + Turbine + `runTest` |
| Integration | Room schema, migrations, Flow emission | Room test DB + real DAOs |
| UI (Compose) | Screen rendering, navigation, form validation | ComposeTestRule + Robolectric |
| E2E (Optional) | Full scan→save→list flow | AndroidJUnitRunner + Espresso/Compose |

## Threat Matrix

N/A — no routing, shell, subprocess, VCS/PR automation, executable-file classification, or process-integration boundary.

## Migration / Rollout

No migration required (greenfield). Room schema v1 = initial create. Future migrations via `RoomDatabase.Migration` when schema evolves.

## Open Questions

- [x] Should Location be a separate Room entity with FK, or embedded string path? — **Resolved: Embedded string path**
- [x] MockProductLookupService data source: hardcoded map vs JSON asset vs generated? — **Resolved: JSON asset**
- [x] Camera permission rationale string for Play Store compliance? — **Resolved**
- [x] Should MovementType be enum in domain or sealed class for extensibility? — **Resolved: Sealed class**

## Resolved Decisions

### Location: Embedded String Path

**Choice**: `location: String = "Freezer/Upper Drawer/Left Slot"`

**Rationale**: 
- No JOIN needed for grouping queries (`WHERE location LIKE 'Freezer/%'`)
- Simple Compose UI grouping by `split("/")`
- Migration-safe (alter table add column vs new table + FK)
- Matches vision doc's 3-level hierarchy directly

### Mock Lookup: JSON Asset

**Choice**: `assets/mock_products.json` with 50-100 common products

```json
{
  "8410066010203": {
    "name": "Tomate Frito",
    "brand": "Orlando",
    "category": "SAUCES",
    "defaultQuantity": 350,
    "unit": "g",
    "imageUrl": "https://..."
  }
}
```

**Rationale**: 
- Maintainable without code changes
- Easy to expand from OpenFoodFacts dump later
- Testable with test assets

### MovementType: Sealed Class

**Choice**:
```kotlin
sealed interface MovementType {
    data class Entry(val oldQuantity: Int?, val newQuantity: Int) : MovementType
    data class Exit(val oldQuantity: Int, val newQuantity: Int?) : MovementType
    data class Freeze(val locationBefore: String, val locationAfter: String) : MovementType
    data class Thaw(val locationBefore: String, val locationAfter: String) : MovementType
    data class LocationChange(val from: String, val to: String) : MovementType
    data class Correction(val field: String, val oldValue: String, val newValue: String) : MovementType
    data class Donation : MovementType
    data class Discard : MovementType
}
```

**Rationale**: 
- Type-safe payload per movement type (no nullable soup)
- Exhaustive `when` in UI/history rendering
- Easy to add new types without schema migration
- Domain-level, not tied to Room enum