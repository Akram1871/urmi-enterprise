# 03_UI_Spec.md — Design System Tokens, Typography & Component Specifications

**Project Name**: ব্যবসার হিসাব (Babshar Hisab)  
**Design Philosophy**: "High-Contrast Field Utility" — Engineered for outdoor sunlight legibility, one-handed thumb operation, zero financial ambiguity, and culturally resonant Bangladeshi visual cues.  

---

## 1. Color Palette & Semantic Tokens (Exact Hex Codes)

In Bangladesh's micro-business context, colors carry immediate financial meaning:
- **Deep Emerald Green (`সবুজ`)**: Prosperity, Cash Inflow, Net Profit, Safe Backup.
- **Warm Crimson / Coral (`লাল — হালকা খাতার রং`)**: Expense Outflow, Loss, Overdue Alert (reminiscent of traditional red-cloth *Halkhata* ledgers).
- **Amber / Gold (`হলুদ/কমলা`)**: Market Dues (*Baki* / Receivables in Shopkeepers' hands) and Pending Maintenance.
- **Royal Indigo / Teal (`নীল`)**: Wholesale Purchases (Inventory Investment) and Primary Interactive Controls.

### 1.1 Light Theme Tokens (Default — Optimized for Direct Sunlight)

| Token Name | Hex Code | Usage | Contrast Ratio |
| :--- | :--- | :--- | :--- |
| `BackgroundPrimary` | `#F8FAF9` | Main scaffold background (soft anti-glare off-white) | — |
| `SurfaceCard` | `#FFFFFF` | Elevated cards, transaction rows, bottom sheets | — |
| `SurfaceSubtle` | `#EEF2F0` | Secondary containers, input field backgrounds, filter chips | — |
| `TextPrimary` | `#0F172A` | Primary Bangla headings, transaction titles, amounts | `17.4:1` on White |
| `TextSecondary` | `#475569` | Timestamps, subtitles, market locations, helper labels | `7.5:1` on White |
| `TextMuted` | `#64748B` | Placeholders, disabled text | `4.7:1` on White |
| `BrandPrimary` | `#047857` | Primary CTA buttons, active bottom tab, top app bar accent | `5.4:1` with `#FFFFFF` |
| `BrandPrimaryContainer` | `#D1FAE5` | Selected chip background, subtle profit highlight | — |
| `ProfitGreen` | `#059669` | Sales revenue (`নগদ বিক্রি`), Net Profit (`নিট লাভ`), Collected *Baki* | — |
| `ProfitGreenBg` | `#ECFDF5` | Background tint for positive P&L and cash inflow badges | — |
| `ExpenseRed` | `#DC2626` | Operational expenses (`খরচ`), Net Loss (`ক্ষতি`), Overdue asset alert | — |
| `ExpenseRedBg` | `#FEF2F2` | Background tint for expense cards and delete confirmation | — |
| `BakiAmber` | `#D97706` | Market Dues (`বাকি পাওনা`), Partial payment status, Sync pending | — |
| `BakiAmberBg` | `#FFFBEB` | Background tint for Shopkeeper *Baki* cards | — |
| `PurchaseBlue` | `#1D4ED8` | Wholesale vendor purchases (`মাল কেনা / মহাজন`), Asset investment | — |
| `PurchaseBlueBg` | `#EFF6FF` | Background tint for wholesale purchase badges | — |
| `BorderDivider` | `#E2E8F0` | Card outlines (`1dp`), table row dividers | — |

### 1.2 Dark Theme Tokens (Optimized for Night-Time Accounting & OLED Battery Saving)

| Token Name | Hex Code | Usage |
| :--- | :--- | :--- |
| `BackgroundPrimary` | `#0B111E` | Main dark scaffold background |
| `SurfaceCard` | `#151F32` | Elevated cards and dialogs |
| `SurfaceSubtle` | `#1E293B` | Input fields, unselected chips |
| `TextPrimary` | `#F8FAFC` | Primary Bangla text and numerals |
| `TextSecondary` | `#94A3B8` | Subtitles and metadata |
| `BrandPrimary` | `#10B981` | Primary interactive emerald accent |
| `BrandPrimaryContainer` | `#064E3B` | Selected chip container in dark mode |
| `ProfitGreen` | `#34D399` | Positive profit & sales text in dark mode |
| `ProfitGreenBg` | `#064E3B` | Positive badge surface |
| `ExpenseRed` | `#F87171` | Expense & loss text in dark mode |
| `ExpenseRedBg` | `#450A0A` | Expense badge surface |
| `BakiAmber` | `#FBBF24` | Market *Baki* receivable text in dark mode |
| `BakiAmberBg` | `#451A03` | Market *Baki* badge surface |
| `PurchaseBlue` | `#60A5FA` | Wholesale purchase text in dark mode |
| `PurchaseBlueBg` | `#1E3A8A` | Wholesale purchase badge surface |
| `BorderDivider` | `#26334D` | Card borders and dividers |

---

## 2. Category Color Palette (For Charts & Progress Bars)

Every expense and transaction category is assigned a deterministic hex color so pie/donut charts, trend lines, and dashboard bars remain 100% consistent across all screens:

| Category ID | Bangla Name | Light Hex | Dark Hex | Material Icon |
| :--- | :--- | :--- | :--- | :--- |
| `CAT_PURCHASE` | `মাল কেনা (পাইকারি)` | `#2563EB` | `#60A5FA` | `ShoppingBag` / `Inventory2` |
| `CAT_FUEL` | `গাড়ির তেল / জ্বালানি` | `#EA580C` | `#FB923C` | `LocalGasStation` |
| `CAT_TRANSPORT` | `যাতায়াত ও ভাড়া` | `#7C3AED` | `#A78BFA` | `LocalShipping` |
| `CAT_FOOD` | `খাবার ও নাস্তা ভাতা` | `#0D9488` | `#2DD4BF` | `Restaurant` |
| `CAT_REPAIR` | `গাড়ি মেরামত ও ব্রেকডাউন` | `#E11D48` | `#FB7185` | `Build` / `CarRepair` |
| `CAT_ASSET` | `সম্পদ ও ব্যাটারি রক্ষণাবেক্ষণ` | `#4F46E5` | `#818CF8` | `BatteryChargingFull` |
| `CAT_OTHER` | `অন্যান্য খরচ` | `#64748B` | `#94A3B8` | `ReceiptLong` |

---

## 3. Typography Scale & Bangla Font Engineering

Bangla script (`বাংলা লিপি`) features upper and lower diacritics (*Kar* / কার ও ফলা — যেমন `ী`, `ু`, `ূ`, `ঁ`) that get clipped if standard Latin line-heights are used. All Compose `TextStyle` definitions MUST set `PlatformTextStyle(includeFontPadding = true)` and generous `lineHeight` (`1.4x` to `1.5x` of `fontSize`).

### 3.1 Bundled Font Families (`res/font/`)
1. **Primary UI & Numeral Font**: `Hind Siliguri` (`hind_siliguri_regular.ttf`, `hind_siliguri_medium.ttf`, `hind_siliguri_semibold.ttf`, `hind_siliguri_bold.ttf`) — Specifically designed for on-screen Bangla UI legibility at both small and large sizes.
2. **Classic Ledger Option**: `SolaimanLipi` (`solaimanlipi.ttf`) — Selectable via a toggle in Settings for users who prefer traditional newspaper/book Bangla styling.
3. **Latin Fallback**: `Inter` (for URLs, phone numbers if unformatted, or technical metadata).

### 3.2 Type Scale Table

| Token | FontSize | LineHeight | Weight | Usage |
| :--- | :--- | :--- | :--- | :--- |
| `DisplayHeroAmount` | `32.sp` | `44.sp` | `Bold (700)` | Today's Net Profit/Loss hero number (`৳ ৪,৮৫০`) |
| `HeadlineLarge` | `24.sp` | `34.sp` | `Bold (700)` | Total Market *Baki*, Keypad live entry display |
| `TitleLarge` | `20.sp` | `28.sp` | `SemiBold (600)` | Screen app bar titles, Section headers (`আজকের হিসাব`) |
| `TitleMedium` | `18.sp` | `26.sp` | `SemiBold (600)` | Card titles, Shopkeeper names, Transaction amounts |
| `BodyLarge` | `16.sp` | `24.sp` | `Medium (500)` | Transaction descriptions, Category names, Button labels |
| `BodyMedium` | `15.sp` | `22.sp` | `Regular (400)` | Notes, vendor names, secondary descriptions |
| `LabelMedium` | `13.sp` | `18.sp` | `Medium (500)` | Badges, chart axis labels, timestamps, sync status pill |

---

## 4. Spacing, Grid & Touch Target Rules

1. **8dp Base Spatial Grid**:
   - `SpaceXXS` = `4.dp` (Icon-to-badge gap)
   - `SpaceXS` = `8.dp` (Chip spacing, compact vertical gap)
   - `SpaceSM` = `12.dp` (Internal card padding for compact rows)
   - `SpaceMD` = `16.dp` (Standard screen horizontal padding & card padding)
   - `SpaceLG` = `24.dp` (Section separation)
   - `SpaceXL` = `32.dp` (Bottom sheet top/bottom breathing room)
2. **Minimum Touch Targets (Thumb-Friendly Field Ergonomics)**:
   - Standard interactive buttons/chips: Minimum height `48.dp`.
   - Primary Save CTA & Quick Action Dashboard buttons: Minimum height `56.dp`.
   - Custom Numeric Keypad keys: Minimum height `60.dp` with `RoundedCornerShape(12.dp)`.
3. **Corner Radii**:
   - Hero & Summary Cards: `16.dp`
   - Transaction & Shopkeeper List Cards: `12.dp`
   - Input Fields & Action Buttons: `12.dp`
   - Status Badges & Filter Chips: `8.dp` (or `50%` full pill for Sync Status)

---

## 5. Core Mobile Component Specifications & States

### 5.1 Real-Time Net Profit & Cash Flow Hero Card (`ProfitSummaryHeroCard`)
- **Layout**: Full-width card at the top of `tab/dashboard` with subtle gradient border.
- **States**:
  - **Net Profit State (`netProfit >= 0`)**:
    - Header label: `আজকের নিট লাভ (Today's Net Profit)`
    - Value: `+ ৳ ২,৪৫০` in `DisplayHeroAmount` (`ProfitGreen`).
    - Badge: `লাভে আছেন` (Green pill).
  - **Net Loss State (`netProfit < 0`)**:
    - Header label: `আজকের নিট ঘাটতি / খরচ বেশি`
    - Value: `- ৳ ১,২০০` in `DisplayHeroAmount` (`ExpenseRed`).
    - Badge: `আজ মাল কেনা বা মেরামত খরচ বেশি` (Soft red pill — non-alarmist, informative).
- **Sub-Row (3 Columns Inside Card)**:
  1. `মোট বিক্রি (Sales)`: `৳ ১২,৫০০` (Includes Cash + Today's *Baki*)
  2. `মাল কেনা (Purchase)`: `৳ ৮,২০০`
  3. `গাড়ি ও অন্যান্য খরচ (Ops)`: `৳ ১,৮৫০`
- **Footer Strip**:
  - `হাতে নগদ ক্যাশ প্রবাহ (Net Cash In Hand Today): ৳ ৯,৪০০` | `দৈনিক ব্যাটারি/সম্পদ সঞ্চয়: ৳ ৫০`

### 5.2 Quick Action 2x2 Grid (`QuickActionGrid`)
- Positioned directly below the Hero Card on the Home Screen for immediate thumb access:
  - Button 1 (`PurchaseBlueBg` / `PurchaseBlue`): `মাল কেনা` (Wholesale Purchase)
  - Button 2 (`ExpenseRedBg` / `ExpenseRed`): `গাড়ি ও দৈনিক খরচ` (Fuel, Transport, Food, Repair)
  - Button 3 (`ProfitGreenBg` / `ProfitGreen`): `নগদ ও বাকি বিক্রি` (Record Sale)
  - Button 4 (`BakiAmberBg` / `BakiAmber`): `বাকি আদায়` (Collect Shopkeeper Due)

### 5.3 Shopkeeper *Baki* Card (`ShopkeeperBakiCard`)
- **Left**: Avatar circle with first Bangla letter of shopkeeper's name + Shopkeeper Name (`বিসমিল্লাহ স্টোর — করিম ভাই`) + Market Tag (`চকবাজার মার্কেট`).
- **Middle/Bottom**: Last transaction timestamp (`সর্বশেষ লেনদেন: ২ দিন আগে`).
- **Right**:
  - Current Due Amount in `TitleMedium` Bold (`BakiAmber` if `> 0`, `ProfitGreen` `"পরিশোধিত"` if `0`).
  - Quick Action Pill Button: `"আদায় +"` (Opens Quick Collection Sheet directly without leaving the list).

### 5.4 Periodic Asset Maintenance Card (`AssetHealthCard`)
- **Header**: Asset Icon (`BatteryChargingFull` / `TireRepair`) + Title (`অটো গাড়ির ব্যাটারি`) + Total Cost (`৳ ২৪,০০০`).
- **Body**:
  - Two-column metadata: `মেয়াদ: ২৪ মাস (২ বছর)` | `মাসিক অবচয়: ৳ ১,০০০/মাস (৳ ৩৩/দিন)`.
  - Linear Progress Indicator showing elapsed lifespan (`১৮ মাস অতিবাহিত — আরও ৬ মাস বাকি`).
- **States**:
  - `HEALTHY (< 75% lifespan used)`: Emerald progress bar.
  - `DUE_SOON (75% – 99% lifespan used)`: Amber progress bar + badge `"শীঘ্রই পরিবর্তনের সময় হবে"`.
  - `OVERDUE (>= 100% lifespan used)`: Crimson progress bar + badge `"মেয়াদ শেষ — পরিবর্তনের সময় হয়েছে"`.

### 5.5 Custom Jetpack Compose Charts (`Canvas` Specifications)
To avoid heavy, buggy third-party chart WebViews and ensure 60fps scrolling with Bangla numerals:
1. **`IncomeExpenseBarChart` (Monthly Analytics)**:
   - Custom Jetpack Compose `Canvas` grouped bar chart.
   - Left Y-axis labels formatted in compact Bangla (`৳ ৫হাজার`, `৳ ৫০হাজার`, `৳ ১লাখ`).
   - Tap on any bar group shows a floating Bangla tooltip card above the bar with exact `মোট আয়`, `মোট খরচ`, and `নিট লাভ`.
2. **`CategoryBreakdownDonutChart`**:
   - Compose `Canvas` arc chart with `28.dp` stroke width, rounded/butt caps with `2.dp` separator gap, and center text showing `মোট খরচ: ৳ ৪২,৩০০`.
3. **`CategoryTrendLineChart`**:
   - Smooth cubic bezier or point-connected line chart on Compose `Canvas` with gradient fill underneath (`alpha = 0.18f` to `0.0f`) and interactive touch scrub points displaying exact month-by-month spend for the selected category (e.g., `গাড়ির তেল খরচ` over the last 6 months).
