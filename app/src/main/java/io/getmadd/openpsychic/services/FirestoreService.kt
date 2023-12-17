package io.getmadd.openpsychic.services

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import io.getmadd.openpsychic.model.User

class FirestoreService {
    private val db = FirebaseFirestore.getInstance()
    private val auth = Firebase.auth
    private val uID = auth.currentUser?.uid
    // user profile

    private val userProfileRef = db.collection("user").document("$uID")
    private val userHistoryRef = db.collection("history").document("$uID")
    private val psychicsOnDisplayRef = db.collection("psychicsOnDisplay")


    fun startPsychicsOnDisplayListener(callback: (ArrayList<User>?) -> Unit) {
        var psychicsOnDisplayList = ArrayList<User>()
        lateinit var psychic: User

        psychicsOnDisplayRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                // Handle error
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.documents.isNotEmpty()) {
                // Data changed, invoke the callback
                var documents = snapshot.documents

                for (document in documents)
                {
                    psychic.userID = document.data?.get("uID").toString()
                    psychic.displayname = document.data?.get("displayname").toString()
                    psychic.email = document.data?.get("email").toString()
                    psychic.firstname = document.data?.get("firstname").toString()
                    psychic.lastname = document.data?.get("lastname").toString()
                    psychic.profileImgSrc = document.data?.get("profileImgSrc").toString()
                    psychic.displayImgSrc = document.data?.get("backgroundImgSrc").toString()

                    psychicsOnDisplayList.add(psychic)
                }

                callback.invoke(psychicsOnDisplayList)
            } else {
                // Document doesn't exist
            }
        }
    }

    // Listening for updates on user profile
    fun startUserProfileListener(callback: (Map<String, Any>?) -> Unit){
        userProfileRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                // Handle error
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                // Data changed, invoke the callback
                callback.invoke(snapshot.data)
            } else {
                // Document doesn't exist
            }
        }
    }

    // Listening for updates on user history
    fun startHistoryListener(callback: (Map<String, Any>?) -> Unit){
        userHistoryRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                // Handle error
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                // Data changed, invoke the callback
                callback.invoke(snapshot.data)
            } else {
                // Document doesn't exist
            }
        }
    }
}