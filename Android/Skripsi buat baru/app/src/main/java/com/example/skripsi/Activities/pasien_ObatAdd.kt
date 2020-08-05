package com.example.skripsi.Activities

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.widget.*
import com.example.skripsi.Api.RetrofitClient
import com.example.skripsi.R
import com.example.skripsi.Api.ObatAddResponse
import kotlinx.android.synthetic.main.activity_add_item.*
import kotlinx.android.synthetic.main.activity_add_item.btn_addTime
import kotlinx.android.synthetic.main.activity_add_item.btn_cancel
import kotlinx.android.synthetic.main.activity_add_item.dosisLayout
import kotlinx.android.synthetic.main.activity_add_item.pilihDosis
import kotlinx.android.synthetic.main.activity_add_item.textView5
import kotlinx.android.synthetic.main.activity_add_item.txtKeterangan
import kotlinx.android.synthetic.main.activity_add_item.txtNamaObat
import kotlinx.android.synthetic.main.dialog_add_item.view.*
import kotlinx.android.synthetic.main.penggantiadddobat.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class pasien_ObatAdd : AppCompatActivity() {
    lateinit var namaObat: EditText

    //Jenis Medicine
    lateinit var rgDosis: RadioGroup
    lateinit var rbSatu: RadioButton
    lateinit var rbDua: RadioButton
    lateinit var rbTiga: RadioButton

    //Aturan Pakai
    lateinit var rgAturanMakan: RadioGroup
    lateinit var rbSesudah: RadioButton
    lateinit var rbSebelum: RadioButton

    //Nomor laci
    lateinit var rgWarnaLaci: RadioGroup
    lateinit var rbLaci1_1: RadioButton
    lateinit var rbLaci1_2: RadioButton
    lateinit var rbLaci1_3: RadioButton
    lateinit var rbLaci1_4: RadioButton
    lateinit var rbLaci2_1: RadioButton
    lateinit var rbLaci2_2: RadioButton
    lateinit var rbLaci2_3: RadioButton
    lateinit var rbLaci2_4: RadioButton
    lateinit var warna_tempat: String

    lateinit var jmlObat: EditText
    lateinit var keterangan: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_item)

        val bundle = intent.extras
        var id_user = bundle.getString("id_user")
        var id_obat = bundle.getString("id_obat")
        var nama_obat = bundle.getString("nama_obat")
        var aturan_konsum = bundle.getString("aturan_konsum")
        var dosis_obat = bundle.getString("dosis_obat")
        var jumlah_obat = bundle.getString("jumlah_obat")
