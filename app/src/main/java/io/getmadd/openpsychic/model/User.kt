package io.getmadd.openpsychic.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "UserProfile")
data class User(
    @PrimaryKey
    var userID: String,
    var email: String,
    var displayname: String,
    val username: String,
    val usertype: String,
    var firstname: String,
    var lastname: String,
    val bio: String,
    var profileImgSrc: String,
    var displayImgSrc: String,
    val joinedLiveStreams: ArrayList<String>?,
)