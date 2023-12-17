package io.getmadd.openpsychic.services

import RoomDb
import android.app.Application
import androidx.lifecycle.LiveData
import io.getmadd.openpsychic.model.User

class AppRepo(application: Application) {

    private val firestoreService: FirestoreService = FirestoreService()
    private val localDatabase: RoomDb = RoomDb.getDatabase(application)


    // LiveData to observe changes in local data
    fun getPsychicsOnDisplayRepo(): LiveData<List<User>> {
        return localDatabase.psychicsOnDisplayDao().getPsychicsOnDisplay()
    }

    // Method to initiate Firestore data sync
    fun syncPsychicsOnDisplay() {
        firestoreService.startPsychicsOnDisplayListener { data ->
            // Update local database when Firestore data changes
            if (data != null) {
                localDatabase.psychicsOnDisplayDao().updatePsychicsOnDisplay(data)
            }
        }
    }

}