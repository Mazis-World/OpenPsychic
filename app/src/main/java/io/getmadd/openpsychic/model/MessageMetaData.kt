package io.getmadd.openpsychic.model

import com.google.firebase.Timestamp
import java.io.Serializable

class MessageMetaData(
    var receiverid: String? = null,
    var senderid: String? = null,
    var psychicdisplayname: String? = null,
    var username: String? = null,
    var userprofileimgsrc: String? = null,
    var psychicprofileimgsrc: String? = null,
    var createdat: Timestamp? = null,
    var lastupdate: Timestamp? = null
):Serializable{
    constructor() : this(
        receiverid = "",
        senderid= "",
        psychicdisplayname = "",
        psychicprofileimgsrc = "",
        username = "",
        userprofileimgsrc = "",
        createdat = Timestamp.now(),
        lastupdate = Timestamp.now()
    )
}