package com.finals.friendsfinder.views.chatting

import android.os.Bundle
import android.view.LayoutInflater
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.finals.friendsfinder.bases.BaseFragment
import com.finals.friendsfinder.databinding.FragmentAllMessageBinding
import com.finals.friendsfinder.models.BaseAccessToken
import com.finals.friendsfinder.utilities.addFragmentToBackstack
import com.finals.friendsfinder.utilities.clickWithDebounce
import com.finals.friendsfinder.utilities.commons.TableKey
import com.finals.friendsfinder.views.chatting.adapter.AllMessageAdapter
import com.finals.friendsfinder.views.chatting.data.ConversationModel
import com.finals.friendsfinder.views.chatting.data.ParticipantModel
import com.finals.friendsfinder.views.friends.data.UserInfo
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AllMessageFragment : BaseFragment<FragmentAllMessageBinding>() {


    companion object {
        private const val USER_LIST = "USER_LIST"
        fun newInstance(listUser: ArrayList<UserInfo?>): AllMessageFragment {
            val arg = Bundle().apply {
                putParcelableArrayList(USER_LIST, listUser)
            }
            return AllMessageFragment().apply {
                arguments = arg
            }
        }
    }

    private var allMessageAdapter: AllMessageAdapter? = null
    private var currentListUser: MutableList<UserInfo>? = null
    private var listConversation: MutableList<ConversationModel>? = null
    private var listParticipant: MutableList<ParticipantModel>? = null
    var onBackEvent: (() -> Unit)? = null

    override fun observeHandle() {
        super.observeHandle()
        currentListUser = arguments?.getParcelableArrayList(USER_LIST)
        listConversation = mutableListOf()
        listParticipant = mutableListOf()
    }

    override fun setupView() {
        super.setupView()
        setAdapter()
        getListUser()
        setListener()
    }

    private fun setListener() {
        with(rootView) {
            layoutHeader.imgPlus.isVisible = true
            layoutHeader.tvMessage.text = "Messengers"
            layoutHeader.imgBack.clickWithDebounce {
                onBackEvent?.invoke()
                activity?.supportFragmentManager?.popBackStack()
            }
            layoutHeader.imgPlus.clickWithDebounce {
                activity?.addFragmentToBackstack(
                    android.R.id.content,
                    CreateGroupFragment.newInstance(ArrayList(currentListUser))
                )
            }
        }
    }

    private fun getListUser() {
        val dbConversationReference =
            FirebaseDatabase.getInstance().getReference(TableKey.CONVERSATION.key)
        val dbParticipantReference =
            FirebaseDatabase.getInstance().getReference(TableKey.PARTICIPANTS.key)

        dbConversationReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listConversation?.clear()
                for (dataSnap: DataSnapshot in snapshot.children) {
                    val conv = dataSnap.getValue(ConversationModel::class.java)
                    conv?.let { listConversation?.add(it) }
                }
                checkCurrentConversation()
            }

            override fun onCancelled(error: DatabaseError) {
                listConversation?.clear()
            }

        })

        dbParticipantReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listParticipant?.clear()
                for (dataSnap: DataSnapshot in snapshot.children) {
                    val conv = dataSnap.getValue(ParticipantModel::class.java)
                    conv?.let { listParticipant?.add(it) }
                }
                checkCurrentConversation()
            }

            override fun onCancelled(error: DatabaseError) {
                listParticipant?.clear()
            }

        })
    }

    private fun checkCurrentConversation() {
        val currentIdUser = BaseAccessToken.accessToken
        val listCurrentConversation: MutableList<ConversationModel> = mutableListOf()
        listConversation?.forEachIndexed { index, conversationModel ->
            listParticipant?.forEachIndexed { index, participantModel ->
                if (conversationModel.conversationId.equals(
                        participantModel.conversationId,
                        true
                    )
                ) {
                    if (conversationModel.creatorId.equals(
                            currentIdUser,
                            true
                        ) || participantModel.userId.equals(currentIdUser, true)
                    ) {
                        listCurrentConversation.add(conversationModel)
                    }
                }
            }
        }
        val newList = listCurrentConversation.distinctBy {
            it.conversationId
        }
        rootView.rvListMessage.isVisible = newList.isNotEmpty()
        rootView.tvNoData.isVisible = newList.isEmpty()
        allMessageAdapter?.setList(newList)
    }

    private fun setAdapter() {
        allMessageAdapter = AllMessageAdapter(requireContext(), onItemClick = { convDTO ->
            // go to chatting
        })
        rootView.rvListMessage.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = allMessageAdapter
        }

    }

    override fun getViewBinding(inflater: LayoutInflater): FragmentAllMessageBinding {
        return FragmentAllMessageBinding.inflate(inflater)
    }
}