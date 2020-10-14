package co.nimblehq.recentapps.thumbnailhiding

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.view.WindowManager

class RecentAppsThumbnailHidingLifecycleTracker : Application.ActivityLifecycleCallbacks {

    private var hardwareKeyWatcher: HardwareKeyWatcher? = null

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
    }

    override fun onActivityStarted(activity: Activity) {
        hardwareKeyWatcher = HardwareKeyWatcher(activity).apply {
            setOnHardwareKeysPressedListenerListener(object :
                HardwareKeyWatcher.OnHardwareKeysPressedListener {
                override fun onHomePressed() {
                    activity.triggerRecentAppsMode(true)
                }

                override fun onRecentAppsPressed() {
                    activity.triggerRecentAppsMode(true)
                }
            })
            startWatch()
        }
    }

    override fun onActivityResumed(activity: Activity) {
        activity.triggerRecentAppsMode(false)
    }

    override fun onActivityPaused(activity: Activity) {
        /*
         * Fix hide app recent for 2 cases:
         * - pulldown notification > settings > recent
         * - messenger chathead > profile in fullscreen > recent
         * - Xiaomi accessibility button > recent
         */
        activity.triggerRecentAppsMode(true)
    }

    override fun onActivityStopped(activity: Activity) {
        hardwareKeyWatcher?.stopWatch()
        hardwareKeyWatcher = null
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
    }

    private fun Activity.triggerRecentAppsMode(inRecentAppsMode: Boolean) {
        when (val activity = this) {
            is RecentAppsThumbnailHidingListener -> activity.onRecentAppsTriggered(inRecentAppsMode)
            else -> activity.showOrHideAppRecentThumbnail(inRecentAppsMode)
        }
    }

    private fun Activity.showOrHideAppRecentThumbnail(inRecentAppsMode: Boolean) {
        if (inRecentAppsMode) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
            )
        } else {
            window.clearFlags(
                WindowManager.LayoutParams.FLAG_SECURE
            )
        }
    }
}
