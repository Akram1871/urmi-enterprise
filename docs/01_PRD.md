# 01_PRD.md — Product Requirements Document (PRD)

**Project Name**: ব্যবসার হিসাব (Babshar Hisab — Micro-Business & Transport Trader Ledger)  
**Platform**: Android (Offline-First Native Mobile Application)  
**Primary Locale**: Bangla (`bn-BD`) — 100% Native Bangla UI, Typography, and Numerals  
**Architecture Status**: Locked Blueprint v1.0  

---

## 1. Executive Summary & Core Problem

Micro-business owners, independent wholesale/retail traders (পাইকারি ও খুচরা ব্যবসায়ী), and transport-based supply vendors (গাড়ি বা ভ্যান ভিত্তিক সরবরাহকারী) in Bangladesh operate in a high-velocity, cash-and-credit hybrid informal economy. Every day, they source goods from one or multiple wholesale vendors (মহাজন/পাইকার), pay for transport, fuel, food allowances, and unexpected vehicle repairs, and distribute goods to local market shopkeepers (দোকানদার). A significant portion of daily sales remains uncollected as market credit ("বাকি" / *Baki*), which is collected incrementally over subsequent days or weeks.

### The Core Problems:
1. **Paper Ledger Fragility (খাতার হিসাবের সীমাবদ্ধতা)**: Paper notebooks (*Tali Khata*) get lost, damaged by rain/dust during transport, and require manual arithmetic that is error-prone after a 14-hour workday.
2. **Invisible Operating Costs & Asset Depreciation**: Traders mentally calculate profit as `(Total Sales - Wholesale Purchase)`, frequently forgetting daily transport fuel, helper food allowances, sudden vehicle breakdown repairs, and periodic high-cost asset maintenance (e.g., replacing a vehicle/inverter battery every 24 months or tires every 18 months). This creates an illusion of profit while capital slowly erodes.
3. **Market Dues (*Baki*) Leakage**: When selling to 15–40 shopkeepers on a route, partial cash payments and rolling balances (*Baki*) are verbally agreed upon or hastily scribbled. Forgotten dues directly translate to permanent net loss.
4. **Device Loss Risk**: Low-to-mid-range Android phones used in the field are prone to screen breakage, theft, or reset. Purely local apps lose all financial history upon device failure, while cloud-only apps fail in rural or basement wholesale markets with poor mobile data connectivity.

### The Solution:
A **100% offline-first, Bangla-native Android application** engineered specifically for single-owner micro-businesses and transport-based traders. It combines instant double-entry-inspired cash flow & expense tracking, dedicated shopkeeper *Baki* (market credit) ledger management, periodic asset maintenance amortization, real-time operational & analytical dashboards, and an automated **WorkManager-driven daily Google Sheets backup** via a serverless Google Apps Script Web App.

---

## 2. Target Persona

### Primary Persona: "Baba / Independent Transport Trader & Distributor"
- **Demographics**: Age 30–60, operates in Bangladesh (semi-urban/urban markets, district routes).
- **Language & Literacy**: Reads and speaks Bangla fluently; uncomfortable with English financial terminology (`Accounts Receivable`, `Depreciation`, `Amortization`, `Sync Queue`). Understands natural Bangla business idioms (`মাল কেনা`, `নগদ বিক্রি`, `বাকি আদায়`, `গাড়ির তেল খরচ`, `ব্যাটারি পরিবর্তন`).
- **Device & Environment**: Uses an Android smartphone (Android 8.0 to Android 15, typically 3GB–6GB RAM) outdoors in bright sunlight or dimly lit wholesale markets, often with one hand while standing next to a delivery vehicle or shop counter.
- **Connectivity**: Intermittent 3G/4G in transit; stable Wi-Fi at home at night.

---

## 3. MVP User Stories & Acceptance Criteria

### Epic 1: Daily Wholesale Purchases & Multi-Vendor Tracking (মাল ক্রয় ও মহাজন হিসাব)
- **US-1.1**: As a trader, I want to record daily product purchases from a single vendor or multiple wholesale vendors in under 15 seconds so that I know my exact cost of goods sold (COGS).
  - *Acceptance Criteria*:
    - Can select an existing Vendor (মহাজন) from quick-chips or create a new Vendor inline (Name + optional Phone/Market location).
    - Supports entering total purchase amount in BDT (`৳`), optional item notes, and payment mode (Full Cash Paid vs. Partial/Vendor Payable if applicable, defaulting to Cash Paid).
    - Supports logging multiple separate vendor purchase entries within the same business day.

