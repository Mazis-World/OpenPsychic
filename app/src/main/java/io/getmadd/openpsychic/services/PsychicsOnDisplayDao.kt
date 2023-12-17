package io.getmadd.openpsychic.services

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import io.getmadd.openpsychic.model.User

@Dao
interface PsychicsOnDisplayDao {

    @Query("SELECT * FROM PsychicsOnDisplay")
    fun getPsychicsOnDisplay(): LiveData<List<User>>

    @Update
    fun updatePsychicsOnDisplay (data: ArrayList<User>)

}
