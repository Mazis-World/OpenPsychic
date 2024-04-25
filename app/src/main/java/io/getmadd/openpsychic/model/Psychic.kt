package io.getmadd.openpsychic.model

import com.google.firebase.Timestamp
import java.io.Serializable

class Psychic (
    var userid: String? = null,
    var email: String? = null,
    var displayname: String? = null,
    var username: String? = null,
    var usertype: String? = null,
    var firstname: String? = null,
    var lastname: String? = null,
    var bio: String? = null,
    var profileimgsrc: String? = null,
    var displayimgsrc: String? = null,
    var psychicondisplay: Boolean? = null,
    var psychicondisplaycategory: String? = null,
    var psychicrating: Double? = null,
    var psychicratingcount: Double? = null,
    var psychicoriginyear: Int? = null,
    var psychicorigincountry: String? = null,
    var requestcount: Int? = null
) : Serializable {
}