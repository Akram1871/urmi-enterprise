# 04_TRD.md — Technical Requirements Document & Architecture Specification

**Project Name**: ব্যবসার হিসাব (Babshar Hisab)  
**Target Platform**: Android Native (`minSdk = 26` [Android 8.0], `targetSdk = 35` [Android 15], `compileSdk = 35`)  
**Locked Tech Stack**: **Kotlin + Jetpack Compose (Material 3) + Room SQLite + Hilt DI + WorkManager + OkHttp/Kotlinx.Serialization**  

---

## 1. Architectural Rationale: Why Native Kotlin + Jetpack Compose + Room + WorkManager?

For a mission-critical offline ledger with background synchronization on budget-to-mid-range Bangladeshi Android devices, **Native Kotlin + Jetpack Compose** is locked as our architecture over hybrid/cross-platform alternatives for four decisive engineering reasons:
1. **First-Class Android `WorkManager` Reliability**: Background daily backup to Google Sheets requires direct OS-level job scheduling (`PeriodicWorkRequest`, `Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED)`, wake locks, and boot-completed receivers) without cross-engine isolate bridges failing when the app is killed by OEM battery savers (Xiaomi/Realme/Transsion/Samsung).
2. **Zero-Overhead Room SQLite Aggregations**: Real-time P&L calculations, multi-vendor purchase rollups, shopkeeper *Baki* running balances, and 6-month category trend queries execute natively via compiled SQLite queries emitting cold/hot Kotlin `Flow<T>`.
3. **Bangla Font Shaping & Complex Script Rendering**: Native Android `Canvas` + `TextMeasurer` in Jetpack Compose handles Bengali Unicode conjuncts (*Juktakkhor* / যুক্তাক্ষর) with zero glyph clipping.
4. **Compact APK Footprint**: `< 12 MB` release APK with R8 full-mode shrinking, ensuring rapid installation and zero storage pressure on 32GB/64GB entry-level phones.

---

## 2. Locked Dependency & Version Matrix (`gradle/libs.versions.toml`)

All dependencies are strictly pinned to stable, production-verified versions:

| Category | Library / Artifact | Locked Version | Purpose |
| :--- | :--- | :--- | :--- |
| **Language & Toolchain** | Kotlin (`org.jetbrains.kotlin.android`) | `2.0.21` | Core language + Compose Compiler plugin |
| **Build System** | Android Gradle Plugin (`com.android.application`) | `8.7.2` | Build orchestration |
| **Annotation Processing** | KSP (`com.google.devtools.ksp`) | `2.0.21-1.0.27` | Fast compile-time codegen for Room & Hilt |
| **UI Toolkit** | Jetpack Compose BOM (`androidx.compose:compose-bom`) | `2024.10.01` | Material 3 (`1.3.1`), UI, Foundation, Canvas graphics |
| **Navigation** | Navigation Compose (`androidx.navigation:navigation-compose`) | `2.8.3` | Type-safe screen routing & backstack management |
| **Lifecycle & State** | Lifecycle ViewModel Compose (`androidx.lifecycle:lifecycle-runtime-compose`) | `2.8.7` | `collectAsStateWithLifecycle()`, `viewModelScope` |
| **Local Database** | Room (`androidx.room:room-runtime`, `room-ktx`, `room-compiler`) | `2.6.1` | ACID relational local database with `Flow` queries |
| **Dependency Injection** | Hilt (`com.google.dagger:hilt-android`, `hilt-work`) | `2.52` | Compile-time DI for Repositories, DAOs, ViewModels, Workers |
| **Background Sync** | WorkManager (`androidx.work:work-runtime-ktx`) | `2.10.0` | Guaranteed offline-queued daily Google Sheets sync |
| **Preferences** | DataStore Preferences (`androidx.datastore:datastore-preferences`) | `1.1.1` | Storing sync metadata, theme mode, font preference |
| **Encrypted Storage** | Security Crypto (`androidx.security:security-crypto`) | `1.1.0-alpha06` | Storing Google Apps Script Webhook URL, Secret Token, & Local PIN hash |
| **Networking** | OkHttp (`com.squareup.okhttp3:okhttp`) | `4.12.0` | Lightweight HTTPS client (follows Google Apps Script `302` redirects properly) |
| **JSON Serialization** | Kotlinx Serialization (`org.jetbrains.kotlinx:kotlinx-serialization-json`) | `1.7.3` | Fast, reflection-free JSON payload serialization for Sheets backup |
| **Coroutines** | Kotlinx Coroutines (`org.jetbrains.kotlinx:kotlinx-coroutines-android`) | `1.9.0` | Structured concurrency (`Dispatchers.IO`, `Dispatchers.Default`) |
| **Unit Testing** | JUnit 4 (`4.13.2`), Turbine (`1.2.0`), Coroutines Test (`1.9.0`), Room Testing | Locked | Repository, ViewModel, Formatter, and Accounting math verification |

