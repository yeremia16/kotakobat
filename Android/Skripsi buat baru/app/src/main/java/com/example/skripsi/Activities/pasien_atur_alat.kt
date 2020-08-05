package com.example.skripsi.Activities

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Adapter
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.skripsi.Api.DefaultResponse
import com.example.skripsi.Api.RetrofitClient
import com.example.skripsi.R
import com.example.skripsi.models.AlatIot
import com.example.skripsi.models.AlatIotAdapter
import com.example.skripsi.models.AlatIotId
import kotlinx.android.synthetic.main.activity_pasien_atur_alat.*
import kotlinx.android.synthetic.main.dialog_tambah_alat.view.*
import kotlinx.android.synthetic.main.list_alat_iot.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class pasien_atur_alat : AppCompatActivity(), AlatIotAdapter.onButtonClickListener {
    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pasien_atur_alat)

        val id_user:String = intent.getStringExtra("id_user")
        val listIot = ArrayList<AlatIotId>()
        var adapter: AlatIotAdapter

        val rycView = findViewById(R.id.rv_tampil) as RecyclerView
        rycView.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)

        //Get all Alat IoT
        val requestCall = RetrofitClient.instance.getListAlat(id_user)
        requestCall.enqueue(object: Callback<List<AlatIot>> {
            override fun onFailure(call: Call<List<AlatIot>>, t: Throwable) {
                Toast.makeText(applicationContext, t.message, Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<List<AlatIot>>, response: Response<List<AlatIot>>) {
//                Toast.makeText(applicationContext, response.body()?.get(0)?.nama_alat, Toast.LENGTH_LONG).show()
                if(response.isSuccessful){
                    val listAlat = response.body()!!
                    for (i in 0.. listAlat.size-1){
                        listIot.add(AlatIotId(
                            listAlat[i].nama_alat,
                            listAlat[i].id_user,
                            id_user
                        ))
                        Log.e("Nama Alat: ", listAlat[i].nama_alat)
                        Log.e("ID user: ", listAlat[i].id_user)
                        adapter = AlatIotAdapter(listIot, this@pasien_atur_alat)
                        rycView.adapter = adapter
                    }
                }
            }
        })
        btnHubungkanAlat.setOnClickListener {
            val mDialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_tambah_alat, null)
            val mBuilder = AlertDialog.Builder(this)
                .setView(mDialogView)

            val mAlertDialog = mBuilder.show()
            //Hubungkan
            mDialogView.btn_hubungkan.setOnClickListener {
                var nama_alat:String = mDialogView.txtNamaKotak.text.toString().trim()
                var password_alat:String = mDialogView.txtPassKotak.text.toString().trim()
                Log.e("NAMA_ALAT", nama_alat)
                Log.e("PASSWORD_ALAT", password_alat)
                RetrofitClient.instance.setAlat(nama_alat , password_alat, id_user).enqueue(object: Callback<DefaultResponse>{
                    override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                        Toast.makeText(applicationContext, "Proses Gagal Silahkan Coba Lagi", Toast.LENGTH_SHORT).show()
                    }

                    override fun onResponse(
                        call: Call<DefaultResponse>,
                        response: Response<DefaultResponse>
                    ) {
                        Toast.makeText(applicationContext, response.body()?.message.toString(), Toast.LENGTH_SHORT).show()
                    }
                })
                startActivity(Intent(applicationContext, pasien_dashboard::class.java))
            }
        }

        btn_kembali.setOnClickListener {
            startActivity(Intent(applicationContext, pasien_dashboard::class.java))
        }
    }
    override fun onBackPressed() {
        startActivity(Intent(applicationContext, pasien_dashboard::class.java))
    }

    override fun onItemClick(item: AlatIotId, position: Int) {

    }
}
