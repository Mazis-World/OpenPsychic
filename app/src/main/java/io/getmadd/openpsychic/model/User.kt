package io.getmadd.openpsychic.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "UserProfile")
data class User(
    @PrimaryKey
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
)