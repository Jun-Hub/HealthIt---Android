package io.jun.healthit

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.*
import io.jun.healthit.view.MainActivity
import io.jun.healthit.view.fragment.MemoFragment
import java.util.*
//TODO 이거 싱글톤으로 주입하기
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

    fun change(info: FragmentInfo) =
        fragmentMap[info.tag]?.let {
            currentTab = info.tag

            if (it.isEmpty()) {
                it.push(info.fragment)

                manager.commit {
                    replace(container, info.fragment)
                }
                return@let
            }

            //마지막으로 방문했던 fragment 보여주기
            manager.commit {
                replace(container, it.last())
                Log.d(TAG, "change: ${it.last()}")
            }
        }

    fun move(info: FragmentInfo, bundle: Bundle? = null) =
        fragmentMap[info.tag]?.let {

            it.push(info.fragment)
            manager.commit {
                Log.d(TAG, "move: ${info.fragment}")
                replace(container, info.fragment.apply { arguments = bundle })
            }
        }

    fun finishAndMove(info: FragmentInfo, bundle: Bundle? = null) =
        fragmentMap[info.tag]?.let {

            it.pop()    //현재 프래그먼트 스택에서 제거
            it.push(info.fragment)
            manager.commit {
                replace(container, info.fragment.apply { arguments = bundle })
            }
        }

    fun back() =
        if(fragmentMap[currentTab]?.size == 1)
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
}