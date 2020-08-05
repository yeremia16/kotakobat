package com.example.skripsi.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import com.example.skripsi.Api.RetrofitClient
import com.example.skripsi.R
import com.example.skripsi.Api.DefaultResponse
import kotlinx.android.synthetic.main.activity_register.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class register : AppCompatActivity() {
    var cek:Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        btn_submit.setOnClickListener {
            signUpUser()
        }

        btn_Batal.setOnClickListener{
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    fun signUpUser(){
        val username = edt_username.text.toString().trim()
        val fullName = edt_fullName.text.toString().trim()
        val nomorHp = edt_NumberPhone.text.toString().trim()
        val password = edt_password.text.toString().trim()
        val rePassword = edt_RePassword.text.toString().trim()

        //Setting Error tiap textbox
        if(username.isEmpty()){
            edt_username.error = "Masukkan Username"
            edt_NumberPhone.requestFocus()
            return
        };if(nomorHp.isEmpty()){
            edt_NumberPhone.error= "Masukkan Email"
            edt_NumberPhone.requestFocus()
            return
        };if(password.isEmpty()){
            edt_password.error = "Masukkan Kata Sandi"
            edt_NumberPhone.requestFocus()
            return
        };if(rePassword.isEmpty()){
            edt_RePassword.error = "Masukkan Ulang Kata Sandi"
            edt_NumberPhone.requestFocus()
            return
        };if(!password.equals(rePassword)){
            Toast.makeText(applicationContext, "Kata Sandi Harus Sama", Toast.LENGTH_SHORT).show()
            edt_RePassword.requestFocus()
            return
        };if(id_role.checkedRadioButtonId == -1){
            Toast.makeText(applicationContext, "Pilih Peran", Toast.LENGTH_SHORT).show()
            id_role.requestFocus()
            return
        };if(fullName.isEmpty()){
            edt_fullName.error="Masukkan Nama Lengkap"
            edt_fullName.requestFocus()
            return
        }

        if(!Patterns.PHONE.matcher(nomorHp).matches()){
            edt_NumberPhone.error = "Masukkan Format Nomor HP"
            edt_NumberPhone.requestFocus()
            return
        }

        var role:String = ""
        if (id_role.checkedRadioButtonId != -1) {
            if (role_pasien.isChecked) {
                role = "pasien"
            }else if (role_pengawas.isChecked) {
                role = "pengawas"
            }
        }

        RetrofitClient.instance.createUser(username, fullName, nomorHp, password, role)
            .enqueue(object: Callback<DefaultResponse>{
                override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                    Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                    return
                }

                override fun onResponse(
                    call: Call<DefaultResponse>,
                    response: Response<DefaultResponse>
                ) {
                    if(response.body()!!.error){
                        //USERNAME EXIST
                        Toast.makeText(applicationContext, response!!.body()!!.message, Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(applicationContext, response!!.body()!!.message, Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@register, LoginActivity::class.java))
                    }
                }
            })
    }
}
