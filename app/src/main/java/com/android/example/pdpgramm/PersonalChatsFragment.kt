package com.android.example.pdpgramm

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.android.example.pdpgramm.adapters.OnItemClickListener
import com.android.example.pdpgramm.adapters.PersonalChatAdapter
import com.android.example.pdpgramm.databinding.FragmentPersonalChatsBinding
import com.android.example.pdpgramm.models.Account
import com.android.example.pdpgramm.models.Message
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.messaging.FirebaseMessaging
import kotlin.math.log

private const val ARG_PARAM1 = "param1"

class PersonalChatsFragment : Fragment() {
    private var number: String? = null
    private lateinit var binding: FragmentPersonalChatsBinding
    private lateinit var adapter: PersonalChatAdapter
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var referenceAccount: DatabaseReference
    private lateinit var referenceMessage: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var list: ArrayList<Account>
    private val TAG = "PersonalChatsFragment"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            number = it.getString(ARG_PARAM1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPersonalChatsBinding.inflate(inflater, container, false)
        list = ArrayList()
        firebaseDatabase = FirebaseDatabase.getInstance()
        referenceAccount = firebaseDatabase.getReference("accounts")
        referenceMessage = firebaseDatabase.getReference("messages")
        firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser

        referenceAccount.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                list.clear()
                val children = snapshot.children
                for (child in children) {
                    val value = child.getValue(Account::class.java)
                    if (value != null && value.phoneNumber != currentUser?.phoneNumber) {
                        list.add(value)
                    }

                }
                adapter = PersonalChatAdapter(list, object : OnItemClickListener {
                    override fun onItemListener(account: Account) {
                        val bundleOf = bundleOf("account" to account)
                        findNavController().navigate(R.id.personalChatFragment, bundleOf)
                    }
                })
                binding.rv.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })




        return binding.root
    }

    fun updateToken(): String {
        var token = ""
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.d(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            token = task.result!!
        })
        return token
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String) =
            PersonalChatsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                }
            }
    }
}