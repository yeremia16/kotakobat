package com.example.skripsi.models

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.skripsi.R

class UserAdapter (val listUser: ArrayList<UserPengawas>, var clickListener: onUserClickListener): RecyclerView.Adapter<UserAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.list_user, parent, false)
        return UserAdapter.ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return listUser.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user: UserPengawas = listUser[position]
        holder.initialize(listUser.get(position), clickListener)
    }

    class ViewHolder(itemUser: View) : RecyclerView.ViewHolder(itemUser){
        var textIdUser = itemView.findViewById<TextView>(R.id.txtIdUser)
        var textNamaUser = itemView.findViewById<TextView>(R.id.txtNamaUser)
        var textNomorHp = itemView.findViewById<TextView>(R.id.txtNomorHp)

        fun initialize(item: UserPengawas, action: onUserClickListener){
            textIdUser.text = item.id
            textNamaUser.text = item.fullname
            textNomorHp.text = item.nomor_hp

            itemView.setOnClickListener {
                action.onItemClick(item, adapterPosition)
            }
        }
    }

    interface onUserClickListener{
        fun onItemClick(item: UserPengawas, position: Int)
    }
}