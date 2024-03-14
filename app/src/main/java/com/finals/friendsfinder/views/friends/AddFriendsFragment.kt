package com.finals.friendsfinder.views.friends

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.PermissionUtils
import com.finals.friendsfinder.R
import com.finals.friendsfinder.bases.BaseFragment
import com.finals.friendsfinder.databinding.FragmentAddFriendsBinding
import com.finals.friendsfinder.dialogs.NotifyDialog
import com.finals.friendsfinder.models.BaseAccessToken
import com.finals.friendsfinder.utilities.Utils
import com.finals.friendsfinder.utilities.clickWithDebounce
import com.finals.friendsfinder.utilities.commons.Constants
import com.finals.friendsfinder.utilities.commons.ConversationKey
import com.finals.friendsfinder.utilities.commons.FriendKey
import com.finals.friendsfinder.utilities.commons.ParticipantKey
import com.finals.friendsfinder.utilities.commons.TableKey
import com.finals.friendsfinder.views.chatting.data.ConversationModel
import com.finals.friendsfinder.views.chatting.data.ParticipantModel
import com.finals.friendsfinder.views.friends.adapter.AddFriendsAdapter
import com.finals.friendsfinder.views.friends.data.Friends
import com.finals.friendsfinder.views.friends.data.UserDTO
import com.finals.friendsfinder.views.friends.data.UserInfo
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class AddFriendsFragment : BaseFragment<FragmentAddFriendsBinding>() {

    companion object {
        fun newInstance(): AddFriendsFragment {
            val arg = Bundle()
            return AddFriendsFragment().apply {
                arguments = arg
            }
        }
    }

    private var addFriendAdapter: AddFriendsAdapter? = null
    private var currentListUser: MutableList<UserInfo>? = null
    private var currentListFriend: MutableList<Friends>? = null
    private lateinit var navListView: List<View>
    private lateinit var navListText: List<TextView>
    private lateinit var navListLn: List<View>
    private var listContact = listOf<UserDTO>()
    private var listConversation: MutableList<ConversationModel>? = null
    private var listParticipant: MutableList<ParticipantModel>? = null
    //private var listMyConversation: MutableList<ConversationModel>? = null

    override fun observeHandle() {
        super.observeHandle()
        currentListUser = mutableListOf()
        currentListFriend = mutableListOf()
        listConversation = mutableListOf()
        listParticipant = mutableListOf()
        //listMyConversation = mutableListOf()
    }

    override fun bindData() {
        super.bindData()
        setText()
        setAdapter()
        getListUser()
        setListener()
    }

    override fun setupView() {
        super.setupView()
        navListLn = listOf(rootView.tabSuggest, rootView.tabPending, rootView.tabFriends)
        navListView = listOf(rootView.viewSuggest, rootView.viewPending, rootView.viewFriends)
        navListText = listOf(rootView.tvSuggest, rootView.tvPending, rootView.tvFriends)
        if (checkPermission()) {
            listContact = getNamePhoneDetails()
        } else {
            showMessage(
                title = "Confirm",
                message = "Do you want to make friends with the friends in your contacts?",
                txtBtnOk = "Yes",
                enableCancel = true,
                listener = object : NotifyDialog.OnDialogListener {
                    override fun onClickButton(isOk: Boolean) {
                        if (isOk) {
                            checkPermission {
                                listContact = getNamePhoneDetails()
                            }
                        }
                    }
                })
        }
    }

    private fun checkPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            requireContext(), Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun setListener() {
        with(rootView) {
            layoutHeader.imgBack.clickWithDebounce {
                activity?.supportFragmentManager?.popBackStack()
            }
        }
    }

    private fun setText() {
        with(rootView) {
            layoutHeader.tvMessage.text = "Find Friends"
        }
    }

    private fun getListUser() {
        val currentUserId = BaseAccessToken.accessToken
        val db = FirebaseDatabase.getInstance()
        val dbReference = db.getReference(TableKey.USERS.key)
        val dbReference2 = db.getReference(TableKey.FRIENDS.key)
        val dbReference3 = db.getReference(TableKey.CONVERSATION.key)
        val dbReference4 = db.getReference(TableKey.PARTICIPANTS.key)

        dbReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                currentListUser?.clear()
                for (dataSnap: DataSnapshot in snapshot.children) {
                    val user = dataSnap.getValue(UserInfo::class.java)
                    //check not me
                    if (!user?.userId.equals(currentUserId)) {
                        currentListUser?.add(user!!)
                    }
                }
                //addFriendAdapter?.setList(currentListUser ?: listOf())
                checkSetList()
            }

            override fun onCancelled(error: DatabaseError) {
                currentListUser?.clear()
            }

        })
        dbReference2.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                currentListFriend?.clear()
                for (dataSnap: DataSnapshot in snapshot.children) {
                    val friend = dataSnap.getValue(Friends::class.java)
                    currentListFriend?.add(friend!!)
//                    Log.d("TAG", "addValueEventListener: $dataSnap ..... $user")
                }
                //addFriendAdapter?.setListFriend(currentListFriend ?: listOf())
                checkSetList()
            }

            override fun onCancelled(error: DatabaseError) {
                currentListFriend?.clear()
            }

        })
        dbReference3.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listConversation?.clear()
                for (dataSnap: DataSnapshot in snapshot.children) {
                    val conv = dataSnap.getValue(ConversationModel::class.java)
                    conv?.let { listConversation?.add(it) }
                }
                //checkCurrentConversation()
            }

            override fun onCancelled(error: DatabaseError) {
                listConversation?.clear()
            }

        })

        dbReference4.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listParticipant?.clear()
                for (dataSnap: DataSnapshot in snapshot.children) {
                    val conv = dataSnap.getValue(ParticipantModel::class.java)
                    conv?.let { listParticipant?.add(it) }
                }
                //checkCurrentConversation()
            }

            override fun onCancelled(error: DatabaseError) {
                listParticipant?.clear()
            }

        })
    }

    private var endList = listOf<UserDTO>()
    private fun checkSetList() {
        val currentUser = Utils.shared.getUser()
        val mListUserDTO: MutableList<UserDTO> = mutableListOf()
        if (currentListUser.isNullOrEmpty() && currentListFriend.isNullOrEmpty()) {

        } else if (currentListUser.isNullOrEmpty() && !currentListFriend.isNullOrEmpty()) {

        } else if (!currentListUser.isNullOrEmpty() && currentListFriend.isNullOrEmpty()) {
            currentListUser?.map { user ->
                val userDTO = UserDTO()
                userDTO.friendId = ""
                userDTO.receiverBlocking = ""
                userDTO.userId = ""
                userDTO.friend = ""
                userDTO.userBlocking = ""
                userDTO.avatar = user.avatar
                userDTO.userName = user.userName
                userDTO.receiverId = user.userId
                userDTO.phone = user.phoneNumber
                userDTO.typeClick = 0
                mListUserDTO.add(userDTO)
            }
        } else {
            currentListUser?.map { user ->
                currentListFriend?.map { friend ->
                    var checkList = mListUserDTO.filter {
                        it.phone == user.phoneNumber
                    }
                    if (checkList.isNotEmpty()) {
                        for (i in checkList.indices) {
                            mListUserDTO.remove(checkList[i])
                        }
                        checkList = listOf()
                    }
                    val userDTO = UserDTO()
                    if (user.userId == friend.userId && currentUser?.userId == friend.receiverId) {
                        userDTO.friendId = friend.friendId
                        userDTO.receiverBlocking = friend.receiverBlocking
                        userDTO.userId = friend.userId
                        userDTO.friend = friend.friend
                        userDTO.userBlocking = friend.userBlocking
                        userDTO.avatar = user.avatar
                        userDTO.userName = user.userName
                        userDTO.receiverId = friend.receiverId
                        userDTO.typeClick = 1
                        userDTO.email = user.email
                        userDTO.phone = user.phoneNumber
                        mListUserDTO.add(userDTO)
                    } else if (user.userId == friend.receiverId && currentUser?.userId == friend.userId) {
                        userDTO.friendId = friend.friendId
                        userDTO.receiverBlocking = friend.receiverBlocking
                        userDTO.userId = friend.userId
                        userDTO.friend = friend.friend
                        userDTO.userBlocking = friend.userBlocking
                        userDTO.avatar = user.avatar
                        userDTO.userName = user.userName
                        userDTO.receiverId = friend.receiverId
                        userDTO.typeClick = 1
                        userDTO.email = user.email
                        userDTO.phone = user.phoneNumber
                        mListUserDTO.add(userDTO)
                    } else {
                        userDTO.friendId = ""
                        userDTO.receiverBlocking = ""
                        userDTO.userId = ""
                        userDTO.friend = ""
                        userDTO.userBlocking = ""
                        userDTO.avatar = user.avatar
                        userDTO.userName = user.userName
                        userDTO.receiverId = user.userId
                        userDTO.typeClick = 0
                        userDTO.email = user.email
                        userDTO.phone = user.phoneNumber
                        mListUserDTO.add(userDTO)
                    }
                }
            }
        }
//
//        val finalList: MutableList<UserDTO> = mutableListOf()
//        finalList.addAll(mList)
//        finalList.addAll(mListFrNull)
        endList = mListUserDTO
//        endList = finalList.distinctBy {
//            it.phone
//        }
//        addFriendAdapter?.setList(endList)
        setUpTab(0)
    }

    private fun setAdapter() {
        val userLocal = Utils.shared.getUser()
        addFriendAdapter = AddFriendsAdapter(requireContext(), userLocal, onClickItem = {

        })
        addFriendAdapter?.addFriend = { info, type ->
            //updateFriend(info)
            when (type) {
                1 -> {
                    // user is sender
                }

                2 -> {
                    // user is receiver
                    showMessage(
                        title = "Confirm",
                        message = "Are you sure you want to accept this friend request?",
                        txtBtnOk = "Yes",
                        enableCancel = true,
                        listener = object : NotifyDialog.OnDialogListener {
                            override fun onClickButton(isOk: Boolean) {
                                if (isOk) {
                                    updateFriend(info)
                                }
                            }
                        })
                }

                else -> {
                    showMessage(
                        title = "Confirm",
                        message = "Are you sure you want to be friends with this person?",
                        txtBtnOk = "Yes",
                        enableCancel = true,
                        listener = object : NotifyDialog.OnDialogListener {
                            override fun onClickButton(isOk: Boolean) {
                                if (isOk) {
                                    createFriend(info)
                                }
                            }
                        })
                }

            }
        }
        addFriendAdapter?.removeFriend = { info, type ->
            if (type == 1) {
                showMessage(
                    title = "Confirm",
                    message = "Are you sure you want to cancel this friend request?",
                    txtBtnOk = "Yes",
                    enableCancel = true,
                    listener = object : NotifyDialog.OnDialogListener {
                        override fun onClickButton(isOk: Boolean) {
                            if (isOk) {
                                removeFriend(info)
                            }
                        }
                    })
            } else {
                showMessage(
                    title = "Confirm",
                    message = "We'll delete your personal chat with this person after you unfriend, are you sure you want to do this?",
                    txtBtnOk = "Yes",
                    enableCancel = true,
                    listener = object : NotifyDialog.OnDialogListener {
                        override fun onClickButton(isOk: Boolean) {
                            if (isOk) {
                                removeFriend(info)
                            }
                        }
                    })
            }

        }
        addFriendAdapter?.shareApp = { info, _ ->
            val currentUser = Utils.shared.getUser()
            showMessage(
                title = "Confirm",
                message = "You will share the application via phone messages, do you agree?",
                txtBtnOk = "Yes",
                enableCancel = true,
                listener = object : NotifyDialog.OnDialogListener {
                    override fun onClickButton(isOk: Boolean) {
                        if (isOk) {
                            val uri = Uri.parse("smsto:${info.phone}")
                            val intent = Intent(Intent.ACTION_SENDTO, uri)
                            intent.putExtra(
                                "sms_body",
                                "${currentUser?.userName} invite you to participate in using the application Friends Finder"
                            )
                            startActivity(intent)
                        }
                    }
                })
        }
        rootView.rvListUser.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = addFriendAdapter
        }

    }

    private fun removeFriend(info: UserDTO) {
        val db = FirebaseDatabase.getInstance()
        val dbReference = db.getReference(TableKey.FRIENDS.key)
        val dbReference2 = db.getReference(TableKey.CONVERSATION.key)
        dbReference.child(info.friendId).removeValue().addOnCompleteListener {
            if (it.isSuccessful) {
                val firstId = info.userId
                val secondId = info.receiverId
                val listFilter: MutableList<ConversationModel> = mutableListOf()
                listConversation?.forEachIndexed { index, conversationModel ->
                    listParticipant?.forEachIndexed { index, participantModel ->
                        if (conversationModel.conversationId.equals(
                                participantModel.conversationId,
                                true
                            )
                        ) {
                            if ((conversationModel.creatorId.equals(
                                    firstId,
                                    true
                                )) && (participantModel.userId.equals(secondId, true)) ||
                                (conversationModel.creatorId.equals(
                                    secondId,
                                    true
                                )) && (participantModel.userId.equals(firstId, true))
                            ) {
                                listFilter.add(conversationModel)
                            }
                        }

                    }
                }
                val newList = listFilter.filter {
                    it.typeGroup != Constants.TYPE_GROUP
                }
                if (newList.isEmpty())
                    return@addOnCompleteListener
                else {
                    val idConv = newList.first().conversationId ?: ""
                    dbReference2.child(idConv).removeValue()
                }
            }
        }
    }

    private fun updateFriend(info: UserDTO) {
        val db = FirebaseDatabase.getInstance()
        val dbReference = db.getReference(TableKey.FRIENDS.key)
        //val localInfo = Utils.shared.getUser()
        //val keyId = Utils.shared.autoGenerateId()
        val hasMap: HashMap<String, String> = HashMap()
        hasMap[FriendKey.FRIEND_ID.key] = info.friendId
        hasMap[FriendKey.USERID.key] = info.userId
        hasMap[FriendKey.RECEIVER_ID.key] = info.receiverId
        when (info.friend) {
            "0" -> {
                //
            }

            "1" -> {
                // show popup confirm
                hasMap[FriendKey.IS_FRIEND.key] = "2"
            }

            "2" -> {
                // show popup confirm
            }

            else -> {

            }
        }
        hasMap[FriendKey.USER_BLOCKING.key] = info.userBlocking
        hasMap[FriendKey.RECEIVER_BLOCKING.key] = info.receiverBlocking
        hasMap[FriendKey.CREATE_AT.key] = ""

        dbReference.child(info.friendId).setValue(hasMap).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                createConversation(info.userId)
            }
        }
    }

    private fun createConversation(userIdFriend: String) {
        val userInfo = currentListUser?.filter {
            it.userId == userIdFriend
        }
        val db = FirebaseDatabase.getInstance()
        val dbReference = db.getReference(TableKey.CONVERSATION.key)
        val keyId = Utils.shared.autoGenerateId()
        val hasMap: HashMap<String, String> = HashMap()
        val currentTime = Utils.shared.getDateTimeNow()
        hasMap[ConversationKey.CONVERSATION_ID.key] = keyId
        hasMap[ConversationKey.CONVERSATION_NAME.key] = userInfo?.get(0)?.userName ?: "AnyUser"
        hasMap[ConversationKey.CREATOR_ID.key] = BaseAccessToken.accessToken
        hasMap[ConversationKey.CREATE_AT.key] = currentTime

        dbReference.child(keyId).setValue(hasMap).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                createParticipant(keyId, userInfo?.get(0)?.userId ?: "")
            }
        }
    }

    private fun createParticipant(keyConversationId: String, idUserReceiver: String) {
        val db = FirebaseDatabase.getInstance()
        val dbReference = db.getReference(TableKey.PARTICIPANTS.key)
        val keyId = Utils.shared.autoGenerateId()
        val hasMap: HashMap<String, String> = HashMap()
        hasMap[ParticipantKey.CONVERSATION_ID.key] = keyConversationId
        hasMap[ParticipantKey.PARTICIPANT_ID.key] = keyId
        hasMap[ParticipantKey.USERID.key] = idUserReceiver

        dbReference.child(keyId).setValue(hasMap).addOnCompleteListener { task ->
            if (task.isSuccessful) {
            }
        }
    }

    private fun createFriend(info: UserDTO) {
        val db = FirebaseDatabase.getInstance()
        val dbReference = db.getReference(TableKey.FRIENDS.key)
        val localInfo = Utils.shared.getUser()
        val keyId = Utils.shared.autoGenerateId()
        val hasMap: HashMap<String, String> = HashMap()
        hasMap[FriendKey.FRIEND_ID.key] = keyId
        hasMap[FriendKey.USERID.key] = localInfo?.userId ?: ""
        hasMap[FriendKey.RECEIVER_ID.key] = info.receiverId
        hasMap[FriendKey.IS_FRIEND.key] = "1"
        hasMap[FriendKey.USER_BLOCKING.key] = "0"
        hasMap[FriendKey.RECEIVER_BLOCKING.key] = "0"
        hasMap[FriendKey.CREATE_AT.key] = ""

        dbReference.child(keyId).setValue(hasMap).addOnCompleteListener { task ->
            if (task.isSuccessful) {

            }
        }
    }

    override fun setupEventControl() {
        super.setupEventControl()
        navListLn.forEachIndexed { index, view ->
            view.clickWithDebounce {
                navListView.forEach { v ->
                    v.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
                }
                navListView[index].setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.black
                    )
                )
                //text
                navListText.forEach { v ->
                    v.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_6F6D6D))
                }
                navListText[index].setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.black
                    )
                )
                setUpTab(index)
            }
        }
    }

    private fun setUpTab(index: Int) {
        Log.d("Tab", " $index")
        var listResult = mutableListOf<UserDTO>()
        when (index) {
            0 -> {
                // suggest
                endList.forEach { userDTO ->
                    listContact.forEach { contact ->
                        if (userDTO.friend != "1" && userDTO.friend != "2") {
                            if (userDTO.phone == contact.phone) {
                                var checkList = listResult.filter {
                                    it.phone == contact.phone
                                }
                                if (checkList.isNotEmpty()) {
                                    for (i in checkList.indices) {
                                        listResult.remove(checkList[i])
                                    }
                                    checkList = listOf()
                                }
                                listResult.add(userDTO)
                            } else {
                                var checkList = listResult.filter {
                                    it.phone == contact.phone
                                }
                                if (checkList.isNotEmpty()) {
                                    for (i in checkList.indices) {
                                        listResult.remove(checkList[i])
                                    }
                                    checkList = listOf()
                                }
                                contact.friend = "4"
                                listResult.add(contact)
                            }
                        } else {
//                            if (userDTO.phone == contact.phone) {
//                                listResult.remove(contact)
//                            }
//                            else{
//                                val check = listResult.filter {
//                                    it.phone == contact.phone
//                                }
//                                if (check.isNotEmpty())
//                                    return@forEach
//                                contact.friend = "4"
//                                listResult.add(contact)
//                            }
                        }
                    }
                }
                //var listCheck: MutableList<UserInfo> = mutableListOf()
                endList.forEach { userDTO ->
                    listResult.forEach { res ->
                        if (userDTO.friend == "1" || userDTO.friend == "2") {
                            if (userDTO.phone == res.phone) {
                                listResult.remove(res)
                            }
                        }
                    }
                }
            }

            1 -> {
                //pending
                listResult = endList.filter { it.friend == "1" }.toMutableList()
            }

            else -> {
                //friends
                listResult = endList.filter { it.friend == "2" }.toMutableList()
            }
        }
        rootView.tvNoData.isVisible = listResult.isEmpty()
        rootView.rvListUser.isVisible = listResult.isNotEmpty()
        addFriendAdapter?.setList(listResult)
    }

    private fun checkPermission(onSuccess: (() -> Unit)) {
        PermissionUtils.permission(*Constants.CONTACT_PER)
            .callback(object : PermissionUtils.FullCallback {
                override fun onGranted(granted: MutableList<String>) {
                    onSuccess.invoke()
                }

                override fun onDenied(
                    deniedForever: MutableList<String>,
                    denied: MutableList<String>,
                ) {
                    //showMessage("Please allow permission Location")
                }
            }).request()

    }

    @SuppressLint("Range")
    fun getNamePhoneDetails(): List<UserDTO> {
        val names = mutableListOf<UserDTO>()
        val cr = activity?.contentResolver
        val cur = cr?.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
            null, null, null
        )
        if ((cur?.count ?: 0) > 0) {
            while (cur?.moveToNext() == true) {
                val id =
                    cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NAME_RAW_CONTACT_ID))
                        ?: ""
                val name =
                    cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                        ?: ""
                val number =
                    (cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                        ?: "").replace(" ", "")
                names.add(UserDTO(userName = name, phone = number))
            }
        }
        val newList = names.distinctBy {
            it.phone
        }
        return newList
    }

    override fun getViewBinding(inflater: LayoutInflater): FragmentAddFriendsBinding {
        return FragmentAddFriendsBinding.inflate(inflater)
    }
}