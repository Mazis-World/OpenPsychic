import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SubscriptionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action

        if ("com.android.vending.RECEIVE_SUBSCRIPTION_CHANGED" == action) {
            // Extract subscription information
            val extras: Bundle? = intent.extras
            extras?.let {
                val isAutoRenewing = it.getBoolean("autoRenewing")
                val expirationTimeMillis = it.getLong("expirationTimeMillis")
                if (!isAutoRenewing && expirationTimeMillis == 0L) {
                    // Subscription has been cancelled or expired
                    updateSubscriptionStateInDatabase()
                }
            }
        }
    }

    private fun updateSubscriptionStateInDatabase() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            val db = FirebaseFirestore.getInstance()
            db.collection("users").document(uid)
                .update("isPremium", false)
                .addOnSuccessListener {
                    Log.d("Subscription", "Subscription state updated successfully")
                }
                .addOnFailureListener { e ->
                    Log.e("Subscription", "Error updating subscription state", e)
                }
        }
    }
}