---

## 3. Clean Modular Package Structure

The codebase enforces a strict **3-Layer Clean Architecture (`data` -> `domain` -> `ui`)** within a single `:app` module so the coding agent can navigate and modify files deterministically:

```text
e:\Akram\projects\baba_buisness\
├── docs/                                  # 6-Document Context Architecture (Single Source of Truth)
│   ├── 01_PRD.md
│   ├── 02_AppFlow.md
│   ├── 03_UI_Spec.md
│   ├── 04_TRD.md
│   ├── 05_Schema.md
│   └── 06_Plan.md
├── CLAUDE.md                              # Agent governance instructions
├── .cursorrules                           # Cursor/IDE agent rules
├── scripts/
│   └── GoogleAppsScript_Webhook.gs        # Ready-to-deploy Google Apps Script for Google Sheets
└── app/
    └── src/
        ├── main/
        │   ├── AndroidManifest.xml
        │   ├── java/com/akram/babsharhisab/
        │   │   ├── BabsharHisabApp.kt     # @HiltAndroidApp + Configuration.Provider for HiltWorkerFactory
        │   │   ├── MainActivity.kt        # @AndroidEntryPoint + Compose Root
        │   │   ├── core/
        │   │   │   ├── common/
        │   │   │   │   ├── BanglaNumberFormatter.kt   # 0-9 <-> ০-৯, South Asian Lakh/Crore currency formatter
        │   │   │   │   ├── BanglaDateUtils.kt         # Bangla date, month names, start/end of day epoch helpers
        │   │   │   │   └── ResultState.kt
        │   │   │   ├── security/
        │   │   │   │   └── SecureConfigStore.kt       # EncryptedSharedPreferences for Webhook URL & PIN
        │   │   │   └── di/
        │   │   │       ├── DatabaseModule.kt
        │   │   │       ├── NetworkModule.kt
        │   │   │       └── RepositoryModule.kt
        │   │   ├── data/
        │   │   │   ├── local/
        │   │   │   │   ├── AppDatabase.kt             # Room Database + Pre-population Callback for Bangla Categories
        │   │   │   │   ├── dao/
        │   │   │   │   │   ├── TransactionDao.kt
        │   │   │   │   │   ├── ShopkeeperDao.kt
        │   │   │   │   │   ├── VendorDao.kt
        │   │   │   │   │   ├── CategoryDao.kt
        │   │   │   │   │   ├── AssetDao.kt
        │   │   │   │   │   └── SyncAuditDao.kt
        │   │   │   │   └── entity/
        │   │   │   │       ├── TransactionEntity.kt
        │   │   │   │       ├── ShopkeeperEntity.kt
        │   │   │   │       ├── VendorEntity.kt
        │   │   │   │       ├── CategoryEntity.kt
        │   │   │   │       ├── AssetMaintenanceEntity.kt
        │   │   │   │       └── SyncLogEntity.kt
        │   │   │   ├── remote/
        │   │   │   │   ├── dto/
        │   │   │   │   │   └── SheetsBackupPayloadDto.kt
        │   │   │   │   └── SheetsWebhookClient.kt     # OkHttp POST client handling Apps Script 302 redirects
        │   │   │   ├── repository/
        │   │   │   │   ├── LedgerRepositoryImpl.kt
        │   │   │   │   ├── BakiRepositoryImpl.kt
        │   │   │   │   ├── AssetRepositoryImpl.kt
        │   │   │   │   └── BackupSyncRepositoryImpl.kt
        │   │   │   └── worker/
        │   │   │       ├── GoogleSheetsBackupWorker.kt # @HiltWorker CoroutineWorker
        │   │   │       └── BackupWorkScheduler.kt      # Enqueues 24h PeriodicWorkRequest + OneTimeWorkRequest
        │   │   ├── domain/
        │   │   │   ├── model/
        │   │   │   │   ├── DailySummary.kt
        │   │   │   │   ├── TransactionItem.kt
        │   │   │   │   ├── ShopkeeperAccount.kt
        │   │   │   │   ├── VendorProfile.kt
        │   │   │   │   ├── AssetItem.kt
        │   │   │   │   └── MonthlyAnalyticsReport.kt
        │   │   │   ├── repository/
        │   │   │   │   ├── LedgerRepository.kt
        │   │   │   │   ├── BakiRepository.kt
        │   │   │   │   ├── AssetRepository.kt
        │   │   │   │   └── BackupSyncRepository.kt
        │   │   │   └── usecase/
        │   │   │       ├── RecordSaleWithBakiUseCase.kt
        │   │   │       ├── CollectShopkeeperBakiUseCase.kt
        │   │   │       ├── RecordVendorPurchaseUseCase.kt
        │   │   │       ├── CalculateDailyProfitUseCase.kt
        │   │   │       └── CalculateAssetDepreciationUseCase.kt
        │   │   └── ui/
        │   │       ├── theme/
        │   │       │   ├── Color.kt
        │   │       │   ├── Type.kt
        │   │       │   └── Theme.kt
        │   │       ├── components/
        │   │       │   ├── BanglaNumericKeypad.kt
        │   │       │   ├── ProfitSummaryHeroCard.kt
        │   │       │   ├── CategoryProgressRow.kt
        │   │       │   └── ComposeCharts.kt           # Bar, Donut, and Multi-Month Line Charts
        │   │       ├── navigation/
        │   │       │   └── AppNavHost.kt
        │   │       ├── dashboard/
        │   │       │   ├── DashboardScreen.kt
        │   │       │   └── DashboardViewModel.kt
        │   │       ├── transaction/
        │   │       │   ├── TransactionEntryScreen.kt
        │   │       │   ├── TransactionListScreen.kt
        │   │       │   └── TransactionViewModel.kt
        │   │       ├── baki/
        │   │       │   ├── BakiListScreen.kt
        │   │       │   ├── ShopkeeperDetailScreen.kt
        │   │       │   └── BakiViewModel.kt
        │   │       ├── analytics/
        │   │       │   ├── AnalyticsScreen.kt
        │   │       │   └── AnalyticsViewModel.kt
        │   │       ├── assets/
        │   │       │   ├── AssetMaintenanceScreen.kt
        │   │       │   └── AssetViewModel.kt
        │   │       └── settings/
        │   │           ├── BackupSettingsScreen.kt
        │   │           ├── VendorListScreen.kt
        │   │           └── BackupSettingsViewModel.kt
        │   └── res/
        │       ├── font/                              # Hind Siliguri & SolaimanLipi TTF files
        │       └── values/
        │           └── strings.xml                    # 100% Bangla string resources
        └── test/
            └── java/com/akram/babsharhisab/           # Comprehensive JUnit + Coroutines unit tests
```

