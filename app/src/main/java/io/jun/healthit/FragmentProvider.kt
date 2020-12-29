package io.jun.healthit

import androidx.fragment.app.Fragment
import io.jun.healthit.view.AddEditFragment
import io.jun.healthit.view.MemoDetailFragment
import io.jun.healthit.view.RoutineDetailFragment
import io.jun.healthit.view.SetTemplateFragment
import io.jun.healthit.view.fragment.*

interface FragmentInfo {
    val tag: Int
    val fragment: Fragment
}

enum class FragmentProvider: FragmentInfo {
    ROUTINE_FRAGMENT {
        override val tag = 0
        override val fragment = RoutineFragment()
    },
    ROUTINE_DETAIL_FRAGMENT {
        override val tag = 0
        override val fragment = RoutineDetailFragment()
    },

    MEMO_FRAGMENT {
        override val tag = 1
        override val fragment = MemoFragment()
    },
    MEMO_DETAIL_FRAGMENT {
        override val tag = 1
        override val fragment = MemoDetailFragment()
    },
    ADD_EDIT_FRAGMENT {
        override val tag = 1
        override val fragment = AddEditFragment()
    },

    TIMER_FRAGMENT {
        override val tag = 2
        override val fragment = TimerFragment()
    },

    INBODY_FRAGMENT {
        override val tag = 3
        override val fragment = InbodyFragment()
    },

    SETTINGS_FRAGMENT {
        override val tag = 4
        override val fragment = SettingsFragment()
    },

    SET_TEMPLATE_FRAGMENT {
        override val tag = 4
        override val fragment = SetTemplateFragment()
    }
}