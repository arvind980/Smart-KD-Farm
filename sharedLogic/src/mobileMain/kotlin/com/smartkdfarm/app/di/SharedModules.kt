package com.smartkdfarm.app.di

import com.smartkdfarm.app.core.data.FirebaseAuthRepository
import com.smartkdfarm.app.core.data.FirebaseAdminRepository
import com.smartkdfarm.app.core.data.FirebaseAnalyticsRepository
import com.smartkdfarm.app.core.data.FirebaseFinanceRepository
import com.smartkdfarm.app.core.data.FirebaseFarmRepository
import com.smartkdfarm.app.core.data.FirebaseFodderRepository
import com.smartkdfarm.app.core.data.FirebaseInventoryRepository
import com.smartkdfarm.app.core.data.FirebaseLivestockRepository
import com.smartkdfarm.app.core.data.FirebaseOuterCenterRepository
import com.smartkdfarm.app.core.data.FirebaseWasteRepository
import com.smartkdfarm.app.core.data.SmsGatewayServiceImpl
import com.smartkdfarm.app.core.domain.repository.AdminRepository
import com.smartkdfarm.app.core.domain.repository.AnalyticsRepository
import com.smartkdfarm.app.core.domain.repository.AuthRepository
import com.smartkdfarm.app.core.domain.repository.FinanceRepository
import com.smartkdfarm.app.core.domain.repository.FarmRepository
import com.smartkdfarm.app.core.domain.repository.FodderRepository
import com.smartkdfarm.app.core.domain.repository.InventoryRepository
import com.smartkdfarm.app.core.domain.repository.LivestockRepository
import com.smartkdfarm.app.core.domain.repository.OuterCenterRepository
import com.smartkdfarm.app.core.domain.repository.WasteRepository
import com.smartkdfarm.app.core.domain.service.SmsGatewayService
import com.smartkdfarm.app.core.domain.usecase.DeactivateStaffAccessProfileUseCase
import com.smartkdfarm.app.core.domain.usecase.ArchiveAnimalUseCase
import com.smartkdfarm.app.core.domain.usecase.AddStaffUserUseCase
import com.smartkdfarm.app.core.domain.usecase.GenerateSupplyPredictionUseCase
import com.smartkdfarm.app.core.domain.usecase.LoginUserUseCase
import com.smartkdfarm.app.core.domain.usecase.LogHealthEventUseCase
import com.smartkdfarm.app.core.domain.usecase.LogInseminationUseCase
import com.smartkdfarm.app.core.domain.usecase.ProduceFeedBatchUseCase
import com.smartkdfarm.app.core.domain.usecase.RecordMilkCollectionUseCase
import com.smartkdfarm.app.core.domain.usecase.RegisterFarmUseCase
import com.smartkdfarm.app.core.domain.usecase.SaveFarmSetupUseCase
import com.smartkdfarm.app.core.domain.usecase.UpsertStaffAccessProfileUseCase
import com.smartkdfarm.app.presentation.admin.AdminViewModel
import com.smartkdfarm.app.presentation.auth.AuthViewModel
import com.smartkdfarm.app.presentation.livestock.LivestockViewModel
import com.smartkdfarm.app.presentation.operations.OperationsViewModel
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.module

private val sharedModule = module {
    // ── Repositories ──────────────────────────────────────────────────────────
    single<AuthRepository>       { FirebaseAuthRepository() }
    single<AdminRepository>      { FirebaseAdminRepository() }
    single<OuterCenterRepository>{ FirebaseOuterCenterRepository() }
    single<InventoryRepository>  { FirebaseInventoryRepository() }
    single<AnalyticsRepository>  { FirebaseAnalyticsRepository() }
    single<FarmRepository>       { FirebaseFarmRepository() }
    single<LivestockRepository>  { FirebaseLivestockRepository() }
    single<FinanceRepository>    { FirebaseFinanceRepository() }
    single<FodderRepository>     { FirebaseFodderRepository() }
    single<WasteRepository>      { FirebaseWasteRepository() }

    // ── Services ──────────────────────────────────────────────────────────────
    single<SmsGatewayService>    { SmsGatewayServiceImpl() }

    // ── Use Cases ─────────────────────────────────────────────────────────────
    factory { RegisterFarmUseCase(get(), get()) }
    factory { LoginUserUseCase(get()) }
    factory { AddStaffUserUseCase(get()) }
    factory { UpsertStaffAccessProfileUseCase(get()) }
    factory { DeactivateStaffAccessProfileUseCase(get()) }
    factory { SaveFarmSetupUseCase(get(), get()) }
    factory { LogInseminationUseCase(get()) }
    factory { ArchiveAnimalUseCase(get(), get()) }
    factory { LogHealthEventUseCase(get()) }
    factory { RecordMilkCollectionUseCase(get(), get(), get(), get()) }
    factory { ProduceFeedBatchUseCase(get(), get()) }
    factory { GenerateSupplyPredictionUseCase(get(), get()) }

    // ── ViewModels ────────────────────────────────────────────────────────────
    factory { AuthViewModel(get(), get(), get(), get(), get()) }
    factory { LivestockViewModel(get(), get(), get(), get(), get()) }
    factory { AdminViewModel(get(), get(), get(), get(), get()) }
    factory { OperationsViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }
}

private object SharedKoinHolder {
    var app: KoinApplication? = null
}

fun initKoin(): KoinApplication = SharedKoinHolder.app ?: startKoin {
    modules(sharedModule)
}.also { app ->
    SharedKoinHolder.app = app
}

class SharedContainer {
    fun start() {
        initKoin()
    }

    fun authViewModel(): AuthViewModel {
        return initKoin().koin.get()
    }

    fun livestockViewModel(): LivestockViewModel {
        return initKoin().koin.get()
    }

    fun adminViewModel(): AdminViewModel {
        return initKoin().koin.get()
    }

    fun operationsViewModel(): OperationsViewModel {
        return initKoin().koin.get()
    }
}
