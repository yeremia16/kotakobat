package com.example.skripsi.Activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.skripsi.Api.DefaultResponse
import com.example.skripsi.Api.RetrofitClient
import com.example.skripsi.R
import com.example.skripsi.models.PasienTime
import com.example.skripsi.models.TimeAdapter
import kotlinx.android.synthetic.main.activity_pengawas_user_detail.*
import kotlinx.android.synthetic.main.medicine_item_detail.*
import kotlinx.android.synthetic.main.medicine_item_detail.errorMsg
import kotlinx.android.synthetic.main.medicine_item_detail.tampilNama
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class pengawas_user_detail : AppCompatActivity() {

    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pengawas_user_detail)

        val timeData = ArrayList<PasienTime>()

        val bundle = intent.extras
        val id_pengawas:String = bundle.getString("id_pengawas")
        val id_user:String = bundle.getString("id_user")
        val username:String = bundle.getString("username")
        val nomorHp:String = bundle.getString("nomor_hp")

        tampilNama.text = username
        tampilNomorHp.text = nomorHp
        //Recycler view
        val rycView = findViewById<RecyclerView>(R.id.rycWaktu)
        rycView.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)

        val requestCall = RetrofitClient.instance.getUserAlarm(id_user)
        requestCall.enqueue(object: Callback<List<PasienTime>>{
                override fun onFailure(call: Call<List<PasienTime>>, t: Throwable) {
                    TODO("Not yet implemented")
                }

                override fun onResponse(
                    call: Call<List<PasienTime>>,
                    response: Response<List<PasienTime>>
                ) {
                    val timeList = response.body()!!
                    if(timeList.size-1 < 0 ){
                        errorMsg.visibility = TextView.VISIBLE

                    }else{
                        errorMsg.visibility = TextView.GONE
                        Log.e("Isi Json: ", response.body()!!.toString())
                        for(i in 0.. timeList.size-1){
                            Log.e("nama obat: ", timeList[i].nama_obat.toString())
                            Log.e("aturan konsum: ", timeList[i].aturan_makan.toString())
                            Log.e("time: ", timeList[i].waktu.toString())
                            timeData.add(PasienTime(
                                timeList[i].nama_obat.toString(),
                                timeList[i].aturan_makan.toString(),
                                timeList[i].waktu.toString()
                            ))
                            val adapter = TimeAdapter(timeData)
                            rycView.adapter = adapter
                        }
                    }
                }
            })
        btn_hapuspasien.setOnClickListener {
            RetrofitClient.instance.deletePasien(id_user, id_pengawas)
                .enqueue(object: Callback<DefaultResponse>{
                    override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                        TODO("Not yet implemented")
                    }

                    override fun onResponse(
                        call: Call<DefaultResponse>,
                        response: Response<DefaultResponse>
                    ) {
                        if(response.isSuccessful){
                            Toast.makeText(applicationContext, response.body()!!.message, Toast.LENGTH_SHORT).show()
                            startActivity(Intent(applicationContext, pengawas_dashboard::class.java))
                        }
                    }
                })
        }

        btn_kembali.setOnClickListener {
            startActivity(Intent(applicationContext, pengawas_dashboard::class.java))
        }


    }

    override fun onBackPressed() {
        startActivity(Intent(applicationContext, pengawas_dashboard::class.java))
    }
}
