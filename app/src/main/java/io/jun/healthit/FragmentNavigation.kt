package io.jun.healthit

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.*
import io.jun.healthit.view.MainActivity
import io.jun.healthit.view.RoutineDetailFragment
import io.jun.healthit.view.fragment.BaseFragment
import io.jun.healthit.view.fragment.RoutineFragment
import java.util.*

//TODO 프래그먼트 뎁스 저장
class FragmentNavigation(private val activity: MainActivity) {

    private val TAG = javaClass.simpleName
    private val fragmentStack = Stack<String>()
    private val manager = activity.supportFragmentManager

    fun replace(fragment: FragmentProvider) =
        manager.commit {
            //stack clear
            if(fragment.index() == 0 && fragmentStack.isNotEmpty()) {
                Log.d(TAG, "rrrr${fragment.getFragment().tag}")
                Log.d(TAG, "aaaa ${fragmentStack.peek()}")
                manager.findFragmentByTag(fragmentStack.pop())?.let {
                    replace(R.id.nav_host_fragment, it)
                }
            } else {
                Log.d(TAG, "dddd${fragment.getFragment().tag}")
                //manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                replace(R.id.nav_host_fragment, fragment.getFragment())
                setReorderingAllowed(true)
            }
        }

    fun move(fragment: FragmentProvider, tag: String, bundle: Bundle? = null) =
        manager.commit {
            Log.d(TAG, "asdfnlafj")
            add(R.id.nav_host_fragment, fragment.getFragment(), tag)
            fragmentStack.push(tag)
            addToBackStack(tag)
            setReorderingAllowed(true)
        }

    fun back() =
        if(manager.backStackEntryCount==0)
            activity.finishApp()
        else
            manager.popBackStack()
}