package com.smartkdfarm.app.di

import com.smartkdfarm.app.core.data.FirebaseAuthRepository
import com.smartkdfarm.app.core.data.FirebaseFinanceRepository
import com.smartkdfarm.app.core.data.FirebaseFarmRepository
import com.smartkdfarm.app.core.data.FirebaseLivestockRepository
import com.smartkdfarm.app.core.domain.repository.AuthRepository
import com.smartkdfarm.app.core.domain.repository.FinanceRepository
import com.smartkdfarm.app.core.domain.repository.FarmRepository
import com.smartkdfarm.app.core.domain.repository.LivestockRepository
import com.smartkdfarm.app.core.domain.usecase.ArchiveAnimalUseCase
import com.smartkdfarm.app.core.domain.usecase.AddStaffUserUseCase
import com.smartkdfarm.app.core.domain.usecase.LoginUserUseCase
import com.smartkdfarm.app.core.domain.usecase.LogInseminationUseCase
import com.smartkdfarm.app.core.domain.usecase.RegisterFarmUseCase
import com.smartkdfarm.app.presentation.auth.AuthViewModel
import com.smartkdfarm.app.presentation.livestock.LivestockViewModel
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.module

private val sharedModule = module {
    single<AuthRepository> { FirebaseAuthRepository() }
    single<FarmRepository> { FirebaseFarmRepository() }
    single<LivestockRepository> { FirebaseLivestockRepository() }
    single<FinanceRepository> { FirebaseFinanceRepository() }
    factory { RegisterFarmUseCase(get(), get()) }
    factory { LoginUserUseCase(get()) }
    factory { AddStaffUserUseCase(get()) }
    factory { LogInseminationUseCase(get()) }
    factory { ArchiveAnimalUseCase(get(), get()) }
    factory { AuthViewModel(get(), get(), get(), get(), get()) }
    factory { LivestockViewModel(get(), get(), get(), get()) }
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
}
