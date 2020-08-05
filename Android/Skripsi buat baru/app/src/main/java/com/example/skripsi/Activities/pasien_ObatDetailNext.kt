package com.example.skripsi.Activities

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import com.example.skripsi.Api.ObatDeleteResponse
import com.example.skripsi.Api.RetrofitClient
import com.example.skripsi.R
import com.example.skripsi.models.Time
import kotlinx.android.synthetic.main.dialog_delete_item.view.*
import kotlinx.android.synthetic.main.medicine_item_detail.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class pasien_ObatDetailNext : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.medicine_item_detail)

        val bundle = intent.extras

        val timeData = ArrayList<Time>()

        id_obat.text = bundle.getString("id_obat")
        id_user.text = bundle.getString("id_user")
        tampilNama.text = bundle.getString("nama_obat")
        tampilAturanMakan.text = bundle.getString("aturan_konsum")
        tampilDosis.text = bundle.getString("dosis_konsum")
        tampilJumlah.text = bundle.getString("jumlah_obat")
        val nomor_laci:String = bundle.getString("nomor_laci")
//        nomor_tempat = bundle.getString("nomor_tempat")
        tampilKeterangan.text = bundle.getString("keterangan")

        val id_user = id_user.text.toString().trim()
        val id_obat = id_obat.text.toString().trim()
        val warna_tempat = bundle.getString("warna_tempat")

        if(nomor_laci=="1"){
            txtTempatLaci.text = "Laci Atas"
        }else{
            txtTempatLaci.text = "Laci Bawah"
        }
        warnaTempat.setBackgroundColor(Color.parseColor(warna_tempat))

        Log.e("id_user: ", id_user)
        Log.e("id_obat: ", id_obat)

        Log.e("Warna Tempat: ", warna_tempat)

        RetrofitClient.instance.getTime(id_user, id_obat)
            .enqueue(object: Callback<List<Time>>{
                override fun onFailure(call: Call<List<Time>>, t: Throwable) {
                    TODO("Not yet implemented")
                }

                override fun onResponse(call: Call<List<Time>>, response: Response<List<Time>>) {
                    if(response.isSuccessful){
                        val listTime = response.body()!!
                        var countListTime:Int = listTime.size-1
                        if(countListTime < 0){
                            hiasan1.visibility = LinearLayout.GONE
                            hiasan2.visibility = LinearLayout.GONE
                            hiasan3.visibility = LinearLayout.GONE
                            errorMsg.visibility = TextView.VISIBLE
                        }else if(countListTime == 0){
                            hiasan2.visibility = LinearLayout.GONE
                            hiasan3.visibility = LinearLayout.GONE
                        }else if(countListTime == 1){
                            hiasan3.visibility = LinearLayout.GONE
                        }
                        for (i in 0.. listTime.size-1) {
                            timeData.add(Time(
                                listTime[i].id_waktu, listTime[i].id_obat,
                                listTime[i].id_user, listTime[i].waktu,
                                listTime[i].aturan_makan)
                            )
                            Log.e("i", i.toString())
                            if(i==0){
                                tr_Time1.visibility = TableRow.VISIBLE
                                idWaktu1.text = timeData[i].id_waktu.trim()
                                shTime1.text = timeData[i].waktu.trim()
                                Log.e("time 1", timeData[i].toString())
                            }else if(i==1){
                                tr_Time2.visibility = TableRow.VISIBLE
                                idWaktu2.text = timeData[i].id_waktu.trim()
                                shTime2.text = timeData[i].waktu.trim()
                                Log.e("time 2", timeData[i].toString())
                            }else if(i==2){
                                tr_Time3.visibility = TableRow.VISIBLE
                                idWaktu3.text = timeData[i].id_waktu.trim()
                                shTime3.text = timeData[i].waktu.trim()
                                Log.e("time 3", timeData[i].toString())
                            }
                        }
                    }
                }
            })

        btnUbahWaktu.setOnClickListener {
            val intent = Intent(applicationContext, addTime::class.java)
            val bundle2 = Bundle()
            if (errorMsg.visibility == TextView.VISIBLE){
                val action = "add"

                bundle2.putString("action", action)
                bundle2.putString("id_obat", id_obat.trim())
                bundle2.putString("id_user", id_user)
                bundle2.putString("nama_obat", tampilNama.text.toString().trim())
                bundle2.putString("aturan_konsum", tampilAturanMakan.text.toString().trim())
                bundle2.putString("dosis_obat", tampilDosis.text.toString().trim())
                bundle2.putString("jumlah_obat", tampilJumlah.text.toString().trim())
                bundle2.putString("keterangan", tampilKeterangan.text.toString().trim())

                intent.putExtras(bundle2)
                startActivity(intent)
            }else{
                val action = "update"

                bundle2.putString("action", action)
                bundle2.putString("id_obat", id_obat.trim())
                bundle2.putString("id_user", id_user)

                bundle2.putString("nama_obat", tampilNama.text.toString().trim())
                bundle2.putString("aturan_konsum", tampilAturanMakan.text.toString().trim())
                bundle2.putString("dosis_obat", tampilDosis.text.toString().trim())
                bundle2.putString("jumlah_obat", tampilJumlah.text.toString().trim())
                bundle2.putString("keterangan", tampilKeterangan.text.toString().trim())

                if(shTime1.visibility == TableRow.VISIBLE){
                    bundle2.putString("jumlah_waktu", "1")
                    bundle2.putString("idWaktu1", idWaktu1.text.toString().trim())
                    bundle2.putString("waktu1", shTime1.text.toString().trim())
                }
                if(shTime1.visibility == TableRow.VISIBLE && shTime2.visibility == TableRow.VISIBLE) {
                    bundle2.putString("jumlah_waktu", "2")

                    bundle2.putString("idWaktu1", idWaktu1.text.toString().trim())
                    bundle2.putString("idWaktu2", idWaktu2.text.toString().trim())

                    bundle2.putString("waktu1", shTime1.text.toString().trim())
                    bundle2.putString("waktu2", shTime2.text.toString().trim())
                }
                if(shTime1.visibility == TableRow.VISIBLE && shTime2.visibility == TableRow.VISIBLE && shTime3.visibility == TableRow.VISIBLE){
                    bundle2.putString("jumlah_waktu", "3")

                    bundle2.putString("idWaktu1", idWaktu1.text.toString().trim())
                    bundle2.putString("idWaktu2", idWaktu2.text.toString().trim())
                    bundle2.putString("idWaktu3", idWaktu3.text.toString().trim())

                    bundle2.putString("waktu1", shTime1.text.toString().trim())
                    bundle2.putString("waktu2", shTime2.text.toString().trim())
                    bundle2.putString("waktu3", shTime3.text.toString().trim())
                }
                Log.e("waktu obat", bundle2.getString("jumlah_waktu"))

                intent.putExtras(bundle2)
                startActivity(intent)
            }
        }

        btnUbahInfo.setOnClickListener {
            val intent = Intent(applicationContext, pasien_ObatAdd::class.java)
            val bundle2 = Bundle()
            val action = "update"

            bundle2.putString("action", action)
            bundle2.putString("id_obat", id_obat.trim())
            bundle2.putString("id_user", id_user)
            bundle2.putString("nama_obat", tampilNama.text.toString().trim())
            bundle2.putString("aturan_konsum", tampilAturanMakan.text.toString().trim())
            bundle2.putString("dosis_obat", tampilDosis.text.toString().trim())
            bundle2.putString("jumlah_obat", tampilJumlah.text.toString().trim())
//            bundle2.putString("nomor_laci", tampilLaci.text.toString().trim())
//            bundle2.putString("nomor_tempat", tampilTempat.text.toString().trim())
            bundle2.putString("warna_tempat", warnaTempat.text.toString().trim())
            bundle2.putString("keterangan", tampilKeterangan.text.toString().trim())

            intent.putExtras(bundle2)
            startActivity(intent)
        }

        btnHapus.setOnClickListener {
            val mDialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_delete_item, null)
            val mBuilder = AlertDialog.Builder(this)
                .setView(mDialogView)

            val mAlertDialog = mBuilder.show()
            mDialogView.btn_cancel.setOnClickListener {
                mAlertDialog.dismiss()
            }

            mDialogView.btn_delete.setOnClickListener {
                RetrofitClient.instance.deleteTime(id_obat.trim()).enqueue(object: Callback<ObatDeleteResponse>{
                    override fun onFailure(call: Call<ObatDeleteResponse>, t: Throwable) {
                        Toast.makeText(applicationContext, "Waktu Obat Gagal Dihapus, coba lagi", Toast.LENGTH_SHORT).show()
                    }

                    override fun onResponse(
                        call: Call<ObatDeleteResponse>,
                        response: Response<ObatDeleteResponse>
                    ) {
                        if (response.isSuccessful){
                            Toast.makeText(applicationContext, response.body()!!.message, Toast.LENGTH_SHORT).show()
                        }else{
                            Toast.makeText(applicationContext, "Waktu Obat Gagal Dihapus", Toast.LENGTH_SHORT).show()
                        }
                    }
                })
                RetrofitClient.instance.deleteObat(id_obat.trim()).enqueue(object: Callback<ObatDeleteResponse>{
                    override fun onFailure(call: Call<ObatDeleteResponse>, t: Throwable) {
                        Toast.makeText(applicationContext, "Obat Gagal dihapus, coba lagi", Toast.LENGTH_SHORT).show()
                    }

                    override fun onResponse(
                        call: Call<ObatDeleteResponse>,
                        response: Response<ObatDeleteResponse>
                    ) {
                        if (response.isSuccessful){
                            Toast.makeText(applicationContext, response.body()!!.message, Toast.LENGTH_SHORT).show()
                        }else{

                            Toast.makeText(applicationContext, "Coba Lagi", Toast.LENGTH_SHORT).show()
                        }
                    }
                })
                startActivity(Intent(applicationContext, pasien_dashboard::class.java))
            }
        }
    }
    override fun onBackPressed() {
        startActivity(Intent(applicationContext, pasien_dashboard::class.java))
    }
}
