package com.finals.friendsfinder.views.chatting

import com.finals.friendsfinder.bases.BaseActivity
import com.finals.friendsfinder.databinding.ActivityChatBinding
import com.finals.friendsfinder.utilities.clickWithDebounce
import com.finals.friendsfinder.utilities.commons.ChatKey
import com.finals.friendsfinder.utilities.commons.SignupKey
import com.finals.friendsfinder.utilities.showActivity
import com.finals.friendsfinder.views.friends.data.UserInfo
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
    override fun observeHandle() {
        super.observeHandle()
        getArg()
    }

    override fun setupEventControl() {
        super.setupEventControl()
        setupDB()
        setListeners()
    }

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
        }
    }

    private fun setupDB() {
        fbUser = FirebaseAuth.getInstance().currentUser
        dbReference = FirebaseDatabase.getInstance().getReference("Users").child(fbUser?.uid ?: "")

        dbReference?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(UserInfo::class.java)
//                if (user?.imageProfile.isNullOrEmpty())
//                    rootVie
//
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun sendMessage(senderId: String, receiverId: String, mess: String) {
        val ref = FirebaseDatabase.getInstance().reference
        val hashMap: HashMap<String, String> = HashMap()
        hashMap[ChatKey.RECEIVER_ID.key] = receiverId
        hashMap[ChatKey.SENDER_ID.key] = senderId
        hashMap[ChatKey.MESSAGE.key] = mess
        ref.child("Chat").push().setValue(hashMap)
    }

    private fun getArg() {
        intent?.let {
            mUserId = it.getStringExtra(SignupKey.USERID.key) as String
            mUserName = it.getStringExtra(SignupKey.USERNAME.key) as String
        }
    }

    override fun getViewBinding(): ActivityChatBinding {
        return ActivityChatBinding.inflate(layoutInflater)
    }
}