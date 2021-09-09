package com.android.example.pdpgramm

import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.android.example.pdpgramm.adapters.AddGroupAdapter
import com.android.example.pdpgramm.databinding.DialogLayoutBinding
import com.android.example.pdpgramm.databinding.FragmentAddGroupBinding
import com.android.example.pdpgramm.models.Account
import com.android.example.pdpgramm.models.Group
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AddGroupFragment : Fragment() {
    private lateinit var binding: FragmentAddGroupBinding
    private lateinit var adapter: AddGroupAdapter
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var referenceGroup: DatabaseReference
    private lateinit var referenceAccount: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var referenceStorage: StorageReference
    private lateinit var allAccounts: ArrayList<Account>
    private lateinit var addedAccounts: ArrayList<Account>
    private var fileAbsolutePath: String = ""
    private var imgUrl = ""
    private var currentTime: Long = 0L
    private lateinit var currentAccount: FirebaseUser
    private lateinit var group: Group
    private lateinit var account: Account


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        account = arguments?.getSerializable("account") as Account
        binding = FragmentAddGroupBinding.inflate(inflater, container, false)
        firebaseDatabase = FirebaseDatabase.getInstance()
        referenceAccount = firebaseDatabase.getReference("accounts")
        referenceGroup = firebaseDatabase.getReference("groups")
        storage = FirebaseStorage.getInstance()
        referenceStorage = storage.getReference("group_images")
        firebaseAuth = FirebaseAuth.getInstance()

        currentAccount = firebaseAuth.currentUser!!
        allAccounts = ArrayList()
        addedAccounts = ArrayList()

        referenceAccount.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                allAccounts.clear()
                addedAccounts.clear()
                val children = snapshot.children
                for (child in children) {
                    val value = child.getValue(Account::class.java)
                    if (value != null && currentAccount.phoneNumber != value.phoneNumber) {
                        allAccounts.add(value)
                    }
                }
                addedAccounts.add(account)
                adapter = AddGroupAdapter(
                    allAccounts,
                    addedAccounts,
                    object : AddGroupAdapter.OnItemClickListener {
                        override fun onItemListener(account: Account, position: Int) {
                            var b = false
                            for (addedAccount in addedAccounts) {
                                if (addedAccount.phoneNumber == account.phoneNumber) {
                                    b = true
                                    break
                                }
                            }
                            if (b) {
                                addedAccounts.remove(account)
                            } else {
                                addedAccounts.add(account)
                            }
                        }
                    })
                binding.rv.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        binding.img.setOnClickListener {
            val bottomSheetDialog = BottomSheetDialog(requireContext())
            val dialogBinding = DialogLayoutBinding.inflate(layoutInflater, null, false)
            bottomSheetDialog.setContentView(dialogBinding.root)
            dialogBinding.cameraImg.setOnClickListener {
                getImageFromCamera()
                bottomSheetDialog.dismiss()
            }
            dialogBinding.galleryImg.setOnClickListener {
                getImageFromGallery()
                bottomSheetDialog.dismiss()
            }
            bottomSheetDialog.show()
        }

        binding.addGroup.setOnClickListener {
            if (currentTime == 0L) {
                currentTime = System.currentTimeMillis()
            }
            val name = binding.inputName.text.toString()
            val uid = "${currentAccount.phoneNumber}_$currentTime"
            val date = Date()
            val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
            val currentDate = simpleDateFormat.format(date)
            val downloadUrl =
                referenceStorage.child("$uid.jpg").downloadUrl
            downloadUrl.addOnSuccessListener {
                imgUrl = it.toString()

                referenceGroup.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        group = Group(
                            name,
                            imgUrl,
                            addedAccounts,
                            currentAccount.phoneNumber,
                            currentDate,
                            uid
                        )
                        referenceGroup.child(uid).setValue(group)
                        val bundleOf = bundleOf("group_uid" to uid)
                        findNavController().navigate(R.id.groupChatFragment, bundleOf)
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })
            }
        }


        return binding.root
    }

    private fun getImageFromGallery() {
        getImageContent.launch("image/*")
    }

    private fun createImageFile(): File {
        val m = System.currentTimeMillis()
        val filesDir = activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES!!)
        val tempFile = File.createTempFile("$m", ".jpg", filesDir)
        fileAbsolutePath = tempFile.absolutePath
        return tempFile
    }

    private fun getImageFromCamera() {
        val createImageFile = createImageFile()
        val photoUri = FileProvider.getUriForFile(
            requireContext(),
            BuildConfig.APPLICATION_ID,
            createImageFile
        )
        getCameraContent.launch(photoUri)
    }

    private val getCameraContent =
        registerForActivityResult(ActivityResultContracts.TakePicture()) {
            binding.img.setImageURI(Uri.fromFile(File(fileAbsolutePath)))
        }

    private val getImageContent = registerForActivityResult(ActivityResultContracts.GetContent()) {
        binding.img.setImageURI(it)
        val openInputStream = activity?.contentResolver?.openInputStream(it)
        currentTime = System.currentTimeMillis()
        val file = File(activity?.filesDir, "$currentTime.jpg")
        val fileOutputStream = FileOutputStream(file)
        val uploadTask =
            referenceStorage.child("${currentAccount.phoneNumber}_$currentTime.jpg").putFile(it)
        uploadTask.addOnSuccessListener { task ->
            if (task.task.isSuccessful) {
                val downloadUrl = task?.metadata?.reference?.downloadUrl
                downloadUrl?.addOnSuccessListener { uri ->
                    imgUrl = uri.toString()
                }
            }
        }.addOnFailureListener {

        }
        openInputStream?.copyTo(fileOutputStream)
        openInputStream?.close()
        fileOutputStream.close()
        fileAbsolutePath = file.absolutePath
        Log.d("TTT", fileAbsolutePath)
    }

}