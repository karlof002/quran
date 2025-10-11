package com.karlof002.quran.ui.utils

import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import kotlinx.coroutines.tasks.await

class UpdateManager(private val context: Context) {
    private val appUpdateManager: AppUpdateManager = AppUpdateManagerFactory.create(context)

    companion object {
        private const val TAG = "UpdateManager"
        private const val UPDATE_REQUEST_CODE = 1001
    }

    /**
     * Check if an update is available
     */
    suspend fun checkForUpdates(): UpdateInfo {
        return try {
            val appUpdateInfo = appUpdateManager.appUpdateInfo.await()

            when (appUpdateInfo.updateAvailability()) {
                UpdateAvailability.UPDATE_AVAILABLE -> {
                    Log.d(TAG, "Update available")
                    UpdateInfo(
                        isUpdateAvailable = true,
                        isImmediateUpdateAllowed = appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE),
                        isFlexibleUpdateAllowed = appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE),
                        appUpdateInfo = appUpdateInfo
                    )
                }
                UpdateAvailability.UPDATE_NOT_AVAILABLE -> {
                    Log.d(TAG, "No update available")
                    UpdateInfo(isUpdateAvailable = false)
                }
                UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS -> {
                    Log.d(TAG, "Update in progress")
                    UpdateInfo(
                        isUpdateAvailable = true,
                        isUpdateInProgress = true,
                        appUpdateInfo = appUpdateInfo
                    )
                }
                else -> {
                    Log.d(TAG, "Unknown update status")
                    UpdateInfo(isUpdateAvailable = false)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error checking for updates", e)
            UpdateInfo(isUpdateAvailable = false, error = e.message)
        }
    }

    /**
     * Start an immediate update flow
     */
    fun startImmediateUpdate(
        activity: Activity,
        appUpdateInfo: AppUpdateInfo,
        updateResultLauncher: ActivityResultLauncher<IntentSenderRequest>
    ) {
        try {
            val updateOptions = AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
            appUpdateManager.startUpdateFlowForResult(
                appUpdateInfo,
                activity,
                updateOptions,
                UPDATE_REQUEST_CODE
            )
        } catch (e: IntentSender.SendIntentException) {
            Log.e(TAG, "Error starting immediate update", e)
        }
    }

    /**
     * Start a flexible update flow
     */
    fun startFlexibleUpdate(
        activity: Activity,
        appUpdateInfo: AppUpdateInfo
    ) {
        try {
            val updateOptions = AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE).build()
            appUpdateManager.startUpdateFlowForResult(
                appUpdateInfo,
                activity,
                updateOptions,
                UPDATE_REQUEST_CODE
            )
        } catch (e: IntentSender.SendIntentException) {
            Log.e(TAG, "Error starting flexible update", e)
        }
    }

    /**
     * Check if an update was downloaded and ready to install (for flexible updates)
     */
    fun completeFlexibleUpdate() {
        appUpdateManager.completeUpdate()
    }

    /**
     * Check if an update is in progress and needs to be resumed
     */
    suspend fun resumeUpdateIfInProgress(activity: Activity) {
        try {
            val appUpdateInfo = appUpdateManager.appUpdateInfo.await()
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                val updateOptions = AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    activity,
                    updateOptions,
                    UPDATE_REQUEST_CODE
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error resuming update", e)
        }
    }
}

data class UpdateInfo(
    val isUpdateAvailable: Boolean,
    val isImmediateUpdateAllowed: Boolean = false,
    val isFlexibleUpdateAllowed: Boolean = false,
    val isUpdateInProgress: Boolean = false,
    val appUpdateInfo: AppUpdateInfo? = null,
    val error: String? = null
)
