package io.jun.healthit.di

import io.jun.healthit.service.TimerService
import io.jun.healthit.viewmodel.InbodyViewModel
import io.jun.healthit.viewmodel.MemoViewModel
import io.jun.healthit.viewmodel.PrefViewModel
import io.jun.healthit.viewmodel.TimerViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val viewModelModule = module {
    viewModel { MemoViewModel(get()) }
    viewModel { InbodyViewModel(get()) }
    viewModel { PrefViewModel(get()) }
    viewModel { TimerViewModel(get()) }
}

val serviceModule = module {
    single { TimerService() }
}