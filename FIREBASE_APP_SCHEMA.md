# Smart KD Farm Firebase Schema

This app is modeled around one root farm document and focused operational subcollections.

## Top Level

### `/users/{userId}`
- `farmId`
- `fullName`
- `email`
- `phoneNumber`
- `role`
- `isActive`
- `createdAtEpochMillis`
- `updatedAtEpochMillis`

### `/user_provisioning_requests/{requestId}`
- `farmId`
- `requestedByUserId`
- `fullName`
- `email`
- `phoneNumber`
- `role`
- `status`
- `isActive`
- `createdAtEpochMillis`
- `updatedAtEpochMillis`

## Farm Root

### `/farms/{farmId}`
- `farmName`
- `primaryPhoneNumber`
- `location`
- `landArea`
- `adminUserId`
- `notes`
- `isActive`
- `createdAtEpochMillis`
- `updatedAtEpochMillis`

## Dashboard + Analytics

### `/farms/{farmId}/dashboard/{snapshotId}`
- KPI snapshot documents for admin home
- Suggested keys:
  - `asOfEpochMillis`
  - `totalMilkLiters`
  - `activeAnimals`
  - `netProfitLoss`
  - `lowStockItemName`
  - `lowStockBalance`

### `/farms/{farmId}/alerts/{alertId}`
- `title`
- `message`
- `severity`
- `actionLabel`
- `acknowledged`
- `createdAtEpochMillis`

### `/farms/{farmId}/analytics/{docId}`
- `lactationCurve`
- `supplyPrediction`
- `predictionGeneratedAtEpochMillis`
- `sourceNotes`

## Livestock

### `/farms/{farmId}/livestock/{animalId}`
- `tagNumber`
- `breed`
- `ageInMonths`
- `purchasePrice`
- `status`
- `breedingLogs[]`
- `healthLogs[]`
- `isActive`
- `archivedAtEpochMillis`
- `archiveReason`
- `createdAtEpochMillis`
- `updatedAtEpochMillis`

## Outer Center

### `/farms/{farmId}/outer_center/{docId}`
- container documents like `settings`, `daily_summary`

### `/farms/{farmId}/outer_center/{docId}/collections/{collectionId}`
- `farmerId`
- `farmerName`
- `session`
- `collectedAtEpochMillis`
- `rateBreakdown`
- `paymentStatus`
- `smsTriggered`
- `createdByUserId`
- `createdAtEpochMillis`

### `/farms/{farmId}/outer_center/{docId}/notifications/{notificationId}`
- `farmerId`
- `channel`
- `message`
- `status`
- `triggeredByRecordId`
- `createdAtEpochMillis`

## Khata

### `/farms/{farmId}/khata/{documentId}`
- container docs like `finance`

### `/farms/{farmId}/khata/{documentId}/transactions/{transactionId}`
- `kind` or `direction`
- `title`
- `amount`
- `sourceModule`
- `linkedRecordId`
- `occurredAtEpochMillis`
- `createdByUserId`
- `notes`
- `createdAtEpochMillis`

## Inventory

### `/farms/{farmId}/inventory/{docId}`
- container docs like `settings`, `summary`

### `/farms/{farmId}/inventory/{docId}/items/{itemId}`
- `name`
- `category`
- `unit`
- `currentStock`
- `reorderLevel`
- `vendorName`
- `notes`
- `updatedAtEpochMillis`

### `/farms/{farmId}/inventory/{docId}/feed_batches/{batchId}`
- `batchName`
- `producedAtEpochMillis`
- `totalOutputKg`
- `ingredients[]`
- `autoStockDeducted`
- `notes`
- `createdByUserId`
- `createdAtEpochMillis`

## Farm Config

### `/farms/{farmId}/config/{docId}`
- root config docs like `setup`
- suggested keys:
  - `validationStatus`
  - `totalArea`
  - `partitionAreaTotal`
  - `sumOfPartsValid`

### `/farms/{farmId}/config/{docId}/land_partitions/{partitionId}`
- `name`
- `area`
- `cropOrUse`
- `colorHex`
- `notes`

## Staff Control

### `/farms/{farmId}/staff/{staffId}`
- `userId`
- `fullName`
- `role`
- `phoneNumber`
- `permissions[]`
- `shiftLabel`
- `isActive`
- `lastActiveAtEpochMillis`
- `createdAtEpochMillis`
- `updatedAtEpochMillis`

## Recommended Firebase Services

- Firebase Auth:
  - Admin
  - Dairy Man
  - Labour
- Cloud Firestore:
  - All operational data
- Cloud Functions:
  - Auto-rate calculation verification
  - SMS trigger after milk collection
  - 3-month supply prediction refresh
  - Low stock alert generation
- Firebase Cloud Messaging:
  - Internal app alerts for staff/admin

## Suggested Automation Triggers

- New milk collection:
  - calculate rate
  - mark `smsTriggered`
  - create notification document
  - push khata transaction

- New feed batch:
  - deduct linked inventory quantities
  - create expense log if needed
  - refresh low stock alerts

- Farm config update:
  - recompute `sumOfPartsValid`
  - reject over-allocation

- Breeding/health updates:
  - refresh lactation curve
  - update dashboard KPIs and alert banners
