package com.android.example.pdpgramm.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.android.example.pdpgramm.GroupChatsFragment
import com.android.example.pdpgramm.PersonalChatsFragment

class ViewPagerAdapter(fragmentActivity: FragmentActivity, private val phoneNumber: String) :
    FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return if (position == 0) {
            PersonalChatsFragment.newInstance(phoneNumber)
        } else {
            GroupChatsFragment.newInstance(phoneNumber)
        }
    }
}