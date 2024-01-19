package io.getmadd.openpsychic.model

import com.google.firebase.Timestamp
import java.io.Serializable

class Message(
    var receiverid: String,
    var senderid: String,
    var message: String,
    var timestamp: Timestamp,
    var status: String
): Serializable {
    constructor() : this(
        receiverid = "",
        senderid= "",
        message = "",
        timestamp= Timestamp.now(),
        status=""
    )
}