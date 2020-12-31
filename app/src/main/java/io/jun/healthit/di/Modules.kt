package io.jun.healthit.di

import android.app.Activity
import io.jun.healthit.FragmentNavigation
import io.jun.healthit.billing.BillingManager
import io.jun.healthit.update.UpdateManager
import io.jun.healthit.util.DialogUtil
import io.jun.healthit.view.*
import io.jun.healthit.view.fragment.AddEditFragment
import io.jun.healthit.view.fragment.MemoDetailFragment
import io.jun.healthit.view.fragment.RoutineDetailFragment
import io.jun.healthit.view.fragment.SetTemplateFragment
import io.jun.healthit.viewmodel.InbodyViewModel
import io.jun.healthit.viewmodel.MemoViewModel
import io.jun.healthit.viewmodel.PrefViewModel
import io.jun.healthit.viewmodel.TimerViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.fragment.dsl.fragment
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { MemoViewModel(get()) }
    viewModel { InbodyViewModel(get()) }
    viewModel { PrefViewModel(get(), androidContext()) }
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

val fragmentModule = module {
    single { (mainActivity: MainActivity) -> FragmentNavigation(mainActivity) }

    fragment { (tipType: String) -> RoutineDetailFragment(tipType) }
    fragment { (templateId: Int) -> SetTemplateFragment(templateId) }
    fragment { (isNewMemo: Boolean, templateId:Int?, tag:Int, memoId:Int) ->
        AddEditFragment(isNewMemo, templateId, tag, memoId)
    }
    fragment { (memoId:Int, pinState:Boolean) -> MemoDetailFragment(memoId, pinState) }
}

val dialogModule = module {
    single { (activity: Activity) -> DialogUtil(activity) }
}

val moduleList = listOf(viewModelModule, billingModule, updateModule, fragmentModule, dialogModule)