### Epic 2: Daily Operational & Transport Expenses (যাতায়াত, তেল, খাবার ও মেরামত খরচ)
- **US-2.1**: As a trader, I want one-tap preset categories for daily operational expenses so I don't have to type category names repeatedly.
  - *Acceptance Criteria*:
    - Pre-seeded, immutable core Bangla categories with distinct icons:
      1. `মাল কেনা (পাইকারি ক্রয়)` — Wholesale Purchase
      2. `যাতায়াত ও ভাড়া` — Transport & Toll/Fare
      3. `গাড়ির জ্বালানি / তেল / গ্যাস` — Vehicle Fuel / CNG / Charging
      4. `খাবার ও নাস্তা ভাতা` — Daily Food & Helper Allowance
      5. `গাড়ি মেরামত ও ব্রেকডাউন` — Vehicle Breakdown & Sudden Repair
      6. `দীর্ঘমেয়াদী সম্পদ ও রক্ষণাবেক্ষণ` — Periodic Asset Maintenance (Battery, Tires, Engine Overhaul)
      7. `অন্যান্য খরচ` — Miscellaneous Expense
    - User can also add custom categories if needed.
- **US-2.2**: As a vehicle-based trader, I want to track periodic large asset replacements (e.g., buying a ৳24,000 battery that lasts 24 months) and see both the immediate cash outflow AND the daily/monthly amortized cost impact, plus get notified when the next replacement is due.
  - *Acceptance Criteria*:
    - Dedicated **Asset & Maintenance Tracker (`সম্পদ ও মেয়াদী রক্ষণাবেক্ষণ`)** module.
    - Fields: Asset Name (e.g., `অটো/গাড়ির ব্যাটারি`), Purchase/Replacement Date, Cost (`৳`), Lifespan in Months (e.g., `24` months = 2 years).
    - Automatically calculates:
      - Monthly Depreciation Reserve (`মাসিক অবচয় সঞ্চয়` = `Cost / Lifespan Months`)
      - Daily Reserve (`দৈনিক সঞ্চয় লক্ষ্য` = `Cost / (Lifespan Months * 30)`)
      - Next Replacement Due Date & Remaining Days progress bar.
    - When recording an asset replacement, user can toggle whether to deduct the full cash from today's cash drawer or record it as a capital expense.

### Epic 3: Sales Revenue & Market Dues / *Baki* (বিক্রয় ও মার্কেটের বাকি হিসাব)
- **US-3.1**: As a trader, I want to record daily sales either as a quick lump-sum cash sale or per-shopkeeper sale with split Cash + *Baki* (credit left with the shopkeeper).
  - *Acceptance Criteria*:
    - **Quick Cash Sale (`দ্রুত নগদ বিক্রি`)**: Enter amount (`৳`) + optional note -> immediately adds to today's Cash Inflow and Total Sales Revenue.
    - **Shopkeeper Sale with *Baki* (`দোকানদারের কাছে বিক্রি ও বাকি`)**:
      - Select or add Shopkeeper (`দোকানদারের নাম ও মার্কেট`).
      - Enter `মোট বিল (Total Bill Amount)` and `নগদ জমা (Cash Received Now)`.
      - App automatically calculates `আজকের বাকি (New Due = Total Bill - Cash Received)` and updates the shopkeeper's running total balance (`মোট পাওনা`).
- **US-3.2**: As a trader collecting previous dues on my route, I want to record a *Baki* collection (`বাকি আদায়`) from a shopkeeper in two taps.
  - *Acceptance Criteria*:
    - Select Shopkeeper -> Tap `বাকি আদায় করুন (Collect Due)` -> Enter collected amount (`৳`).
    - Immediately decreases the shopkeeper's outstanding *Baki* balance.
    - Increases today's **Cash In Hand (`নগদ ক্যাশ জমা`)** without double-counting it as new Sales Revenue (prevents revenue inflation while accurately tracking cash flow).
    - Maintains a complete chronological ledger per shopkeeper (`বিল বনাম জমা`).

