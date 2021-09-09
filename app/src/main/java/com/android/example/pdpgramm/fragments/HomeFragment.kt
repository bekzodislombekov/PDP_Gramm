package com.android.example.pdpgramm.fragments

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.example.pdpgramm.R
import com.android.example.pdpgramm.adapters.ViewPagerAdapter
import com.android.example.pdpgramm.databinding.FragmentHomeBinding
import com.android.example.pdpgramm.databinding.ItemTabBinding
import com.android.example.pdpgramm.models.Account
import com.google.android.material.card.MaterialCardView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.database.*

private const val ARG_PARAM1 = "number"

class HomeFragment : Fragment() {
    private var phoneNumber: String? = null
    private lateinit var binding: FragmentHomeBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    private lateinit var adapter: ViewPagerAdapter
    private lateinit var chats: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            phoneNumber = it.getString(ARG_PARAM1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.app_name)
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        adapter = ViewPagerAdapter(requireActivity(), phoneNumber!!)
        firebaseDatabase = FirebaseDatabase.getInstance()
        reference = firebaseDatabase.getReference("accounts")

        chats()
        binding.viewPager.adapter = adapter
        TabLayoutMediator(
            binding.tabLayout,
            binding.viewPager
        ) { tab, position ->
            tab.text = chats[position]
        }.attach()
        setTabs()
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val customView = tab?.customView
                val tabCard = customView?.findViewById<MaterialCardView>(R.id.tab_card)
                val tabName = customView?.findViewById<TextView>(R.id.tab_name)
                tabName?.setTextColor(Color.WHITE)
                tabCard?.setCardBackgroundColor(resources.getColor(R.color.main_color))
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                val customView = tab?.customView
                val tabCard = customView?.findViewById<MaterialCardView>(R.id.tab_card)
                val tabName = customView?.findViewById<TextView>(R.id.tab_name)
                tabName?.setTextColor(Color.parseColor("#7A7A7A"))
                tabCard?.setCardBackgroundColor(Color.parseColor("#E3E3E3"))
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })

        return binding.root
    }

    private fun setTabs() {
        val tabCount = binding.tabLayout.tabCount
        for (i in 0 until tabCount) {
            val bindingTab = ItemTabBinding.inflate(layoutInflater)
            val tabName = bindingTab.tabName
            val tabCard = bindingTab.tabCard
            tabName.text = chats[i]
            if (i == 0) {
                tabName.setTextColor(Color.WHITE)
                tabCard.setCardBackgroundColor(resources.getColor(R.color.main_color))
            } else {
                tabName.setTextColor(Color.parseColor("#7A7A7A"))
                tabCard.setCardBackgroundColor(Color.parseColor("#E3E3E3"))
            }
            binding.tabLayout.getTabAt(i)?.customView = bindingTab.root
        }
    }

    private fun chats() {
        chats = ArrayList()
        chats.add("Personal")
        chats.add("Groups")
    }

}