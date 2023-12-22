package io.getmadd.openpsychic.model

import com.google.firebase.Timestamp
import java.io.Serializable

class Request(
    var fullName: String = "",
    var dateOfBirth: String = "",
    var specificQuestion: String = "",
    var energyFocus: String = "",
    var openToInsights: Boolean = false,
    var preferredReadingMethod: String = "",
    var message: String = "",
    var senderid: String = "",
    var receiverid: String = "",
    var timestamp: Timestamp = Timestamp.now(),
    var requeststatus: String = "",
    var messagetype: String = "",
    var requestid: String = "",
    var requesttype: String = ""
) : Serializable {
    // No-argument constructor for Firestore deserialization
    constructor() : this(
        fullName = "",
        dateOfBirth = "",
        specificQuestion = "",
        energyFocus = "",
        openToInsights = false,
        preferredReadingMethod = "",
        message = "",
        senderid = "",
        receiverid = "",
        timestamp = Timestamp.now(),
        requeststatus = "",
        messagetype = "",
        requestid = "",
        requesttype = ""
    )
}
