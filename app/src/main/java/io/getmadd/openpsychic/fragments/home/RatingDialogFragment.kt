package io.getmadd.openpsychic.fragments.home

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import androidx.fragment.app.DialogFragment
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.firestore
import io.getmadd.openpsychic.R
import io.getmadd.openpsychic.model.Request

class RatingDialogFragment(request: Request) : DialogFragment() {
    private var db = Firebase.firestore
    var userreview = db.collection("users").document(request.senderid).collection("request").document(request.receiverid).collection("review")
    var psychicreview = db.collection("users").document(request.receiverid).collection("request").document(request.senderid).collection("review")
    var psychicreviewspublic = db.collection("users").document(request.receiverid).collection("reviews").document()
    var request = request

    interface RatingDialogListener {
        fun onRatingSubmitted(rating: Float, message: String)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_rating_psychic, null)

        val ratingBar = view.findViewById<RatingBar>(R.id.ratingBar)
        val messageEditText = view.findViewById<EditText>(R.id.messageEditText)
        val submitButton =view.findViewById<Button>(R.id.submitButton)

        val builder = AlertDialog.Builder(requireActivity())
            .setView(view)

        val dialog = builder.create()

        submitButton.setOnClickListener {
            val rating = ratingBar.rating
            val message = messageEditText.text.toString()

            val reviewMap = mapOf(
                "uid" to request.senderid,
                "fullname" to request.fullName,
                "requestcategory" to request.requestcategory,
                "requestreadingmethod" to request.preferredReadingMethod,
                "requesttype" to request.requesttype,
                "requesttimestamp" to request.timestamp,
                "reviewrating" to rating,
                "reviewmessage" to message,
                "reviewtimestamp" to Timestamp.now()
            )
            userreview.add(reviewMap)
            psychicreview.add(reviewMap)
            psychicreviewspublic.set(reviewMap)


            // Notify the listener (activity or fragment) with the submitted rating and message
            (requireActivity() as? RatingDialogListener)?.onRatingSubmitted(rating, message)

            // Dismiss the dialog
            dialog.dismiss()
        }

        return dialog
    }
}
