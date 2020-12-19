package io.jun.healthit.di

import android.app.Activity
import io.jun.healthit.billing.BillingManager
import io.jun.healthit.update.UpdateManager
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

val billingModule = module {
    single { (activity: Activity,
                 onPurchased: (String) -> Unit,
                 onUpdatePrice: (Double) -> Unit,
                 onBillingError:() -> Unit) -> BillingManager(activity, onPurchased, onUpdatePrice, onBillingError) }
}

val updateModule = module {
    single {
        (activity: Activity,
            updateComplete:() -> Unit) -> UpdateManager(activity, updateComplete)
    }
}