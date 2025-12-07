## Response Style
- Provide concise, insightful answers with clear information flow
- Write at a bachelor-level academic standard
- Ensure each sentence connects logically to the previous point—avoid abrupt topic shifts

## Code Style Requirements
- Use **tabs** for indentation (not spaces)
- Minimize comments—only add them when explicitly requested
- Keep code clean and readable with minimal verbosity

## Programming Language Proficiency (in order)
1. Kotlin (Android)
2. Jetpack Compose UI & 
3. Room Database
4. Android app architecture (MVVM, Repository pattern)

## Code Generation Policy
- Always produce original implementations—never copy code verbatim from sources
- Apply subtle modifications to ensure functional equivalence while maintaining uniqueness
- Use "Tab" characters for indentation and replace any occurances of spaces used for indentation

---

## Architecture Overview

### Project Structure
This is an **Android calorie tracking app** built with **Kotlin, Jetpack Compose, Room Database, and Hilt DI**.

**Directory structure:**
- `Model/` - Data layer (database, repository, formulas, DI)
  - `database/` - Room entities, DAOs, converters
  - `CalorieRepository` - Singleton 
  - `formula/` - Calorie calculation logic (`MifflinModel`, strategies, enums)
  - `di/` - Hilt modules (`DatabaseModule`)
- `View/` - UI layer (Compose screens, components, navigation)
  - `screens/` - Main screens (`Overview`, `History`, `ProfileSettings`)
  - `components/` - Modularized Composables and reusable components
  - `navigation/` - Navigation graph and bottom nav
  - `theme/` - Material3 theming
- `Viewmodel/` - Presentation layer (ViewModels with `@HiltViewModel`)

### Key Architectural Decisions

**Single Repository Pattern**: The app uses **one consolidated `CalorieRepository`** instead of multiple repositories. All data operations flow through this single source of truth. When adding features, extend `CalorieRepository` rather than creating new repositories.

**Database Schema** (3 entities with foreign key relationships):
1. `UserData` - Single user profile (base table)
2. `DailyData` - Per-day calorie configuration (FK to UserData)
3. `ActivityLog` - Food/workout entries (FK to DailyData, stores pictureUri as string)

**IMPORTANT**: When asked to verify schema or compare with ERD/diagrams, **always scan the actual entity files** in `Model/database/entities/` folder. Do not rely on this documentation alone as the schema may have evolved through migrations.

**Flow Pattern**: ViewModels expose `StateFlow` from repository `Flow` using `.stateIn()`. Screens collect with `collectAsStateWithLifecycle()` or `collectAsState()`.

**Calorie Calculation Logic**: Lives in `MifflinModel` (static companion object with mutable state). Uses Mifflin-St Jeor equation for RMR + activity factors + granularity adjustments. 

**Database migrations**: Managed in `DatabaseModule.kt`. Current version: **11**. Add new `Migration` objects for schema changes (see `MIGRATION_8_9`, `MIGRATION_9_10`, `MIGRATION_10_11` as examples).

**Adding new Room entities:**
1. Create entity in `Model/database/entities/`
2. Create DAO in `Model/database/dao/`
3. Register in `AppDatabase` entities array
4. Provide DAO in `DatabaseModule`
5. Increment database version
6. Create migration

**Type Converters**: Custom types (enums, dates) need converters in `Converters.kt`:
- `ActivityLevel`, `GoalType`, `CalorieStrategy`, `ActivityType` → String
- `Date` → Long (timestamp)

### Project-Specific Patterns

**Enum Display Names**: Enums have Indonesian display names via `getDisplayName()` methods (e.g., `GoalType.LOSE_WEIGHT` → "Menurunkan berat badan (Cutting)"). Always use these for UI text.

**Activity Types**: Only two types exist - `WORKOUT` (calories burned) and `CONSUMPTION` (calories consumed). Do not add new types without schema redesign.

**Hilt Injection**: 
- Application class: `@HiltAndroidApp` on `PencatatanKalori`
- Activity: `@AndroidEntryPoint` on `MainActivity`
- ViewModels: `@HiltViewModel` with `@Inject constructor`
- Repository: `@Singleton` with `@Inject constructor`
- Modules: Use `@InstallIn(SingletonComponent::class)`

**Navigation**: Bottom navigation with 3 screens. Add new screens to `Screen` sealed class and register in `NavGraph.kt`. Start destination is `Screen.Overview.route`.

### Integration Points

**Dependencies** (key libraries):
- Compose BOM 2024.09.00
- Room 2.7.1 with KSP 2.0.21-1.0.27
- Hilt 2.51.1
- Navigation Compose 2.7.7
- kotlinx-datetime 0.5.0
- Coil (image loading)

**Data Flow Example** (Overview screen):
1. `OverviewViewModel` combines flows from repository
2. `repository.getUserProfile()` + `repository.getTodayActivities()` + `repository.getTodayDailyDataFlow()`
3. Calculate consumed/burned calories, apply `MifflinModel.calculateRemainingCalories()`
4. Emit `OverviewData` via `overviewData` StateFlow
5. `OverviewScreen` collects with `collectAsStateWithLifecycle()`

### Common Pitfalls

- **Don't modify `MifflinModel` singleton state from ViewModels** - it's shared globally
- **Always use `collectAsStateWithLifecycle()` in screens** - not `collectAsState()` (lifecycle-aware)
- **Room converters are global** - changing enum names breaks existing database data
- **DailyData is per-day** - create/update for current date before logging activities
- **Foreign key cascades** - Deleting UserData cascades to DailyData, which cascades to ActivityLog
- **Database migrations are critical** - Always add migrations for schema changes to avoid crashes on updates
