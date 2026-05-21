package om.smartkdfarm.app.di

import om.smartkdfarm.app.core.data.FirebaseAuthRepository
import om.smartkdfarm.app.core.data.FirebaseFarmRepository
import om.smartkdfarm.app.core.domain.repository.AuthRepository
import om.smartkdfarm.app.core.domain.repository.FarmRepository
import om.smartkdfarm.app.core.domain.usecase.AddStaffUserUseCase
import om.smartkdfarm.app.core.domain.usecase.LoginUserUseCase
import om.smartkdfarm.app.core.domain.usecase.RegisterFarmUseCase
import om.smartkdfarm.app.presentation.auth.AuthViewModel
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.module

private val sharedModule = module {
    single<AuthRepository> { FirebaseAuthRepository() }
    single<FarmRepository> { FirebaseFarmRepository() }
    factory { RegisterFarmUseCase(get(), get()) }
    factory { LoginUserUseCase(get()) }
    factory { AddStaffUserUseCase(get()) }
    factory { AuthViewModel(get(), get(), get(), get(), get()) }
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
}
