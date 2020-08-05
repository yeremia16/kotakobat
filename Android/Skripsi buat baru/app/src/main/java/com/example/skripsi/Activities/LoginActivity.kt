package com.example.skripsi.Activities

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.skripsi.Api.RetrofitClient
import com.example.skripsi.R
import com.example.skripsi.Api.LoginResponse
import com.example.skripsi.storage.SessionManager
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Response
import java.util.*
import kotlin.collections.HashMap

class LoginActivity : AppCompatActivity() {
    lateinit var edtUsername: EditText
    lateinit var edtPassword: EditText

    lateinit var btnMasuk: Button
    lateinit var btnRegis: Button

    lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        session = SessionManager(applicationContext)
        if(session.isLoggedIn()){
            var user: HashMap<String, String> = session.getUserRole()
            var role: String = user.get(SessionManager.ROLE)!!
            Log.e("getUserRole()", role)
            if(role.equals("pengawas")){
                var i: Intent = Intent(applicationContext, pengawas_dashboard::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(i)
                finish()
            }else if(role.equals("pasien")){
                var i: Intent = Intent(applicationContext, pasien_dashboard::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(i)
                finish()
            }
        }

        edtUsername = findViewById(R.id.edt_NumberPhone)
        edtPassword = findViewById(R.id.edt_Password)

        btnMasuk = findViewById(R.id.btn_enter)
        btnRegis = findViewById(R.id.btn_register)

        btnRegis.setOnClickListener {
            startActivity(Intent(this, register::class.java))
            finish()
        }

        btnMasuk.setOnClickListener {
            doLogin()
        }
    }

    private fun doLogin(){
        val username = edtUsername.text.toString().trim()
        val password = edtPassword.text.toString().trim()

        if(username.isEmpty()){
            edt_NumberPhone.error = "Masukkan Username"
            return
        }
        if(password.isEmpty()){
            edt_Password.error = "Masukkan Password"
            return
        }

        RetrofitClient.instance.userLogin(username, password)
            .enqueue(object: retrofit2.Callback<LoginResponse> {
                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Toast.makeText(applicationContext, t.message, Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    if (!response.body()?.error!!){
//                        debugging role useer
//                        Toast.makeText(applicationContext, response.body()?.role, Toast.LENGTH_LONG).show()
                        val userRole: String = response.body()?.role.toString()
                        session.createLoginSession(username, response.body()!!.user.id, userRole)
                        if(userRole.equals("pasien")){
                            var i: Intent = Intent(applicationContext, pasien_dashboard::class.java)
                            startActivity(i)
                        }else if(userRole.equals("pengawas")){
                            var j: Intent = Intent(applicationContext, pengawas_dashboard::class.java)
                            startActivity(j)
                        }
                    }else{
                        Toast.makeText(applicationContext, response.body()?.message, Toast.LENGTH_SHORT).show()
                    }
                }

            })
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onBackPressed() {
        startActivity(Intent(applicationContext, LoginActivity::class.java))
    }
}
