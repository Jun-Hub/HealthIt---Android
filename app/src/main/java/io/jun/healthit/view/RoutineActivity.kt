package io.jun.healthit.view

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import io.jun.healthit.R
import io.jun.healthit.adapter.ExpListAdapter
import io.jun.healthit.util.Setting
import kotlinx.android.synthetic.main.activity_routine.*

class RoutineActivity : AppCompatActivity() {

    private lateinit var adapter: ExpListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_routine)

        //툴바 세팅
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = null
        }

        when(intent.getStringExtra("tipType")) {
            "full_body" -> adapter = ExpListAdapter(Setting.fullBodyRoutineList)
            "2day_split" -> adapter = ExpListAdapter(Setting.split2dayRoutineList)
            "3day_split" -> adapter = ExpListAdapter(Setting.split3dayRoutineList)
            "4day_split" -> adapter = ExpListAdapter(Setting.split4dayRoutineList)
            "5day_split" -> adapter = ExpListAdapter(Setting.split5dayRoutineList)
            "strength" -> adapter = ExpListAdapter(Setting.strengthRoutineList)
            "common_sense" -> adapter = ExpListAdapter(Setting.commonSenseList)
            "common_sense_diet" -> adapter = ExpListAdapter(Setting.commonSenseDietList)
        }

        recyclerView.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {  //툴바의 뒤로가기 버튼
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

}
