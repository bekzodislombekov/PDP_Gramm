package com.android.example.pdpgramm

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.android.example.pdpgramm.adapters.MessageAdapter
import com.android.example.pdpgramm.databinding.FragmentPersonalChatBinding
import com.android.example.pdpgramm.models.Account
import com.android.example.pdpgramm.models.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

private const val ARG_PARAM1 = "account"

class PersonalChatFragment : Fragment() {
    private var account: Account? = null
    private lateinit var binding: FragmentPersonalChatBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var referenceMessage: DatabaseReference
    private lateinit var referenceAccount: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var list: ArrayList<Message>
    private lateinit var adapter: MessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            account = it.getSerializable(ARG_PARAM1) as Account
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as AppCompatActivity).supportActionBar?.hide()
        binding = FragmentPersonalChatBinding.inflate(inflater, container, false)
        list = ArrayList()
        firebaseDatabase = FirebaseDatabase.getInstance()
        referenceMessage = firebaseDatabase.getReference("messages")
        referenceAccount = firebaseDatabase.getReference("accounts")
        firebaseAuth = FirebaseAuth.getInstance()
        val currentAccount = firebaseAuth.currentUser

        binding.sendBtn.setOnClickListener {
            val text = binding.inputMessage.text.toString()
            val date = Date()
            val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm")
            val currentDate = simpleDateFormat.format(date)
            val message =
                Message(text, currentAccount?.phoneNumber, account?.phoneNumber, currentDate)
            val key = referenceMessage.push().key
            referenceMessage.child("${currentAccount?.phoneNumber}/${account?.phoneNumber}/$key")
                .setValue(message)
            referenceMessage.child("${account?.phoneNumber}/${currentAccount?.phoneNumber}/$key")
                .setValue(message)
            binding.inputMessage.setText("")
        }

        referenceMessage.child("${currentAccount?.phoneNumber}/${account?.phoneNumber}")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    list.clear()
                    val children = snapshot.children
                    for (child in children) {
                        val value = child.getValue(Message::class.java)
                        if (value != null) {
                            list.add(value)
                        }
                    }
                    adapter = MessageAdapter(list, currentAccount?.phoneNumber!!)
                    binding.rv.adapter = adapter
                    binding.rv.smoothScrollToPosition(list.size)

                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

        referenceAccount.child("${account?.phoneNumber}").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val value = snapshot.getValue(Account::class.java)
                Picasso.get().load(value?.accountPhoto).into(binding.img)
                binding.name.text = "${value?.firstName} ${value?.lastName}"
                if (value?.isOnline!!) {
                    binding.date.text = "Online"
                } else {
                    binding.date.text = "last seen at ${value.lastEnter}"
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        (activity as AppCompatActivity).supportActionBar?.show()
    }

}