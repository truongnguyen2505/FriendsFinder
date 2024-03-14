package com.finals.friendsfinder.views.home.menu

import android.os.Bundle
import android.view.LayoutInflater
import com.finals.friendsfinder.bases.BaseFragment
import com.finals.friendsfinder.databinding.FragmentPrivacyPolicyBinding
import com.finals.friendsfinder.utilities.clickWithDebounce
import com.finals.friendsfinder.views.home.data.PrivacyPolicyModel

class PrivacyPolicyFragment : BaseFragment<FragmentPrivacyPolicyBinding>() {

    companion object {
        fun newInstance(): PrivacyPolicyFragment {
            val arg = Bundle()
            return PrivacyPolicyFragment().apply {
                arguments = arg
            }
        }
    }

    override fun setupView() {
        super.setupView()
        val privacyPolicyModel = PrivacyPolicyModel()
        rootView.apply {
            tvPolicy.text = privacyPolicyModel.policy
            layoutHeader.tvMessage.text = "Privacy Policy"
            layoutHeader.imgBack.clickWithDebounce {
                activity?.supportFragmentManager?.popBackStack()
            }
        }

    }

    override fun getViewBinding(inflater: LayoutInflater): FragmentPrivacyPolicyBinding {
        return FragmentPrivacyPolicyBinding.inflate(inflater)
    }
}