---

## 4. Automated Google Sheets Backup Engineering Specification

### 4.1 Why Google Apps Script Web App (`doPost`) Over Raw OAuth2 Client Credentials?
For an independent micro-business app distributed directly (APK or Play Store) without a paid middleman server:
- Requiring a non-technical trader to configure Google Cloud Console OAuth Consent Screens or refresh OAuth scopes that expire every 7 days in testing mode is a common failure point.
- By pairing the Android app's `GoogleSheetsBackupWorker` with a self-contained **Google Apps Script (`scripts/GoogleAppsScript_Webhook.gs`)** attached to the owner's personal Google Sheet (or deployed once by the developer/family member), the Android app simply sends an authenticated HTTPS `POST` request with a shared `secretToken`.
- **Technical Nuance Handled in `SheetsWebhookClient.kt`**:
  - When a `POST` request is sent to `https://script.google.com/macros/s/{DEPLOYMENT_ID}/exec`, Google Apps Script processes the `doPost(e)` payload and responds with an HTTP `302 Found` redirect to `script.googleusercontent.com` to return the JSON response.
  - Standard HTTP clients convert a `302` after `POST` into a `GET`, or fail if redirects aren't followed properly. Our `SheetsWebhookClient` using `OkHttpClient` is explicitly configured to handle Apps Script's `302` response cleanly and parse the returned `{ "status": "SUCCESS", "syncedUuids": [...] }` JSON body.

