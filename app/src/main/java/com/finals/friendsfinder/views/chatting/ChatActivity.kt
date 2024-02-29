package com.finals.friendsfinder.views.chatting

import android.annotation.SuppressLint
import android.view.MotionEvent
import androidx.recyclerview.widget.LinearLayoutManager
import com.finals.friendsfinder.bases.BaseActivity
import com.finals.friendsfinder.databinding.ActivityChatBinding
import com.finals.friendsfinder.utilities.Utils
import com.finals.friendsfinder.utilities.clickWithDebounce
import com.finals.friendsfinder.utilities.showActivity
import com.finals.friendsfinder.views.chatting.adapter.ChatAdapter
import com.finals.friendsfinder.views.chatting.data.ChatModel
import com.finals.friendsfinder.views.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChatActivity : BaseActivity<ActivityChatBinding>() {

    companion object {

    }

    private var fbUser: FirebaseUser? = null
    private var dbReference: DatabaseReference? = null
    private var mUserId: String = ""
    private var mUserName: String = ""
    private var chatAdapter: ChatAdapter? = null
    override fun observeHandle() {
        super.observeHandle()
        getArg()
    }

    override fun setupEventControl() {
        super.setupEventControl()
        setupDB()
        setListeners()
        setAdapter()
        readMess(fbUser?.uid ?: "", mUserId)
    }

    private fun setAdapter() {
        chatAdapter = ChatAdapter(this@ChatActivity)
        rootView.rvChatting.apply {
            layoutManager =
                LinearLayoutManager(this@ChatActivity, LinearLayoutManager.VERTICAL, false)
            adapter = chatAdapter
        }
    }

    private fun readMess(senderId: String, receiverId: String) {
        val chatList: MutableList<ChatModel> = mutableListOf()
        dbReference = FirebaseDatabase.getInstance().getReference("Chat")

        dbReference?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                chatList.clear()
                for (dataSnap: DataSnapshot in snapshot.children) {
                    val chat = dataSnap.getValue(ChatModel::class.java)
                    if (chat?.senderId.equals(senderId) && chat?.receiverId.equals(receiverId) || chat?.senderId.equals(
                            receiverId
                        ) && chat?.receiverId.equals(senderId)
                    ) {
                        rootView.rvChatting.apply {
                            layoutManager =
                                LinearLayoutManager(
                                    this@ChatActivity,
                                    LinearLayoutManager.VERTICAL,
                                    false
                                )
                            adapter = chatAdapter
                        }
                        chat?.let { chatList.add(it) }
                    }
                }
                chatAdapter?.setList(chatList)
                rootView.rvChatting.scrollToPosition(chatList.size.minus(1))
                rootView.edtMess.apply {
                    setText("")
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setListeners() {
        with(rootView) {
            layoutHeader.imgBack.clickWithDebounce {
                onBackPressedDispatcher.onBackPressed()
            }
            layoutHeader.tvMessage.text = mUserName
            btnSend.clickWithDebounce {
                val mMess = edtMess.text.toString().trim()
                if (fbUser?.uid.isNullOrEmpty())
                    showActivity<LoginActivity>(goRoot = true)
                else {
                    if (mMess.isEmpty())
                        return@clickWithDebounce
                    else sendMessage(
                        senderId = fbUser?.uid ?: "",
                        receiverId = mUserId,
                        mess = mMess
                    )
                }
            }
            frChat.setOnTouchListener { view, motionEvent ->
                if ((motionEvent.action == MotionEvent.ACTION_UP))
                    if (Utils.shared.checkKeyboardVisible()) {
                        Utils.shared.showHideKeyBoard(this@ChatActivity, false)
                    }
                return@setOnTouchListener true
            }
        }
    }

    private fun setupDB() {
        fbUser = FirebaseAuth.getInstance().currentUser
    }

    private fun sendMessage(senderId: String, receiverId: String, mess: String) {
        val ref = FirebaseDatabase.getInstance().reference
        val hashMap: HashMap<String, String> = HashMap()
//        hashMap[ChatKey.RECEIVER_ID.key] = receiverId
//        hashMap[ChatKey.SENDER_ID.key] = senderId
//        hashMap[ChatKey.MESSAGE.key] = mess
        ref.child("Chat").push().setValue(hashMap)
    }

    private fun getArg() {
        intent?.let {
//            mUserId = it.getStringExtra(SignupKey.USERID.key) as String
//            mUserName = it.getStringExtra(SignupKey.USERNAME.key) as String
        }
    }

    override fun getViewBinding(): ActivityChatBinding {
        return ActivityChatBinding.inflate(layoutInflater)
    }
}