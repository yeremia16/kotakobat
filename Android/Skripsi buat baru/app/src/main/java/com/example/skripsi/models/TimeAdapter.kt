package com.example.skripsi.models

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.skripsi.R
import kotlinx.android.synthetic.main.list_waktu_pasien.view.*
import org.w3c.dom.Text
import kotlin.coroutines.coroutineContext

class TimeAdapter (val listTime: ArrayList<PasienTime>): RecyclerView.Adapter<TimeAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.list_waktu_pasien, parent, false)
        return TimeAdapter.ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return listTime.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindTime(listTime[position])
    }

    class ViewHolder(itemTime: View) : RecyclerView.ViewHolder(itemTime){
        var textNamaObat = itemView.findViewById<TextView>(R.id.txtNamaObat)
        var textAturanKonsum = itemView.findViewById<TextView>(R.id.txtAturanKonsumsi)
        var textTime = itemView.findViewById<TextView>(R.id.txtJam)

        fun bindTime(item: PasienTime){
            textTime.text = item.waktu
            textNamaObat.text = item.nama_obat
            textAturanKonsum.text = item.aturan_makan
            if(textAturanKonsum.text.equals("Sesudah Makan")){

            }else if(textAturanKonsum.text.equals("Sebelum Makan")){
                textAturanKonsum.setTextColor(Color.BLUE)
            }
        }
    }
}