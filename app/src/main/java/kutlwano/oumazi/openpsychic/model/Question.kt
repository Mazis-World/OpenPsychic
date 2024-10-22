package kutlwano.oumazi.openpsychic.model

import com.google.firebase.Timestamp
import java.io.Serializable

data class Question(
    var questionId: String? = " ",
    val questionText: String? = " ",
    val questionTimestamp: Timestamp? = Timestamp.now(),
    val isAnswered: Boolean? = false,
    val answerText: String? = null,
    val answerTimestamp: Long? = null,
    val isPublic: Boolean? = false,
    val userUid: String? = " ",
    val psychicUid: String? = " ",
    val psychicUsername: String? = " ",
    val psychicProfileImg: String? = " ",
    val userRating: Int? = null,
    val userFeedback: String? = null
):Serializable
