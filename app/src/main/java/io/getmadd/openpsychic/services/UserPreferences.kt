package io.getmadd.openpsychic.services

import android.content.Context
import android.content.SharedPreferences

class UserPreferences(context: Context) {
    private val sharedPreferences: SharedPreferences

    init {
        sharedPreferences = context.getSharedPreferences(pref_name, Context.MODE_PRIVATE)
    }

    fun saveSubscription(state: String){
        val editor = sharedPreferences.edit()
        editor.putString(key_subscriptionstate, state)
        editor.apply()
    }

    fun saveUser(userid: String?, email: String?,displayname: String?,username: String?,usertype:
    String?,firstname: String?,lastname: String?, bio: String?, profileimgsrc: String?, displayimgsrc: String?
    ) {
        val editor = sharedPreferences.edit()
        editor.putString(key_uid, userid)
        editor.putString(key_email, email)
        editor.putString(key_displayname, displayname)
        editor.putString(key_username, username)
        editor.putString(key_usertype, usertype)
        editor.putString(key_firstname, firstname)
        editor.putString(key_lastname, lastname)
        editor.putString(key_bio, bio)
        editor.putString(key_profileimgsrc, profileimgsrc)
        editor.putString(key_displayimgsrc, displayimgsrc)
        editor.apply()
    }

    val uid: String?
        get() = sharedPreferences.getString(key_uid, "")
    val email: String?
        get() = sharedPreferences.getString(key_email, "")
    val displayname: String?
        get() = sharedPreferences.getString(key_displayname, "")
    val username: String?
        get() = sharedPreferences.getString(key_username, "")
    val usertype: String?
        get() = sharedPreferences.getString(key_usertype, "")
    val firstname: String?
        get() = sharedPreferences.getString(key_firstname, "")
    val lastname: String?
        get() = sharedPreferences.getString(key_lastname, "")
    val bio: String?
        get() = sharedPreferences.getString(key_bio, "")
    val profileimgsrc: String?
        get() = sharedPreferences.getString(key_profileimgsrc, "")
    val displayimgsrc: String?
        get() = sharedPreferences.getString(key_displayimgsrc, "")
    val subscriptionstate: String?
        get() = sharedPreferences.getString(key_subscriptionstate, "")

    companion object {
        private const val pref_name = "user_prefs"
        private const val key_uid = "uid"
        private const val key_email = "email"
        private const val key_displayname = "displayname"
        private const val key_username = "username"
        private const val key_usertype = "usertype"
        private const val key_firstname = "firstname"
        private const val key_lastname = "lastname"
        private const val key_bio = "bio"
        private const val key_profileimgsrc = "profileimgsrc"
        private const val key_displayimgsrc = "displayimgsrc"

        // subscription
        private const val key_subscriptionstate = "state"
    }
}