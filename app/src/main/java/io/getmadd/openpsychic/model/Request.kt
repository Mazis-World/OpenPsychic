import java.sql.Timestamp

data class Request(
    val fullName: String,
    val dateOfBirth: String,
    val specificQuestion: String,
    val energyFocus: String,
    val isOpenToInsights: Boolean,
    val preferredReadingMethod: String,
    val message: String,
    val senderid: String,
    val receiverid: String,
    val timestamp: Long,
    val requeststatus: String,
    val messagetype: String,
    val requestid: String,
    val requesttype: String
)
