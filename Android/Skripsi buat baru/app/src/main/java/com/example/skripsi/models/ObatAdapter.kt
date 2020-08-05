package com.example.skripsi.models

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.skripsi.R

class ObatAdapter (val listMedicine: ArrayList<Medicine>, var clickListener: onMedicineClickListener): RecyclerView.Adapter<ObatAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.medicine_item, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return listMedicine.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val medicine: Medicine = listMedicine[position]
        holder.initialize(listMedicine.get(position), clickListener)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var textIdObat = itemView.findViewById(R.id.txtIdObat) as TextView
        val textIdUser = itemView.findViewById(R.id.txtIdUser) as TextView
        val textNamaObat = itemView.findViewById(R.id.txtNamaObat) as TextView
        val textAturanKonsumsi = itemView.findViewById(R.id.txtAturanKonsumsi) as TextView
        val textDosisKonsumsi = itemView.findViewById<TextView>(R.id.txtDosisKonsumsi)
//        val textNomorLaci = itemView.findViewById<TextView>(R.id.tampilLaci)
//        val textNomorTempat = itemView.findViewById<TextView>(R.id.tampilTempat)
        val textKeterangan = itemView.findViewById<TextView>(R.id.txtKeterangan)
        val textJumlahObat = itemView.findViewById<TextView>(R.id.txtSisaObat)

        fun initialize(item: Medicine, action: onMedicineClickListener){
            textIdObat.text = item.id_obat
            textIdUser.text = item.id_user
            textNamaObat.text = item.nama_obat
            textAturanKonsumsi.text = item.aturan_makan
            textDosisKonsumsi.text = item.dosis_konsumsi
//            textNomorLaci.text = item.nomor_laci
//            textNomorTempat.text = item.nomor_tempat
            textJumlahObat.text = item.jumlah_obat
            textKeterangan.text = item.keterangan

            itemView.setOnClickListener{
                action.onItemClick(item, adapterPosition)
            }
        }
    }

    interface onMedicineClickListener{
        fun onItemClick(item: Medicine, position: Int)
    }
}