package com.example.skripsi.Activities

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.skripsi.Api.RetrofitClient
import com.example.skripsi.R
import com.example.skripsi.models.Medicine
import com.example.skripsi.models.ObatAdapter
import com.example.skripsi.models.TimePasien
import com.example.skripsi.notification.AlarmReceiver
import com.example.skripsi.storage.SessionManager
import kotlinx.android.synthetic.main.activity_pasien_dashboard.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class pasien_dashboard: AppCompatActivity(), ObatAdapter.onMedicineClickListener {
    lateinit var session: SessionManager

    //Alarm manager
    lateinit var context: Context
    lateinit var alarmManager: AlarmManager
    lateinit var calendar: Calendar
    lateinit var inn: Intent
    lateinit var pendingInn: PendingIntent

    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pasien_dashboard)

        val listObat = ArrayList<Medicine>()
        val listTime = ArrayList<TimePasien>()
        val listBroadcastId = ArrayList<Int>()

        session = SessionManager(applicationContext)
        session.checkLogin()

        var user: HashMap<String, String> = session.getUserDetails()
        var id_user: String = user.get(SessionManager.USER_ID)!!

        //Tes Broadcast receiver
        context=this
        alarmManager=getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val rycView = findViewById(R.id.rv_tampil) as RecyclerView
        rycView.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)

        //Get all medicine
        val requestCall = RetrofitClient.instance.getAllMedicine(id_user)
        requestCall.enqueue(object: Callback<List<Medicine>>{
            override fun onFailure(call: Call<List<Medicine>>, t: Throwable) {
                Toast.makeText(applicationContext, t.message, Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<List<Medicine>>, response: Response<List<Medicine>>) {
                if(response.isSuccessful){
                    val obatList = response.body()!!
                    Log.e("JSON: ", obatList.toString())
                    for (i in 0.. obatList.size-1) {
                        listObat.add(Medicine(
                            obatList[i].id_obat, obatList[i].id_user,
                            obatList[i].nama_obat, obatList[i].jumlah_obat,
                            obatList[i].aturan_makan, obatList[i].warna_tempat,
                            obatList[i].dosis_konsumsi, obatList[i].nomor_laci,
                            obatList[i].nomor_tempat, obatList[i].keterangan)
                        )
                        Log.e("Obat nomor laci: ", obatList[i].nomor_laci)
                        Log.e("Obat nomor tempat: ", obatList[i].nomor_tempat)
                        val adapter = ObatAdapter(listObat, this@pasien_dashboard)
                        rycView.adapter = adapter
                    }
                }
            }
        })

        btnAturAlat.setOnClickListener {
            val intent = Intent(applicationContext, pasien_atur_alat::class.java)
            intent.putExtra("id_user", id_user)
            startActivity(intent)
        }

        fab_add_schedule.setOnClickListener {
            val intent = Intent(applicationContext, pasien_ObatAdd::class.java)
            val bundle2 = Bundle()
            val action = "add"
            bundle2.putString("id_user", id_user)
            bundle2.putString("action", action)
            intent.putExtras(bundle2)
            startActivity(intent)
        }

        btn_SetAlarm.setOnClickListener {
            val requestTime = RetrofitClient.instance.getAllTime(id_user)
            requestTime.enqueue(object: Callback<List<TimePasien>>{
                override fun onFailure(call: Call<List<TimePasien>>, t: Throwable) {
                    Toast.makeText(applicationContext, t.message, Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(
                    call: Call<List<TimePasien>>,
                    response: Response<List<TimePasien>>
                ) {
                    val timeList = response.body()!!
                    for(i in 0..timeList.size-1) {
                        listTime.add( TimePasien(
                            timeList[i].nama_obat,
                            timeList[i].waktu
                        ))

                        var sJam:String = timeList[i].waktu.substring(0, 2)
                        var iJam:Int = sJam.toInt()
                        var sMenit:String = timeList[i].waktu.substring(3, 5)
                        var iMenit:Int = sMenit.toInt()
                        calendar = Calendar.getInstance().apply {
                            set(Calendar.HOUR_OF_DAY, iJam)
                            set(Calendar.MINUTE, iMenit)
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                        }

                        inn = Intent(context, AlarmReceiver::class.java)
                        inn.putExtra("nama_obat", timeList[i].nama_obat)
                        inn.putExtra("role", "pasien")

                        val requestId = System.currentTimeMillis().toInt()
                        pendingInn = PendingIntent.getBroadcast(this@pasien_dashboard, requestId, inn, 0)
                        listBroadcastId.add(i, requestId)

//                        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+5, AlarmManager.INTERVAL_DAY ,pendingInn)
                        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY ,pendingInn)
                        Log.e("Alarm: ", "Created: " + iJam.toString()+":"+iMenit.toString())
                    }
                }
            })
            Toast.makeText(applicationContext, "Alaram berhasil dibuat", Toast.LENGTH_SHORT).show()
        }

        btn_DeleteAlarm.setOnClickListener {
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val myIntent = Intent(applicationContext, AlarmReceiver::class.java)
            for(i in 0..listBroadcastId.size-1){
                val pendingIntent = PendingIntent.getBroadcast(applicationContext, listBroadcastId.get(i), myIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                alarmManager.cancel(pendingIntent)
            }
            Toast.makeText(applicationContext, "Alaram berhasil dihapus", Toast.LENGTH_SHORT).show()
        }

        btn_logout.setOnClickListener{
            session.logoutUser()
            session.checkLogin()
        }
    }

//    fun finishMe() { finish() }

    override fun onBackPressed() {
        startActivity(Intent(applicationContext, this::class.java))
    }

    override fun onItemClick(item: Medicine, position: Int) {
        val intent = Intent(this, pasien_ObatDetailNext::class.java)
        val bundle3 = Bundle()

        bundle3.putString("id_obat", item.id_obat)
        bundle3.putString("id_user", item.id_user)
        bundle3.putString("nama_obat", item.nama_obat)
        bundle3.putString("aturan_konsum", item.aturan_makan)
        bundle3.putString("dosis_konsum", item.dosis_konsumsi)
        bundle3.putString("nomor_laci", item.nomor_laci)
        bundle3.putString("warna_tempat", item.warna_tempat)
        bundle3.putString("jumlah_obat", item.jumlah_obat)
        bundle3.putString("keterangan", item.keterangan)

        intent.putExtras(bundle3)
        startActivity(intent)
    }
}