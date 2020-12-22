package io.jun.healthit

import androidx.fragment.app.Fragment
import io.jun.healthit.view.RoutineDetailFragment
import io.jun.healthit.view.fragment.*

interface FragmentInfo {
    fun index(): Int
    fun getFragment(): Fragment
}

enum class FragmentProvider: FragmentInfo {
    ROUTINE_FRAGMENT {
        override fun index() = 0
        override fun getFragment() = RoutineFragment()
    },

    ROUTINE_DETAIL_FRAGMENT {
        override fun index() = 1
        override fun getFragment() = RoutineDetailFragment()
    },
    MEMO_FRAGMENT {
        override fun index() = 2
        override fun getFragment() = MemoFragment()
    },
    TIMER_FRAGMENT {
        override fun index() = 3
        override fun getFragment() = TimerFragment()
    },
    INBODY_FRAGMENT {
        override fun index() = 4
        override fun getFragment() = InbodyFragment()
    },
    SETTINGS_FRAGMENT {
        override fun index() = 5
        override fun getFragment() = SettingsFragment()
    }
}