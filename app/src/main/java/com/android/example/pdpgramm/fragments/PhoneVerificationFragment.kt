package com.android.example.pdpgramm.fragments

import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.android.example.pdpgramm.R
import com.android.example.pdpgramm.databinding.FragmentPhoneVerificationBinding
import com.android.example.pdpgramm.models.Account
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.database.*
import java.util.concurrent.TimeUnit

private const val ARG_PARAM1 = "number"

class PhoneVerificationFragment : Fragment() {
    private lateinit var phoneNumber: String
    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var referenceDatabase: DatabaseReference
    private lateinit var binding: FragmentPhoneVerificationBinding
    private lateinit var storedVerificationId: String
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var countDownTimer: CountDownTimer
    var i = 0
    private val TAG = "PhoneVerificationFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            phoneNumber = it.getString(ARG_PARAM1)!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as AppCompatActivity).supportActionBar?.title = phoneNumber
        binding = FragmentPhoneVerificationBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        referenceDatabase = firebaseDatabase.getReference("accounts")
        sendCode(phoneNumber)

        countDownTimer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val sec = millisUntilFinished / 1000
                i++
                binding.slider.value = i.toFloat()
                if (sec < 10) {
                    binding.timer.text = "00:0$sec"
                } else {
                    binding.timer.text = "00:$sec"
                }
            }

            override fun onFinish() {

            }

        }.start()

        binding.slider.addOnChangeListener { slider, value, fromUser ->
            if (fromUser) {
                binding.slider.isClickable = false
            }
        }

        binding.inputCode.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                verifyCode()
            }

        })

        binding.refresh.setOnClickListener {
            resendCode(phoneNumber)
            countDownTimer.cancel()
            countDownTimer.start()
        }

        return binding.root
    }


    private fun sendCode(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun resendCode(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(requireActivity())                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .setForceResendingToken(resendToken)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun verifyCode() {
        val code = binding.inputCode.text.toString()
        if (code.length == 6) {
            val credential = PhoneAuthProvider.getCredential(storedVerificationId, code)
            signInWithPhoneAuthCredential(credential)
        }
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
            if (credential.smsCode != null) {
                binding.inputCode.setText(credential.smsCode)
            }
            Log.d(TAG, "onVerificationCompleted:$credential")
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
            Log.w(TAG, "onVerificationFailed", e)

            if (e is FirebaseAuthInvalidCredentialsException) {
                // Invalid request
            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
            }

            // Show a message and update the UI
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            Log.d(TAG, "onCodeSent:$verificationId")

            // Save verification ID and resending token so we can use them later
            storedVerificationId = verificationId
            resendToken = token
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    val user = task.result?.user
                    val currentUser = auth.currentUser
                    referenceDatabase.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val children = snapshot.children
                            var b = false
                            for (child in children) {
                                val value = child.getValue(Account::class.java)
                                if (value?.phoneNumber == currentUser?.phoneNumber) {
                                    b = true
                                    break
                                }
                            }
                            val bundle = bundleOf("number" to phoneNumber)
                            if (b) {
                                val sharedPreferences =
                                    requireContext().getSharedPreferences(
                                        "pdp_gram",
                                        Context.MODE_PRIVATE
                                    )
                                val editor = sharedPreferences.edit()
                                editor.putString("phone_number", phoneNumber)
                                editor.apply()
                                editor.commit()
                                findNavController().popBackStack()
                                findNavController().navigate(R.id.homeFragment, bundle)
                            } else {
                                findNavController().navigate(R.id.setUserFragment, bundle)
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {

                        }
                    })
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                    // Update UI
                }
            }
    }


    override fun onDestroy() {
        super.onDestroy()
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.app_name)
    }
}