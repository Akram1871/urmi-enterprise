/**
 * ============================================================================
 * ব্যবসার হিসাব (Babshar Hisab) — Google Sheets Automated Daily Backup Script
 * ============================================================================
 *
 * সেটআপ করার নিয়ম (Setup Instructions):
 * ১. আপনার Google Drive-এ একটি নতুন Google Sheet খুলুন।
 * ২. উপরের মেনু থেকে Extensions -> Apps Script এ ক্লিক করুন।
 * ৩. আগের সব কোড মুছে এই পুরো ফাইলটি পেস্ট করে Save (Ctrl+S) করুন।
 * ৪. ডানপাশে উপরে "Deploy" -> "New deployment" এ ক্লিক করুন।
 * ৫. Select type: "Web app" নির্বাচন করুন।
 *    - Execute as: "Me"
 *    - Who has access: "Anyone"
 * ৬. Deploy বাটনে চাপ দিয়ে প্রাপ্ত "Web app URL" (https://script.google.com/macros/s/.../exec)
 *    কপি করে অ্যাপের "গুগল শিট ব্যাকআপ সেটিংস" স্ক্রিনে পেস্ট করুন।
 */

function doGet(e) {
  var ss = SpreadsheetApp.getActiveSpreadsheet();
  return pullAllDataFromSheets(ss);
}

