package com.example.skripsi.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.skripsi.Activities.pengawas_dashboard
import com.example.skripsi.R
import java.util.*

class AlarmReceiver: BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context?, intent: Intent?) {
        val role = intent?.extras?.getString("role")
        Log.e("Alarm", "Receiver: "+ Date().toString())

        if(role.equals("pengawas")){
            Log.e("Role: ", "Pengawas")
            val nama_pasien = intent?.extras?.getString("nama_pasien")
            val service = Intent(context, NotificationService::class.java)
            service.putExtra("nama_pasien", nama_pasien)
            service.putExtra("role", "pengawas")
            Log.e("nama_pasien", nama_pasien)

            context?.startService(service)
        }else{
            Log.e("Role: ", "Pasien")
            val nama_obat = intent?.extras?.getString("nama_obat")
            val service = Intent(context, NotificationService::class.java)
            service.putExtra("nama_obat", nama_obat)
            service.putExtra("role", "pasien")
            Log.e("nama_obat", nama_obat)

            context?.startService(service)
        }
    }
}