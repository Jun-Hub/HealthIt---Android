package io.jun.healthit.update

import android.app.Activity
import android.util.Log
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import io.jun.healthit.util.Setting

class UpdateManager(val activity: Activity) {

    private val TAG = javaClass.name
    // Creates instance of the manager.
    private val appUpdateManager = AppUpdateManagerFactory.create(activity)
    // Returns an intent object that you use to check for an update.
    private val appUpdateInfoTask = appUpdateManager.appUpdateInfo

    fun checkUpdate() {
        Log.d(TAG, "checkUpdate: ${appUpdateInfoTask}")
    // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            Log.d(TAG, " : ${appUpdateInfo.updateAvailability()}")
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
            ) {
                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    AppUpdateType.FLEXIBLE,
                    activity,
                    Setting.UPDATE_REQUEST_CODE)
            }
        }
    }

    fun checkUpdateStaleness() {
        // Checks whether the platform allows the specified type of update,
        // and current version staleness.
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            appUpdateInfo.clientVersionStalenessDays()?.let {
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && it >= 3
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
                ) {
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        AppUpdateType.FLEXIBLE,
                        activity,
                        Setting.UPDATE_REQUEST_CODE)
                }
            }
        }
    }

    private fun requestFlexibleUpdate() {

    }

}