function doPost(e) {
  var lock = LockService.getScriptLock();
  lock.tryLock(25000);

  try {
    if (!e || !e.postData || !e.postData.contents) {
      return jsonResponse("ERROR", "খালি রিকোয়েস্ট পাওয়া গেছে (Empty payload)", 0);
    }

    var payload = JSON.parse(e.postData.contents);
    var ss = SpreadsheetApp.getActiveSpreadsheet();

    // If a new phone requests to pull/restore all data from Google Sheets:
    if (payload.action === "PULL_ALL") {
      return pullAllDataFromSheets(ss);
    }

    var syncedCount = 0;

    // 1. Upsert Daily Summary Tab
    if (payload.dailySummary) {
      var summarySheet = getOrCreateSheet(ss, "Daily_Summary", [
        "তারিখ (Date)",
        "মোট বিক্রি (Total Sales ৳)",
        "নগদ বিক্রি (Cash Sales ৳)",
        "নতুন বাকি (New Baki ৳)",
        "বাকি আদায় (Baki Collected ৳)",
        "মাল কেনা (Wholesale Purchase ৳)",
        "গাড়ি ও দৈনিক খরচ (Operating Expense ৳)",
        "সম্পদ/ব্যাটারি খরচ (Asset Maintenance ৳)",
        "দৈনিক অবচয় সঞ্চয় (Daily Depreciation ৳)",
        "নিট লাভ/ক্ষতি (Operational Profit ৳)",
        "অবচয় বাদে প্রকৃত লাভ (Adjusted Profit ৳)",
        "হাতে নগদ ক্যাশ প্রবাহ (Net Cash Flow ৳)",
        "মার্কেটে মোট বকেয়া (Total Active Market Baki ৳)",
        "মহাজনের মোট বকেয়া দেনা (Total Vendor Baki ৳)",
        "বাসা ও সংসারের খরচ (Home Expense ৳)",
        "সর্বশেষ আপডেট (Last Synced)"
      ]);

      var ds = payload.dailySummary;
      upsertRowByKey(summarySheet, 1, ds.dateKey, [
        ds.dateKey,
        ds.totalSalesBdt,
        ds.cashSalesBdt,
        ds.newBakiGivenBdt,
        ds.bakiCollectedBdt,
        ds.wholesalePurchaseBdt,
        ds.operatingExpenseBdt,
        ds.assetMaintenanceBdt,
        ds.dailyDepreciationBdt,
        ds.netOperationalProfitBdt,
        ds.adjustedNetProfitBdt,
        ds.netCashFlowBdt,
        ds.totalActiveMarketBakiBdt,
        ds.totalActiveVendorBakiBdt || 0,
        ds.personalHomeExpenseBdt || 0,
        new Date().toLocaleString("bn-BD", { timeZone: "Asia/Dhaka" })
      ]);
      syncedCount++;
    }

    // 2. Upsert Transactions Tab
    if (payload.transactions && payload.transactions.length > 0) {
      var txSheet = getOrCreateSheet(ss, "Transactions", [
        "Transaction_UUID",
        "তারিখ (Date)",
        "ধরন (Type)",
        "খাত (Category)",
        "মহাজন (Vendor)",
        "দোকানদার (Shopkeeper)",
        "মোট বিল (Total Bill ৳)",
        "নগদ লেনদেন (Cash ৳)",
        "বাকি পরিবর্তন (Baki Delta ৳)",
        "দোকানদারের ব্যালেন্স (Running Baki ৳)",
        "নোট (Note)",
        "সংস্করণ (Version)",
        "অবস্থা (Status)",
        "Vendor_UUID",
        "Shopkeeper_UUID",
        "Asset_UUID"
      ]);

      for (var i = 0; i < payload.transactions.length; i++) {
        var tx = payload.transactions[i];
        upsertRowByKey(txSheet, 1, tx.transactionId, [
          tx.transactionId,
          tx.dateKey,
          tx.txType,
          tx.categoryId,
          tx.vendorNameBn || "",
          tx.shopkeeperNameBn || "",
          tx.totalBillBdt,
          tx.cashAmountBdt,
          tx.bakiDeltaBdt,
          tx.runningShopkeeperBakiBdt,
          tx.noteBn || "",
          tx.version,
          tx.isDeleted ? "DELETED" : "ACTIVE",
          tx.vendorId || "",
          tx.shopkeeperId || "",
          tx.assetId || ""
        ]);
        syncedCount++;
      }
    }

    // 3. Upsert Shopkeeper Baki Tab
    if (payload.shopkeepers && payload.shopkeepers.length > 0) {
      var shopSheet = getOrCreateSheet(ss, "Shopkeeper_Baki", [
        "Shopkeeper_UUID",
        "দোকানদারের নাম (Name)",
        "দোকানের নাম (Shop Name)",
        "মার্কেট/এলাকা (Market Area)",
        "মোবাইল নম্বর (Phone)",
        "বর্তমান বাকি পাওনা (Current Baki ৳)",
        "অবস্থা (Status)"
      ]);

      for (var j = 0; j < payload.shopkeepers.length; j++) {
        var sk = payload.shopkeepers[j];
        upsertRowByKey(shopSheet, 1, sk.shopkeeperId, [
          sk.shopkeeperId,
          sk.nameBn,
          sk.shopNameBn || "",
          sk.marketAreaBn || "",
          sk.phoneNumber || "",
          sk.currentBakiBdt,
          sk.isDeleted ? "DELETED" : "ACTIVE"
        ]);
        syncedCount++;
      }
    }

    // 4. Upsert Assets & Battery Maintenance Tab
    if (payload.assets && payload.assets.length > 0) {
      var assetSheet = getOrCreateSheet(ss, "Assets_Maintenance", [
        "Asset_UUID",
        "সম্পদের নাম (Asset Name)",
        "ক্রয় মূল্য (Cost ৳)",
        "মেয়াদ মাস (Lifespan Months)",
        "কেনার তারিখ (Purchase Date)",
        "পরবর্তী পরিবর্তন তারিখ (Next Due Date)",
        "দৈনিক অবচয় (Daily Reserve ৳)",
        "মাসিক অবচয় (Monthly Reserve ৳)",
        "নোট (Notes)",
        "অবস্থা (Status)"
      ]);

      for (var k = 0; k < payload.assets.length; k++) {
        var ast = payload.assets[k];
        upsertRowByKey(assetSheet, 1, ast.assetId, [
          ast.assetId,
          ast.assetNameBn,
          ast.purchaseCostBdt,
          ast.lifespanMonths,
          ast.purchaseDateKey,
          ast.nextDueDateKey,
          ast.dailyDepreciationBdt,
          ast.monthlyDepreciationBdt,
          ast.notesBn || "",
          ast.isDeleted ? "DELETED" : (ast.isActive ? "ACTIVE" : "ARCHIVED")
        ]);
        syncedCount++;
      }
    }

    // 5. Upsert Vendor (Mahajan) Baki Tab
    if (payload.vendors && payload.vendors.length > 0) {
      var vendorSheet = getOrCreateSheet(ss, "Vendor_Baki", [
        "Vendor_UUID",
        "মহাজনের নাম (Vendor Name)",
        "আড়ত/বাজার (Market Location)",
        "মোবাইল নম্বর (Phone)",
        "মোট মাল কেনা (Total Purchased ৳)",
        "বর্তমান বকেয়া দেনা (Current Baki Payable ৳)",
        "অবস্থা (Status)"
      ]);

      for (var m = 0; m < payload.vendors.length; m++) {
        var v = payload.vendors[m];
        upsertRowByKey(vendorSheet, 1, v.vendorId, [
          v.vendorId,
          v.nameBn,
          v.marketLocationBn || "",
          v.phoneNumber || "",
          v.totalPurchasedBdt,
          v.currentBakiBdt || 0,
          v.isDeleted ? "DELETED" : "ACTIVE"
        ]);
        syncedCount++;
      }
    }

    return jsonResponse("SUCCESS", "ব্যাকআপ সফলভাবে সম্পন্ন হয়েছে", syncedCount);
  } catch (err) {
    return jsonResponse("ERROR", err.toString(), 0);
  } finally {
    lock.releaseLock();
  }
}

