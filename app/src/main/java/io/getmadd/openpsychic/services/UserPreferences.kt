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

    var uid: String?
        get() = sharedPreferences.getString(key_uid, "")
        set(value) = sharedPreferences.edit().putString(key_uid, value).apply()

    var email: String?
        get() = sharedPreferences.getString(key_email, "")
        set(value) = sharedPreferences.edit().putString(key_email, value).apply()

    var displayname: String?
        get() = sharedPreferences.getString(key_displayname, "")
        set(value) = sharedPreferences.edit().putString(key_displayname, value).apply()

    var username: String?
        get() = sharedPreferences.getString(key_username, "")
        set(value) = sharedPreferences.edit().putString(key_username, value).apply()

    var usertype: String?
        get() = sharedPreferences.getString(key_usertype, "")
        set(value) = sharedPreferences.edit().putString(key_usertype, value).apply()

    var firstname: String?
        get() = sharedPreferences.getString(key_firstname, "")
        set(value) = sharedPreferences.edit().putString(key_firstname, value).apply()

    var lastname: String?
        get() = sharedPreferences.getString(key_lastname, "")
        set(value) = sharedPreferences.edit().putString(key_lastname, value).apply()

    var bio: String?
        get() = sharedPreferences.getString(key_bio, "")
        set(value) = sharedPreferences.edit().putString(key_bio, value).apply()

    var profileimgsrc: String?
        get() = sharedPreferences.getString(key_profileimgsrc, "")
        set(value) = sharedPreferences.edit().putString(key_profileimgsrc, value).apply()

    var displayimgsrc: String?
        get() = sharedPreferences.getString(key_displayimgsrc, "")
        set(value) = sharedPreferences.edit().putString(key_displayimgsrc, value).apply()

    var subscriptionstate: String?
        get() = sharedPreferences.getString(key_subscriptionstate, "")
        set(value) = sharedPreferences.edit().putString(key_subscriptionstate, value).apply()

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