package io.getmadd.openpsychic.model

import com.google.firebase.Timestamp
import java.io.Serializable

data class Question(
    val questionText: String,
    val questionTimestamp: Timestamp,
    val isAnswered: Boolean? = false,
    val answerText: String? = null,
    val answerTimestamp: Long? = null,
    val isPublic: Boolean? = false,
    val userUid: String,
    val psychicUid: String,
    val psychicUsername: String,
    val psychicProfileImg: String,
    val userRating: Int? = null,
    val userFeedback: String? = null
):Serializable