function getOrCreateSheet(ss, sheetName, headers) {
  var sheet = ss.getSheetByName(sheetName);
  if (!sheet) {
    sheet = ss.insertSheet(sheetName);
    sheet.appendRow(headers);
    sheet.getRange(1, 1, 1, headers.length)
      .setFontWeight("bold")
      .setBackground("#047857")
      .setFontColor("#FFFFFF");
    sheet.setFrozenRows(1);
  } else {
    sheet.getRange(1, 1, 1, headers.length).setValues([headers]);
  }
  return sheet;
}

function upsertRowByKey(sheet, keyColIndex1Based, keyValue, rowValues) {
  var lastRow = sheet.getLastRow();
  if (lastRow > 1) {
    var keyRange = sheet.getRange(2, keyColIndex1Based, lastRow - 1, 1).getValues();
    for (var i = 0; i < keyRange.length; i++) {
      if (String(keyRange[i][0]) === String(keyValue)) {
        sheet.getRange(i + 2, 1, 1, rowValues.length).setValues([rowValues]);
        return;
      }
    }
  }
  sheet.appendRow(rowValues);
}

function jsonResponse(status, message, syncedCount) {
  var out = JSON.stringify({
    status: status,
    message: message,
    syncedCount: syncedCount
  });
  return ContentService.createTextOutput(out).setMimeType(ContentService.MimeType.JSON);
}

