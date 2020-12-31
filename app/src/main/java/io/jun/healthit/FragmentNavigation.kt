package io.jun.healthit

import android.util.Log
import androidx.fragment.app.*
import io.jun.healthit.view.*
import io.jun.healthit.view.fragment.*
import java.util.*

class FragmentNavigation(private val activity: MainActivity) {

    private val TAG = javaClass.simpleName
    private val manager = activity.supportFragmentManager

    private val fragmentMap = HashMap<Int, Stack<Fragment>>()
    private var currentTab = 1  //운동일지 프래그먼트가 시작점
    private val container = R.id.nav_host_fragment

    init {
        fragmentMap[0] = Stack<Fragment>()
        fragmentMap[1] = Stack<Fragment>().apply { push(MemoFragment()) }   //첫시작 프래그먼트를 추가해줌
        fragmentMap[2] = Stack<Fragment>()
        fragmentMap[3] = Stack<Fragment>()
        fragmentMap[4] = Stack<Fragment>()
    }

    fun change(fragment: Fragment) =
        fragmentMap[getFragmentTag(fragment)]?.let {
            currentTab = getFragmentTag(fragment)

            if (it.isEmpty()) {
                it.push(fragment)
                manager.commit {
                    replace(container, fragment)
                }
                return@let
            }

            //마지막으로 방문했던 fragment 보여주기
            manager.commit {
                replace(container, it.last())
            }
        }

    fun move(fragment: Fragment) =
        fragmentMap[getFragmentTag(fragment)]?.let {
            it.push(fragment)
            manager.commit {
                replace(container, fragment)
            }
        }

    fun finishAndMove(fragment: Fragment) =
        fragmentMap[getFragmentTag(fragment)]?.let {

            it.pop()    //현재 프래그먼트 스택에서 제거
            it.push(fragment)
            manager.commit {
                replace(container, fragment)
            }
        }

    fun back() =
        if (fragmentMap[currentTab]?.size == 1)
            activity.finishApp()
        else
            popFragment()

    private fun popFragment() =
        fragmentMap[currentTab]?.let {
            it.pop()

            manager.commit {
                replace(container, it.peek())
            }
        }

    private fun getFragmentTag(fragment: Fragment) =
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
}