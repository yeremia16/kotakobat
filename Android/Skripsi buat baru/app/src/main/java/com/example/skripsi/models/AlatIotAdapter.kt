package com.example.skripsi.models

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.skripsi.Activities.pasien_dashboard
import com.example.skripsi.Api.DefaultResponse
import com.example.skripsi.Api.RetrofitClient
import com.example.skripsi.R
import retrofit2.Callback
import retrofit2.Response

class AlatIotAdapter (val listAlat: ArrayList<AlatIotId>, var clickListener: onButtonClickListener): RecyclerView.Adapter<AlatIotAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.list_alat_iot, parent, false)
        return AlatIotAdapter.ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return listAlat.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val alatiot:AlatIotId = listAlat[position]
        holder.bindAlat(listAlat.get(position), clickListener)
    }

    class ViewHolder(itemAlat: View): RecyclerView.ViewHolder(itemAlat){
        var textNamaAlat = itemView.findViewById<TextView>(R.id.txtNamaAlat)
        var btnSambungkan = itemView.findViewById<Button>(R.id.btnSambungkan)
        val applicationcontext = itemAlat.context
        var exist:Boolean = true

        fun bindAlat(item: AlatIotId, action: onButtonClickListener){
            textNamaAlat.text = item.nama_alat
            if(item.id_user.equals("0")){
                btnSambungkan.setText("Sambungkan")
                btnSambungkan.setTextColor(Color.BLACK)
                btnSambungkan.setBackgroundColor(Color.GREEN)
            }else if(item.id_user.equals(item.id_user_login)){
                btnSambungkan.setText("Lepas Alat")
                btnSambungkan.setTextColor(Color.BLACK)
                btnSambungkan.setBackgroundColor(Color.GRAY)
            }else{
                btnSambungkan.setText("Sedang Dipakai")
                btnSambungkan.isEnabled = false
                btnSambungkan.setBackgroundColor(Color.RED)
            }

            btnSambungkan.setOnClickListener {
                action.onItemClick(item, adapterPosition)
                Log.e("Button clicked: ", btnSambungkan.text.toString())

//              Fungsi retrofit disini
                if(btnSambungkan.text.equals("Sambungkan")){

                }else if(btnSambungkan.text.equals("Sedang Dipakai")){

                }else{
                    //Lepas Alat
                    RetrofitClient.instance.lepasalat(item.nama_alat, item.id_user)
                        .enqueue(object: Callback<DefaultResponse>{
                            override fun onFailure(call: retrofit2.Call<DefaultResponse>,t: Throwable) {
                                Toast.makeText(applicationcontext, t.message, Toast.LENGTH_LONG).show()
                            }

                            override fun onResponse(call: retrofit2.Call<DefaultResponse>,response: Response<DefaultResponse>) {
                                Toast.makeText(applicationcontext, response.body()!!.message, Toast.LENGTH_SHORT).show()
                                Toast.makeText(applicationcontext, "Silahkan kembali ke menu utama", Toast.LENGTH_SHORT).show()
                            }
                        })
                }
            }
        }
    }

    interface onButtonClickListener{
        fun onItemClick(item: AlatIotId, position: Int)
    }
}