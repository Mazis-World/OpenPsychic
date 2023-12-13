package io.getmadd.openpsychic.model


data class User(
    val backgroundImgSrc: String,
    val bio: String,
    val username: String,
    val profileImgSrc: String,
    val uId: String,
    val displayName: String,
    val email: String,
)