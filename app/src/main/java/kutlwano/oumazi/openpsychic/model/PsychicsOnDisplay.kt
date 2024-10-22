package kutlwano.oumazi.openpsychic.model
import androidx.room.Entity

@Entity(tableName = "PsychicsOnDisplay")
data class PsychicsOnDisplay(
    var psychicsondisplay: ArrayList<Psychic>
)