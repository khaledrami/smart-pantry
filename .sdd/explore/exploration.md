# Exploration: Smart Pantry — Greenfield Mobile App

## Current State
Empty project — no codebase exists. This is a greenfield Android-first mobile application for smart pantry/inventory management with AI-powered features.

## Affected Areas (Future)
- `app/` — Android application (Kotlin/Jetpack Compose)
- `core/` — Shared business logic, data models, repositories
- `data/` — Local database (Room), sync layer, API clients
- `feature/inventory/` — Product CRUD, barcode scanning, location management
- `feature/shopping-list/` — Smart list generation, categorization
- `feature/ai/` — Recipe generation, consumption predictions, waste analytics
- `feature/stats/` — Consumption dashboards, waste tracking, spending reports

## Approaches

### 1. **Native Android (Kotlin + Compose + Room + Hilt)** — Recommended
- **Pros**: Best performance, native UX, direct hardware access (camera for barcode), Material 3, mature testing tooling, Play Store optimization
- **Cons**: iOS later requires separate codebase
- **Effort**: Medium-High (single platform first)

### 2. **Kotlin Multiplatform (KMP) + Compose Multiplatform**
- **Pros**: Shared business logic + UI across Android/iOS, single codebase for core/domain
- **Cons**: Compose iOS still maturing, larger binary, debugging complexity, smaller ecosystem
- **Effort**: High (steeper learning curve, more setup)

### 3. **Flutter (Dart)**
- **Pros**: Single codebase for Android/iOS from day one, fast dev cycle, great UI flexibility
- **Cons**: Not native Kotlin, larger app size, platform channels for native features (barcode, NFC), separate testing stack
- **Effort**: Medium (if team knows Dart)

### 4. **React Native (TypeScript)**
- **Pros**: Web skill reuse, large ecosystem, CodePush for OTA updates
- **Cons**: Bridge overhead, native module maintenance, less smooth 60fps animations, barcode scanning via native modules
- **Effort**: Medium

## Recommendation
**Approach 1: Native Android (Kotlin + Compose + Room + Hilt + Flow)**

**Why:**
- Project explicitly targets Android first, iOS "later"
- Vision emphasizes "extremely fast", "very visual", "one-handed use" — native Compose excels here
- Camera/barcode scanning, NFC tags, Bluetooth scales need reliable native APIs
- Room + Flow + Coroutines give reactive offline-first architecture naturally
- Strict TDD viable with JUnit5 + Turbine + Compose Testing + MockK
- Aligns with Googles modern Android stack (MAD skills)

**Architecture Pattern**: Clean Architecture + Modularization by feature
```
app/              # Application class, DI graph, navigation
core/             # Domain models, use cases, repository interfaces
data/             # Room DAOs, Repository impls, API clients, DataStore
feature/inventory/   # Inventory UI + ViewModels
feature/shopping/    # Shopping list UI + ViewModels
feature/ai/          # AI integration (local LLM or API)
feature/stats/       # Charts, analytics UI
```

**Tech Stack:**
| Layer | Choice |
|-------|--------|
| Language | Kotlin 2.0 |
| UI | Jetpack Compose (Material 3) |
| DI | Hilt |
| DB | Room (SQLite) + SQLDelight (optional for KMP later) |
| Networking | Ktor / Retrofit |
| Async | Coroutines + Flow |
| Testing | JUnit5, MockK, Turbine, Compose Test, Robolectric |
| CI | GitHub Actions + Gradle Managed Devices |
| AI | On-device (MediaPipe/ML Kit) + Cloud API fallback |

## Risks
- **Scope creep** — Product concept is massive (AI, IoT, gamification, multi-platform). Must slice into MVP.
- **No existing codebase** — All patterns must be established from scratch (opportunity + risk).
- **AI integration** — Recipe generation needs LLM API (cost, latency, privacy). Start with rule-based suggestions.
- **Barcode scanning** — ML Kit Barcode Scanning works offline but needs camera permission handling.
- **Data sync/backup** — Not in MVP but architecture must support it later (Room + sync engine).
- **Strict TDD** — Greenfield means writing tests first for everything; slows initial velocity but pays off.

## Ready for Proposal
**Yes** — but recommend defining a tight MVP scope first (inventory CRUD + barcode scan + shopping list + expiry alerts). The full vision maps to 6-8 SDD changes minimum.

**Suggested first change**: `inventory-core` — Product model, Room schema, CRUD repository, Compose list screen, barcode scan intent.