package io.getmadd.openpsychic.fragments.home

import android.R
import android.app.Activity
import android.content.ContentValues
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
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import io.getmadd.openpsychic.databinding.FragmentProfileBinding
import io.getmadd.openpsychic.model.Psychic
import io.getmadd.openpsychic.model.User

class ProfileFragment : Fragment() {

    private lateinit var _binding: FragmentProfileBinding
    private val binding get() = _binding
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private var userId = auth.uid.toString()
    private var profileImageURI: Uri? = null
    private var backdropImageURI: Uri? = null
    private var selectedView: ImageView? = null
    private var profileImgSrc: String? = null
    private var displayImgSrc: String? = null
    private var userType: String? = null
    private var user: User? = null
    private var psychic: Psychic? = null
    private var selectedCategory: String? = null

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
        // Add listener to the on/off switch
        binding.psychicOnDisplaySwitch.setOnCheckedChangeListener { _, isChecked ->
            handlePsychicOnDisplaySwitchChanged(isChecked)
        }

        // Add listener to the spinner
        binding.categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Handle the selection change
                selectedCategory = parent?.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle case where nothing is selected (optional)
            }
        }
        val adRequest1 = AdRequest.Builder().build()

        InterstitialAd.load(
            context!!,
            "ca-app-pub-2450865968732279/3376431783",
            adRequest1,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d(ContentValues.TAG, adError.message)
                }

                override fun onAdLoaded(ad: InterstitialAd) {
                    Log.d(ContentValues.TAG, "Ad was loaded.")
                    activity?.let { ad.show(it) }
                }
            }
        )

    }

    private fun handlePsychicOnDisplaySwitchChanged(isChecked: Boolean) {
        if (isChecked) {
            // The switch is enabled, perform action to add the user to the database
            addPsychicToDatabase()
            binding.categorySpinner.isEnabled = false
        } else {
            // The switch is disabled, perform action to remove the user from the database
            removePsychicFromDatabase()
            binding.categorySpinner.isEnabled = true
        }
    }

    private fun setupUI() {
        db.collection("users").document(userId)
            .get()
            .addOnSuccessListener { result ->
                userType = result.getString("usertype").toString()

                if(userType == "user") {

                    user = User(
                        userid = result.getString("userID").toString(),
                        email = result.getString("email").toString(),
                        displayname = result.getString("displayname").toString(),
                        username = result.getString("username").toString(),
                        usertype = result.getString("usertype").toString(),
                        firstname = result.getString("firstname").toString(),
                        lastname = result.getString("lastname").toString(),
                        bio = result.getString("bio").toString(),
                        profileimgsrc = result.getString("profileimgsrc").toString(),
                        displayimgsrc = result.getString("displayimgsrc").toString(),
                        joinedlivestreams = null
                    )

                    binding.displayNameTV.text = "Base User "
                    binding.psychicLayout.visibility = View.GONE

                    binding.usersnameTV.text = "@" + user?.username
                    binding.bioEditText.text = Editable.Factory.getInstance()
                        .newEditable(user?.bio ?: "Edit Your Bio")
                    profileImgSrc = user?.profileimgsrc
                    displayImgSrc = user?.displayimgsrc

                }
                else if (userType == "psychic"){

                    psychic = Psychic(
                        userid = result.getString("userID").toString(),
                        email = result.getString("email").toString(),
                        displayname = result.getString("displayname").toString(),
                        username = result.getString("username").toString(),
                        usertype = result.getString("usertype").toString(),
                        firstname = result.getString("firstname").toString(),
                        lastname = result.getString("lastname").toString(),
                        bio = result.getString("bio").toString(),
                        profileimgsrc = result.getString("profileimgsrc").toString(),
                        displayimgsrc = result.getString("displayimgsrc").toString(),
                        psychicondisplay = result.getBoolean("psychicondisplay")!!,
                        psychicondisplaycategory = result.getString("psychicondisplaycategory").toString()
                    )

                    binding.psychicLayout.visibility = View.VISIBLE
                    binding.displayNameTV.text = psychic?.displayname

                    binding.usersnameTV.text = "@" + psychic?.username
                    binding.bioEditText.text = Editable.Factory.getInstance()
                        .newEditable(psychic?.bio ?: "Edit Your Bio")
                    profileImgSrc = psychic?.profileimgsrc
                    displayImgSrc = psychic?.displayimgsrc

                    if(psychic?.psychicondisplay == true){
                        selectedCategory = psychic!!.psychicondisplaycategory
                        binding.psychicOnDisplaySwitch.isChecked = true
                        binding.categorySpinner.isEnabled = false
                        categorySpinnerList(selectedCategory!!)
                    }
                }

                loadImages(profileImgSrc,displayImgSrc)
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }
    }

    private fun categorySpinnerList(selectedcategory:String){
       val psychicCategories = listOf(
            "Past Lives",
            "Dream Interpretations",
            "Love & Relationships",
            "Fortune Telling",
            "Palm Readings",
            "Astrology",
            "Lingering Spirits",
            "Tarot Readings",
            "General Readings"
        )

        // Creating an ArrayAdapter using the string array and a default spinner layout
        val adapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, psychicCategories)

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Apply the adapter to the spinner
        binding.categorySpinner.adapter = adapter

        // Set the selected value programmatically (e.g., setting to "Category3")
        val position = adapter.getPosition(selectedCategory)

        binding.categorySpinner.setSelection(position)
    }

    private fun addPsychicToDatabase() {

        psychic!!.psychicondisplay = true
        psychic!!.psychicondisplaycategory = selectedCategory

        db.collection("psychicOnDisplay")
            .document(selectedCategory!!)
            .collection("psychicsOnDisplay")
            .document(userId)
            .set(psychic!!)
            .addOnSuccessListener {
                Log.d(TAG, "User added as a psychic with category: $selectedCategory")
                db.collection("users").document(userId).update("psychicondisplay", true, "psychicondisplaycategory", selectedCategory)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error adding user as a psychic: $e")
            }
    }

    private fun removePsychicFromDatabase() {
        psychic!!.psychicondisplay = false

        selectedCategory.let {
            if (it != null) {
                db.collection("psychicOnDisplay")
                    .document(it)
                    .collection("psychicsOnDisplay")
                    .document(userId)
                    .delete()
                    .addOnSuccessListener {
                        Log.d(TAG, "User removed from the psychic database")
                        db.collection("users").document(userId).update("psychicondisplay", false, "psychicondisplaycategory", " ")
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Error removing user from the psychic database: $e")
                    }
            }
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
                    .update("profileimgsrc", profileDownloadUrl.toString())
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
                    .update("displayimgsrc", backdropDownloadUrl.toString())
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
