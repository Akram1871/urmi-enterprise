# 06_Plan.md — Atomic Implementation Checklist & Milestone Tracker

**Project Name**: ব্যবসার হিসাব (Babshar Hisab)  
**Execution Rule**: Execute tasks sequentially by dependency phase. After completing and verifying any task or milestone, update its checkbox from `[ ]` to `[x]` and record the verification notes in this file before moving to the next phase.  

---

## Phase 0: Context Architecture & Governance Setup
- [x] **0.1**: Generate `docs/01_PRD.md` (Core problem, persona, MVP user stories, Bangla font/numeral standards, strict Non-Goals).
- [x] **0.2**: Generate `docs/02_AppFlow.md` (Screen-by-screen state machine, navigation routes, button actions, offline-first handling).
- [x] **0.3**: Generate `docs/03_UI_Spec.md` (Hex color tokens, Bangla typography scale, spacing rules, dark/light themes, component & chart states).
- [x] **0.4**: Generate `docs/04_TRD.md` (Locked Kotlin + Jetpack Compose + Room + WorkManager stack, dependency matrix, folder structure, security constraints).
- [x] **0.5**: Generate `docs/05_Schema.md` (Relational database schema, ER diagram, indexes, sync flags, and exact SQL accounting formulas).
- [x] **0.6**: Generate `docs/06_Plan.md`, `CLAUDE.md`, and `.cursorrules`.

---

## Phase 1: Project Scaffolding, Local Database & Core State Layer
*Goal: Establish the Gradle build, Bangla numeral/date utilities, Room SQLite database with pre-seeded Bangla categories, DAOs, and Hilt DI wiring.*

- [x] **1.1: Gradle & Project Initialization**
  - Configure `settings.gradle.kts`, root `build.gradle.kts`, `gradle/libs.versions.toml`, and `app/build.gradle.kts` with locked versions from `04_TRD.md`.
  - Configure `AndroidManifest.xml` (`INTERNET`, `ACCESS_NETWORK_STATE`, `RECEIVE_BOOT_COMPLETED`, Hilt application class `BabsharHisabApp`, and WorkManager custom initialization).
  - *Verification*: Gradle 8.12 wrapper generated; `local.properties` and `proguard-rules.pro` configured.
- [x] **1.2: Bangla Localization & Formatting Core (`core/common/`)**
  - Implement `BanglaNumberFormatter.kt`:
    - `toBanglaDigits(input: String/Long): String` (`0-9` -> `০-৯`).
    - `toAsciiDigits(input: String): String` (`০-৯` -> `0-9` for safe numeric parsing).
    - `formatTaka(amountBdt: Long, showSymbol: Boolean = true, showSign: Boolean = false): String` with South Asian Lakh/Crore comma grouping (`৳ ১,২৫,৫০০`).
    - `formatCompactTaka(amountBdt: Long): String` for chart axes (`৳ ১২.৫হাজার`, `৳ ১.২লাখ`).
  - Implement `BanglaDateUtils.kt`:
    - Timezone locked to `Asia/Dhaka`.
    - `toDateKey(millis: Long): String` (`YYYY-MM-DD`) and `toYearMonth(millis: Long): String` (`YYYY-MM`).
    - Human-friendly Bangla date formatting (e.g., `আজ, ১৩ নভেম্বর ২০২৬`, `গতকাল`, Bangla month names).
- [x] **1.3: Room Entities & Pre-Seeded Categories (`data/local/entity/`)**
  - Implement `CategoryEntity.kt` with the 9 pre-seeded Bangla categories defined in `05_Schema.md`.
  - Implement `VendorEntity.kt` (Wholesale vendors / মহাজন).
  - Implement `ShopkeeperEntity.kt` (Market customers / দোকানদার ও বাকি ব্যালেন্স).
  - Implement `AssetMaintenanceEntity.kt` (Periodic assets / গাড়ির ব্যাটারি ও দীর্ঘমেয়াদী সম্পদ).
  - Implement `TransactionEntity.kt` (Unified ledger with foreign keys, composite indexes, and `sync_status`).
  - Implement `SyncLogEntity.kt` (Audit log for Google Sheets backups).
- [x] **1.4: Room DAOs & `AppDatabase.kt` (`data/local/dao/`)**
  - Implement `CategoryDao.kt`, `VendorDao.kt`, `ShopkeeperDao.kt`, `AssetDao.kt`, `TransactionDao.kt`, and `SyncAuditDao.kt`.
  - Write reactive `Flow` queries for daily P&L, active market *Baki*, category breakdown, and monthly aggregations matching `05_Schema.md` Section 3.
  - Configure `AppDatabase.kt` with `RoomDatabase.Callback` + idempotent startup seeding of the 9 core Bangla categories.
  - Wire `DatabaseModule.kt` and `SecureConfigStore.kt` via Hilt.

