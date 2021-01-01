package io.jun.healthit

import android.util.Log
import androidx.fragment.app.*
import io.jun.healthit.view.*
import java.util.*

class FragmentNavigation(private val activity: MainActivity) {

    private val TAG = javaClass.simpleName
    private val manager = activity.supportFragmentManager

    private val fragmentMap = HashMap<Int, Stack<Fragment>>()
    private var currentTab = 1  //운동일지 프래그먼트가 시작점
    private val container = R.id.nav_host_fragment

    init {  //첫시작 프래그먼트들 추가해줌
        fragmentMap[0] = Stack<Fragment>().apply { push(FragmentFactory.getRoutineFragment()) }
        fragmentMap[1] = Stack<Fragment>().apply { push(FragmentFactory.getMemoFragment()) }
        fragmentMap[2] = Stack<Fragment>().apply { push(FragmentFactory.getInbodyFragment()) }
        fragmentMap[3] = Stack<Fragment>().apply { push(FragmentFactory.getTimerFragment()) }
        fragmentMap[4] = Stack<Fragment>().apply { push(FragmentFactory.getSettingsFragment()) }
    }

    fun change(fragment: Fragment) =
        fragmentMap[getFragmentTag(fragment)]?.let {

            //마지막으로 방문했던 fragment 보여주기
            switchFragment(it.last())
            currentTab = getFragmentTag(fragment)
        }

    fun move(fragment: Fragment) =
        fragmentMap[getFragmentTag(fragment)]?.let {
            it.push(fragment)

            switchFragment(fragment)
        }

    fun finishAndMove(fragment: Fragment) =
        fragmentMap[getFragmentTag(fragment)]?.let {
            it.pop()    //현재 프래그먼트 스택에서 제거
            it.push(fragment)

            switchFragment(fragment)
        }

    fun back() =
        if (fragmentMap[currentTab]?.size == 1)
            activity.finishApp()
        else
            popFragment()

    private fun popFragment() =
        fragmentMap[currentTab]?.let {
            it.pop()

            switchFragment(it.peek())
        }

    private fun switchFragment(fragment: Fragment) =
        manager.commit {
            if(fragment.isAdded) {
                show(fragment)
            } else {
                add(container, fragment)
            }
            manager.fragments.forEach { f ->
                if(f != fragment && !f.isHidden) {
                    hide(f)
                }
            }
        }
}