package com.mcards.sdk.accounts.demo

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG
import com.google.android.material.snackbar.Snackbar
import com.mcards.sdk.accounts.AccountsSdk
import com.mcards.sdk.accounts.AccountsSdkProvider
import com.mcards.sdk.accounts.demo.databinding.FragmentDemoBinding
import com.mcards.sdk.accounts.model.FundingSource
import com.mcards.sdk.auth.AuthSdk
import com.mcards.sdk.auth.AuthSdkProvider
import com.mcards.sdk.auth.model.auth.User
import com.mcards.sdk.core.model.AuthTokens
import com.mcards.sdk.core.network.SdkResult
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.Disposable

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class DemoFragment : Fragment() {

    private var _binding: FragmentDemoBinding? = null
    private val binding get() = _binding!!

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
                activity?.runOnUiThread {
                    Snackbar.make(view, message, Snackbar.LENGTH_LONG).show()
                }
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
    }

    @SuppressLint("CheckResult")
    private fun initAccountsSdk() {
        val accountsSdk = AccountsSdkProvider.getInstance()

        accountsSdk.init(
            requireContext(),
            accessToken,
            debug = true,
            useFirebase =  false,
            object : AccountsSdk.InvalidTokenCallback {
                override fun onTokenInvalid(): String {
                    return AuthSdkProvider.getInstance().refreshTokens().accessToken
                }
            })

        accountsSdk.getFundingSources()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : SingleObserver<SdkResult<Array<FundingSource>>> {
                override fun onSubscribe(d: Disposable) {
                    activity?.runOnUiThread {
                        binding.progressbar.visibility = View.VISIBLE
                    }
                }

                override fun onError(e: Throwable) {
                    activity?.runOnUiThread {
                        binding.progressbar.visibility = View.GONE
                        Snackbar.make(requireView(), e.message!!, LENGTH_LONG).show()
                    }
                }

                override fun onSuccess(t: SdkResult<Array<FundingSource>>) {
                    activity?.runOnUiThread {
                        binding.progressbar.visibility = View.GONE
                    }

                    t.result?.let {
                        if (it.isNotEmpty()) {
                            val account = it[0]
                        }
                    } ?: t.errorMsg?.let {
                        activity?.runOnUiThread {
                            Snackbar.make(requireView(), it, LENGTH_LONG).show()
                        }
                    }
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