### Epic 4: Real-Time Operational Dashboard (আজকের ব্যবসার সারসংক্ষেপ)
- **US-4.1**: As a trader opening the app at any point during the day, I want an immediate, zero-lag view of today's financial health.
  - *Acceptance Criteria*:
    - **Hero Summary Card**:
      - `আজকের নিট লাভ / ক্ষতি (Today's Net Profit / Loss)`: Calculated as `(Today's Total Sales [Cash + New Baki] - Today's Wholesale Purchases - Today's Operational/Transport/Repair Expenses)`.
      - `আজকের নগদ ক্যাশ প্রবাহ (Today's Net Cash in Hand)`: Calculated as `(Today's Cash Sales + Today's Baki Collected - Today's Cash Expenses)`.
      - Optional toggle to include `দৈনিক সম্পদ অবচয় (Daily Asset Depreciation Reserve)` in Adjusted Net Profit (`প্রকৃত নিট লাভ`).
    - **Active Market Dues Card (`মার্কেটে মোট বকেয়া পাওনা`)**: Shows total uncollected *Baki* across all shopkeepers + count of shopkeepers with pending dues.
    - **Category-Wise Expense Breakdown**: Visual horizontal progress bars and Bangla currency amounts for each expense category today.

### Epic 5: Analytical Dashboard (মাসিক ও দীর্ঘমেয়াদী বিশ্লেষণ)
- **US-5.1**: As a business owner, I want to review monthly financial trends, compare income vs. expenses, and inspect category trend lines to see if fuel or repair costs are spiking.
  - *Acceptance Criteria*:
    - Month/Year selector (in Bangla months/Gregorian months in Bangla script, e.g., `নভেম্বর ২০২৬`) plus custom date range filter (`গত ৭ দিন`, `গত ৩০ দিন`, `এই মাস`, `গত ৬ মাস`).
    - **Income vs. Expense Grouped Bar Chart**: Weekly or monthly comparison of Total Revenue vs. Total Expenses vs. Net Profit.
    - **Category Expense Donut / Breakdown List**: Percentage and exact BDT spent on Wholesale Goods, Fuel, Transport, Food, Repairs, and Asset Maintenance.
    - **Category Trend Line Chart**: Multi-month trend line allowing the user to filter by a specific category (e.g., `গাড়ির তেল খরচ` or `গাড়ি মেরামত`) across the last 6 months.

### Epic 6: Automated Google Sheets Daily Backup (স্বয়ংক্রিয় গুগল শিট ব্যাকআপ)
- **US-6.1**: As a user whose phone might get damaged or lost, I want all my transactions, shopkeeper *Baki* balances, and asset records automatically backed up to my personal Google Sheet every day in the background without me having to export files manually.
  - *Acceptance Criteria*:
    - Uses **Android WorkManager** (`PeriodicWorkRequest` every 24 hours with `NetworkType.CONNECTED` constraint + immediate `OneTimeWorkRequest` on manual "এখনই ব্যাকআপ নিন" tap).
    - Pushes unsynced records (`sync_status = PENDING` or `UPDATED`) and a Daily Summary Snapshot to a configured **Google Apps Script Web App URL** (or Google Sheets REST endpoint) over HTTPS JSON payload.
    - Google Sheet automatically organizes into clean Bangla/English dual-header tabs:
      1. `Daily_Summary (দৈনিক সামারি)`
      2. `Transactions (সকল লেনদেন)`
      3. `Shopkeeper_Baki (দোকানদারের বাকি তালিকা)`
      4. `Assets_Maintenance (সম্পদ ও ব্যাটারি হিসাব)`
    - Displays clear Bangla sync status on the Home & Settings screen: `সর্বশেষ ব্যাকআপ: আজ রাত ১০:১৫ (সব তথ্য নিরাপদ)` or `অফলাইন — ৫টি লেনদেন ব্যাকআপের অপেক্ষায়`.

---

## 4. Bangla Localization, Numerals & Typography Standards

To ensure zero cognitive friction for Bangladeshi micro-business owners, the application enforces strict localization standards:

