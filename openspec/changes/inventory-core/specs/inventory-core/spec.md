# Inventory Core Specification

## Purpose
Core inventory domain: product lifecycle, location hierarchy, categorization, status tracking, and audit trail for all pantry items.

## Requirements

### Requirement: Product CRUD

The system SHALL allow users to create, read, update, and delete products with full attribute fidelity.

#### Scenario: Create product with all fields

- GIVEN user opens Add Product screen
- WHEN user fills all fields (name, description, brand, category, barcode, quantity, unit, price, purchase date, open date, freeze date, best-before date, expiry date, location, status, notes, tags) and saves
- THEN product persists in Room with all fields populated
- AND product appears in ProductListScreen grouped by location

#### Scenario: Create product with minimal required fields

- GIVEN user opens Add Product screen
- WHEN user fills only name, quantity, unit, and location then saves
- THEN product persists with required fields
- AND optional fields are null/empty

#### Scenario: Update product quantity

- GIVEN product exists with quantity > 0
- WHEN user edits quantity to new value and saves
- THEN product quantity updates in Room
- AND Movement log entry created with type=CORRECTION, oldQty, newQty
- AND ProductListScreen updates reactively via Flow

#### Scenario: Delete product

- GIVEN product exists in active list
- WHEN user deletes product
- THEN product status changes to CONSUMED
- AND Movement log entry created with type=EXIT
- AND product removed from active ProductListScreen
- AND product remains queryable in history

### Requirement: Location Hierarchy

The system SHALL support three-level location hierarchy: storage type (Freezer/Fridge/Pantry) → zone (Drawer/Shelf/Zone) → slot (Top/Middle/Bottom/Door/Veggies).

#### Scenario: Select location when creating product

- GIVEN user opens location picker
- WHEN user selects Freezer → Upper Drawer → Left Slot
- THEN product.location = "Freezer/Upper Drawer/Left Slot"
- AND ProductListScreen groups product under Freezer > Upper Drawer > Left Slot

#### Scenario: Move product between locations

- GIVEN product exists in Freezer/Upper Drawer
- WHEN user changes location to Pantry/Top Shelf
- THEN product.location updates
- AND Movement log entry created with type=LOCATION_CHANGE
- AND ProductListScreen regrouping reflects new location

### Requirement: Category Enum

The system SHALL enforce 15 predefined categories from vision document.

#### Scenario: Assign category to product

- GIVEN user creates/edits product
- WHEN user selects category from picker
- THEN category stored as enum (MEAT, FISH, VEGETABLES, FRUITS, DAIRY, FROZEN, BEVERAGES, CANNED, LEGUMES, PASTA, RICE, SPICES, BREAD, SAUCES, SNACKS, OTHER)
- AND ProductListScreen can filter by category

### Requirement: Status Enum

The system SHALL track product state via 7 statuses.

#### Scenario: Status transitions

- GIVEN product created with status=AVAILABLE
- WHEN user opens product → status=AVAILABLE (default)
- WHEN user marks as opened → status=OPENED
- WHEN user freezes → status=FROZEN
- WHEN user consumes fully → status=CONSUMED
- WHEN expiry date passes → status=EXPIRED (auto or manual)
- WHEN user donates → status=DONATED
- WHEN user discards → status=DISCARDED
- THEN each transition creates Movement log entry with type matching transition

### Requirement: Movement Audit Log

The system SHALL record every product change as immutable movement entry.

#### Scenario: Movement created on quantity change

- GIVEN product quantity = 5
- WHEN user changes quantity to 3
- THEN Movement entry created: productId, type=CORRECTION, oldQuantity=5, newQuantity=3, timestamp, userId

#### Scenario: Movement created on status change

- GIVEN product status=AVAILABLE
- WHEN user marks as CONSUMED
- THEN Movement entry created: productId, type=EXIT, oldStatus=AVAILABLE, newStatus=CONSUMED, timestamp

#### Scenario: Movement query by product

- GIVEN multiple movements exist for product
- WHEN user views ProductDetailScreen history tab
- THEN all movements for product displayed chronologically newest-first
- AND each shows type, field changed, old value, new value, timestamp