# Proposal: inventory-core

## Intent

Build the foundational inventory module for Smart Pantry: product data model, Room persistence, CRUD repository, Compose list/detail screens, and barcode scanning entry point. This is the "heart" of the app — everything else (shopping list, AI recipes, stats) depends on reliable inventory data.

## Scope

### In Scope
- Product entity + Room schema (all fields from product model in vision doc)
- Location hierarchy (Freezer > Drawer, Fridge > Zone, Pantry > Shelf)
- Category enum (15 categories from vision)
- Status enum (7 states from vision)
- Movement log entity (audit trail for every change)
- Repository interface + Room implementation with Flow queries
- Compose: ProductListScreen (grouped by location/expiry), ProductDetailScreen, AddEditProductScreen
- Barcode scan via ML Kit → auto-fill product fields (OpenFoodFacts fallback later)
- Unit tests (repository, mappers, viewmodels) + Compose UI tests

### Out of Scope
- Shopping list module (separate change)
- AI recipe suggestions (separate change)
- Statistics/dashboard (separate change)
- Cloud sync/backup (future)
- Multi-home/multi-user (premium feature)
- NFC/Bluetooth sensors (future integration)

## Capabilities

### New Capabilities
- `inventory-core`: Product CRUD, location hierarchy, category/status enums, movement audit log
- `barcode-scan`: Camera permission, ML Kit barcode detection, product lookup/auto-fill

### Modified Capabilities
- None (greenfield)

## Approach

Clean Architecture with feature module `inventory`:
- `domain/` — entities, repository interfaces, use cases (GetProducts, AddProduct, UpdateProduct, DeleteProduct, ScanBarcode)
- `data/` — Room entities/DAOs, repository impl, mappers, barcode scanner wrapper
- `presentation/` — ViewModels (StateFlow), Compose screens, navigation
- DI via Hilt module in `inventory/`

Strict TDD: write repository/usecase tests first (JUnit5 + MockK + Turbine), then ViewModel tests, then Compose tests.

## Affected Areas

| Area | Impact | Description |
|------|--------|-------------|
| `inventory/domain/` | New | Entities, repository interfaces, use cases |
| `inventory/data/` | New | Room schema, DAOs, repository impl, barcode scanner |
| `inventory/presentation/` | New | ViewModels, Compose screens, navigation graph |
| `app/` | Modified | Hilt module registration, nav host entry point |

## Risks

| Risk | Likelihood | Mitigation |
|------|------------|------------|
| Room schema migration complexity later | Medium | Design schema v1 carefully; add `@Entity` versioning from start |
| ML Kit barcode accuracy on curved packages | Medium | Manual entry fallback; OpenFoodFacts API as backup |
| Scope creep into shopping list | Low | Hard module boundary; shopping list in separate change |
| Compose preview/test flakiness | Medium | Use `ComposeTestRule` with `runOnIdle`; disable animations in tests |

## Rollback Plan

1. Revert `inventory/` module folder
2. Remove `inventory` from `settings.gradle.kts`
3. Remove Hilt module from `AppModule`
4. Delete navigation entry in `AppNavHost`
5. No DB migration needed (new tables only)

## Dependencies

- Android SDK 34+, Kotlin 2.0, Compose BOM 2024.08+
- Room 2.6+, Hilt 2.48+, ML Kit Barcode Scanning 17.2+
- JUnit5, MockK, Turbine, Compose Test, Robolectric

## Success Criteria

- [ ] Add product via UI → persists in Room → appears in list grouped by location
- [ ] Scan barcode → auto-fills name/category/brand (mock API) → save → appears in list
- [ ] Edit quantity → creates Movement log entry → list updates reactively (Flow)
- [ ] Delete product → status=CONSUMED + Movement log → removed from active list
- [ ] Unit test coverage ≥ 80% on domain/data layers
- [ ] Compose tests: list renders, detail navigation, form validation
- [ ] Build + all tests pass in CI (GitHub Actions)