1. **100% Bangla User Interface**:
   - Every label, button, dialog, validation error, empty state, and chart axis MUST be in natural, idiomatic Bangladeshi Bangla.
   - Zero raw English technical strings (e.g., never show `NullPointerException`, `Sync Failed`, `HTTP 500`, or `No Data`).
2. **Bangla Numerals & Currency Formatting (`বাংলা সংখ্যা ও টাকা`)**:
   - All numbers displayed in the UI MUST be formatted through a central `BanglaNumberFormatter` utility that converts ASCII digits (`0-9`) to Bangla digits (`০-৯`).
   - Currency MUST use the Bangladeshi Taka symbol (`৳`) and follow the **South Asian numbering system (Lakh/Crore — লাখ/কোটি)** comma placement:
     - Example: `125000` -> `৳ ১,২৫,০০০` (NOT `৳ 125,000`).
     - Input fields MUST accept both Bangla (`০-৯`) and English (`0-9`) keyboard digits seamlessly and normalize them to integer/decimal values (`Long` Poisha or `Double` Taka) before database persistence.
3. **Typography & Font Pairing**:
   - **Primary Bangla & UI Font**: **Hind Siliguri** (Regular 400, Medium 500, SemiBold 600, Bold 700) bundled offline in `res/font/` as the primary modern screen-optimized Bangla typeface, paired with **SolaimanLipi** (`solaimanlipi.ttf`) as an optional classic ledger typography preference in Settings, and **Inter** as fallback for Latin characters (e.g., Google Apps Script URL input).
   - **Minimum Touch & Font Sizing**: Because users often view the screen in bright sunlight or while moving, body text must never be smaller than `15sp`, and financial numerals on cards must be `20sp–32sp` Bold with high contrast (`WCAG AAA` target).

---

## 5. Success Metrics (KPIs)

| Metric | Target Standard | Measurement Method |
| :--- | :--- | :--- |
| **Transaction Entry Speed** | `< 10 seconds` for a standard expense or *Baki* collection | Maximum 3 taps from Home Screen to Saved state |
| **Offline Reliability** | `100%` feature availability with Airplane Mode ON | Full CRUD, Dashboard calculation, and Analytics rendering from local Room SQLite DB |
| **Cold Start Time** | `< 800 ms` on budget Android devices (e.g., Helio G85 / Snapdragon 680) | Baseline profile + zero network calls on startup |
| **Accounting Accuracy** | `0` rounding or cash-vs-revenue conflation bugs | Strict separation of *Accrued Sales Revenue* vs. *Realized Cash Flow* in unit tests |
| **Backup Durability** | `100%` eventual consistency with Google Sheets | WorkManager exponential backoff + idempotent `uuid` deduplication in Apps Script |

---

## 6. Strict "Out of Scope (Non-Goals)"

To keep the application fast, focused, and maintainable, the coding agent MUST NOT build or introduce any of the following features:

1. **NO Multi-User Role-Based Access Control (RBAC) or Cloud Login Wall**: The app launches directly into the owner's local ledger (with an optional local 4-digit PIN lock). Do not build Firebase Auth, email/password registration, or staff sub-accounts.
2. **NO SKU-Level Barcode Inventory / Warehouse Stock Counting**: Traders buy bulk goods from vendors and sell by value/weight dynamically. Do not build barcode scanners, SKU stock-out blockers, or complex bill-of-materials manufacturing modules.
3. **NO In-App Payment Gateways (bKash/Nagad API Integration)**: The app *records* payments (whether received in cash or mobile money notes), but does not initiate live merchant payment gateway API transactions.
4. **NO Double-Entry Chartered Accountancy Jargon (Debit/Credit Ledgers)**: While the underlying database accurately tracks cash vs. receivables, never expose `Dr./Cr.`, `Balance Sheet`, or `Trial Balance` tables to the user.
5. **NO iOS or Web Target in MVP**: We are building a rock-solid, native **Kotlin + Jetpack Compose** Android application first.
6. **NO Paid Third-Party Cloud Backend**: All remote backup happens directly from the Android device's `WorkManager` to the user's personal Google Sheet via Google Apps Script Web App (`POST` webhook) with zero monthly server cost.
