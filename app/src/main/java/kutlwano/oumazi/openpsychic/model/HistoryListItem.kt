package kutlwano.oumazi.openpsychic.model

import androidx.room.Entity

@Entity(tableName = "UserHistoryObjects")
class UserHistoryObject(
    val associatedUID: String,
    val eventId: String,
    val timestamp: String,
    val eventType: EventType,
    val description: String,
    val details: String?,
)

enum class EventType{
    Message,
    REQUEST,
    LIVE
}