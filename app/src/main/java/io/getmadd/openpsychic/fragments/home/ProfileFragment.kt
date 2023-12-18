package io.getmadd.openpsychic.fragments.home

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import io.getmadd.openpsychic.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private lateinit var _binding: FragmentProfileBinding
    private val binding get() = _binding
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private var userId = auth.uid
    private var profileImageURI: Uri? = null
    private var backdropImageURI: Uri? = null
    private var selectedView: ImageView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()

        binding.backdropImageView.setOnClickListener {
            selectImage(binding.backdropImageView)
        }
        binding.profileImageView.setOnClickListener {
            selectImage(binding.profileImageView)
        }
        binding.updateButton.setOnClickListener {
            updateUserDate()
        }
    }

    private fun setupUI() {
        db.collection("users").document("$userId")
            .get()
            .addOnSuccessListener { result ->
                val userType = result.getString("usertype")
                binding.displayNameTV.text = if (userType == "psychic") {
                    result.getString("displayname")
                } else {
                    "Base User"
                }
                binding.usersnameTV.text = "@${result.getString("username")}"
                binding.bioEditText.text = Editable.Factory.getInstance()
                    .newEditable(result.getString("bio"))

                loadImages(result.getString("profileImgSrc"), result.getString("displayImgSrc"))
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }
    }

    private fun loadImages(profileImgSrc: String?, displayImgSrc: String?) {
        if (!profileImgSrc.isNullOrEmpty()) {
            Glide.with(this).load(profileImgSrc).into(binding.profileImageView)
        }
        if (!displayImgSrc.isNullOrEmpty()) {
            Glide.with(this).load(displayImgSrc).into(binding.backdropImageView)
        }
    }

    private fun selectImage(imageView: ImageView) {
        val galleryIntent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        selectedView = imageView
        getContent.launch(galleryIntent)
    }

    private val getContent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { selectedImage ->
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

    private fun uploadImageToFirebaseStorage() {
        val storageRef = storage.reference
        val profileImgRef = storageRef.child("images/${auth.uid}/profileImg.jpg")
        val backdropImgRef = storageRef.child("images/${auth.uid}/backdropImg.jpg")

        profileImageURI?.let {
            uploadImage(profileImgRef, it) { profileDownloadUrl ->
                db.collection("users").document(auth.uid!!)
                    .update("profileImgSrc", profileDownloadUrl.toString())
                    .addOnSuccessListener {
                        Log.d("UploadImageActivity", "Profile Image URL updated in Firestore.")
                    }
                    .addOnFailureListener { e ->
                        Log.e("UploadImageActivity", "Error updating profile image URL in Firestore: $e")
                    }
            }
        }

        backdropImageURI?.let {
            uploadImage(backdropImgRef, it) { backdropDownloadUrl ->
                db.collection("users").document(auth.uid!!)
                    .update("displayImgSrc", backdropDownloadUrl.toString())
                    .addOnSuccessListener {
                        Log.d("UploadImageActivity", "Backdrop Image URL updated in Firestore.")
                    }
                    .addOnFailureListener { e ->
                        Log.e("UploadImageActivity", "Error updating backdrop image URL in Firestore: $e")
                    }
            }
        }
    }

    private fun uploadImage(imageRef: StorageReference, imageUri: Uri, onSuccess: (String) -> Unit) {
        imageRef.putFile(imageUri)
            .addOnSuccessListener { task ->
                task.storage.downloadUrl.addOnSuccessListener { downloadUrl ->
                    onSuccess.invoke(downloadUrl.toString())
                }
            }
            .addOnFailureListener { e ->
                Log.e("UploadImageActivity", "Error uploading image: $e")
            }
    }

    private fun updateUserDate() {
        val updatedBio = binding.bioEditText.text.toString()
        if (profileImageURI != null || backdropImageURI != null) {
            uploadImageToFirebaseStorage()
        }
        if (updatedBio.isNotEmpty()) {
            db.collection("users").document(auth.uid!!).update("bio", updatedBio)
        }
    }
}
