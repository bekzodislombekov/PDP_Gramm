package com.android.example.pdpgramm.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.android.example.pdpgramm.R
import com.android.example.pdpgramm.databinding.FragmentConfirmPhoneBinding

class ConfirmPhoneFragment : Fragment() {
    private lateinit var binding: FragmentConfirmPhoneBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentConfirmPhoneBinding.inflate(inflater, container, false)
        val sharedPreferences =
            requireContext().getSharedPreferences("pdp_gram", Context.MODE_PRIVATE)
        val phoneNumber = sharedPreferences.getString("phone_number", "")
        if (phoneNumber != "") {
            val bundleOf = bundleOf("number" to phoneNumber)
            findNavController().popBackStack()
            findNavController().navigate(R.id.homeFragment, bundleOf)
        }
        binding.next.setOnClickListener {
            val number = binding.inputNumber.text.toString()
            val pNumber = "+998$number"
            val bundle = bundleOf("number" to pNumber)
            findNavController().navigate(R.id.phoneVerificationFragment, bundle)
        }
        return binding.root
    }
}