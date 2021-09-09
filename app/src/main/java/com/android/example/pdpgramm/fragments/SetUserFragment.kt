package com.android.example.pdpgramm.fragments

import android.content.Context
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
import androidx.navigation.fragment.findNavController
import com.android.example.pdpgramm.BuildConfig
import com.android.example.pdpgramm.R
import com.android.example.pdpgramm.databinding.DialogLayoutBinding
import com.android.example.pdpgramm.databinding.FragmentSetUserBinding
import com.android.example.pdpgramm.models.Account
import com.android.example.pdpgramm.models.Group
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.io.FileOutputStream

private const val ARG_PARAM1 = "number"

class SetUserFragment : Fragment() {
    private var phoneNumber: String? = null
    private lateinit var binding: FragmentSetUserBinding
    private lateinit var firebaseStorage: FirebaseStorage
    private lateinit var referenceStorage: StorageReference
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var referenceDatabase: DatabaseReference
    private var fileAbsolutePath: String = ""
    private var imgUrl = ""
    private val TAG = "SetUserFragment"

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
        binding = FragmentSetUserBinding.inflate(inflater, container, false)
        firebaseStorage = FirebaseStorage.getInstance()
        referenceStorage = firebaseStorage.getReference("user_images")
        firebaseDatabase = FirebaseDatabase.getInstance()
        referenceDatabase = firebaseDatabase.getReference("accounts")

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

        binding.save.setOnClickListener {
            val fName = binding.inputFName.text.toString()
            val lName = binding.inputLName.text.toString()
            val account = Account(fName, lName, phoneNumber, true, "", imgUrl)
            val downloadUrl = referenceStorage.child("$phoneNumber.jpg").downloadUrl
            downloadUrl.addOnSuccessListener {
                imgUrl = it.toString()
            }
            referenceDatabase.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    referenceDatabase.child(phoneNumber!!).setValue(account)
                    val bundleOf = bundleOf("number" to phoneNumber)
                    val sharedPreferences =
                        requireContext().getSharedPreferences("pdp_gram", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putString("phone_number", phoneNumber)
                    editor.apply()
                    editor.commit()
                    findNavController().navigate(R.id.homeFragment, bundleOf)
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
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
        val file = File(activity?.filesDir, "$phoneNumber.jpg")
        val fileOutputStream = FileOutputStream(file)
        val uploadTask = referenceStorage.child("$phoneNumber.jpg").putFile(it)
        uploadTask.addOnSuccessListener { task ->
            if (task.task.isSuccessful) {
                val downloadUrl = task?.metadata?.reference?.downloadUrl
                downloadUrl?.addOnSuccessListener { uri ->
                    imgUrl = uri.toString()
                }
            }
        }.addOnFailureListener {
            Log.d(TAG, "${it.message}")
        }
        openInputStream?.copyTo(fileOutputStream)
        openInputStream?.close()
        fileOutputStream.close()
        fileAbsolutePath = file.absolutePath
        Log.d("TTT", fileAbsolutePath)
    }
}