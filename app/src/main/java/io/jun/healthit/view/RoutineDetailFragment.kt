package io.jun.healthit.view

import android.os.Bundle
import android.view.*
import io.jun.healthit.adapter.ExpListAdapter
import io.jun.healthit.databinding.FragmentRoutineDetailBinding
import io.jun.healthit.util.Setting
import io.jun.healthit.view.fragment.BaseFragment

class RoutineDetailFragment : BaseFragment() {

    private var viewBinding: FragmentRoutineDetailBinding? = null
    private val binding get() = viewBinding!!
    private lateinit var adapter: ExpListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentRoutineDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = when(arguments?.getString("tipType")) {
            "full_body" -> ExpListAdapter(Setting.fullBodyRoutineList)
            "2day_split" -> ExpListAdapter(Setting.split2dayRoutineList)
            "3day_split" -> ExpListAdapter(Setting.split3dayRoutineList)
            "4day_split" -> ExpListAdapter(Setting.split4dayRoutineList)
            "5day_split" -> ExpListAdapter(Setting.split5dayRoutineList)
            "strength" -> ExpListAdapter(Setting.strengthRoutineList)
            "common_sense" -> ExpListAdapter(Setting.commonSenseList)
            else -> ExpListAdapter(Setting.commonSenseDietList)
        }

        binding.recyclerView.adapter = adapter
    }


}