function pullAllDataFromSheets(ss) {
  var transactions = [];
  var shopkeepers = [];
  var vendors = [];
  var assets = [];

  var txSheet = ss.getSheetByName("Transactions");
  if (txSheet && txSheet.getLastRow() > 1) {
    var txRows = txSheet.getRange(2, 1, txSheet.getLastRow() - 1, txSheet.getLastColumn()).getValues();
    for (var i = 0; i < txRows.length; i++) {
      var r = txRows[i];
      if (!r[0]) continue;
      transactions.push({
        transactionId: String(r[0]),
        dateKey: formatCellDateKey(r[1]),
        txType: String(r[2] || "EXPENSE"),
        categoryId: String(r[3] || "CAT_OTHER"),
        vendorNameBn: String(r[4] || ""),
        shopkeeperNameBn: String(r[5] || ""),
        totalBillBdt: Number(r[6] || 0),
        cashAmountBdt: Number(r[7] || 0),
        bakiDeltaBdt: Number(r[8] || 0),
        runningShopkeeperBakiBdt: Number(r[9] || 0),
        noteBn: String(r[10] || ""),
        version: Number(r[11] || 1),
        isDeleted: String(r[12] || "") === "DELETED",
        vendorId: r.length > 13 ? String(r[13] || "") : "",
        shopkeeperId: r.length > 14 ? String(r[14] || "") : "",
        assetId: r.length > 15 ? String(r[15] || "") : ""
      });
    }
  }

  var shopSheet = ss.getSheetByName("Shopkeeper_Baki");
  if (shopSheet && shopSheet.getLastRow() > 1) {
    var sRows = shopSheet.getRange(2, 1, shopSheet.getLastRow() - 1, shopSheet.getLastColumn()).getValues();
    for (var j = 0; j < sRows.length; j++) {
      var sr = sRows[j];
      if (!sr[0]) continue;
      shopkeepers.push({
        shopkeeperId: String(sr[0]),
        nameBn: String(sr[1] || ""),
        shopNameBn: String(sr[2] || ""),
        marketAreaBn: String(sr[3] || ""),
        phoneNumber: String(sr[4] || ""),
        currentBakiBdt: Number(sr[5] || 0),
        isDeleted: String(sr[6] || "") === "DELETED"
      });
    }
  }

  var vendorSheet = ss.getSheetByName("Vendor_Baki");
  if (vendorSheet && vendorSheet.getLastRow() > 1) {
    var vRows = vendorSheet.getRange(2, 1, vendorSheet.getLastRow() - 1, vendorSheet.getLastColumn()).getValues();
    for (var m = 0; m < vRows.length; m++) {
      var vr = vRows[m];
      if (!vr[0]) continue;
      var totalPurch = Number(vr[4]) || 0;
      var rawBakiCol = vr.length > 5 ? vr[5] : 0;
      var isLegacy6Col = (String(rawBakiCol) === "ACTIVE" || String(rawBakiCol) === "DELETED");
      var currBaki = isLegacy6Col ? totalPurch : (Number(rawBakiCol) || 0);
      var statusStr = isLegacy6Col ? String(rawBakiCol) : (vr.length > 6 ? String(vr[6] || "") : "ACTIVE");
      vendors.push({
        vendorId: String(vr[0]),
        nameBn: String(vr[1] || ""),
        marketLocationBn: String(vr[2] || ""),
        phoneNumber: String(vr[3] || ""),
        totalPurchasedBdt: totalPurch,
        currentBakiBdt: currBaki,
        isDeleted: statusStr === "DELETED"
      });
    }
  }

  var assetSheet = ss.getSheetByName("Assets_Maintenance");
  if (assetSheet && assetSheet.getLastRow() > 1) {
    var aRows = assetSheet.getRange(2, 1, assetSheet.getLastRow() - 1, assetSheet.getLastColumn()).getValues();
    for (var k = 0; k < aRows.length; k++) {
      var ar = aRows[k];
      if (!ar[0]) continue;
      assets.push({
        assetId: String(ar[0]),
        assetNameBn: String(ar[1] || ""),
        purchaseCostBdt: Number(ar[2] || 0),
        lifespanMonths: Number(ar[3] || 24),
        purchaseDateKey: formatCellDateKey(ar[4]),
        nextDueDateKey: formatCellDateKey(ar[5]),
        dailyDepreciationBdt: Number(ar[6] || 0),
        monthlyDepreciationBdt: Number(ar[7] || 0),
        notesBn: String(ar[8] || ""),
        isActive: String(ar[9] || "") !== "ARCHIVED",
        isDeleted: String(ar[9] || "") === "DELETED"
      });
    }
  }

  var totalPulled = transactions.length + shopkeepers.length + vendors.length + assets.length;
  var out = JSON.stringify({
    status: "SUCCESS",
    message: "ডাটা সফলভাবে ডাউনলোড হয়েছে",
    syncedCount: totalPulled,
    transactions: transactions,
    shopkeepers: shopkeepers,
    vendors: vendors,
    assets: assets
  });
  return ContentService.createTextOutput(out).setMimeType(ContentService.MimeType.JSON);
}

function formatCellDateKey(val) {
  if (!val) return "";
  if (val instanceof Date) {
    return Utilities.formatDate(val, "Asia/Dhaka", "yyyy-MM-dd");
  }
  return String(val).substring(0, 10);
}