---

## Phase 2: Domain Use Cases, Core Transactions, Multi-Vendor Purchases & *Baki* Ledger
*Goal: Build the business logic, atomic SQLite transactions, and UI screens for recording purchases, operational expenses, sales, shopkeeper Baki collections, and periodic asset maintenance.*

- [x] **2.1: Repositories & Domain Use Cases (`data/repository/` & `domain/usecase/`)**
  - Implement `LedgerRepositoryImpl.kt`, `BakiRepositoryImpl.kt`, and `AssetRepositoryImpl.kt`.
  - Implement `AccountingCalculator.kt` alongside repository atomic `@Transaction` workflows for multi-vendor purchases, split Cash + *Baki* sales, *Baki* collections (zero revenue double-counting), and 24-month battery/asset depreciation math.
- [x] **2.2: Design System & Reusable Bangla UI Components (`ui/theme/` & `ui/components/`)**
  - Implement `Color.kt`, `Type.kt` (`includeFontPadding = true`), and `Theme.kt` (Light & Dark palettes per `03_UI_Spec.md`).
  - Implement `BanglaNumericKeypad.kt` (Fast custom calculator/numpad supporting `০-৯`, `০০`, `+৳১০০`, `+৳৫০০`, `+৳১০০০`, backspace, and instant BDT formatting).
- [x] **2.3: Unified Transaction Entry & Full History Screens (`ui/transaction/`)**
  - Implement `TransactionViewModel.kt` and `TransactionEntryScreen.kt`:
    - Tabbed/Mode switch for `মাল কেনা (Purchase)`, `গাড়ি ও দৈনিক খরচ (Expense)`, and `বিক্রি ও বাকি (Sale)`.
    - Inline Vendor creation & `"সেভ ও আরেক মহাজন +"` rapid multi-vendor workflow.
    - Inline Shopkeeper creation & automatic *Baki* split calculation (`পুরো টাকা নগদ` / `পুরো টাকা বাকি` quick chips).
    - Today / Yesterday (`আজ` / `গতকাল`) toggle for late-night accounting.
  - Implement `TransactionListScreen.kt` with soft-delete support and automatic balance reversal.
- [x] **2.4: Shopkeeper *Baki* Ledger & Quick Collection Screens (`ui/baki/`)**
  - Implement `BakiViewModel.kt`, `BakiListScreen.kt`, and `ShopkeeperDetailScreen.kt`.
  - Build the 1-tap `QuickBakiCollectionSheet` modal bottom sheet (`আজ জমা দিলো ৳` -> instant balance preview -> atomic save).
  - Add 1-tap phone dialer intent (`কল করুন`) and chronological shopkeeper statement table.
- [x] **2.5: Periodic Asset Maintenance & Vendor Directory (`ui/assets/` & `ui/settings/`)**
  - Implement `AssetViewModel.kt` and `AssetMaintenanceScreen.kt` (Add/Edit battery/tire/overhaul assets, 24-month lifecycle health progress bar, daily/monthly depreciation display, and 1-tap replacement logging).
  - Implement `VendorListScreen.kt` (Directory of wholesale suppliers and purchase totals).

---

## Phase 3: Real-Time Operational Dashboard & Analytical Dashboard UI
*Goal: Build the Home Operational Dashboard and Monthly Analytical Dashboard with custom 60fps Jetpack Compose Canvas charts.*

- [x] **3.1: Real-Time Operational Dashboard (`ui/dashboard/`)**
  - Implement `DashboardViewModel.kt` combining reactive Room `Flow` streams for the selected date (`আজ` or historical date navigation).
  - Implement `ProfitSummaryHeroCard.kt` showing Today's Net Profit/Loss, Adjusted Profit (after daily asset depreciation reserve), Total Sales, Wholesale Purchases, Operational Expenses, and Net Cash in Hand.
  - Implement `DashboardScreen.kt` with Quick Action 2x2 Grid, Active Market *Baki* Banner, Category-Wise Expense Breakdown bars, Backup Status Pill, and Today's Recent Transactions list.
