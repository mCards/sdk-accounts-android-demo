package com.mcards.sdk.accounts.demo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.snackbar.Snackbar
import com.mcards.sdk.accounts.AccountsSdk
import com.mcards.sdk.accounts.AccountsSdkProvider
import com.mcards.sdk.accounts.demo.databinding.FragmentDemoBinding
import com.mcards.sdk.accounts.ui.AccountsViewModel
import com.mcards.sdk.auth.AuthSdk
import com.mcards.sdk.auth.AuthSdkProvider
import com.mcards.sdk.auth.model.auth.User
import com.mcards.sdk.core.model.AuthTokens

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class DemoFragment : Fragment() {

    private var _binding: FragmentDemoBinding? = null
    private val binding get() = _binding!!
    private val accountsVM: AccountsViewModel by activityViewModels()

    private var userPhoneNumber = ""
    private var accessToken = ""
    private var idToken = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDemoBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val loginCallback = object : AuthSdk.LoginCallback {
            override fun onSuccess(
                user: User,
                tokens: AuthTokens,
                regionChanged: Boolean,
                cardId: String?
            ) {
                accessToken = tokens.accessToken
                idToken = tokens.idToken
                userPhoneNumber = user.userClaim.phoneNumber
                initAccountsSdk()
            }

            override fun onFailure(message: String) {
                Snackbar.make(view, message, Snackbar.LENGTH_LONG).show()
            }
        }

        val authSdk = AuthSdkProvider.getInstance()
        binding.loginBtn.setOnClickListener {
            if (userPhoneNumber.isBlank()) {
                authSdk.login(requireContext(), loginCallback)
            } else {
                authSdk.login(requireContext(), userPhoneNumber, loginCallback)
            }
        }

        requireActivity().runOnUiThread {
            accountsVM.accounts.observe(viewLifecycleOwner) { response ->
                response?.let {
                    //TODO do something with the accounts
                    if (it.isNotEmpty()) {
                        val account = it[0]
                    }
                }
            }
        }
    }

    private fun initAccountsSdk() {
        AccountsSdkProvider.getInstance().init(requireContext(),
            accessToken,
            debug = true,
            useFirebase =  false,
            object : AccountsSdk.InvalidTokenCallback {
                override fun onTokenInvalid(): String {
                    return AuthSdkProvider.getInstance().refreshTokens().accessToken
                }
            })

        accountsVM.requestAccounts()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
