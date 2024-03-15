package com.finals.friendsfinder.views.chatting

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import androidx.recyclerview.widget.LinearLayoutManager
import com.finals.friendsfinder.bases.BaseActivity
import com.finals.friendsfinder.bases.BaseFragment
import com.finals.friendsfinder.databinding.ActivityChatBinding
import com.finals.friendsfinder.models.BaseAccessToken
import com.finals.friendsfinder.utilities.Utils
import com.finals.friendsfinder.utilities.clickWithDebounce
import com.finals.friendsfinder.utilities.commons.MessageKey
import com.finals.friendsfinder.utilities.commons.TableKey
import com.finals.friendsfinder.utilities.showActivity
import com.finals.friendsfinder.views.chatting.adapter.ChatAdapter
import com.finals.friendsfinder.views.chatting.data.ConversationModel
import com.finals.friendsfinder.views.chatting.data.ConversationModelDTO
import com.finals.friendsfinder.views.chatting.data.MessageModel
import com.finals.friendsfinder.views.friends.data.UserInfo
import com.finals.friendsfinder.views.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChatFragment : BaseFragment<ActivityChatBinding>() {

    companion object {
        private const val CONVERSATION = "CONVERSATION"
        fun newInstance(conversation: ConversationModelDTO): ChatFragment {
            val arg = Bundle().apply {
                putParcelable(CONVERSATION, conversation)
            }
            return ChatFragment().apply {
                arguments = arg
            }
        }
    }

    private var dbReference: DatabaseReference? = null
    private var chatAdapter: ChatAdapter? = null
    private var mConversation: ConversationModelDTO? = null

    override fun observeHandle() {
        super.observeHandle()
        val currentIdUser = BaseAccessToken.accessToken
        arguments?.let {
            mConversation = it.getParcelable(CONVERSATION)
            rootView.layoutHeader.tvMessage.text = if (currentIdUser.equals(mConversation?.creatorId, true)){
                mConversation?.conversationName ?: "Anyone"
            }else mConversation?.secondConversationName ?: "Anyone"
        }
    }

    override fun setupEventControl() {
        super.setupEventControl()
        setListeners()
        setAdapter()
        readMess()
    }

    private fun setAdapter() {
        chatAdapter = ChatAdapter(requireContext())
        rootView.rvChatting.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = chatAdapter
        }
    }

    private fun readMess() {
        val chatList: MutableList<MessageModel> = mutableListOf()
        dbReference = FirebaseDatabase.getInstance().getReference(TableKey.MESSAGES.key)

        dbReference?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                chatList.clear()
                for (dataSnap: DataSnapshot in snapshot.children) {
                    val chat = dataSnap.getValue(MessageModel::class.java)
                    if (mConversation?.conversationId.equals(chat?.conversationId, true)) {
                        chat?.let {
                            chatList.add(it)
                        }
                    }
                }
                chatList.sortBy {
                    it.createAt
                }
                chatAdapter?.setList(chatList)
                rootView.rvChatting.scrollToPosition(chatList.size.minus(1))
                rootView.edtMess.apply {
                    setText("")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                chatList.clear()
            }

        })
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setListeners() {
        val currentIdUser = BaseAccessToken.accessToken
        with(rootView) {
            layoutHeader.imgBack.clickWithDebounce {
                activity?.supportFragmentManager?.popBackStack()
            }
            btnSend.clickWithDebounce {
                val mMess = edtMess.text.toString().trim()
                if (currentIdUser.isEmpty()) {
                    activity?.showActivity<LoginActivity>(goRoot = true)
                    return@clickWithDebounce
                } else {
                    if (mMess.isEmpty())
                        return@clickWithDebounce
                    else sendMessage(
                        senderId = currentIdUser,
                        mess = mMess
                    )
                }
            }
            frChat.setOnTouchListener { view, motionEvent ->
                if ((motionEvent.action == MotionEvent.ACTION_UP))
                    if (Utils.shared.checkKeyboardVisible()) {
                        Utils.shared.showHideKeyBoard(requireActivity(), false)
                    }
                return@setOnTouchListener true
            }
        }
    }

    private fun sendMessage(senderId: String, mess: String) {
        val ref = FirebaseDatabase.getInstance().reference
        val newId = Utils.shared.autoGenerateId()
        val currentTime = Utils.shared.getDateTimeNow()
        val hashMap: HashMap<String, String> = HashMap()
        hashMap[MessageKey.MESSAGE_ID.key] = newId
        hashMap[MessageKey.MESSAGE.key] = mess
        hashMap[MessageKey.USER_ID.key] = senderId
        hashMap[MessageKey.CONVERSATION_ID.key] = mConversation?.conversationId ?: ""
        hashMap[MessageKey.CREATE_AT.key] = currentTime
        ref.child(TableKey.MESSAGES.key).child(newId).setValue(hashMap).addOnCompleteListener {
            if (it.isSuccessful) {

            }
        }
    }

    override fun getViewBinding(inflater: LayoutInflater): ActivityChatBinding {
        return ActivityChatBinding.inflate(inflater)
    }
}