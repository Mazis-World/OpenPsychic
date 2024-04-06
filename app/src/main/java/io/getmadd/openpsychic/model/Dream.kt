import com.google.firebase.Timestamp
import java.io.Serializable

// Dream.kt
data class Dream(
    val dreamId: String = "",
    val userId: String = "",
    val content: String = "",
    val timestamp: Timestamp = Timestamp.now(),
    val date: String = "",
    val hearts: Int = 0,
    val replies: Int = 0,
    val userProfileImgSrc: String = "",
    val userName: String = ""
) : Serializable