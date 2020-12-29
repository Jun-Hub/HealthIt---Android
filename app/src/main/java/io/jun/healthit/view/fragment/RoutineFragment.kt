package io.jun.healthit.view.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import com.cleveroad.fanlayoutmanager.FanLayoutManager
import com.cleveroad.fanlayoutmanager.FanLayoutManagerSettings
import com.cleveroad.fanlayoutmanager.callbacks.FanChildDrawingOrderCallback
import io.jun.healthit.FragmentProvider
import io.jun.healthit.R
import io.jun.healthit.adapter.TipListAdapter
import io.jun.healthit.databinding.FragmentRoutineBinding
import io.jun.healthit.model.data.Tip
import kotlinx.android.synthetic.main.fragment_routine.adView

//TODO 프래그먼트 addToBackStack 또는 popBackStack 됨에 따라 이동 애니메이션 추가하기
class RoutineFragment : BaseFragment() {

    private val TAG = "RoutineFragment"
    
    private var viewBinding: FragmentRoutineBinding? = null
    private val binding get() = viewBinding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentRoutineBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        context?.let { 
            setView(it)
        }
    }

    private fun setView(context: Context) {
        val fanLayoutManagerSettings = FanLayoutManagerSettings
            .newBuilder(context)
            .withFanRadius(true)
            .withAngleItemBounce(5f)
            .withViewHeightDp(180f)
            .withViewWidthDp(120f)
            .build()

        val fanLayoutManager = FanLayoutManager(context, fanLayoutManagerSettings)

        val tipAdapter = TipListAdapter(context, fanLayoutManager) { bundle ->
            navigation.move(FragmentProvider.ROUTINE_DETAIL_FRAGMENT, bundle)
        }

        tipAdapter.apply {
            add(Tip(getString(R.string.text_common_sense), null, R.drawable.ic_moonk, "common_sense"))
            add(Tip(getString(R.string.text_common_sense_diet), null, R.drawable.ic_hamburger, "common_sense_diet"))
            add(Tip(getString(R.string.text_full_body), "for beginner", R.drawable.ic_0, "full_body"))
            add(Tip(getString(R.string.text_2day_split), "for intermediate", R.drawable.ic_2, "2day_split"))
            add(Tip(getString(R.string.text_3day_split), "for advanced", R.drawable.ic_3, "3day_split"))
            add(Tip(getString(R.string.text_4day_split), "for advanced", R.drawable.ic_4, "4day_split"))
            add(Tip(getString(R.string.text_5day_split), "not recommended", R.drawable.ic_5, "5day_split"))
            add(Tip(getString(R.string.text_strength), "optional", R.drawable.ic_strength, "strength"))
        }

        tipAdapter.setOnItemClickListener(object : TipListAdapter.OnItemClickListener {
            override fun onItemClicked(pos: Int, view: View?) {
                fanLayoutManager.switchItem(binding.recyclerView, pos)
            }
        })

        binding.recyclerView.apply {
            layoutManager = fanLayoutManager
            itemAnimator = DefaultItemAnimator()
            adapter = tipAdapter
            setChildDrawingOrderCallback(FanChildDrawingOrderCallback(fanLayoutManager))
        }
    }
    
    override fun checkProVersion(isProVersion: Boolean) {
        super.checkProVersion(isProVersion)
        if(isProVersion) {
            adView.visibility = View.GONE
            return
        }

        loadBannerAd(adView)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        navigation.back()
    }

}