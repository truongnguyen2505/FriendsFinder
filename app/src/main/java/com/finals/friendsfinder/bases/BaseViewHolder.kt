package com.finals.friendsfinder.bases

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

open class BaseViewHolder<VB : ViewBinding, DT>(var binding: VB) : RecyclerView.ViewHolder(binding.root) {

    open fun setupView() {

    }

    open fun bindData(data: DT, position: Int) {

    }

}
