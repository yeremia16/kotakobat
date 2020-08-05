package com.example.skripsi.storage

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import com.example.skripsi.Activities.LoginActivity

class SessionManager {
    lateinit var pref: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    lateinit var con: Context
    var PRIVATE_MODE: Int = 0

    constructor(con: Context){
        this.con = con
        pref = con.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        editor = pref.edit()
    }

    companion object{
        val PREF_NAME: String = "KotlinDemo"
        val IS_LOGIN: String = "isLoggedIn"
        val NUMBER_PHONE: String = "numberphone"
        val USER_ID:String = "user_id"
        val ROLE:String = "role"
    }

    fun createLoginSession(numberphone:String, userid:String, role:String){
        editor.putBoolean(IS_LOGIN, true)
        editor.putString(NUMBER_PHONE, numberphone)
        editor.putString(USER_ID, userid)
        editor.putString(ROLE, role)
        editor.commit()
    }

    fun checkLogin(){
        if(!this.isLoggedIn()){
            var i: Intent = Intent(con, LoginActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            con.startActivity(i)
        }
    }

    fun getUserDetails(): HashMap<String, String>{
        var user: Map<String, String> = HashMap<String, String>()
        (user as HashMap).put(NUMBER_PHONE,pref.getString(NUMBER_PHONE, null))
        (user as HashMap).put(USER_ID,pref.getString(USER_ID, null))
        return user
    }

    fun getUserRole(): HashMap<String, String>{
        var user: Map<String, String> = HashMap<String, String>()
        (user as HashMap).put(NUMBER_PHONE,pref.getString(NUMBER_PHONE, null))
        (user as HashMap).put(ROLE,pref.getString(ROLE, null))
        return user
    }

    fun logoutUser(){
        editor.clear()
        editor.apply()
        editor.commit()
        checkLogin()
    }

    fun isLoggedIn(): Boolean{
        return pref.getBoolean(IS_LOGIN, false)
    }
}