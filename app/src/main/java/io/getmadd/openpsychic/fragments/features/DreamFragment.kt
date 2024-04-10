package io.getmadd.openpsychic.fragments.features

import Dream
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import io.getmadd.openpsychic.R
import io.getmadd.openpsychic.services.UserPreferences
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class DreamsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private lateinit var prefs: UserPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dreams, container, false)

        prefs = UserPreferences(requireContext())

        val fabAddDream: FloatingActionButton = view.findViewById(R.id.fab_add_dream)
        recyclerView = view.findViewById(R.id.recycler_view_dreams)
        recyclerView.layoutManager = LinearLayoutManager(context)

        getDreamsFromFirestore()

        fabAddDream.setOnClickListener {
            showMakePostBottomSheet()
        }

        return view
    }

    private fun getDreamsFromFirestore() {
        firestore.collection("dreamPost")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                val dreams = mutableListOf<Dream>()
                for (document in result) {
                    val dream = document.toObject(Dream::class.java)
                    dreams.add(dream)
                }
                updateAdapter(dreams)
            }
            .addOnFailureListener { e ->
                // Handle failure
            }
    }

    private fun updateAdapter(dreams: List<Dream>) {
        val adapter = DreamAdapter(context,dreams)
        recyclerView.adapter = adapter
    }

    private fun showMakePostBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val bottomSheetView = layoutInflater.inflate(R.layout.layout_make_post_dream, null)
        bottomSheetDialog.setContentView(bottomSheetView)

        val editTextPostContent = bottomSheetView.findViewById<EditText>(R.id.edit_text_post_content)
        val buttonPost = bottomSheetView.findViewById<Button>(R.id.button_post)

        buttonPost.setOnClickListener {
            val postContent = editTextPostContent.text.toString()
            addDreamToFirestore(postContent)
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.show()
    }

    private fun addDreamToFirestore(content: String) {

        val dream = hashMapOf(
            "userId" to auth.currentUser!!.uid,
            "userName" to prefs.username,
            "userProfileImgSrc" to prefs.profileimgsrc,
            "content" to content,
            "timestamp" to Timestamp.now(),
            "date" to getCurrentDate(),
            "replies" to 0,
            "hearts" to 0
        )

        firestore.collection("dreamPost")
            .add(dream)
            .addOnSuccessListener { documentReference ->
                val dreamId = documentReference.id
                documentReference.update("dreamId", dreamId)
                    .addOnSuccessListener {
                        // Handle success
                        Log.d(TAG, "DocumentSnapshot successfully updated!")
                    }
                    .addOnFailureListener { e ->
                        // Handle error
                        Log.w(TAG, "Error updating document", e)
                    }
            }
            .addOnFailureListener { e ->
                // Handle error
                Log.w(TAG, "Error adding document", e)
            }
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = Calendar.getInstance().time
        return dateFormat.format(date)
    }

}