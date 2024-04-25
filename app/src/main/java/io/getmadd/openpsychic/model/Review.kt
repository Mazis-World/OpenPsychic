package io.getmadd.openpsychic.model

import com.google.firebase.Timestamp
import java.io.Serializable

data class Review(
    val uid: String? = null,
    var profileimgsrc: String? = null,
    var username: String? = null,
    val fullname: String? = null,
    val psychicuid: String,
    val requestcategory: String? = null,
    val requestreadingmethod: String? = null,
    val requesttype: String? = null,
    val requesttimestamp: Timestamp? = null,
    var reviewrating: Int? = null,
    var reviewmessage: String? = null,
    var reviewtimestamp: Timestamp? = null
): Serializable {
    constructor() : this(null, null, null, null, "", null, null, null, null, null, null, null)
}
