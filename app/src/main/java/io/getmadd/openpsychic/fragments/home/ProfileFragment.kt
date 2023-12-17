package io.getmadd.openpsychic.fragments.home

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import io.getmadd.openpsychic.databinding.FragmentProfileBinding


class ProfileFragment : Fragment() {

    private lateinit var _binding: FragmentProfileBinding
    private val binding get() = _binding
    private var db = Firebase.firestore
    var userId = Firebase.auth.uid
    private var profileImageURI: Uri? = null
    private var backdropImageURI: Uri? = null
    private var selectedView: ImageView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var profileImgSrc: String
        var displayImgSrc: String

        db.collection("users").document("$userId")
            .get()
            .addOnSuccessListener { result ->
                if (result.get("usertype") == "psychic") {
                    binding.displayNameTV.text = result.get("displayname").toString()
                } else {
                    binding.displayNameTV.text = "Base User"
                }
                "@${result.get("username").toString()}".also { binding.usersnameTV.text = it }
                binding.bioEditText.text = Editable.Factory.getInstance().newEditable(result.get("bio").toString())
                profileImgSrc = result.get("profileImgSrc").toString()
                displayImgSrc = result.get("displayImgSrcs").toString()



                //Glide.with(this).load(profileImgSrc).into(binding.backdropImageView);
                //Glide.with(this).load(displayImgSrc).into(binding.profileImageView);

            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }

        binding.backdropImageView.setOnClickListener {
            val galleryIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            selectedView = binding.backdropImageView
            getContent.launch(galleryIntent)
        }
        binding.profileImageView.setOnClickListener {
            val galleryIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            selectedView = binding.profileImageView
            getContent.launch(galleryIntent)
        }
        binding.updateButton.setOnClickListener {
            updateUserDate()
        }
    }

    private val getContent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                if (data != null) {
                    // Handle the selected image
                    val selectedImage: Uri? = data.data
                    if (selectedImage != null) {
                        // Set the selected image to the ImageView
                        selectedView?.setImageURI(selectedImage)
                        if (selectedView == binding.backdropImageView) {
                            backdropImageURI = selectedImage
                        }
                        if (selectedView == binding.profileImageView) {
                            profileImageURI = selectedImage
                        }
                    }
                }
            }
        }


    private fun uploadImageToFirebaseStorage() {
        // Check if an image is selected
        if (selectedView != null) {

            // Create a reference to the Firebase Storage
            val storage = Firebase.storage
            val storageRef = storage.reference
            val auth = Firebase.auth

            // Create a reference to the image in storage with a unique name (e.g., user ID + timestamp)
            val profileImgRef =
                storageRef.child("images/${auth.uid}/profileImg.jpg")

            val backdropImgRef = storageRef.child("images/${auth.uid}/backdropImg.jpg")

            if (profileImageURI != null) {
                profileImgRef.putFile(profileImageURI!!)
                    .addOnSuccessListener {
                        // Image uploaded successfully
                        Log.d("UploadImageActivity", "Image uploaded successfully.")

                        db.collection("users").document(auth.uid!!)
                            .update("profileImageSrc", profileImgRef.downloadUrl)

                    }
                    .addOnFailureListener { e ->
                        // Handle the failure
                        Log.e("UploadImageActivity", "Error uploading image: $e")
                    }
            } else {
                // No image selected
                Log.e("UploadImageActivity", "No image selected.")
            }

            if (backdropImageURI != null) {
                backdropImgRef.putFile(backdropImageURI!!)
                    .addOnSuccessListener {
                        // Image uploaded successfully
                        Log.d("UploadImageActivity", "Image uploaded successfully.")
                        db.collection("users").document(auth.uid!!)
                            .update("displayImgeSrc", backdropImgRef.downloadUrl)

                    }
                    .addOnFailureListener { e ->
                        // Handle the failure
                        Log.e("UploadImageActivity", "Error uploading image: $e")
                    }
            } else {
                // No image selected
                Log.e("UploadImageActivity", "No image selected.")
            }
        }
        // Upload the images

    }

    private fun updateUserDate() {
        var updatedBio = binding.bioEditText.text.toString()
        var auth = Firebase.auth
        if (profileImageURI != null || backdropImageURI != null) {
            uploadImageToFirebaseStorage()
        }
        if (updatedBio.isNotEmpty()) {
            db.collection("users").document(auth.uid!!).update("bio", updatedBio)
        }
    }
}