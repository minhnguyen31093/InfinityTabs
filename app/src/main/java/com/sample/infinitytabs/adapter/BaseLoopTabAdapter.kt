package com.sample.infinitytabs.adapter

import android.content.Context
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearSmoothScroller
import android.support.v7.widget.RecyclerView
import android.util.SparseArray
import android.view.ViewGroup
import com.sample.infinitytabs.model.Tab


/**
 * Created by Minh Nguyen on 7/13/2018.
 */
class BaseLoopTabAdapter(private var tabs: ArrayList<Tab>, fm: FragmentManager?) : FragmentStatePagerAdapter(fm) {

    private val registeredFragments = SparseArray<Fragment>()
    private var viewPager: ViewPager? = null
    private var recyclerView: RecyclerView? = null
    private var onPageChangeListener: OnPageChangeListener? = null
    private var jumpPosition = -1

    private var tabsSize = 0
    private var pageSize = 0

    var selectedPosition = 0
        set(value) {
            field = value
            Handler().postDelayed({
                setSelectedTab(tabsSize / 2 + value)
            }, 100)
        }

    private val onItemClickListener = object : BaseLoopAdapter.OnItemClickListener {
        override fun onClick(item: Tab, position: Int) {
            val realPosition = position % pageSize
            if (jumpPosition == -1) {
                jumpPosition = when {
                    (position < pageSize) || (position < tabsSize && position > tabsSize - pageSize) -> tabsSize / 2 + realPosition
                    else -> -1
                }
                if (jumpPosition >= 0) {
                    setSelectedTab(jumpPosition)
                } else {
                    scrollTo(position, realPosition, true)
                }
            } else {
                jumpPosition = -1
                scrollTo(position, realPosition, true)
            }
        }
    }

    private val onScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            if (tabsSize > 0) {
                val mLayoutManager = recyclerView.layoutManager as LinearLayoutManager
                val totalItemCount = mLayoutManager.itemCount
                val firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition() - 2
                val lastVisibleItem = mLayoutManager.findLastVisibleItemPosition() + 2
                if (firstVisibleItem <= 0 || totalItemCount <= lastVisibleItem) {
                    val currentPosition = (recyclerView.adapter as BaseLoopAdapter).selectedPosition % pageSize
                    setSelectedTab(tabsSize / 2 + currentPosition)
                }
            }
        }
    }

    private fun setSelectedTab(position: Int) {
        if (recyclerView != null && recyclerView!!.adapter != null && recyclerView!!.adapter!!.itemCount > 0) {
            val adapter = recyclerView!!.adapter as BaseLoopAdapter
            if (position > -1 && adapter.itemCount > 0 && position < adapter.itemCount) {
                adapter.changePosition(position)
                scrollTo(position, position % pageSize, false)
            }
        }
    }

    private fun scrollTo(position: Int, realPosition: Int, isSmooth: Boolean) {
        if (isSmooth) {
            recyclerView!!.smoothScrollToPosition(position)
        } else {
            (recyclerView!!.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(position - 2, 0)
        }
        setSelected(realPosition)
    }

    private fun setSelected(position: Int) {
        viewPager!!.setCurrentItem(position, false)
        if (onPageChangeListener != null) {
            val tab = tabs[position]
            onPageChangeListener!!.onPageSelected(position, tab)
        }
    }

    override fun getItem(position: Int): Fragment {
        return tabs[position].fragment
    }

    override fun getItemPosition(`object`: Any): Int {
        return POSITION_UNCHANGED
    }

    override fun getCount(): Int {
        return tabs.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return tabs[position].name
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val fragment = super.instantiateItem(container, position) as Fragment
        registeredFragments.put(position, fragment)
        return fragment
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        registeredFragments.remove(position)
        super.destroyItem(container, position, `object`)
    }

    fun getRegisteredFragment(position: Int): Fragment {
        return registeredFragments.get(position)
    }

    fun getFragment(position: Int): Fragment {
        return tabs[position].fragment
    }

    fun getName(position: Int): String {
        return tabs[position].fragment::class.java.simpleName
    }

    fun getPositionOf(fragment: Fragment): Int {
        return getPositionOf(fragment::class.java.simpleName)
    }

    fun getPositionOf(name: String): Int {
        tabs.forEachIndexed { index, tab ->
            if (tab.fragment::class.java.simpleName == name) {
                return index + 2
            }
        }
        return -1
    }

    fun setOnPageChangeListener(viewPager: ViewPager, recyclerView: RecyclerView, onPageChangeListener: OnPageChangeListener) {
        viewPager.offscreenPageLimit = 6
        recyclerView.addOnScrollListener(onScrollListener)
        recyclerView.layoutManager = CenterLayoutManager(recyclerView.context)
        this.viewPager = viewPager
        this.recyclerView = recyclerView
        this.onPageChangeListener = onPageChangeListener
        setUpTab()
    }

    private fun setUpTab() {
        if (recyclerView != null) {
            pageSize = tabs.size
            tabsSize = pageSize * 200
            val tempTabs = ArrayList<Tab>()
            for (i in 1..200) {
                tempTabs.addAll(tabs)
            }
            val adapter = BaseLoopAdapter(recyclerView!!.context, tempTabs, pageSize, 5)
            adapter.onItemClickListener = onItemClickListener
            recyclerView!!.adapter = adapter
        }
    }

    interface OnPageChangeListener {
        fun onPageSelected(position: Int, tab: Tab)
    }

    class CenterLayoutManager(context: Context) : LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false) {

        override fun smoothScrollToPosition(recyclerView: RecyclerView, state: RecyclerView.State?, position: Int) {
            val smoothScroller = CenterSmoothScroller(recyclerView.context)
            smoothScroller.targetPosition = position
            startSmoothScroll(smoothScroller)
        }

        private class CenterSmoothScroller internal constructor(context: Context) : LinearSmoothScroller(context) {

            override fun calculateDtToFit(viewStart: Int, viewEnd: Int, boxStart: Int, boxEnd: Int, snapPreference: Int): Int {
                return boxStart + (boxEnd - boxStart) / 2 - (viewStart + (viewEnd - viewStart) / 2)
            }
        }
    }
}