package kutlwano.oumazi.openpsychic.model

import com.google.firebase.Timestamp
import java.io.Serializable

class User(
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
    var joinedlivestreams: ArrayList<String>?,
    var questionsAvailable: Int = 0,
    var requestcount: Int,
    var isPremium: Boolean = false,
    var isOnline: Boolean? = false,
    var lastOnline: Timestamp?
    ): Serializable