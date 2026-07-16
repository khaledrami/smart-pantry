# Barcode Scan Specification

## Purpose
Camera-based barcode detection to accelerate product entry by auto-filling product fields from scanned codes.

## Requirements

### Requirement: Camera Permission

The system SHALL request and handle camera permission for barcode scanning.

#### Scenario: Grant camera permission

- GIVEN user taps "Scan Barcode" button
- WHEN system requests CAMERA permission
- AND user grants permission
- THEN barcode scanner screen opens with camera preview

#### Scenario: Deny camera permission

- GIVEN user taps "Scan Barcode" button
- WHEN system requests CAMERA permission
- AND user denies permission
- THEN toast shows "Camera permission required for barcode scanning"
- AND user returns to AddEditProductScreen with manual entry focused

#### Scenario: Permanently deny camera permission

- GIVEN user previously denied CAMERA permission with "Don't ask again"
- WHEN user taps "Scan Barcode"
- THEN dialog directs user to app settings to enable permission

### Requirement: ML Kit Barcode Detection

The system SHALL detect EAN-13, EAN-8, UPC-A, UPC-E, QR_CODE, CODE_128 barcodes using ML Kit.

#### Scenario: Scan valid EAN-13 barcode

- GIVEN camera preview active on barcode scanner screen
- WHEN user points camera at product with EAN-13 barcode
- AND barcode is in focus and well-lit
- THEN ML Kit detects barcode within 2 seconds
- AND barcode value returned as raw string
- AND scanner screen closes
- AND AddEditProductScreen populates barcode field with scanned value

#### Scenario: Scan fails due to poor lighting

- GIVEN camera preview active
- WHEN user points camera at barcode in low light for 5 seconds
- AND no barcode detected
- THEN overlay shows "Move closer or improve lighting"
- AND user can tap to retry or cancel

#### Scenario: Scan fails due to damaged barcode

- GIVEN camera preview active
- WHEN user points camera at torn/wrinkled barcode for 3 seconds
- AND no barcode detected
- THEN overlay shows "Barcode not recognized. Enter manually?"
- AND user can tap "Enter Manually" to return to form with barcode field focused

### Requirement: Product Lookup (Mock)

The system SHALL support future OpenFoodFacts integration via a mock lookup for MVP.

#### Scenario: Mock lookup returns product data

- GIVEN barcode scanned successfully (e.g., "8410066010203")
- WHEN mock lookup service called with barcode
- THEN service returns ProductData: name="Tomate Frito", brand="Orlando", category=SAUCES, defaultQuantity=350, unit="g", imageUrl="..."
- AND AddEditProductScreen auto-fills name, brand, category, quantity, unit
- AND user can edit any field before saving

#### Scenario: Mock lookup returns no match

- GIVEN barcode scanned successfully
- WHEN mock lookup service called
- AND no product found in mock database
- THEN AddEditProductScreen shows "Product not found. Fill manually."
- AND only barcode field pre-filled
- AND user enters remaining fields manually

#### Scenario: Network error during lookup

- GIVEN barcode scanned successfully
- WHEN lookup service throws network exception
- THEN toast shows "Lookup failed. Enter manually."
- AND AddEditProductScreen only has barcode pre-filled
- AND user can proceed with manual entry

### Requirement: Duplicate Barcode Handling

The system SHALL detect and handle duplicate barcodes gracefully.

#### Scenario: Scan barcode already in inventory

- GIVEN product with barcode "8410066010203" exists in inventory
- WHEN user scans same barcode
- THEN dialog shows "Product already in pantry. Open existing?"
- AND user can choose "Open Existing" → navigates to ProductDetailScreen
- OR "Add New Anyway" → proceeds to form with barcode pre-filled

## Non-Functional Requirements

| NFR | Target | Verification |
|-----|--------|--------------|
| Scan latency (ideal conditions) | < 2s | Manual test on mid-range device |
| Scan success rate (good lighting) | > 90% | Test with 20 products |
| Camera preview startup | < 500ms | Compose UI test with timing |
| Battery impact | < 5% per 10 scans | Profiler measurement |