- [x] **3.2: Custom Compose Canvas Charts & Analytical Dashboard (`ui/analytics/`)**
  - Implement `ComposeCharts.kt`:
    - `IncomeExpenseBarChart`: Monthly/Weekly grouped bar chart with Bangla currency tooltips.
    - `CategoryBreakdownDonutChart`: Proportional arc chart with category legend and % share.
    - `CategoryTrendLineChart`: Multi-month category trend line chart with category filter chips (`গাড়ির তেল`, `গাড়ি মেরামত`, `যাতায়াত`, `খাবার ভাতা`, `মাল কেনা`).
  - Implement `AnalyticsViewModel.kt` and `AnalyticsScreen.kt` with period selectors (`এই মাস`, `গত ৩ মাস`, `গত ৬ মাস`, `গত ১২ মাস`).
- [x] **3.3: Root Navigation & Optional Local PIN Lock (`ui/navigation/`)**
  - Wire all screens into `AppNavHost.kt` with the 4-tab Bangla `NavigationBar` (`আজকের হিসাব`, `মার্কেটের বাকি`, `মাসিক বিশ্লেষণ`, `সম্পদ ও ব্যাকআপ`).

---

## Phase 4: Automated Google Sheets Daily Backup (`WorkManager` + Apps Script)
*Goal: Deliver reliable background synchronization of daily snapshots and transaction logs to a personal Google Sheet.*

- [x] **4.1: Companion Google Apps Script (`scripts/GoogleAppsScript_Webhook.gs`)**
  - Write the complete, production-ready Google Apps Script (`doPost(e)`) that automatically creates and updates 4 formatted tabs in the user's Google Sheet (`Daily_Summary`, `Transactions`, `Shopkeeper_Baki`, `Assets_Maintenance`) with idempotent UUID upsert logic and Bangla headers.
- [x] **4.2: Network Client & Sync Repository (`data/remote/` & `data/repository/BackupSyncRepositoryImpl.kt`)**
  - Implement `SheetsBackupPayloadDto.kt` (`@Serializable` payload containing unsynced transactions, shopkeeper balances, assets, vendors, and daily summary snapshot).
  - Implement `SheetsWebhookClient.kt` using `OkHttpClient` configured to handle Google Apps Script `302` redirects properly and verify JSON ack responses.
  - Implement `BackupSyncRepositoryImpl.kt` to query `sync_status != 'SYNCED'` rows, push batches to the webhook, mark returned UUIDs as `SYNCED`, and record a `SyncLogEntity` entry.
- [x] **4.3: Android WorkManager Worker & Scheduler (`data/worker/`)**
  - Implement `GoogleSheetsBackupWorker.kt` (`@HiltWorker CoroutineWorker`) with `Result.success()`, `Result.retry()` (exponential backoff on transient network errors), and `Result.failure()`.
  - Implement `BackupWorkScheduler.kt` registering both the periodic daily worker (`babshar_hisab_daily_sheets_backup`) and debounced instant sync worker (`babshar_hisab_instant_sync`).
- [x] **4.4: Backup Settings & Local Export UI (`ui/settings/BackupSettingsScreen.kt`)**
  - Build the Google Sheets Backup configuration screen (Webhook URL input, Secret Token, `"এখনই ব্যাকআপ নিন"` live progress button, 1-tap `"শিট স্ক্রিপ্ট কপি করুন"` helper, sync audit history list, and offline CSV file export).

---

## Phase 5: Edge-Case Hardening, Offline Verification & Unit Testing
*Goal: Prove accounting mathematical accuracy, Bangla numeral parsing resilience, and offline sync idempotency through automated tests.*

- [x] **5.1: Bangla Numeral & Currency Formatter Unit Tests**
  - Test bidirectional `০-৯` <-> `0-9` conversion, Lakh/Crore comma placement (`৳ ১,২৫,০০০`), negative numbers, and mixed Bangla/English keyboard input edge cases (`BanglaNumberFormatterTest.kt`).
- [x] **5.2: Accounting & *Baki* State Machine Unit Tests**
  - Verify that a split Cash + *Baki* sale accurately increments Sales Revenue by `total_bill_bdt`, Cash Inflow by `cash_amount_bdt`, and Shopkeeper *Baki* by `total_bill_bdt - cash_amount_bdt`.
  - Verify that a subsequent `BAKI_COLLECTION` increments Cash Inflow and reduces Shopkeeper *Baki* while adding `0` to Sales Revenue (zero double-counting).
  - Verify that multi-vendor purchases and 24-month battery depreciation calculations match `05_Schema.md` formulas to the exact Taka (`AccountingAndBakiMathTest.kt`).
