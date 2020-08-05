package com.example.skripsi.Api

import com.example.skripsi.models.User

data class LoginResponse(val error: Boolean, val message:String, val role:String, val user: User)