package com.android.example.pdpgramm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.Navigation
import com.android.example.pdpgramm.models.Account
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var currentUser: FirebaseUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        firebaseDatabase = FirebaseDatabase.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()
        currentUser = firebaseAuth.currentUser!!
        reference = firebaseDatabase.getReference("accounts")
        reference.child(currentUser.phoneNumber!!)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val value = snapshot.getValue(Account::class.java)
                    value?.isOnline = true
                    reference.child(currentUser.phoneNumber!!).setValue(value)
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }

    override fun onSupportNavigateUp(): Boolean {
        return Navigation.findNavController(this, R.id.my_nav_host_fragment).navigateUp()
    }

    override fun onDestroy() {
        super.onDestroy()
        reference.child(currentUser.phoneNumber!!)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val value = snapshot.getValue(Account::class.java)
                    val date = Date()
                    val simpleDateFormat = SimpleDateFormat("HH:mm")
                    val lastDate = simpleDateFormat.format(date)
                    value?.isOnline = false
                    value?.lastEnter = lastDate
                    reference.child(currentUser.phoneNumber!!).setValue(value)
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }
}