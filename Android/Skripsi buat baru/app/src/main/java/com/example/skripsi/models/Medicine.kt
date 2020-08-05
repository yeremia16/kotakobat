package com.example.skripsi.models

data class Medicine(
    val id_obat:String,
    val id_user:String,
    val nama_obat:String,
    val jumlah_obat:String,
    val aturan_makan:String,
    val warna_tempat:String,
    val dosis_konsumsi:String,
    val nomor_laci:String,
    val nomor_tempat:String,
    val keterangan:String
)