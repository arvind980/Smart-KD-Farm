import Foundation
import SharedLogic

/// Swift-side observable wrapper for OperationsViewModel.
/// Bridges the Kotlin StateFlow to @Published so SwiftUI views
/// can reactively render operations state (milk collections,
/// inventory, ledger, notifications, fodder yields, waste logs).
@MainActor
final class OperationsScreenModel: ObservableObject {
    @Published private(set) var state: OperationsUiState

    private let viewModel: OperationsViewModel
    private var handle: CloseableHandle?

    init(container: SharedContainer = SharedContainer()) {
        container.start()
        let viewModel = container.operationsViewModel()
        self.viewModel = viewModel
        self.state = viewModel.currentState()
        self.handle = viewModel.startObserving { [weak self] latestState in
            guard let latestState else { return }
            Task { @MainActor in
                self?.state = latestState
            }
        }
    }

    deinit {
        handle?.close()
        viewModel.dispose()
    }

    func refresh() {
        viewModel.refresh()
    }

    func recordMilkCollection(_ entry: MilkCollectionEntry) {
        viewModel.recordMilkCollection(entry: entry)
    }

    func saveInventoryItem(
        existingId: String?,
        name: String,
        category: InventoryCategory,
        unit: String,
        currentStock: Double,
        reorderLevel: Double,
        vendorName: String? = nil
    ) {
        viewModel.saveInventoryItem(
            existingId:   existingId,
            name:         name,
            category:     category,
            unit:         unit,
            currentStock: currentStock,
            reorderLevel: reorderLevel,
            vendorName:   vendorName
        )
    }

    func recordManualLedgerEntry(title: String, amount: Double, kind: KhataEntryKind, notes: String? = nil) {
        viewModel.recordManualLedgerEntry(title: title, amount: amount, kind: kind, notes: notes)
    }

    func saveFodderYield(
        landPartitionId: String,
        partitionName: String,
        cropType: String,
        yieldKg: Double,
        harvestDateEpochMillis: Int64,
        stockFraction: Float = 1.0,
        notes: String? = nil
    ) {
        viewModel.saveFodderYield(
            landPartitionId:        landPartitionId,
            partitionName:          partitionName,
            cropType:               cropType,
            yieldKg:                yieldKg,
            harvestDateEpochMillis: harvestDateEpochMillis,
            stockFraction:          stockFraction,
            notes:                  notes
        )
    }

    func saveWasteLog(
        category: WasteCategory,
        quantityKg: Double,
        biogasOutputCubicMeters: Double? = nil,
        compositeReadyKg: Double? = nil,
        notes: String? = nil
    ) {
        viewModel.saveWasteLog(
            category:                category,
            quantityKg:              quantityKg,
            biogasOutputCubicMeters: biogasOutputCubicMeters,
            compositeReadyKg:        compositeReadyKg,
            notes:                   notes
        )
    }

    func logBiogasOutput(dungKg: Double, biogasCubicMeters: Double, compositeReadyKg: Double? = nil) {
        viewModel.logBiogasOutput(
            dungKg:            dungKg,
            biogasCubicMeters: biogasCubicMeters,
            compositeReadyKg:  compositeReadyKg,
            notes:             nil
        )
    }

    func clearError() {
        viewModel.clearTransientMessage()
    }
}
