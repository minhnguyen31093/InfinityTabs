package com.sample.infinitytabs.adapter

import android.content.Context
import android.view.ViewGroup
import com.sample.infinitytabs.R
import com.sample.infinitytabs.model.Tab
import com.sample.infinitytabs.utils.ScreenUtils
import kotlinx.android.synthetic.main.item_tab.*

/**
 * Created by Minh Nguyen on 7/24/2018.
 */
class BaseLoopAdapter(context: Context, items: ArrayList<Tab>?, var realSize: Int, visibleItems: Int) : BaseAdapter<Tab>(items) {

    private var width = -2

    var selectedPosition = -1
    var onItemClickListener: OnItemClickListener? = null

    init {
        width = ScreenUtils.getScreenWidth(context) / visibleItems
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<Tab> {
        return ViewHolder(R.layout.item_tab, parent)
    }

    inner class ViewHolder(layoutId: Int, parent: ViewGroup) : BaseViewHolder<Tab>(layoutId, parent) {

        init {
            if (width > 0) {
                itemView.layoutParams.width = width
            }
        }

        override fun bind(item: Tab?, position: Int) {
            if (item != null) {
                tvMain.text = item.name

                val realSelectedPosition = selectedPosition % realSize
                val realPosition = position % realSize
                tvMain.isSelected = realSelectedPosition == realPosition

                tvMain.setOnClickListener {
                    if (selectedPosition != position) {
                        changePosition(position)
                        if (onItemClickListener != null) {
                            onItemClickListener!!.onClick(item, position)
                        }
                    }
                }
            }
        }
    }

    fun changePosition(position: Int) {
//        val prePosition = selectedPosition
        selectedPosition = position
//        if (prePosition != -1) {
//            notifyItemChanged(prePosition)
//        }
//        notifyItemChanged(position)
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onClick(item: Tab, position: Int)
    }
}