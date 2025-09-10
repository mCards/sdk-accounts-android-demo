package com.mcards.sdk.accounts.demo

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG
import com.google.android.material.snackbar.Snackbar
import com.mcards.sdk.accounts.AccountsSdk
import com.mcards.sdk.accounts.AccountsSdkProvider
import com.mcards.sdk.accounts.demo.databinding.FragmentDemoBinding
import com.mcards.sdk.accounts.model.Account
import com.mcards.sdk.auth.AuthSdk
import com.mcards.sdk.auth.AuthSdkProvider
import com.mcards.sdk.auth.model.auth.User
import com.mcards.sdk.core.model.AuthTokens
import com.mcards.sdk.core.network.model.SdkResult
import com.mcards.sdk.core.util.LoggingCallback
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.Disposable

private const val TEST_PHONE_NUMBER = "4052938132"

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

        val loginCallback = object : AuthSdk.Auth0Callback {
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
                authSdk.auth0Authenticate(requireContext(), TEST_PHONE_NUMBER, loginCallback)
            } else {
                authSdk.auth0Authenticate(requireContext(), userPhoneNumber, loginCallback)
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
            object : AccountsSdk.InvalidTokenCallback {
                override fun onTokenInvalid(): String {
                    return AuthSdkProvider.getInstance().refreshAuth0Tokens().accessToken
                }
            })

        //optional, to use your standard logging methods. Needs to be set on each sdk individually
        accountsSdk.setLoggingCallback(object : LoggingCallback {
            override fun log(t: Throwable) {
                //TODO log exception
            }

            override fun log(msg: String) {
                //TODO log message
            }
        })

        accountsSdk.getAccounts()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : SingleObserver<SdkResult<Array<Account>>> {
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

                override fun onSuccess(t: SdkResult<Array<Account>>) {
                    activity?.runOnUiThread {
                        binding.progressbar.visibility = View.GONE
                    }

                    t.result?.let {
                        activity?.runOnUiThread {
                            MaterialAlertDialogBuilder(requireContext())
                                .setTitle("Success")
                                .setMessage("Success! Fetched " + it.size + " Accounts. Debug" +
                                        " to inspect the data.")
                                .setPositiveButton("Ok") { dialog, _ ->
                                    dialog.dismiss()
                                }.create().show()
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
