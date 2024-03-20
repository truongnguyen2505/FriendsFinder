package com.finals.friendsfinder.views.chatting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.finals.friendsfinder.bases.BaseFragment
import com.finals.friendsfinder.databinding.FragmentCreateGroupBinding
import com.finals.friendsfinder.dialogs.NotifyDialog
import com.finals.friendsfinder.models.BaseAccessToken
import com.finals.friendsfinder.utilities.Utils
import com.finals.friendsfinder.utilities.clickWithDebounce
import com.finals.friendsfinder.utilities.commons.Constants
import com.finals.friendsfinder.utilities.commons.ConversationKey
import com.finals.friendsfinder.utilities.commons.ParticipantKey
import com.finals.friendsfinder.utilities.commons.TableKey
import com.finals.friendsfinder.views.chatting.adapter.CreateGroupAdapter
import com.finals.friendsfinder.views.chatting.adapter.SelectGroupAdapter
import com.finals.friendsfinder.views.friends.data.UserDTO
import com.finals.friendsfinder.views.friends.data.UserInfo
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.firebase.database.FirebaseDatabase

class CreateGroupFragment : BaseFragment<FragmentCreateGroupBinding>() {


    companion object {
        private const val TAG = "CreateGroupFragment"
        private const val USER_LIST = "USER_LIST"
        fun newInstance(listUser: ArrayList<UserInfo?>): CreateGroupFragment {
            val arg = Bundle().apply {
                putParcelableArrayList(USER_LIST, listUser)
            }
            return CreateGroupFragment().apply {
                arguments = arg
            }
        }
    }

    private var listUser: MutableList<UserInfo> = mutableListOf()
    private var searchByNameAdapter: CreateGroupAdapter? = null
    private var selectGroupAdapter: SelectGroupAdapter? = null
    private var listUserSelected: MutableList<UserInfo>? = mutableListOf()

    override fun observeHandle() {
        super.observeHandle()
        getArg()
    }

    private fun getArg() {
        arguments?.let {
            listUser = it.getParcelableArrayList(USER_LIST) ?: arrayListOf()
        }
    }

    override fun setupView() {
        super.setupView()
        setText()
        setButtonCreate(false)
        setAdapter()
        setGroupAdapter()
    }

    private fun setButtonCreate(b: Boolean) {
        if (b) {
            rootView.btnCreate.apply {
                isEnabled = b
                alpha = 1f
            }
        } else {
            rootView.btnCreate.apply {
                isEnabled = b
                alpha = 0.5f
            }
        }
    }

    private fun setGroupAdapter() {
        selectGroupAdapter = SelectGroupAdapter(requireContext())
        setFlexLayout(rootView.rvParticipant)
    }

    private fun setText() {
        with(rootView) {
            edtNameGroup.setLabel("Name group")
            edtNameGroup.setHint("Input name group")
        }
    }

    override fun bindData() {
        super.bindData()
        with(rootView) {
            edtSearch.apply {
                addTextChangedListener {
                    val txt = it.toString().trim()
                    if (txt.isEmpty()) {
                        rvSearch.isVisible = false
                        searchByNameAdapter?.clearList()
                        return@addTextChangedListener
                    } else {
                        val listSearch = listUser.filter {
                            it.userName.contains(txt)
                        }
                        rvSearch.isVisible = listSearch.isNotEmpty()
                        val listUserDTO: MutableList<UserDTO> = mutableListOf()
                        listSearch.forEach { info ->
                            val userDTO = UserDTO()
                            userDTO.userName = info.userName
                            userDTO.userId = info.userId
                            userDTO.phone = info.phoneNumber
                            listUserDTO.add(userDTO)
                        }
                        listUserDTO.forEach {dto ->
                            listUserSelected?.forEach { us->
                                if (dto.userId.equals(us.userId, true)){
                                    dto.isSelected = true
                                }
                            }
                        }
                        searchByNameAdapter?.setList(listUserDTO)
                    }
                }
            }
            btnBack.clickWithDebounce {
                activity?.supportFragmentManager?.popBackStack()
            }
            edtNameGroup.onTextChangeCallback = {
                if (it.isEmpty()) {
                    edtNameGroup.setMessageError("Name group is not empty!")
                    setButtonCreate(false)
                } else {
                    edtNameGroup.hideMessageError()
                    setButtonCreate(true)
                }
            }
            btnCreate.clickWithDebounce {
                val nameGroup = edtNameGroup.getText()
                if (nameGroup.isEmpty()) {
                    edtNameGroup.setMessageError("Name group is not empty!")
                    return@clickWithDebounce
                } else {
                    // create group
                    val listItemGroup = selectGroupAdapter?.getListSelected()
                    if ((listItemGroup?.size ?: 0) < 2) {
                        showMessage(
                            title = "Notice",
                            message = "Number of participants must be greater than 1!"
                        )
                    } else {
                        createGroup(nameGroup, listItemGroup)
                    }
                }
            }
        }
    }

