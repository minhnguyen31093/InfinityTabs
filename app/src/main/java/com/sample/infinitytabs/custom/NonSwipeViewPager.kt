package com.sample.infinitytabs.custom

import android.annotation.SuppressLint
import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent

class NonSwipeViewPager(context: Context, attrs: AttributeSet) : ViewPager(context, attrs) {

    private var swipeable: Boolean = false

    init {
        this.swipeable = false
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (this.swipeable) {
            super.onTouchEvent(event)
        } else false

    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return if (this.swipeable) {
            super.onInterceptTouchEvent(event)
        } else false
    }

    override fun setCurrentItem(item: Int, smoothScroll: Boolean) {
        super.setCurrentItem(item, false)
    }

    override fun setCurrentItem(item: Int) {
        super.setCurrentItem(item, false)
    }

    fun setPagingEnabled(enabled: Boolean) {
        this.swipeable = enabled
    }
}
