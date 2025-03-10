package kutlwano.oumazi.openpsychic.fragments.home

import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import androidx.fragment.app.DialogFragment
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kutlwano.oumazi.openpsychic.R
import kutlwano.oumazi.openpsychic.model.Request
import kutlwano.oumazi.openpsychic.model.Review
import kutlwano.oumazi.openpsychic.services.UserPreferences

class RatingDialogFragment(item: Any) : DialogFragment() {
    private var db = Firebase.firestore
    var item = item

    interface RatingDialogListener {
        fun onRatingSubmitted(rating: Float, message: String)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_rating_psychic, null)

        val ratingBar = view.findViewById<RatingBar>(R.id.explorepsychicsratingBar)
        val messageEditText = view.findViewById<EditText>(R.id.messageEditText)
        val submitButton =view.findViewById<Button>(R.id.postCommentButton)
        var prefs = UserPreferences(requireActivity())

        val builder = AlertDialog.Builder(requireActivity())
            .setView(view)

        val dialog = builder.create()

        submitButton.setOnClickListener {
            val rating = ratingBar.rating
            val message = messageEditText.text.toString()

            when(item){
                is Request -> {
                    var item = item as Request
                    var userreview = db.collection("users").document(item.senderid).collection("request").document(item.receiverid).collection("review")
                    var psychicreview = db.collection("users").document(item.receiverid).collection("request").document(item.senderid).collection("review")
                    var psychicreviewspublic = db.collection("users").document(item.receiverid).collection("reviews").document()

                    val reviewMap = mapOf(
                        "uid" to item.senderid,
                        "fullname" to item.fullName,
                        "profileimgsrc" to prefs.profileimgsrc,
                        "username" to prefs.username,
                        "requestcategory" to item.requestcategory,
                        "requestreadingmethod" to item.preferredReadingMethod,
                        "requesttype" to item.requesttype,
                        "requesttimestamp" to item.timestamp,
                        "reviewrating" to rating,
                        "reviewmessage" to message,
                        "reviewtimestamp" to Timestamp.now()
                    )
                    userreview.add(reviewMap)
                    psychicreview.add(reviewMap)
                    psychicreviewspublic.set(reviewMap)
                    addRating(item.receiverid,rating)
                }
                is Review -> {
                    var item = item as Review
                    var psychicreviewspublic = db.collection("users").document(item.psychicuid).collection("reviews")
                    var userreviewspublic = db.collection("users").document(item.uid!!).collection("reviews")
                    item.reviewrating = rating.toInt()
                    item.reviewmessage = message
                    item.reviewtimestamp = Timestamp.now()
                    item.profileimgsrc = prefs.profileimgsrc!!
                    item.username = prefs.username!!
                    psychicreviewspublic.add(item)
                    userreviewspublic.add(item)
                    addRating(item.psychicuid,rating)
                }
            }

            // Notify the listener (activity or fragment) with the submitted rating and message
            (requireActivity() as? RatingDialogListener)?.onRatingSubmitted(rating, message)

            // Dismiss the dialog
            dialog.dismiss()
        }

        return dialog
    }

    fun addRating(psychicId:String, rating: Float){
        val db = FirebaseFirestore.getInstance()
        val psychicUserRef = db.collection("users").document(psychicId)

        db.runTransaction { transaction ->
            val snapshot = transaction.get(psychicUserRef)
            val currentRating = snapshot.getDouble("psychicrating") ?: 0.0
            val currentRatingCount = snapshot.getLong("psychicratingcount") ?: 0

            val newRating = currentRating + rating
            val newRatingCount = currentRatingCount + 1

            // Update the fields
            transaction.update(psychicUserRef, "psychicrating", newRating)
            transaction.update(psychicUserRef, "psychicratingcount", newRatingCount)

            null
        }.addOnSuccessListener {
            // Transaction success
            Log.d(TAG, "Transaction success!")
        }.addOnFailureListener { e ->
            // Transaction failed
            Log.w(TAG, "Transaction failure.", e)
        }
    }
}
