package com.finals.friendsfinder.views.chatting.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.finals.friendsfinder.bases.BaseViewHolder
import com.finals.friendsfinder.databinding.ItemChattingBinding
import com.finals.friendsfinder.utilities.Utils
import com.finals.friendsfinder.utilities.clickWithDebounce
import com.finals.friendsfinder.views.chatting.data.ConversationModel
import com.finals.friendsfinder.views.chatting.data.ConversationModelDTO

class AllMessageAdapter(
    private val context: Context,
    private val currentUserId: String,
    private val onItemClick: ((ConversationModelDTO) -> Unit)
) :
    RecyclerView.Adapter<AllMessageAdapter.AllMessageVH>() {

    private val listConversation: MutableList<ConversationModelDTO> = mutableListOf()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AllMessageAdapter.AllMessageVH {
        val binding = ItemChattingBinding.inflate(LayoutInflater.from(context))
        return AllMessageVH(binding).apply {
            setupView()
        }
    }

    override fun onBindViewHolder(holder: AllMessageAdapter.AllMessageVH, position: Int) {
        holder.bindData(listConversation, position)
    }

    override fun getItemCount(): Int {
        return listConversation.size
    }

    fun setList(newList: List<ConversationModelDTO>) {
        listConversation.clear()
        listConversation.addAll(newList)
        notifyDataSetChanged()
    }

    inner class AllMessageVH(binding: ItemChattingBinding) :
        BaseViewHolder<ItemChattingBinding, MutableList<ConversationModelDTO>>(binding) {
        override fun bindData(data: MutableList<ConversationModelDTO>, position: Int) {
            super.bindData(data, position)
            val item = listConversation[position]
            with(binding) {
                tvName.text = if (currentUserId.equals(item.creatorId, true)){
                    item.conversationName ?: "Anyone"
                }else item.secondConversationName ?: "Anyone"
                tvDesc.text = Utils.shared.convertStringDate(
                    oldFormat = "yyyy-MM-dd'T'hh:mm:ss.SSS'Z'",
                    newFormat = "dd-MM-yyyy HH:mm",
                    item.createAt ?: ""
                )
                layoutMain.clickWithDebounce {
                    onItemClick.invoke(item)
                }
            }
        }

    }
}