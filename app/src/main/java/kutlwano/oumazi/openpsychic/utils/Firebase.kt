package kutlwano.oumazi.openpsychic.utils

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Firebase private constructor() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun setUserOnlineStatus(isOnline: Boolean) {
        val firebaseUser = auth.currentUser ?: return
        val userRef = db.collection("users").document(firebaseUser.uid)

        userRef.update("isOnline", isOnline)
            .addOnSuccessListener {
                Log.d(LOG_TAG, "User status updated to: $isOnline")
                updateOnlineUsersCollection(firebaseUser.uid, isOnline)
            }
            .addOnFailureListener { e ->
                Log.w(LOG_TAG, "Error updating user status", e)
            }
    }

    fun updateOnlineUsersCollection(uid: String, isOnline: Boolean) {
        val onlineUsersRef = db.collection("onlineUsers").document(uid)

        if (isOnline) {
            onlineUsersRef.set(emptyMap<String, Any>())
                .addOnSuccessListener {
                    Log.d(LOG_TAG, "Successfully added user $uid to online users")
                }
                .addOnFailureListener { e ->
                    Log.w(LOG_TAG, "Error adding user $uid to online users", e)
                }
        } else {
            onlineUsersRef.delete()
                .addOnSuccessListener {
                    Log.d(LOG_TAG, "Successfully removed user $uid from online users")
                }
                .addOnFailureListener { e ->
                    Log.w(LOG_TAG, "Error removing user $uid from online users", e)
                }
        }
    }

    companion object {
        private const val LOG_TAG = "FirebaseUserStatusManager"
        private var instance: Firebase? = null

        @Synchronized
        fun getInstance(): Firebase {
            if (instance == null) {
                instance = Firebase()
            }
            return instance!!
        }
    }
}
