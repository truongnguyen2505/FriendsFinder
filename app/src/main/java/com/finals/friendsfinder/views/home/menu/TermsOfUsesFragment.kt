package com.finals.friendsfinder.views.home.menu

import android.os.Bundle
import android.view.LayoutInflater
import com.finals.friendsfinder.bases.BaseFragment
import com.finals.friendsfinder.databinding.FragmentPrivacyPolicyBinding
import com.finals.friendsfinder.utilities.clickWithDebounce
import com.finals.friendsfinder.views.home.data.PrivacyPolicyModel
import com.finals.friendsfinder.views.home.data.TermsOfUsesModel

class TermsOfUsesFragment: BaseFragment<FragmentPrivacyPolicyBinding>() {

    companion object {
        fun newInstance(): TermsOfUsesFragment {
            val arg = Bundle()
            return TermsOfUsesFragment().apply {
                arguments = arg
            }
        }
    }

    override fun setupView() {
        super.setupView()
        val termsOfUsesModel = TermsOfUsesModel()
        rootView.apply {
            tvPolicy.text = termsOfUsesModel.terms
            layoutHeader.tvMessage.text = "Terms of Uses"
            layoutHeader.imgBack.clickWithDebounce {
                activity?.supportFragmentManager?.popBackStack()
            }
        }

    }

    override fun getViewBinding(inflater: LayoutInflater): FragmentPrivacyPolicyBinding {
        return FragmentPrivacyPolicyBinding.inflate(inflater)
    }
}