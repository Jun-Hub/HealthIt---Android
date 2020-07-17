package io.jun.healthit.view

import android.content.pm.PackageInfo
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.jun.healthit.R

class MainActivity : AppCompatActivity() {

    companion object {
        var inKorea = true
    }

    //뒤로가기 연속 클릭 대기 시간
    private var backWait:Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.apply {
            itemRippleColor = if(Build.VERSION.SDK_INT > 22) getColorStateList(R.color.color_state_list)
                            else resources.getColorStateList(R.color.color_state_list)
            background = if(Build.VERSION.SDK_INT > 22) getDrawable(R.drawable.bottom_nav)
                            else resources.getDrawable(R.drawable.bottom_nav)
        }

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_routine,
                R.id.navigation_memo,
                R.id.navigation_timer,
                R.id.navigation_settings
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val language = if(Build.VERSION.SDK_INT >= 24) {
            resources.configuration.locales.get(0).language
        } else
            resources.configuration.locale.language

        inKorea = language == "ko"

        getVersionInfo()
    }

    override fun onBackPressed() {
        // 뒤로가기 버튼 클릭
        if(System.currentTimeMillis() - backWait >=2000 ) {
            backWait = System.currentTimeMillis()
            Toast.makeText(this, getString(R.string.notice_exit), Toast.LENGTH_SHORT).show()
        } else {
            finish()
        }
    }

    private fun getVersionInfo() {
        val info = packageManager.getPackageInfo(packageName, 0)
        val version = info.versionName
        Log.e("Asdasd", "" + version)
    }

}