package com.sample.infinitytabs.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.sample.infinitytabs.R
import com.sample.infinitytabs.adapter.BaseLoopTabAdapter
import com.sample.infinitytabs.fragment.*
import com.sample.infinitytabs.model.Tab
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar!!.elevation = 0f
        initTabs()
    }

    private fun initTabs() {
        val titles = resources.getStringArray(R.array.tab_main_activity)
        val tabs = ArrayList<Tab>()
        tabs.add(Tab(titles[0], FragmentA()))
        tabs.add(Tab(titles[1], FragmentB()))
        tabs.add(Tab(titles[2], FragmentC()))
        tabs.add(Tab(titles[3], FragmentD()))
        tabs.add(Tab(titles[4], FragmentE()))
        tabs.add(Tab(titles[5], FragmentF()))
        val adapter = BaseLoopTabAdapter(tabs, supportFragmentManager)
        adapter.setOnPageChangeListener(vpMain, rvMain, object : BaseLoopTabAdapter.OnPageChangeListener {
            override fun onPageSelected(position: Int, tab: Tab) {

            }
        })
        vpMain.adapter = adapter
        adapter.selectedPosition = 2
    }
}
