package io.getmadd.openpsychic.model

import com.google.firebase.Timestamp
import java.io.Serializable

class Message(
    val receiverid: String,
    val senderid: String,
    val message: String,
    val timestamp: Timestamp,
    val status: String
): Serializable {
    constructor() : this(
        receiverid = "",
        senderid= "",
        message = "",
        timestamp= Timestamp.now(),
        status=""
    )
}