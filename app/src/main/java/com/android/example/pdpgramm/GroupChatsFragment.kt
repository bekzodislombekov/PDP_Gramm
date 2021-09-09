package com.android.example.pdpgramm

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.android.example.pdpgramm.adapters.GroupChatsAdapter
import com.android.example.pdpgramm.databinding.FragmentGroupChatsBinding
import com.android.example.pdpgramm.models.Account
import com.android.example.pdpgramm.models.Group
import com.google.firebase.database.*

private const val ARG_PARAM1 = "param1"

class GroupChatsFragment : Fragment() {
    private var number: String? = null
    private lateinit var binding: FragmentGroupChatsBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var referenceDatabase: DatabaseReference
    private lateinit var referenceAccount: DatabaseReference
    private lateinit var adapter: GroupChatsAdapter
    private lateinit var list: ArrayList<Group>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.app_name)
            number = it.getString(ARG_PARAM1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGroupChatsBinding.inflate(inflater, container, false)
        firebaseDatabase = FirebaseDatabase.getInstance()
        referenceDatabase = firebaseDatabase.getReference("groups")
        referenceAccount = firebaseDatabase.getReference("accounts")

        list = ArrayList()
        referenceDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                list.clear()
                val children = snapshot.children
                for (child in children) {
                    val value = child.getValue(Group::class.java)
                    if (value != null) {
                        val accounts = value.accounts
                        for (a in accounts!!) {
                            if (a.phoneNumber == number) {
                                list.add(value)
                            }
                        }
                    }
                }
                adapter = GroupChatsAdapter(list, object : GroupChatsAdapter.OnItemClickListener {
                    override fun onItemListener(group: Group) {
                        val bundleOf = bundleOf("group" to group.uid)
                        findNavController().navigate(R.id.groupChatFragment, bundleOf)
                    }
                })
                binding.rv.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        binding.addGroup.setOnClickListener {
            referenceAccount.child(number!!).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val value = snapshot.getValue(Account::class.java)
                    val bundleOf = bundleOf("account" to value)
                    findNavController().navigate(R.id.addGroupFragment, bundleOf)
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
        }

        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String) =
            GroupChatsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                }
            }
    }
}