package com.example.skripsi.Activities

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import com.example.skripsi.Api.DefaultResponse
import com.example.skripsi.Api.LoginResponse
import com.example.skripsi.Api.RetrofitClient
import com.example.skripsi.R
import kotlinx.android.synthetic.main.activity_pengawas_tambah_pasien.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class pengawas_tambah_pasien : AppCompatActivity() {
    lateinit var usernamePasien: EditText
    lateinit var passwordPasien: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pengawas_tambah_pasien)

        val bundle = intent.extras
        var id_pengawas:String = bundle.getString("id_pengawas")
        var listIdUser:ArrayList<String> = bundle.getStringArrayList("id_pasien")

        //Untuk mengecek apakah pasien sudah ada atau belum di list pengawas
        var check:Boolean = true

        usernamePasien = findViewById(R.id.edtNamaPasien)

        btnCaripasien.setOnClickListener {
            if (usernamePasien.text.toString().isEmpty()) {
                usernamePasien.error = "Masukkan Nama Pasien"
            }
            val username_pasien = usernamePasien.text.toString().trim()
            Log.e("username pasien: ", username_pasien)
            RetrofitClient.instance.getPasien(username_pasien)
                .enqueue(object: Callback<LoginResponse>{
                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        TODO("Not yet implemented")
                    }

                    override fun onResponse(
                        call: Call<LoginResponse>,
                        response: Response<LoginResponse>
                    ) {
                        val error:String = response.body()!!.error.toString()

                        if(error.equals("true")){
                            llBerhasil.visibility = LinearLayout.GONE
                            llSama.visibility = LinearLayout.GONE
                            llGagal.visibility = LinearLayout.VISIBLE
                        }else{
                            val id_user = response.body()!!.user.id.toString().trim()
                            Log.e("id_user: ", id_user)

                            for(i in 0 ..listIdUser.size-1){
                                Log.e("listIdUser: ", listIdUser[i])
                                Log.e("id_user", id_user)
                                if(id_user.equals(listIdUser[i])){
                                    check=false
                                    break
                                }
                            }
                            if(check==false){
                                llBerhasil.visibility = LinearLayout.GONE
                                llSama.visibility = LinearLayout.VISIBLE
                                llGagal.visibility = LinearLayout.GONE
                            }else{
                                llGagal.visibility = LinearLayout.GONE
                                llSama.visibility = LinearLayout.GONE
                                llBerhasil.visibility = LinearLayout.VISIBLE

                                btnHubungkan.setOnClickListener {
                                    passwordPasien = findViewById(R.id.edtPasswordPasien)
                                    val password_pasien = passwordPasien.text.toString().trim()
                                    RetrofitClient.instance.connectUser(username_pasien, password_pasien, id_user, id_pengawas)
                                        .enqueue(object: Callback<DefaultResponse>{
                                            override fun onFailure(
                                                call: Call<DefaultResponse>,
                                                t: Throwable
                                            ) {
                                                TODO("Not yet implemented")
                                            }

                                            override fun onResponse(
                                                call: Call<DefaultResponse>,
                                                response: Response<DefaultResponse>
                                            ) {
                                                Toast.makeText(applicationContext, response.body()?.message.toString(), Toast.LENGTH_SHORT).show()
                                                startActivity(Intent(this@pengawas_tambah_pasien, pengawas_dashboard::class.java))
                                            }

                                        })
                                }
                            }
                        }
                    }
                })
        }
    }
}