    private fun createGroup(nameGroup: String, listItemGroup: List<UserInfo>?) {
        val db = FirebaseDatabase.getInstance()
        val dbReference = db.getReference(TableKey.CONVERSATION.key)
        val keyId = Utils.shared.autoGenerateId()
        val hasMap: HashMap<String, String> = HashMap()
        val currentTime = Utils.shared.getDateTimeNow()
        hasMap[ConversationKey.CONVERSATION_ID.key] = keyId
        hasMap[ConversationKey.CONVERSATION_NAME_FOR_CREATOR.key] = nameGroup
        hasMap[ConversationKey.CONVERSATION_NAME_FOR_RECEIVER.key] = nameGroup
        hasMap[ConversationKey.CREATOR_ID.key] = BaseAccessToken.accessToken
        hasMap[ConversationKey.CREATE_AT.key] = currentTime
        hasMap[ConversationKey.TYPE_GROUP.key] = Constants.TYPE_GROUP

        dbReference.child(keyId).setValue(hasMap).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                addParticipants(keyId, listItemGroup)
                showMessage(
                    title = "Successfully!",
                    message = "Create group name \"$nameGroup\" is successful!",
                    false,
                    object : NotifyDialog.OnDialogListener {
                        override fun onClickButton(isOk: Boolean) {
                            if (isOk) {
                                activity?.supportFragmentManager?.popBackStack()
                            }
                        }
                    })
            }
        }
    }

    private fun setFlexLayout(recyclerView: RecyclerView) {
        val layoutManager = FlexboxLayoutManager(requireContext())
        layoutManager.flexDirection = FlexDirection.ROW
        layoutManager.justifyContent = JustifyContent.FLEX_START
        layoutManager.alignItems = AlignItems.CENTER
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = selectGroupAdapter
        recyclerView.visibility = View.VISIBLE
    }

    private fun addParticipants(convId: String, listItemGroup: List<UserInfo>?) {
        val db = FirebaseDatabase.getInstance()
        val dbReference = db.getReference(TableKey.PARTICIPANTS.key)
        val hasMap: HashMap<String, String> = HashMap()
        hasMap[ParticipantKey.CONVERSATION_ID.key] = convId

        for (i in listItemGroup?.indices!!) {
            val keyId = Utils.shared.autoGenerateId()
            hasMap[ParticipantKey.USERID.key] = listItemGroup[i].userId
            hasMap[ParticipantKey.PARTICIPANT_ID.key] = convId
            dbReference.child(keyId).setValue(hasMap)
        }
    }

    private fun setAdapter() {
        searchByNameAdapter = CreateGroupAdapter(requireContext(), onItemClick = {
            val userInfo = UserInfo()
            userInfo.apply {
                userId = it.userId
                userName = it.userName
            }
            if (it.isSelected) {
                listUserSelected?.add(userInfo)
                selectGroupAdapter?.addItem(userInfo)
            } else {
                listUserSelected?.remove(userInfo)
                selectGroupAdapter?.removeItem(userInfo)
            }
            rootView.apply {
                edtSearch.apply {
                    setText("")
                    clearFocus()
                }
                rvSearch.isVisible = false
            }
        })
        rootView.rvSearch.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = searchByNameAdapter
        }
    }

    override fun getViewBinding(inflater: LayoutInflater): FragmentCreateGroupBinding {
        return FragmentCreateGroupBinding.inflate(inflater)
    }
}