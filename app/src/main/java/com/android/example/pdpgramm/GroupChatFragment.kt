package com.android.example.pdpgramm

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.android.example.pdpgramm.adapters.GroupMessageAdapter
import com.android.example.pdpgramm.databinding.FragmentGroupChatBinding
import com.android.example.pdpgramm.models.Account
import com.android.example.pdpgramm.models.Group
import com.android.example.pdpgramm.models.GroupMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

private const val ARG_PARAM1 = "group"
private const val ARG_PARAM2 = "group_uid"

class GroupChatFragment : Fragment() {
    private var groupUid: String? = null
    private var uid: String? = null
    private lateinit var binding: FragmentGroupChatBinding
    private lateinit var adapter: GroupMessageAdapter
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var referenceMessage: DatabaseReference
    private lateinit var referenceGroup: DatabaseReference
    private lateinit var referenceAccount: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var list: ArrayList<GroupMessage>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            groupUid = it.getString(ARG_PARAM1)
            uid = it.getString(ARG_PARAM2)
            if (groupUid == null) {
                groupUid = uid
            }
            (activity as AppCompatActivity).supportActionBar?.hide()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGroupChatBinding.inflate(inflater, container, false)
        list = ArrayList()
        firebaseDatabase = FirebaseDatabase.getInstance()
        referenceMessage = firebaseDatabase.getReference("group_messages")
        referenceGroup = firebaseDatabase.getReference("groups")
        referenceAccount = firebaseDatabase.getReference("accounts")
        firebaseAuth = FirebaseAuth.getInstance()
        val currentAccount = firebaseAuth.currentUser

        binding.sendBtn.setOnClickListener {
            val text = binding.inputMessage.text.toString()
            val date = Date()
            val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm")
            val currentDate = simpleDateFormat.format(date)
            referenceAccount.child(currentAccount?.phoneNumber!!)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val value = snapshot.getValue(Account::class.java)
                        if (value != null) {
                            val groupMessage =
                                GroupMessage(text, value, currentDate)
                            val key = referenceMessage.push().key
                            referenceMessage.child("$groupUid/$key")
                                .setValue(groupMessage)
                            binding.inputMessage.setText("")
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })
        }

        referenceMessage.child(groupUid!!).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                list.clear()
                val children = snapshot.children
                for (child in children) {
                    val value = child.getValue(GroupMessage::class.java)
                    if (value != null) {
                        list.add(value)
                    }
                }
                adapter = GroupMessageAdapter(list, currentAccount?.phoneNumber!!)
                binding.rv.adapter = adapter
                binding.rv.smoothScrollToPosition(list.size)
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        referenceGroup.child(groupUid!!).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val value = snapshot.getValue(Group::class.java)
                if (value != null) {
                    Picasso.get().load(value.groupPhoto).into(binding.img)
                    binding.name.text = value.name
                    binding.count.text = "${value.accounts?.size} members"
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        findNavController().popBackStack(R.id.homeFragment, false)
    }

}