//        bundle2.putString("nomor_laci", tampilLaci.text.toString().trim())
//        bundle2.putString("nomor_tempat", tampilTempat.text.toString().trim())
        var nomor_laci = bundle.getString("nomor_laci")
        var nomor_tempat = bundle.getString("nomor_tempat")
        var keterangan_obat = bundle.getString("keterangan")
        val action = bundle.getString("action")

        namaObat = findViewById(R.id.txtNamaObat)
        jmlObat = findViewById(R.id.txtJumlahObat)
        keterangan = findViewById(R.id.txtKeterangan)

        var obatTime:String=""

        //Define Radio Group Jenis Medicine
        rgDosis = findViewById(R.id.rgDosis) as RadioGroup
        rbSatu = findViewById(R.id.radioSatu) as RadioButton
        rbDua = findViewById(R.id.radioDua) as RadioButton
        rbTiga = findViewById(R.id.radioTiga) as RadioButton

        //Define Radio Group Aturan Makan
        rgAturanMakan = findViewById(R.id.rgAturanMakan) as RadioGroup
        rbSebelum = findViewById(R.id.radioSebelumMakan) as RadioButton
        rbSesudah = findViewById(R.id.radioSesudahMakan) as RadioButton

        //Define Radio Group Laci
        rgWarnaLaci = findViewById(R.id.rgWarnaLaci) as RadioGroup
        rbLaci1_1 = findViewById(R.id.radioLaci1_1) as RadioButton
        rbLaci1_2 = findViewById(R.id.radioLaci1_2) as RadioButton
        rbLaci1_3 = findViewById(R.id.radioLaci1_3) as RadioButton
        rbLaci1_4 = findViewById(R.id.radioLaci1_4) as RadioButton
        rbLaci2_1 = findViewById(R.id.radioLaci2_1) as RadioButton
        rbLaci2_2 = findViewById(R.id.radioLaci2_2) as RadioButton
        rbLaci2_3 = findViewById(R.id.radioLaci2_3) as RadioButton
        rbLaci2_4 = findViewById(R.id.radioLaci2_4) as RadioButton



        if(action.equals("update")){
            txtNamaObat.setText(nama_obat)
            if(aturan_konsum.equals("Sesudah Makan")){
                rbSesudah.isChecked = true
            }else{
                rbSebelum.isChecked = true
            }
            if(dosis_obat.equals("1")){
                rbSatu.isChecked = true
            }else if(dosis_obat.equals("2")){
                rbDua.isChecked = true
            }else{
                rbTiga.isChecked = true
            }

            //Nomor Laci dan Nomor Tempat
            if(nomor_laci.equals("1") && nomor_tempat.equals("1")){
                rbLaci1_1.isChecked = true
            }else if(nomor_laci.equals("1") && nomor_tempat.equals("2")){
                rbLaci1_2.isChecked = true
            }else if(nomor_laci.equals("1") && nomor_tempat.equals("3")){
                rbLaci1_3.isChecked = true
            }else if(nomor_laci.equals("1") && nomor_tempat.equals("4")){
                rbLaci1_4.isChecked = true
            }else if(nomor_laci.equals("2") && nomor_tempat.equals("1")){
                rbLaci2_1.isChecked = true
            }else if(nomor_laci.equals("2") && nomor_tempat.equals("2")){
                rbLaci2_2.isChecked = true
            }else if(nomor_laci.equals("2") && nomor_tempat.equals("3")){
                rbLaci2_3.isChecked = true
            }else if(nomor_laci.equals("2") && nomor_tempat.equals("4")){
                rbLaci2_4.isChecked = true
            }

            textView5.visibility = TextView.GONE
            dosisLayout.visibility = LinearLayout.GONE
            pilihDosis.visibility = LinearLayout.GONE

            jmlObat.setText(jumlah_obat)
            keterangan.setText(keterangan_obat)
        }

        btn_cancel.setOnClickListener {
            val intent = Intent(applicationContext, pasien_dashboard::class.java)
            val bundle = Bundle()
            bundle.putString("id_user", id_user)
            intent.putExtras(bundle)
        }

        btn_addTime.setOnClickListener {
            //Check Radio Button Dosis
            var result = ""
            if (rgDosis.checkedRadioButtonId != -1) {
                if (rbSatu.isChecked) {
                    result = "1"
                } else if (rbDua.isChecked) {
                    result = "2"
                } else if (rbTiga.isChecked) {
                    result = "3"
                }
            }

            //Declare Laci and Tempat
            var laci = ""
            var tempat = ""
            if(rgWarnaLaci.checkedRadioButtonId != -1){
                if(rbLaci1_1.isChecked){
                    laci="1"
                    tempat="1"
                    warna_tempat=rbLaci1_1.text.toString()
                }else if(rbLaci1_2.isChecked){
                    laci="1"
                    tempat="2"
                    warna_tempat=rbLaci1_2.text.toString()
                }else if(rbLaci1_3.isChecked){
                    laci="1"
                    tempat="3"
                    warna_tempat=rbLaci1_3.text.toString()
                }else if(rbLaci1_4.isChecked){
                    laci="1"
                    tempat="4"
                    warna_tempat=rbLaci1_4.text.toString()
                }else if(rbLaci2_1.isChecked){
                    laci="2"
                    tempat="1"
                    warna_tempat=rbLaci2_1.text.toString()
                }else if(rbLaci2_2.isChecked){
                    laci="2"
                    tempat="2"
                    warna_tempat=rbLaci2_2.text.toString()
                }else if(rbLaci2_3.isChecked){
                    laci="2"
                    tempat="3"
                    warna_tempat=rbLaci2_3.text.toString()
                }else if(rbLaci2_4.isChecked){
                    laci="2"
                    tempat="4"
                    warna_tempat=rbLaci2_4.text.toString()
                }
            }

            var obatRule = ""
            if (rgAturanMakan.checkedRadioButtonId != -1) {
                if (rbSebelum.isChecked) {
                    obatRule = "Sebelum Makan"
                } else if (rbSesudah.isChecked) {
                    obatRule = "Sesudah Makan"
                }
            }

            //Ambil nilai dari textView
            val obatName = namaObat.text.toString().trim()
            val obatDosis = result.trim()
            val obatCount = jmlObat.text.toString().trim()
            val ket = txtKeterangan.text.toString().trim()

            //Checking Empty
            if (namaObat.text.toString().isEmpty()) {
                namaObat.error = "Masukkan Nama Medicine"
                return@setOnClickListener
            }
            if (jmlObat.text.toString().isEmpty()) {
                jmlObat.error = "Masukkan Jumlah Medicine"
                return@setOnClickListener
            }

            if(action.equals("add")) {
                Log.e("action equals", "Masuk Add")
                RetrofitClient.instance.addMedicine(id_user, obatName, obatCount, warna_tempat, obatRule, obatDosis, laci, tempat, ket)
                    .enqueue(object : Callback<ObatAddResponse> {
                        override fun onFailure(call: Call<ObatAddResponse>, t: Throwable) {
                            Toast.makeText(applicationContext, t.message, Toast.LENGTH_SHORT).show()
                        }

                        override fun onResponse(
                            call: Call<ObatAddResponse>,
                            response: Response<ObatAddResponse>
                        ) {
                            val mDialogView = LayoutInflater.from(this@pasien_ObatAdd)
                                .inflate(R.layout.dialog_add_item, null)
                            val mBuilder = AlertDialog.Builder(this@pasien_ObatAdd)
                                .setView(mDialogView)

                            val mAlertDialog = mBuilder.show()
                            mDialogView.btn_cancel.setOnClickListener {
                                mAlertDialog.dismiss()
                                startActivity(Intent(applicationContext, pasien_dashboard::class.java))
                            }

                            mDialogView.btn_addTime.setOnClickListener {
                                val intent = Intent(applicationContext, addTime::class.java)
                                val bundle = Bundle()

                                bundle.putString("id_user", id_user)
                                bundle.putString("nama_obat", obatName)
                                bundle.putString("jumlah_obat", obatCount)
                                bundle.putString("aturan_konsum", obatRule)
                                bundle.putString("dosis_obat", obatDosis)
                                bundle.putString("keterangan", ket)
                                bundle.putString("action", "add")

                                intent.putExtras(bundle)
                                startActivity(intent)
                            }
                        }
                    })
            }else if(action.equals("update")){
                Log.e("action equals", "Masuk Update")
                RetrofitClient.instance.updateObat(id_obat, id_user, obatName, obatCount, warna_tempat, obatRule, obatDosis, laci, tempat, ket)
                    .enqueue(object: Callback<ObatAddResponse>{
                        override fun onFailure(call: Call<ObatAddResponse>, t: Throwable) {
                            Toast.makeText(applicationContext, t.message, Toast.LENGTH_SHORT).show()
                        }

                        override fun onResponse(
                            call: Call<ObatAddResponse>,
                            response: Response<ObatAddResponse>
                        ) {
                            if (response.isSuccessful){
                                Toast.makeText(applicationContext, "Obat berhasil diubah", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(applicationContext, pasien_dashboard::class.java))
                            }
                        }
                    })
            }
            btn_cancel.setOnClickListener {
                startActivity(Intent(applicationContext, pasien_dashboard::class.java))
            }
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(applicationContext, pasien_dashboard::class.java))
    }
}
