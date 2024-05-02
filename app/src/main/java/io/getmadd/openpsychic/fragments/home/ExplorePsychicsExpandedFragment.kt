package io.getmadd.openpsychic.fragments.home

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.getmadd.openpsychic.R
import io.getmadd.openpsychic.databinding.FragmentExplorePsychicsExpandedBinding
import io.getmadd.openpsychic.fragments.features.DreamAdapter
import io.getmadd.openpsychic.fragments.features.ReviewsAdapter
import io.getmadd.openpsychic.model.MessageMetaData
import io.getmadd.openpsychic.model.Psychic
import io.getmadd.openpsychic.model.Review

class ExplorePsychicsExpandedFragment: Fragment() {

    private lateinit var _binding: FragmentExplorePsychicsExpandedBinding
    private val binding get() = _binding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExplorePsychicsExpandedBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bundle = arguments
        val db = FirebaseFirestore.getInstance()
        var auth = FirebaseAuth.getInstance()
        var userId = auth.uid.toString()
        var userType: String? = null
        var usermetadata = MessageMetaData()

        lateinit var psychic: Psychic
        if (bundle != null) {
            psychic = (bundle.getSerializable("psychic") as? Psychic)!!
        }

        if(psychic.requestcount != null){
            binding.requestsnumtextview.text = psychic.requestcount.toString()
        }

        if(psychic.psychicrating != null){
            binding.explorepsychicexpandedsratingBar.rating = (psychic.psychicrating!! / psychic.psychicratingcount!!).toFloat()
            binding.ratingnumtextview.text = (psychic.psychicrating!! / psychic.psychicratingcount!!).toString()
        }
        else {
            binding.explorepsychicexpandedsratingBar.rating = 3.5F
            binding.ratingnumtextview.text = "3.5"

        }
       if(psychic.psychicoriginyear != null){
           binding.sincedatetextview.text = psychic.psychicoriginyear.toString()
       }else{
           binding.sincedatetextview.text = "2024"
       }

        binding.expandedBioTextView.text = psychic.bio
        binding.expandedDisplayNameTextView.text = psychic.displayname
        binding.expandedUsernameTextView.text = "@"+psychic.username

        if(psychic.displayimgsrc != " "){
            Glide.with(this).load(psychic.displayimgsrc).into(binding.expandedBackgroundImageView)
        }
        else
            Glide.with(this).load(R.drawable.openpsychiclogo).into(binding.expandedBackgroundImageView)

        db.collection("users").document(userId)
            .get()
            .addOnSuccessListener { result ->
                userType = result.getString("usertype").toString()
                userType = result.getString("usertype").toString()

                if(userType == "user") {
                    usermetadata.userprofileimgsrc = result.getString("profileimgsrc").toString()
                    usermetadata.username = result.getString("username").toString()
                }
                else if (userType == "psychic"){
                }
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents.", exception)
            }

        db.collection("users").document(userId).collection("paymentmethods")
            .get()
            .addOnSuccessListener { result ->
                var paymentProvider = "No Provider Set"
                var paymentAddress = "No Address  Set"
                for(doc in result){
                    //should be 1
                    paymentProvider = doc.getString("provider").toString()
                    paymentAddress = doc.getString("address").toString()
                }
                binding.paymentMethodAddressTextView.text = paymentAddress
                binding.paymentMethodProviderTextView.text = paymentProvider
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents.", exception)
            }

        val firestore = FirebaseFirestore.getInstance()
        val reviewsRef = firestore.collection("users").document(psychic.userid.toString()).collection("reviews")
        val reviewsList = mutableListOf<Review>()
        val layoutManager = LinearLayoutManager(context)
        val adapter = ReviewsAdapter(reviewsList)

        binding.reviewsRecyclerView.layoutManager = layoutManager
        binding.reviewsRecyclerView.adapter = adapter

        reviewsRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.e("Review Listener", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && !snapshot.isEmpty) {
                binding.noReviewsTextView.visibility = View.GONE
                binding.reviewsRecyclerView.visibility = View.VISIBLE
                reviewsList.clear()
                for (document in snapshot.documents) {
                    val review = document.toObject(Review::class.java)
                    if (review != null) {
                        reviewsList.add(review)
                    }
                }
                adapter.notifyDataSetChanged()
            } else {
                Log.d("Review Listener", "No comments found")
            }
        }

        binding.requestreadingbtn.setOnClickListener{
            if(userType == "psychic") {
                Toast.makeText(context,"Psychic Function Not Allowed",Toast.LENGTH_SHORT).show()
            }
            else{
                val bundle = Bundle()
                bundle.putSerializable("psychic", psychic)
                findNavController().navigate(R.id.action_explore_psychics_expanded_to_request_reading_fragment, bundle)
            }
        }
        binding.sendprivatemessagebtn.setOnClickListener{
            if(userType == "psychic") {
                Toast.makeText(context,"Psychic Function Not Allowed",Toast.LENGTH_SHORT).show()
            }
            else {
                usermetadata.psychicprofileimgsrc = psychic.profileimgsrc
                val bundle = Bundle()
                bundle.putSerializable("psychic", psychic)
                bundle.putSerializable("usermetadata",usermetadata)
                findNavController().navigate(
                    R.id.action_explore_psychics_expanded_to_message_thread_fragment,
                    bundle
                )
            }
        }
        binding.startvoicethread.setOnClickListener{

        }
        binding.textViewLeaveAReview.setOnClickListener{
            var review = Review(
                uid = userId,
                psychicuid = psychic.userid!!
            )
            val dialog = RatingDialogFragment(review)
            dialog.show(fragmentManager!!,"RequestReviewDialog")
        }
        binding.fabAskPsychicAdvisor.setOnClickListener{
            val bundle = Bundle()
            bundle.putSerializable("psychic", psychic)
            findNavController().navigate(R.id.action_expanded_to_ask_advisor_fragment, bundle)
        }
    }
}