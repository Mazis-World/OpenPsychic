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
    var psychicrating: Float? = null,
    var psychicoriginyear: Int? = null,
    var psychicorigincountry: String? = null,
) : Serializable {
    constructor() : this(
        userid = null,
        email = null,
        displayname = null,
        username = null,
        usertype = null,
        firstname = null,
        lastname = null,
        bio = null,
        profileimgsrc = null,
        displayimgsrc = null,
        psychicondisplay = false,
        psychicondisplaycategory = null,
        psychicrating = null,
        psychicoriginyear = null,
        psychicorigincountry = null,
    )
}