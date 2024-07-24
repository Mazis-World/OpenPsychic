package io.getmadd.openpsychic.fragments.home

import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import io.getmadd.openpsychic.BuildConfig
import io.getmadd.openpsychic.R
import io.getmadd.openpsychic.databinding.FragmentProfileBinding
import io.getmadd.openpsychic.model.Psychic
import io.getmadd.openpsychic.model.User
import io.getmadd.openpsychic.services.UserPreferences
import kotlin.properties.Delegates

class ProfileFragment : Fragment() {

    private lateinit var _binding: FragmentProfileBinding
    private lateinit var prefs: UserPreferences
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
    private var versionName: String = BuildConfig.VERSION_NAME

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefs = UserPreferences(requireContext())
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
        binding.accountlayoutbutton.setOnClickListener{
            findNavController().navigate(R.id.account_fragment)
        }
        binding.notificationslayoutbutton.setOnClickListener{
            findNavController().navigate(R.id.notif_fragment)
        }
        binding.termslayoutbutton.setOnClickListener{
            findNavController().navigate(R.id.terms_fragment)
        }
        binding.privacylayoutbutton.setOnClickListener{
            findNavController().navigate(R.id.privacy_fragment)
        }
        binding.aboutlayoutbutton.setOnClickListener{
            Toast.makeText(context, "About",Toast.LENGTH_SHORT).show()
        }
        binding.helplayoutbutton.setOnClickListener{
            Toast.makeText(context, "Help",Toast.LENGTH_SHORT).show()
        }
        binding.rateuslayoutbutton.setOnClickListener{
            openAppPageForRating()
        }
        binding.logouttextbutton.setOnClickListener{
            val builder: AlertDialog.Builder =
                AlertDialog.Builder(ContextThemeWrapper(context, R.style.AlertDialogCustom))

            builder
                .setMessage("Are you sure you want to logout?")
                .setTitle("Logout")
                .setCancelable(false)
                .setPositiveButton("Logout") { dialog, which ->
                    FirebaseAuth.getInstance().signOut()
                }
                .setNegativeButton("Cancel") { dialog, which ->
                    dialog.cancel()
                }.create().show()
        }
        binding.versionnumbertextview.text = "V." + versionName
    }

    fun openAppPageForRating() {
        try {
            startActivity( Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=io.getmadd.openpsychic")))
        } catch (e: ActivityNotFoundException) {
            // If Play Store app is not installed, open the URL in a web browser
            startActivity( Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=io.getmadd.openpsychic")))
        }
    }

    private fun setupUI() {
        val adRequest1 = AdRequest.Builder().build()
        var state = context?.let { UserPreferences(it).subscriptionstate }
        if (state != "active") {

            InterstitialAd.load(
                context!!,
                "ca-app-pub-2450865968732279/3376431783",
                adRequest1,
                object : InterstitialAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        Log.d(TAG, adError.message)
                    }

                    override fun onAdLoaded(ad: InterstitialAd) {
                        Log.d(TAG, "Ad was loaded.")
                        activity?.let { ad.show(it) }
                    }
                }
            )
        }

        binding.usersnameTV.text = "@${prefs.username}"
        binding.displayNameTV.text = prefs.displayname

        loadImages(prefs.profileimgsrc, prefs.displayimgsrc)

    }

    private fun loadImages(profileImgSrc: String?, displayImgSrc: String?) {
        if (profileImgSrc != " ") {
            Glide.with(this).load(profileImgSrc).apply(RequestOptions.circleCropTransform()).into(binding.profileImageView)
        }else{
            Glide.with(this).load(R.drawable.openpsychiclogo).apply(RequestOptions.circleCropTransform()).into(binding.profileImageView)
        }
        if (displayImgSrc != " ") {
            Glide.with(this).load(displayImgSrc).into(binding.backdropImageView)
        }
        else{
            Glide.with(this).load(R.drawable.openpsychiclogo).into(binding.backdropImageView)
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
                    if (selectedView == binding.backdropImageView) {
                        Glide.with(this).load(selectedImage).into(binding.backdropImageView)
                        backdropImageURI = selectedImage
                    }
                    if (selectedView == binding.profileImageView) {
                        Glide.with(this).load(selectedImage).apply(RequestOptions.circleCropTransform()).into(binding.profileImageView)
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
                        prefs.profileimgsrc = profileDownloadUrl
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
                        prefs.displayimgsrc = backdropDownloadUrl
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
        if (profileImageURI != null || backdropImageURI != null) {
            uploadImageToFirebaseStorage()
        }
    }
}
