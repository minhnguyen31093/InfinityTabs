package com.sample.infinitytabs.utils

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.v4.content.ContextCompat
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.WindowManager
import com.sample.infinitytabs.R


/**
 * Created by Minh. Nguyen Le on 9/18/2015.
 */
object ScreenUtils {

    fun getScreenHeight(context: Context): Int {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)
        return dm.heightPixels
    }

    fun getScreenWidth(context: Context): Int {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)
        return dm.widthPixels
    }

    fun isTablet(context: Context): Boolean = context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_LARGE

    fun getOrientation(context: Context): Int = context.resources.configuration.orientation

    fun isLandscape(context: Context): Boolean = getOrientation(context) == Configuration.ORIENTATION_LANDSCAPE

    fun isPortrait(context: Context): Boolean = getOrientation(context) == Configuration.ORIENTATION_PORTRAIT

    /**
     * Locks the device window in landscape mode.
     */
    fun lockOrientationLandscape(activity: Activity) {
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    /**
     * Locks the device window in portrait mode.
     */
    fun lockOrientationPortrait(activity: Activity) {
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    /**
     * Allows user to freely use portrait or landscape mode.
     */
    fun unlockOrientation(activity: Activity) {
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }

    // ==========================================
    // Inner/Anonymous Classes/Interfaces
    // ==========================================

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp      A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @return A float value to represent px equivalent to dp depending on device density
     */
    fun dpToPx(dp: Float): Int = Math.round(dp * Resources.getSystem().displayMetrics.density)

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px      A value in px (pixels) unit. Which we need to convert into db
     * @return A float value to represent dp equivalent to px value
     */
    fun pxToDp(px: Int): Float = px / (Resources.getSystem().displayMetrics.densityDpi.toFloat() / 160f)

    fun changeIconDrawableToGray(context: Context, drawable: Drawable?) {
        if (drawable != null) {
            drawable.mutate()
            drawable.setColorFilter(ContextCompat.getColor(context, android.R.color.darker_gray), PorterDuff.Mode.SRC_ATOP)
        }
    }

    fun restart(context: Context, delay: Int) {
        var delayVar = delay
        if (delayVar == 0) {
            delayVar = 1
        }
        Log.e("", "restarting app")
        val restartIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        restartIntent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        val intent = PendingIntent.getActivity(context, 0, restartIntent, PendingIntent.FLAG_ONE_SHOT)
        val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        manager.set(AlarmManager.RTC, System.currentTimeMillis() + delayVar, intent)
        System.exit(2)
    }

    fun recreate(context: Context) {
        if (context is Activity) {
            val intent = context.intent
            context.finish()
            context.startActivity(intent)
            context.overridePendingTransition(0, 0)
        }
    }

    fun setBackgroundResource(view: View, resId: Int) {
        val bottom = view.paddingBottom
        val top = view.paddingTop
        val right = view.paddingRight
        val left = view.paddingLeft
        view.setBackgroundResource(resId)
        view.setPadding(left, top, right, bottom)
    }

    fun setBackgroundResource(view: View, resIdBackground: Int, resIdPadding: Int) {
        val pad = view.context.resources.getDimensionPixelSize(resIdPadding)
        view.setBackgroundResource(resIdBackground)
        view.setPadding(pad, pad, pad, pad)
    }

    fun setFullScreen(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                activity.window.statusBarColor = Color.TRANSPARENT
            } else {
                setTranslucentStatusFlag(activity, true)
            }
        }
        setTranslucentNavigationFlag(activity, true)
    }

    fun getNavigationBarHeight(context: Context): Int {
        var result = 0
        if (hasNavBar(context)) {
            val resourceId = context.resources.getIdentifier("navigation_bar_height", "dimen", "android")
            if (resourceId > 0) {
                result = context.resources.getDimensionPixelSize(resourceId)
            }
        }
        return result
    }

    fun hasNavBar(context: Context): Boolean {
        val id = context.resources.getIdentifier("config_showNavigationBar", "bool", "android")
        return id > 0 && context.resources.getBoolean(id)
    }

    fun setTranslucentStatusFlag(activity: Activity, on: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val win = activity.window
            val winParams = win.attributes
            val bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
            if (on) {
                winParams.flags = winParams.flags or bits
            } else {
                winParams.flags = winParams.flags and bits.inv()
            }
            win.attributes = winParams
        }
    }

    fun setTranslucentNavigationFlag(activity: Activity, on: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val win = activity.window
            val winParams = win.attributes
            val bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
            if (on) {
                winParams.flags = winParams.flags or bits
            } else {
                winParams.flags = winParams.flags and bits.inv()
            }
            win.attributes = winParams
        }
    }

    fun fetchPrimaryColor(context: Context): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(R.attr.colorPrimary, typedValue, true)
        return typedValue.data
    }

    fun closeApp() = android.os.Process.killProcess(android.os.Process.myPid())
}