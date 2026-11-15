# CLAUDE.md — Principal Engineering & Agent Execution Rules

## Project Overview
**ব্যবসার হিসাব (Babshar Hisab)** is a production-ready, 100% offline-first Android mobile application localized in Bangla (`bn-BD`), designed for micro-business owners, independent traders, and transport-based vendors in Bangladesh.

---

## MANDATORY PRE-EXECUTION PROTOCOL (READ BEFORE WRITING ANY CODE)

Before executing **any** coding task, creating files, modifying database entities, or building UI components, you **MUST** read and cross-reference the 6-Document Context Architecture located in `/docs`:

1. **`docs/01_PRD.md`**: Core problem, Bangladeshi trader persona, MVP user stories, Bangla localization & typography standards (`Hind Siliguri` / `SolaimanLipi` + Bangla numerals `০-৯` + South Asian Lakh/Crore formatting), and strict **Out of Scope (Non-Goals)**.
2. **`docs/02_AppFlow.md`**: Complete screen-by-screen MVI/UDF state machine (`UiState`, `UiEvent`, `UiEffect`), navigation routes, button actions, offline-first local handling, and empty/error states.
3. **`docs/03_UI_Spec.md`**: Design system tokens (exact hex palette for Light/Dark themes, deterministic category colors, Bangla typography line-height rules, 8dp spacing grid, and custom Compose Canvas chart specifications).
4. **`docs/04_TRD.md`**: Locked tech stack (**Kotlin 2.0.21 + Jetpack Compose Material 3 + Room SQLite 2.6.1 + Hilt 2.52 + Android WorkManager 2.10.0 + OkHttp 4.12.0 + Kotlinx.Serialization**), package structure, and Google Apps Script `doPost` redirect handling.
5. **`docs/05_Schema.md`**: Complete relational database schema (`categories`, `vendors`, `shopkeepers`, `assets_maintenance`, `transactions`, `sync_audit_logs`), foreign keys, composite indexes, `sync_status` flags, and exact SQL financial aggregation formulas.
6. **`docs/06_Plan.md`**: Atomic, sequential implementation checklist ordered by dependency (Phase 1 -> Phase 5).

---

## MANDATORY MILESTONE UPDATE RULE

- **Always execute tasks in the exact dependency order specified in `docs/06_Plan.md`.**
- **After completing every task or milestone, you MUST immediately update `docs/06_Plan.md`** by marking the completed item `[x]` and appending brief verification notes before starting the next task.

---

## NON-NEGOTIABLE ENGINEERING GUARDRAILS

1. **100% Offline-First Execution**:
   - Every read operation MUST flow reactively from Room SQLite (`Flow<T>`).
   - Every write operation MUST commit atomically to Room SQLite first (`@Transaction`) with `sync_status = "PENDING"` and never block on network availability.
2. **Zero Accounting Conflation (Sales Revenue vs. Cash Flow vs. Market *Baki*)**:
   - **Accrued Sales Revenue (`মোট বিক্রি`)** = Sum of `total_bill_bdt` where `tx_type = 'SALE'`.
   - **Realized Cash Inflow (`নগদ ক্যাশ জমা`)** = Sum of `cash_amount_bdt` where `tx_type IN ('SALE', 'BAKI_COLLECTION')`.
   - Collecting previous market dues (`BAKI_COLLECTION`) reduces `shopkeepers.current_baki_bdt` and increases Cash In Hand, but **NEVER** adds to Sales Revenue a second time.
3. **Integer Currency Precision**:
   - Store all monetary values in SQLite and domain models as `Long` (whole Bangladeshi Taka `৳`). Never use `Float` or `Double` for currency storage.
4. **100% Bangla UI & Numeral Formatting**:
   - All user-facing strings must be in natural Bangladeshi Bangla.
   - All displayed numbers and currency amounts must pass through `BanglaNumberFormatter` (`৳ ১,২৫,০০০`).
   - All numeric inputs must accept both Bangla (`০-৯`) and ASCII (`0-9`) digits seamlessly.
5. **Strict Scope Discipline**:
   - Do NOT introduce any feature listed under `Out of Scope (Non-Goals)` in `docs/01_PRD.md` (no cloud login walls, no barcode SKU stock-out blockers, no paid backend dependencies).
