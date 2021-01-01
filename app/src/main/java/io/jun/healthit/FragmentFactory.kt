package io.jun.healthit

import androidx.fragment.app.Fragment
import io.jun.healthit.view.fragment.AddEditFragment
import io.jun.healthit.view.fragment.MemoDetailFragment
import io.jun.healthit.view.fragment.RoutineDetailFragment
import io.jun.healthit.view.fragment.SetTemplateFragment
import io.jun.healthit.view.fragment.*
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.parameter.parametersOf

object FragmentFactory: KoinComponent {
    fun getRoutineFragment() = RoutineFragment()

    fun getRoutineDetailFragment(tipType: String): Fragment {
        val fragment by inject<RoutineDetailFragment> { parametersOf(tipType) }
        return fragment
    }

    fun getMemoFragment() = MemoFragment()

    fun getMemoDetailFragment(memoId: Int, pinState:Boolean): Fragment {
        val fragment by inject<MemoDetailFragment> { parametersOf(memoId, pinState) }
        return fragment
    }

    fun getAddEditFragment(isNewMemo: Boolean = false,
                           templateId:Int? = 0,
                           tag:Int = 0,
                           memoId:Int = 0): Fragment {
        val fragment by inject<AddEditFragment> { parametersOf(isNewMemo, templateId, tag, memoId) }
        return fragment
    }

    fun getInbodyFragment() = InbodyFragment()

    fun getTimerFragment() = TimerFragment()

    fun getSettingsFragment() = SettingsFragment()

    fun getSetTemplateFragment(templateId: Int): Fragment {
        val fragment by inject<SetTemplateFragment> { parametersOf(templateId) }
        return fragment
    }
}

fun getFragmentTag(fragment: Fragment) =
    when(fragment) {
        is RoutineFragment -> 0
        is RoutineDetailFragment -> 0

        is MemoFragment -> 1
        is MemoDetailFragment -> 1
        is AddEditFragment -> 1

        is InbodyFragment -> 2

        is TimerFragment -> 3

        is SettingsFragment -> 4
        is SetTemplateFragment -> 4

        else -> -1
    }