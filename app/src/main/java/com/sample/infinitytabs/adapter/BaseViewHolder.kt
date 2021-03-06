package com.sample.infinitytabs.adapter

import android.content.Context
import android.content.res.Resources
import android.support.annotation.LayoutRes
import android.support.annotation.StringRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.extensions.LayoutContainer

/**
 * Created by Minh Nguyen on 6/8/2018.
 */
abstract class BaseViewHolder<T>(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {
    constructor(@LayoutRes layoutId: Int, parent: ViewGroup) : this(LayoutInflater.from(parent.context).inflate(layoutId, parent, false))

    abstract fun bind(item: T?, position: Int)

    val context: Context
        get() = containerView.context

    val resources: Resources
        get() = context.resources

    fun getString(@StringRes resId: Int): String {
        return resources.getString(resId)
    }

    fun getString(@StringRes resId: Int, vararg formatArgs: Any): String {
        return resources.getString(resId, *formatArgs)
    }
}