- [x] **5.3: Offline Sync & WorkManager Idempotency Tests**
  - Verify that editing or soft-deleting an already-synced transaction flips `sync_status` back to `'PENDING'` and increments `version`.
  - Verify project compilation and test suite execution (`./gradlew testDebugUnitTest assembleDebug` passed with zero warnings/errors; Room schema exported to `app/schemas/`).
- [x] **5.4: Independent Mode/Category Input State, Note Focus Release & "Urmi Enterprise" Branding**
  - Isolate draft inputs in `TransactionFormState` (`purchaseAmountInput`, `expenseAmountByCategory[categoryId]`, `saleTotalBillInput`) so entering a value in `মাল কেনা` never carries over into `গাড়ি ও দৈনিক খরচ` or `বিক্রি ও বাকি`, and each expense category maintains its own separate draft value.
  - Anchor `BanglaNumericKeypad` + Save buttons in a sticky `Scaffold(bottomBar = ...)` with `isNoteFieldFocused` mutual exclusion so **only 1 keypad** is ever shown on screen: the built-in Bangla keypad (`০-৯`) is shown for all amount entry (across `মাল কেনা`, `গাড়ি ও দৈনিক খরচ`, and `বিক্রি ও বাকি`), automatically hides when typing a text Note, and immediately reappears when tapping the Amount card or `"সম্পন্ন"`.
  - Create custom adaptive vector launcher icons (`ic_launcher_background.xml`, `ic_launcher_foreground.xml`, `mipmap-anydpi-v26/ic_launcher.xml`) and update app branding to **ঊর্মি এন্টারপ্রাইজ (Urmi Enterprise)**.
- [x] **5.5: Analytics Chart Text Layout Fix & Multi-Shopkeeper Daily *Baki* Sale + Collection Workflow**
  - Redesign `IncomeExpenseBarChart` tooltip in `ComposeCharts.kt` into a 2-tier layout (month header + 3 equal-weight `maxLines = 1` metric columns) so `"লাভ: ৳৫হাজার"` never wraps vertically character-by-character, and compact 6-month X-axis (`আগ'২৬`, `সেপ্টে'২৬`) and bottom `NavigationBar` labels so they never break onto 2 lines.
  - Upgrade `বিক্রি ও বাকি` (`TransactionEntryScreen.kt` & `TransactionViewModel.kt`) into a 3-way sub-mode workflow (`নগদ বিক্রি` | `দোকানদারকে বাকি` | `বাকি আদায়`) with `"সেভ ও আরেক দোকানদার +"` and `"সেভ ও আরেক আদায় +"` rapid multi-shopkeeper batch buttons, enabling the trader to give *Baki* to multiple shopkeepers and collect previous *Baki* from multiple other shopkeepers in the same day.
- [x] **5.6: Mahajan *Baki* + Partial *Nagad* Purchase & Later Mahajan *Baki* Payment Workflow**
  - Add `current_baki_bdt` to `vendors` (`AppDatabase` v2 with non-destructive `MIGRATION_1_2`) and `TxType.VENDOR_BAKI_PAYMENT` (`CAT_VENDOR_BAKI_PAY`) so buying from a Mahajan on *Baki* with partial *Nagad* records the full purchase cost (`total_bill_bdt`), actual cash paid (`cash_amount_bdt`), and new payable (`baki_delta_bdt`), while later Mahajan *Baki* payments reduce `current_baki_bdt` and deduct from daily cash flow without double-counting purchase cost.
  - Upgrade `মাল কেনা (মহাজন)` (`TransactionEntryScreen.kt`) with a 3-way sub-mode selector (`নগদ কেনা` | `মহাজনের বাকি` | `মহাজন পরিশোধ`) and upgrade `বাকি খাতা` (`BakiScreens.kt`) with a 2-tab switcher (`দোকানদারের বাকি (পাওনা)` | `মহাজনের বাকি (দেনা)`).
