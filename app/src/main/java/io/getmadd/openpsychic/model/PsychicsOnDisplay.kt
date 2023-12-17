package io.getmadd.openpsychic.model
import androidx.room.Entity

@Entity(tableName = "PsychicsOnDisplay")
data class PsychicsOnDisplay(
    var psychicsOnDisplay: ArrayList<User>
)