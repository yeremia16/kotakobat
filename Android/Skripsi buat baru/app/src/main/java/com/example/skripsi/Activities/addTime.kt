package com.example.skripsi.Activities

import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TableRow
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import com.example.skripsi.Api.AddTimeResponse
import com.example.skripsi.Api.GetObatIdResponse
import com.example.skripsi.Api.RetrofitClient
import com.example.skripsi.R
import kotlinx.android.synthetic.main.activity_add_time.*
import kotlinx.android.synthetic.main.activity_add_time.shTime1
import kotlinx.android.synthetic.main.activity_add_time.shTime2
import kotlinx.android.synthetic.main.activity_add_time.shTime3
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class addTime : AppCompatActivity() {
    lateinit var bundle: Bundle
    lateinit var id_obat: String

    lateinit var tRow1: TableRow
    lateinit var tRow2: TableRow
    lateinit var tRow3: TableRow

    lateinit var btnTime1: TextView
    lateinit var btnTime2: TextView
    lateinit var btnTime3: TextView

    lateinit var idwaktu1: String
    lateinit var idwaktu2: String
    lateinit var idwaktu3: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_time)

        tRow1 = findViewById(R.id.tableRow1)
        tRow2 = findViewById(R.id.tableRow2)
        tRow3 = findViewById(R.id.tableRow3)

        bundle = intent.extras
        val action:String = bundle.getString("action")

        if(action.equals("update")){
            val jumlah_waktu:String = bundle.getString("jumlah_waktu")
            if(jumlah_waktu.equals("1")){
                idwaktu1= bundle.getString("idWaktu1")

                shTime1.setText(bundle.getString("waktu1"))
            }else if(jumlah_waktu.equals("2")){
                idwaktu1 = bundle.getString("idWaktu1")
                idwaktu2 = bundle.getString("idWaktu2")

                shTime1.setText(bundle.getString("waktu1"))
                shTime2.setText(bundle.getString("waktu2"))
            }else{
                idwaktu1 = bundle.getString("idWaktu1")
                idwaktu2 = bundle.getString("idWaktu2")
                idwaktu3 = bundle.getString("idWaktu3")

                shTime1.setText(bundle.getString("waktu1"))
                shTime2.setText(bundle.getString("waktu2"))
                shTime3.setText(bundle.getString("waktu3"))
            }
        }

        val id_user:String = bundle.getString("id_user")
        val nama_obat:String = bundle.getString("nama_obat")
        val jumlah_obat:String = bundle.getString("jumlah_obat")
        val aturan_konsum:String = bundle.getString("aturan_konsum")
        val dosis_obat:String = bundle.getString("dosis_obat")
        val keterangan:String = bundle.getString("keterangan")

        RetrofitClient.instance.getObatId(id_user, nama_obat, jumlah_obat, aturan_konsum, dosis_obat, keterangan)
            .enqueue(object: Callback<GetObatIdResponse>{
                override fun onFailure(call: Call<GetObatIdResponse>, t: Throwable) {
                    Log.e("error id medicine", "ID Medicine gagal ambil")
                }
                override fun onResponse(
                    call: Call<GetObatIdResponse>,
                    response: Response<GetObatIdResponse>
                ) {
                    id_obat = response.body()!!.id_obat
                    Log.e("Id medicine", id_obat)
                }
            })

        var obatTime:String=""

        Log.e("id", id_user)
        Log.e("dosis", dosis_obat)


        if(dosis_obat.equals("1")){
            tRow2.visibility = TableRow.GONE
            tRow3.visibility = TableRow.GONE

            btnTime1 = findViewById(R.id.btn_Time1)

            btnTime1.setOnClickListener {
                val cal = Calendar.getInstance()
                val timeSetListener = TimePickerDialog.OnTimeSetListener{ view: TimePicker?, hour: Int, minute: Int ->
                    cal.set(Calendar.HOUR_OF_DAY, hour)
                    cal.set(Calendar.MINUTE, minute)
                    shTime1.setText(SimpleDateFormat("HH:mm").format(cal.time).toString())
                }
                TimePickerDialog(this, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
            }
        }else if(dosis_obat.equals("2")){
            tRow3.visibility = TableRow.GONE

            btnTime1 = findViewById(R.id.btn_Time1)
            btnTime2 = findViewById(R.id.btn_Time2)

            btnTime1.setOnClickListener {
                val cal = Calendar.getInstance()
                val timeSetListener = TimePickerDialog.OnTimeSetListener{ view: TimePicker?, hour: Int, minute: Int ->
                    cal.set(Calendar.HOUR_OF_DAY, hour)
                    cal.set(Calendar.MINUTE, minute)
                    shTime1.setText(SimpleDateFormat("HH:mm").format(cal.time).toString())
                }
                TimePickerDialog(this, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
            }

            btnTime2.setOnClickListener {
                val cal = Calendar.getInstance()
                val timeSetListener = TimePickerDialog.OnTimeSetListener{ view: TimePicker?, hour: Int, minute: Int ->
                    cal.set(Calendar.HOUR_OF_DAY, hour)
                    cal.set(Calendar.MINUTE, minute)
                    shTime2.setText(SimpleDateFormat("HH:mm").format(cal.time).toString())
                }
                TimePickerDialog(this, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
            }
        }else{
            btnTime1 = findViewById(R.id.btn_Time1)
            btnTime2 = findViewById(R.id.btn_Time2)
            btnTime3 = findViewById(R.id.btn_Time3)

            btnTime1.setOnClickListener {
                val cal = Calendar.getInstance()
                val timeSetListener = TimePickerDialog.OnTimeSetListener{ view: TimePicker?, hour: Int, minute: Int ->
                    cal.set(Calendar.HOUR_OF_DAY, hour)
                    cal.set(Calendar.MINUTE, minute)
                    shTime1.setText(SimpleDateFormat("HH:mm").format(cal.time).toString())
                }
                TimePickerDialog(this, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
            }

            btnTime2.setOnClickListener {
                val cal = Calendar.getInstance()
                val timeSetListener = TimePickerDialog.OnTimeSetListener{ view: TimePicker?, hour: Int, minute: Int ->
                    cal.set(Calendar.HOUR_OF_DAY, hour)
                    cal.set(Calendar.MINUTE, minute)
                    shTime2.setText(SimpleDateFormat("HH:mm").format(cal.time).toString())
                }
                TimePickerDialog(this, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
            }

            btnTime3.setOnClickListener {
                val cal = Calendar.getInstance()
                val timeSetListener = TimePickerDialog.OnTimeSetListener{ view: TimePicker?, hour: Int, minute: Int ->
                    cal.set(Calendar.HOUR_OF_DAY, hour)
                    cal.set(Calendar.MINUTE, minute)
                    shTime3.setText(SimpleDateFormat("HH:mm").format(cal.time).toString())
                }
                TimePickerDialog(this, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
            }
        }

        btn_addTime.setOnClickListener {
            if(action.equals("add")){
                if (dosis_obat.equals("1")){
                    if(shTime1.text.toString().trim().isEmpty()){
                        Toast.makeText(applicationContext, "Waktu belum diisi", Toast.LENGTH_SHORT).show()
                    }
                    RetrofitClient.instance.addTime(id_obat, id_user, shTime1.text.toString(), aturan_konsum)
                        .enqueue(object: Callback<AddTimeResponse>{
                            override fun onFailure(call: Call<AddTimeResponse>, t: Throwable) {

                            }

                            override fun onResponse(
                                call: Call<AddTimeResponse>,
                                response: Response<AddTimeResponse>
                            ) {
                                Log.e("Submit onclick", "Waktu medicine berhasil ditambah")
                            }
                        })
                    Toast.makeText(applicationContext, "Waktu Obat Berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, pasien_dashboard::class.java));
                }else if(dosis_obat.equals("2")){
                    if(shTime1.text.toString().trim().isEmpty() or shTime2.text.toString().trim().isEmpty()){
                        Toast.makeText(applicationContext, "Waktu belum diisi", Toast.LENGTH_SHORT).show()
                    }
                    RetrofitClient.instance.addTime(id_obat, id_user, shTime1.text.toString(), aturan_konsum)
                        .enqueue(object: Callback<AddTimeResponse>{
                            override fun onFailure(call: Call<AddTimeResponse>, t: Throwable) {

                            }

                            override fun onResponse(
                                call: Call<AddTimeResponse>,
                                response: Response<AddTimeResponse>
                            ) {
                                Log.e("Submit onclick", "Waktu medicine berhasil ditambah")
                            }
                        })

                    RetrofitClient.instance.addTime(id_obat, id_user, shTime2.text.toString(), aturan_konsum)
                        .enqueue(object: Callback<AddTimeResponse>{
                            override fun onFailure(call: Call<AddTimeResponse>, t: Throwable) {

                            }

                            override fun onResponse(
                                call: Call<AddTimeResponse>,
                                response: Response<AddTimeResponse>
                            ) {
                                Log.e("Submit onclick", "Waktu medicine berhasil ditambah")
                            }
                        })


                    Toast.makeText(applicationContext, "Waktu Obat Berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, pasien_dashboard::class.java));
                }else{
                    if(shTime1.text.toString().trim().isEmpty() or shTime2.text.toString().trim().isEmpty() or shTime3.text.toString().trim().isEmpty()){
                        Toast.makeText(applicationContext, "Waktu belum diisi", Toast.LENGTH_SHORT).show()
                    }
                    RetrofitClient.instance.addTime(id_obat, id_user, shTime1.text.toString(), aturan_konsum)
                        .enqueue(object: Callback<AddTimeResponse>{
                            override fun onFailure(call: Call<AddTimeResponse>, t: Throwable) {

                            }

                            override fun onResponse(
                                call: Call<AddTimeResponse>,
                                response: Response<AddTimeResponse>
                            ) {
                                Log.e("Submit onclick", "Waktu medicine berhasil ditambah")
                            }
                        })

                    RetrofitClient.instance.addTime(id_obat, id_user, shTime2.text.toString(), aturan_konsum)
                        .enqueue(object: Callback<AddTimeResponse>{
                            override fun onFailure(call: Call<AddTimeResponse>, t: Throwable) {

                            }

                            override fun onResponse(
                                call: Call<AddTimeResponse>,
                                response: Response<AddTimeResponse>
                            ) {
                                Log.e("Submit onclick", "Waktu medicine berhasil ditambah")
                            }
                        })

                    RetrofitClient.instance.addTime(id_obat, id_user, shTime3.text.toString(), aturan_konsum)
                        .enqueue(object: Callback<AddTimeResponse>{
                            override fun onFailure(call: Call<AddTimeResponse>, t: Throwable) {

                            }

                            override fun onResponse(
                                call: Call<AddTimeResponse>,
                                response: Response<AddTimeResponse>
                            ) {
                                Log.e("Submit onclick", "Waktu medicine berhasil ditambah")
                            }
                        })
                    Toast.makeText(applicationContext, "Waktu Obat Berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, pasien_dashboard::class.java));
                }
            }else if(action.equals("update")){
                if (dosis_obat.equals("1")){
                    if(shTime1.text.toString().trim().isEmpty()){
                        Toast.makeText(applicationContext, "Waktu belum diisi", Toast.LENGTH_SHORT).show()
                    }
                    RetrofitClient.instance.updateTime(idwaktu1, id_obat, id_user, shTime1.text.toString(), aturan_konsum)
                        .enqueue(object: Callback<AddTimeResponse>{
                            override fun onFailure(call: Call<AddTimeResponse>, t: Throwable) {

                            }

                            override fun onResponse(
                                call: Call<AddTimeResponse>,
                                response: Response<AddTimeResponse>
                            ) {
                                Log.e("Submit onclick", "Waktu obat berhasil ditambah")
                            }
                        })
                    Toast.makeText(applicationContext, "Waktu obat berhasil diperbarui", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, pasien_dashboard::class.java));
                }else if(dosis_obat.equals("2")){
                    if(shTime1.text.toString().trim().isEmpty() or shTime2.text.toString().trim().isEmpty()){
                        Toast.makeText(applicationContext, "Waktu belum diisi", Toast.LENGTH_SHORT).show()
                    }
                    RetrofitClient.instance.updateTime(idwaktu1, id_obat, id_user, shTime1.text.toString(), aturan_konsum)
                        .enqueue(object: Callback<AddTimeResponse>{
                            override fun onFailure(call: Call<AddTimeResponse>, t: Throwable) {

                            }

                            override fun onResponse(
                                call: Call<AddTimeResponse>,
                                response: Response<AddTimeResponse>
                            ) {
                                Log.e("Submit onclick", "Waktu obat berhasil diperbarui")
                            }
                        })

                    RetrofitClient.instance.updateTime(idwaktu2, id_obat, id_user, shTime2.text.toString(), aturan_konsum)
                        .enqueue(object: Callback<AddTimeResponse>{
                            override fun onFailure(call: Call<AddTimeResponse>, t: Throwable) {

                            }

                            override fun onResponse(
                                call: Call<AddTimeResponse>,
                                response: Response<AddTimeResponse>
                            ) {
                                Log.e("Submit onclick", "Waktu obat berhasil diperbarui")
                            }
                        })
                    Toast.makeText(applicationContext, "Waktu obat berhasil diperbarui", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, pasien_dashboard::class.java));
                }else{
                    if(shTime1.text.toString().trim().isEmpty() or shTime2.text.toString().trim().isEmpty() or shTime3.text.toString().trim().isEmpty()){
                        Toast.makeText(applicationContext, "Waktu belum diisi", Toast.LENGTH_SHORT).show()
                    }
                    RetrofitClient.instance.updateTime(idwaktu1, id_obat, id_user, shTime1.text.toString(), aturan_konsum)
                        .enqueue(object: Callback<AddTimeResponse>{
                            override fun onFailure(call: Call<AddTimeResponse>, t: Throwable) {

                            }

                            override fun onResponse(
                                call: Call<AddTimeResponse>,
                                response: Response<AddTimeResponse>
                            ) {
                                Log.e("Submit onclick", "Waktu medicine berhasil ditambah")
                            }
                        })

                    RetrofitClient.instance.updateTime(idwaktu2, id_obat, id_user, shTime2.text.toString(), aturan_konsum)
                        .enqueue(object: Callback<AddTimeResponse>{
                            override fun onFailure(call: Call<AddTimeResponse>, t: Throwable) {

                            }

                            override fun onResponse(
                                call: Call<AddTimeResponse>,
                                response: Response<AddTimeResponse>
                            ) {
                                Log.e("Submit onclick", "Waktu medicine berhasil ditambah")
                            }
                        })

                    RetrofitClient.instance.updateTime(idwaktu3, id_obat, id_user, shTime3.text.toString(), aturan_konsum)
                        .enqueue(object: Callback<AddTimeResponse>{
                            override fun onFailure(call: Call<AddTimeResponse>, t: Throwable) {

                            }

                            override fun onResponse(
                                call: Call<AddTimeResponse>,
                                response: Response<AddTimeResponse>
                            ) {
                                Log.e("Submit onclick", "Waktu medicine berhasil ditambah")
                            }
                        })
                    Toast.makeText(applicationContext, "Waktu obat berhasil diperbarui", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, pasien_dashboard::class.java));
                }
            }
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(applicationContext, pasien_dashboard::class.java))
    }
}
