package io.getmadd.openpsychic.model

import java.io.Serializable

class Psychic(
    var userid: String,
    var email: String,
    var displayname: String,
    var username: String,
    var usertype: String,
    var firstname: String,
    var lastname: String,
    var bio: String,
    var profileimgsrc: String,
    var displayimgsrc: String,
    var psychicondisplay: Boolean,
    var psychicondisplaycategory: String?
) : Serializable