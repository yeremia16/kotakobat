package com.example.skripsi.Activities

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.skripsi.Api.RetrofitClient
import com.example.skripsi.R
import com.example.skripsi.models.TimeOnly
import com.example.skripsi.models.UserAdapter
import com.example.skripsi.models.UserPengawas
import com.example.skripsi.notification.AlarmReceiver
import com.example.skripsi.storage.SessionManager
import kotlinx.android.synthetic.main.activity_pengawas_dashboard.*
import retrofit2.Call
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList
import kotlin.time.ExperimentalTime


class pengawas_dashboard : AppCompatActivity(), UserAdapter.onUserClickListener {
    lateinit var session: SessionManager

    //Alarm manager
    lateinit var context: Context
    lateinit var alarmManager: AlarmManager
    lateinit var calendar: Calendar
    lateinit var inn: Intent
    lateinit var pendingInn: PendingIntent
    lateinit var idPengawas: String

    @ExperimentalTime
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pengawas_dashboard)

        val listTime = java.util.ArrayList<TimeOnly>()
        val listBroadcastId = ArrayList<Int>()

        val listUser = ArrayList<UserPengawas>()
        val listIdUser = ArrayList<String>()

        session = SessionManager(applicationContext)
        session.checkLogin()

        var user: HashMap<String, String> = session.getUserDetails()
        idPengawas = user.get(SessionManager.USER_ID)!!

        //Tes Broadcast receiver
        context=this
        alarmManager=getSystemService(Context.ALARM_SERVICE) as AlarmManager

//      Cek id pengawas
        Log.e("id_pengawas", idPengawas)

        //Recycler view
        val rycView = findViewById(R.id.rv_tampilUser) as RecyclerView
        rycView.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)

        //Get All User
        val requestCall = RetrofitClient.instance.getAllPasien(idPengawas)
        requestCall.enqueue(object: retrofit2.Callback<List<UserPengawas>>{
            override fun onFailure(call: Call<List<UserPengawas>>, t: Throwable) {
                Toast.makeText(applicationContext, t.message, Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(
                call: Call<List<UserPengawas>>,
                response: Response<List<UserPengawas>>
            ) {
                val userList = response.body()!!
                for(i in 0.. userList.size-1){
                    listUser.add(UserPengawas(
                        userList[i].id,
                        userList[i].fullname,
                        userList[i].nomor_hp
                    ))
                    listIdUser.add(userList[i].id)
                    val adapter = UserAdapter(listUser, this@pengawas_dashboard)
                    rycView.adapter = adapter
                }
            }
        })

        btn_SetAlarm.setOnClickListener {
            //Get All waktu
            val requestTime = RetrofitClient.instance.getAllUserAlarm(idPengawas)
            requestTime.enqueue(object: retrofit2.Callback<List<TimeOnly>>{
                override fun onFailure(call: Call<List<TimeOnly>>, t: Throwable) {
                    Toast.makeText(applicationContext, t.message, Toast.LENGTH_SHORT).show()
                }
                override fun onResponse(
                    call: Call<List<TimeOnly>>,
                    response: Response<List<TimeOnly>>
                ) {
                    val timeList = response.body()!!
                    for(i in 0..timeList.size-1){
                        listTime.add(TimeOnly(
                            timeList[i].username,
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
                        inn.putExtra("nama_pasien", timeList[i].username)
                        inn.putExtra("role", "pengawas")

                        val requestId = System.currentTimeMillis().toInt()
                        pendingInn = PendingIntent.getBroadcast(this@pengawas_dashboard, requestId, inn, 0)
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

        btn_tambahPasien.setOnClickListener {
            val intent = Intent(this, pengawas_tambah_pasien::class.java)
            val bundle = Bundle()
            bundle.putString("id_pengawas", idPengawas)
            bundle.putStringArrayList("id_pasien", listIdUser)

            intent.putExtras(bundle)
            startActivity(intent)
        }

        btn_logout.setOnClickListener{
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val myIntent = Intent(applicationContext, AlarmReceiver::class.java)
            for(i in 0..listBroadcastId.size-1){
                val pendingIntent = PendingIntent.getBroadcast(applicationContext, listBroadcastId.get(i), myIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                alarmManager.cancel(pendingIntent)
            }
            session.logoutUser()
            session.checkLogin()
        }
    }

    override fun onItemClick(item: UserPengawas, position: Int) {
        val intent = Intent(this, pengawas_user_detail::class.java)
        val bundle = Bundle()
        bundle.putString("id_pengawas", idPengawas)
        bundle.putString("id_user", item.id)
        bundle.putString("username", item.fullname)
        bundle.putString("nomor_hp", item.nomor_hp)

        intent.putExtras(bundle)
        startActivity(intent)
    }

    override fun onBackPressed() {
        startActivity(Intent(applicationContext, pengawas_dashboard::class.java))
    }
}