### 4.2 WorkManager Scheduling Contract (`BackupWorkScheduler.kt`)
1. **Daily Periodic Sync**:
   - Work Name: `"babshar_hisab_daily_sheets_backup"`
   - Policy: `ExistingPeriodicWorkPolicy.KEEP`
   - Repeat Interval: `12 Hours` (with flex interval of `2 Hours`, ensuring at least 1–2 reliable backups daily whenever Wi-Fi or mobile data connects).
   - Constraints: `NetworkType.CONNECTED`, `requiresBatteryNotLow = false` (must still back up even if phone battery is at 15% at night).
   - Backoff Policy: `BackoffPolicy.EXPONENTIAL` starting at `30 seconds`.
2. **Immediate Mutation Trigger (Debounced)**:
   - Whenever a transaction, *Baki* collection, or asset log is saved locally, `BackupWorkScheduler.scheduleImmediateSync()` enqueues a `OneTimeWorkRequest` (`"babshar_hisab_instant_sync"`) with `ExistingWorkPolicy.REPLACE` and an initial delay of `15 seconds` (so rapid consecutive entries batch into a single HTTP request).
3. **Manual "এখনই ব্যাকআপ নিন" Button**:
   - Enqueues an expedited `OneTimeWorkRequest` with zero delay and streams live progress to the UI.

---

## 5. Financial & Accounting Precision Rules

1. **Integer Storage (Zero Floating-Point Drift)**:
   - Never store currency as `Float` or `Double` in SQLite.
   - All monetary amounts in the database are stored as **`Long` representing whole Bangladeshi Taka (`৳`)** or **`Long` Poisha (`1 Taka = 100 Poisha`)**. Given Bangladeshi wholesale/market trade operates strictly in whole Taka (`৳ ১` minimum unit), storing amounts as `Long` (whole Taka `৳`) with exact integer arithmetic guarantees zero floating-point drift and allows values up to `৳ ৯,২২,৩৩,৭২,০৩,৬৮,৫৪,৭৭৫` (`Long.MAX_VALUE`).
2. **Strict Separation of Revenue vs. Cash vs. Receivable (*Baki*)**:
   - When a trader sells `৳ ১০,০০০` of goods to a shopkeeper and receives `৳ ৬,০০০` in cash (leaving `৳ ৪,০০০` as *Baki*):
     - **Accrued Sales Revenue (`মোট বিক্রি`)** increases by `৳ ১০,০০০`.
     - **Realized Cash Inflow (`নগদ জমা`)** increases by `৳ ৬,০০০`.
     - **Shopkeeper *Baki* Balance (`দোকানদারের বাকি`)** increases by `৳ ৪,০০০`.
   - Three days later, when the shopkeeper pays `৳ ২,৫০০` towards that *Baki* (`BAKI_COLLECTION`):
     - **Realized Cash Inflow (`নগদ জমা`)** increases by `৳ ২,৫০০`.
     - **Shopkeeper *Baki* Balance (`দোকানদারের বাকি`)** decreases by `৳ ২,৫০০` (now `৳ ১,৫০০`).
     - **Accrued Sales Revenue (`মোট বিক্রি`)** increases by `৳ ০` (NOT counted again as new sales!).

---

## 6. Security & Local Storage Constraints

1. **SQLite WAL Mode**: Room is configured with `JournalMode.WRITE_AHEAD_LOGGING` for concurrent read/write performance and power-loss corruption resistance.
2. **Encrypted Webhook Credentials**: The Google Apps Script URL and `secretToken` are stored via `EncryptedSharedPreferences` (`AES256_SIV` key / `AES256_GCM` value) backed by Android Keystore.
3. **Database Export/Restore Safety**: Users can also export a local `.json` / `.csv` backup to device storage via Android's Storage Access Framework (`ActivityResultContracts.CreateDocument`) requiring zero invasive `READ_EXTERNAL_STORAGE` permissions.