- [x] **5.7: Compact *Baki* Summary Card Fix, Personal Home Expense Tracking Outside P&L, & New Phone Cloud Restore from Google Sheets**
  - Fix the tall `মোট বকেয়া` summary card in `BakiScreens.kt` by applying `Modifier.weight(1f)` to the left column and `maxLines = 1, softWrap = false` on the right count badge and filter chips (`"৳১,০০০+ বাকি"`), reducing the header card height from ~220dp to ~62dp so multiple shopkeepers/Mahajans remain clearly visible.
  - Add `CategoryEntity.CAT_HOME_EXPENSE` (`বাসা ও সংসারের খরচ`) and `TxType.PERSONAL_HOME_EXPENSE` so personal/family spending is tracked on the Home Dashboard (today + monthly total) and Monthly Analytics screen while remaining **100% excluded** from business Profit/Loss (`নিট লাভ/ক্ষতি`) calculations.
  - Implement 2-way Google Sheets Cloud Restore (`BackupSyncRepository.restoreFromGoogleSheets()`, `SheetsWebhookClient.fetchRestorePayload()`, and `pullAllDataFromSheets(ss)` in `GoogleAppsScript_Webhook.gs` / `BackupSettingsScreen.kt`) so switching to a new phone pulls all Transactions, Shopkeepers (*Baki*), Mahajans (*Baki*), and Assets directly into the new phone's local SQLite database.
- [x] **5.8: Edit/Delete Dokandar, Mahajan & *Baki* Transactions with Mandatory Delete Warning Popup + Vertically Scrollable Toast Selector**
  - Add full Edit (`EditShopkeeperDialog`, `EditVendorDialog`, `EditTransactionDialog`) and Delete (`deleteShopkeeper`, `deleteVendor`, `deleteTransaction`) capabilities across `BakiListScreen`, `ShopkeeperDetailScreen`, `VendorListScreen`, `DashboardScreen`, and `TransactionListScreen`, automatically recalculating Shopkeeper/Mahajan *Baki* balances when transactions are edited or deleted.
  - Enforce a mandatory confirmation warning popup (`DeleteConfirmationDialog`) before every delete action across the entire app (Shopkeepers, Mahajans, Transactions, and Assets).
  - Replace the horizontal chip sliders (`LazyRow`) in `TransactionEntryScreen.kt` with a vertically oriented scrollable toast list (`VerticalSelectorToastItem` inside a bounded-height `LazyColumn`) for both Dokandar and Mahajan selection.
- [x] **5.9: Mahajan Total *Baki* Syncing & Anytime Partial/Combined Payment, Multi-Mahajan & Multi-Dokandar Daily Entry, and GitHub (`akram1871`) Auto-Update**
  - Reconcile legacy/restored Mahajan (`vendors`) rows via `MIGRATION_2_3` (`AppDatabase` v3), `onOpen` reconciliation, and 6-to-7 column `Vendor_Baki` Google Sheets compatibility so `current_baki_bdt` and `Vendor_Baki` sync accurately.
  - Upgrade `LedgerRepositoryImpl.recordWholesalePurchase`, `TransactionViewModel`, and `TransactionEntryScreen` to support selecting **multiple Mahajans** in a single day, entering each Mahajan's new purchase and partial/extra cash payment (automatically applying cash exceeding today's purchase towards previous Mahajan *Baki*), and paying any portion of Mahajan *Baki* anytime.
  - Upgrade `বিক্রি ও বাকি` (`SaleSubMode.SHOPKEEPER_SALE` & `BAKI_COLLECT`) to support selecting **multiple Dokandars** from the vertical scrollable list within a single Total Selling entry, entering each Dokandar's *Baki* amount, and automatically computing and recording the remaining Cash Sales portion.
  - Implement automatic in-app self-update via GitHub Releases (`GithubAutoUpdater.kt`, `AppUpdateViewModel.kt`, `FileProvider` in `AndroidManifest.xml`, and `.github/workflows/release-apk.yml`) configured for GitHub username `akram1871`.
  - Ensure all active transactions (`getAllActiveTransactions()`) and assets (`getAllActiveAssets()`) — including `বাসা ও সংসারের খরচ` (`PERSONAL_HOME_EXPENSE` / `cat_home_expense`) — are always pushed to Google Sheets during `performGoogleSheetsSync()`, reduce `BackupWorkScheduler` mutation debounce to 1 second, and normalize restored `dateKey`/`yearMonth` + `Daily_Summary` home expense fallback during `restoreFromGoogleSheets()`.
  - Fix `QuickVendorBakiPaySheet` and `QuickBakiCollectionSheet` in `BakiScreens.kt` with `rememberModalBottomSheetState(skipPartiallyExpanded = true)`, `.verticalScroll(rememberScrollState()).navigationBarsPadding()`, `Modifier.weight(1f)` on the left amount column so `"পুরো দেনা শোধ"` never squishes into a blank vertical strip, and place the **"পরিশোধ সেভ করুন" / "বাকি আদায় সেভ করুন"** button row directly below the keypad so it is always visible.
