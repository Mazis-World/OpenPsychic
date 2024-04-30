package io.getmadd.openpsychic.model

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
    var requestcount: Int
)