package com.example.skripsi.Api

import com.example.skripsi.models.*
import retrofit2.Call
import retrofit2.http.*

interface Api {
    @FormUrlEncoded
    @POST("Pengawas")
    fun connectUser(
        @Field("username") username:String,
        @Field("password") password:String,
        @Field("id_pasien") id:String,
        @Field("id_pengawas") id_pengawas:String
    ):Call<DefaultResponse>

    @FormUrlEncoded
    @POST("Users")
    fun createUser(
        @Field("username") username: String,
        @Field("fullname") fullname:String,
        @Field("nomor_hp") no_hp: String,
        @Field("password") password: String,
        @Field("role") role:String
    ):Call<DefaultResponse>

    @FormUrlEncoded
    @POST("Addtime")
    fun addTime(
        @Field("id_obat") id_obat:String,
        @Field("id_user") id_user: String,
        @Field("waktu") waktu:String,
        @Field("aturan_makan") aturan_makan:String
    ):Call<AddTimeResponse>

    @FormUrlEncoded
    @POST("Addtime/updateobat")
    fun updateTime(
        @Field("id_waktu") id_waktu:String,
        @Field("id_obat") id_obat:String,
        @Field("id_user") id_user: String,
        @Field("waktu") waktu:String,
        @Field("aturan_makan") aturan_konsum:String
    ):Call<AddTimeResponse>

    @FormUrlEncoded
    @POST("Addtime/deletetime")
    fun deleteTime(
        @Field("id_obat") id_obat: String
    ):Call<ObatDeleteResponse>

    @FormUrlEncoded
    @POST("Login")
    fun userLogin(
        @Field("username") username:String,
        @Field("password") password:String
    ):Call<LoginResponse>

    @FormUrlEncoded
    @POST("Obat")
    fun addMedicine(
        @Field("id_user") id_user: String,
        @Field("nama_obat") namaObat: String,
        @Field("jumlah_obat") jumlahObat: String,
        @Field("warna_tempat") warnaTempat: String,
        @Field("aturan_makan") aturanMakan: String,
        @Field("dosis_konsumsi") dosisKonsumsi: String,
        @Field("nomor_laci") nomorLaci: String,
        @Field("nomor_tempat") nomorTempat: String,
        @Field("keterangan") keterangan:String
    ):Call<ObatAddResponse>

    @FormUrlEncoded
    @POST("Obat/updateobat")
    fun updateObat(
        @Field("id_obat") id_obat: String,
        @Field("id_user") id_user: String,
        @Field("nama_obat") namaObat: String,
        @Field("jumlah_obat") jumlahObat: String,
        @Field("warna_tempat") warnaTempat: String,
        @Field("aturan_makan") aturanMakan: String,
        @Field("dosis_konsumsi") dosisKonsumsi: String,
        @Field("nomor_laci") nomorLaci: String,
        @Field("nomor_tempat") nomorTempat: String,
        @Field("keterangan") keterangan:String
    ):Call<ObatAddResponse>

    @FormUrlEncoded
    @POST("Obat/deleteobat")
    fun deleteObat(
        @Field("id_obat") id_obat: String
    ):Call<ObatDeleteResponse>

    @GET("Obat")
    fun getAllMedicine(
        @Query("id_user") id_user: String
    ):Call<List<Medicine>>

    @GET("pengawas/selectpasien")
    fun getAllPasien(
        @Query("id_pengawas") id_pengawas: String
    ):Call<List<UserPengawas>>

    @FormUrlEncoded
    @POST("pengawas/hapuspasien")
    fun deletePasien(
        @Field("id_pasien") id_user: String,
        @Field("id_pengawas") id_pengawas: String
    ):Call<DefaultResponse>

    @GET("Obatid")
    fun getObatId(
        @Query("id_user") id_user: String,
        @Query("nama_obat") namaObat: String,
        @Query("jumlah_obat") jumlahObat: String,
        @Query("aturan_makan") aturanMakan: String,
        @Query("dosis_konsumsi") dosisKonsumsi: String,
        @Query("keterangan") keterangan:String
    ):Call<GetObatIdResponse>

    @GET("Users/getalat")
    fun getListAlat(
        @Query("id_user") id_user: String
    ):Call<List<AlatIot>>

    @FormUrlEncoded
    @POST("Users/setalat")
    fun setAlat(
        @Field("nama_alat") nama_alat:String,
        @Field("password_alat") password_alat:String,
        @Field("id_user") id_user: String
    ):Call<DefaultResponse>

    @FormUrlEncoded
    @POST("Users/lepasalat")
    fun lepasalat(
        @Field("nama_alat") nama_alat:String,
        @Field("id_user") id_user: String
    ):Call<DefaultResponse>

    @GET("Pengawas/selectwaktupasien")
    fun getUserAlarm(
        @Query("id_user") id_user: String
    ):Call<List<PasienTime>>

    @GET("Obat/getalltime")
    fun getAllTime(
        @Query("id_user") id_user: String
    ):Call<List<TimePasien>>

    @GET("Pengawas/getwaktupasien")
    fun getAllUserAlarm(
        @Query("id_pengawas") id_pengawas: String
    ):Call<List<TimeOnly>>

    @GET("Obat/gettime")
    fun getTime(
        @Query("id_user") id_user: String,
        @Query("id_obat") id_obat: String
    ):Call<List<Time>>

    @GET("Pengawas")
    fun getPasien(
        @Query("username") username: String
    ):Call<LoginResponse>
}