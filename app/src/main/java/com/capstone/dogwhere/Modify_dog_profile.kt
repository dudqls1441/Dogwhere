package com.capstone.dogwhere

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import com.capstone.dogwhere.DTO.DogProfile
import com.capstone.dogwhere.R.id.state_true
import kotlinx.android.synthetic.main.activity_modify_dog_profile.*
import kotlinx.android.synthetic.main.activity_modify_dog_profile.DogProfilePhoto
import kotlinx.android.synthetic.main.activity_modify_dog_profile.dogpprofileneutralization
import kotlinx.android.synthetic.main.activity_modify_dog_profile.dogprofileage
import kotlinx.android.synthetic.main.activity_modify_dog_profile.dogprofilebreed
import kotlinx.android.synthetic.main.activity_modify_dog_profile.dogprofilename
import kotlinx.android.synthetic.main.activity_modify_dog_profile.dogprofilesex
import kotlinx.android.synthetic.main.activity_modify_dog_profile.dogprofilesize
import kotlinx.android.synthetic.main.activity_register_dog_profile.*

class Modify_dog_profile : AppCompatActivity() {
    private val TAG = Modify_dog_profile::class.java.simpleName
    private lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseFirestore
    private lateinit var dogSize: String
    private lateinit var downloadUri :String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modify_dog_profile)

        init()
        radio_size()
        radio_sex()
        radio_state()
        radio_dogneneutralization()
        btn_modify.setOnClickListener {
            modify()
        }
    }

    private fun init() {
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        val uid = auth.currentUser?.uid.toString()
        val docid = intent.getStringExtra("docid").toString()
        Log.d(TAG,"ybyb docid -> ${docid}")
        //창창2



        db.collection("users").document(uid).collection("dogprofiles").document(docid).get()
            .addOnSuccessListener {
                val result = it.toObject<DogProfile>()
                dogprofilename.setText(result!!.dogName)
                dogprofilebreed.setText(result!!.dogBreed)
                dogprofileage.setText(result!!.dogAge)
                Glide.with(this).load(result?.photoUrl).placeholder(R.drawable.zzarri)
                    .apply(RequestOptions())
                    .circleCrop().into(DogProfilePhoto)
                downloadUri = result!!.photoUrl


                if (result?.dogSex.equals("수컷")) {
                    dogprofilesex.check(R.id.sex_male)
                } else {
                    dogprofilesex.check(R.id.sex_female)
                }

                if (result?.dogSize.equals("small")) {
                    dogprofilesize.check(R.id.size_small)
                } else if (result?.dogSize.equals("middle")) {
                    dogprofilesize.check(R.id.size_middle)
                } else {
                    dogprofilesize.check(R.id.size_big)
                }

                if (result?.neutering.equals("true")) {
                    dogpprofileneutralization.check(R.id.neutralization_T)
                } else {
                    dogpprofileneutralization.check(R.id.neutralization_F)
                }

            }

    }

    private fun radio_sex() {
        dogprofilesex.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.sex_male -> checked_male()
                R.id.sex_female -> checked_female()


            }
        }
    }

    private fun radio_dogneneutralization() {
        dogpprofileneutralization.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.neutralization_T -> checked_dogneneutralization_T()
                R.id.neutralization_F -> checked_dogneneutralization_F()


            }
        }
    }

    private fun radio_state() {
        dogprofilestate.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.state_true -> checked_state_T()
                R.id.state_false -> checked_state_F()


            }
        }
    }

    private fun radio_size() {
        dogprofilesize.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.size_small -> let {
                    checked_small()
                    dogSize = "small"
                }
                R.id.size_middle -> let {
                    checked_middle()
                    dogSize = "middle"
                }
                R.id.size_big -> let {
                    checked_big()
                    dogSize = "big"
                }
            }
        }
    }

    private fun checked_small() {
        val size_small = findViewById<RadioButton>(R.id.size_small)
        val size_middle = findViewById<RadioButton>(R.id.size_middle)
        val size_big = findViewById<RadioButton>(R.id.size_big)
        size_small.setBackgroundResource(R.drawable.backgroundgreencircle)
        size_small.setTextColor(Color.parseColor("#00C09F"))
        size_middle.setBackgroundResource(R.drawable.backgroundgraycircle)
        size_middle.setTextColor(Color.parseColor("#52443C3C"))
        size_big.setBackgroundResource(R.drawable.backgroundgraycircle)
        size_big.setTextColor(Color.parseColor("#52443C3C"))
    }

    private fun checked_middle() {
        val size_small = findViewById<RadioButton>(R.id.size_small)
        val size_middle = findViewById<RadioButton>(R.id.size_middle)
        val size_big = findViewById<RadioButton>(R.id.size_big)
        size_middle.setBackgroundResource(R.drawable.backgroundgreencircle)
        size_middle.setTextColor(Color.parseColor("#00C09F"))
        size_small.setBackgroundResource(R.drawable.backgroundgraycircle)
        size_small.setTextColor(Color.parseColor("#52443C3C"))
        size_big.setBackgroundResource(R.drawable.backgroundgraycircle)
        size_big.setTextColor(Color.parseColor("#52443C3C"))
    }

    private fun checked_big() {
        val size_small = findViewById<RadioButton>(R.id.size_small)
        val size_middle = findViewById<RadioButton>(R.id.size_middle)
        val size_big = findViewById<RadioButton>(R.id.size_big)
        size_big.setBackgroundResource(R.drawable.backgroundgreencircle)
        size_big.setTextColor(Color.parseColor("#00C09F"))
        size_middle.setBackgroundResource(R.drawable.backgroundgraycircle)
        size_middle.setTextColor(Color.parseColor("#52443C3C"))
        size_small.setBackgroundResource(R.drawable.backgroundgraycircle)
        size_small.setTextColor(Color.parseColor("#52443C3C"))
    }

    private fun checked_male() {
        val sex_male = findViewById<RadioButton>(R.id.sex_male)
        val sex_female = findViewById<RadioButton>(R.id.sex_female)
        sex_male.setBackgroundResource(R.drawable.backgroundgreencircle)
        sex_male.setTextColor(Color.parseColor("#00C09F"))
        sex_female.setBackgroundResource(R.drawable.backgroundgraycircle)
        sex_female.setTextColor(Color.parseColor("#52443C3C"))

    }

    private fun checked_female() {
        val sex_male = findViewById<RadioButton>(R.id.sex_male)
        val sex_female = findViewById<RadioButton>(R.id.sex_female)
        sex_female.setBackgroundResource(R.drawable.backgroundgreencircle)
        sex_female.setTextColor(Color.parseColor("#00C09F"))
        sex_male.setBackgroundResource(R.drawable.backgroundgraycircle)
        sex_male.setTextColor(Color.parseColor("#52443C3C"))

    }

    private fun checked_state_T() {
        val state_t = findViewById<RadioButton>(R.id.state_true)
        val state_f = findViewById<RadioButton>(R.id.state_false)
        state_t.setBackgroundResource(R.drawable.backgroundgreencircle)
        state_t.setTextColor(Color.parseColor("#00C09F"))
        state_f.setBackgroundResource(R.drawable.backgroundgraycircle)
        state_f.setTextColor(Color.parseColor("#52443C3C"))

    }

    private fun checked_state_F() {
        val state_t = findViewById<RadioButton>(R.id.state_true)
        val state_f = findViewById<RadioButton>(R.id.state_false)
        state_f.setBackgroundResource(R.drawable.backgroundgreencircle)
        state_f.setTextColor(Color.parseColor("#00C09F"))
        state_t.setBackgroundResource(R.drawable.backgroundgraycircle)
        state_t.setTextColor(Color.parseColor("#52443C3C"))

    }




    private fun checked_dogneneutralization_T() {
        val neutralization_T = findViewById<RadioButton>(R.id.neutralization_T)
        val neutralization_F = findViewById<RadioButton>(R.id.neutralization_F)
        neutralization_T.setBackgroundResource(R.drawable.backgroundgreencircle)
        neutralization_T.setTextColor(Color.parseColor("#00C09F"))
        neutralization_F.setBackgroundResource(R.drawable.backgroundgraycircle)
        neutralization_F.setTextColor(Color.parseColor("#52443C3C"))

    }

    private fun checked_dogneneutralization_F() {
        val neutralization_T = findViewById<RadioButton>(R.id.neutralization_T)
        val neutralization_F = findViewById<RadioButton>(R.id.neutralization_F)
        neutralization_F.setBackgroundResource(R.drawable.backgroundgreencircle)
        neutralization_F.setTextColor(Color.parseColor("#00C09F"))
        neutralization_T.setBackgroundResource(R.drawable.backgroundgraycircle)
        neutralization_T.setTextColor(Color.parseColor("#52443C3C"))

    }

    private fun modify() {
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        val uid = auth.currentUser?.uid.toString()
        val docid = intent.getStringExtra("docid").toString()

        val dogname = findViewById<EditText>(R.id.dogprofilename).getText().toString()
        val dogage = findViewById<EditText>(R.id.dogprofileage).getText().toString()
        val dogbreed = findViewById<EditText>(R.id.dogprofilebreed).getText().toString()
        val sex = findViewById<RadioGroup>(R.id.dogprofilesex)
        val checked_sex = sex.checkedRadioButtonId
        var sex_value = ""
        //
        val size = findViewById<RadioGroup>(R.id.dogprofilesize)
        val checked_size = size.checkedRadioButtonId
        var size_value = ""
        //
        val neutr = findViewById<RadioGroup>(R.id.dogpprofileneutralization)
        val checked_neut = neutr.checkedRadioButtonId
        var neutr_value :Boolean = false

        val state = findViewById<RadioGroup>(R.id.dogprofilestate)
        val checked_state = state.checkedRadioButtonId
        var state_value : Boolean = false

        when(checked_sex){
            R.id.sex_male -> sex_value ="수컷"
            R.id.sex_female -> sex_value ="암컷"
        }

        when(checked_size){
            R.id.size_small -> size_value ="small"
            R.id.size_middle -> size_value ="middle"
            R.id.size_big -> size_value ="big"
        }

        when(checked_neut){
            R.id.neutralization_T -> neutr_value = true
            R.id.neutralization_F -> neutr_value = false
        }
        when(checked_state){
            R.id.state_true -> state_value = true
            R.id.state_false -> state_value = false
        }




        dogprofilesex.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.sex_male -> sex_value ="수컷"
                R.id.sex_female -> sex_value ="암컷"
            }

        }
        dogprofilesize.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.size_small -> size_value ="small"
                R.id.size_middle -> size_value ="middle"
                R.id.size_big -> size_value ="big"
            }
        }
        dogpprofileneutralization.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.neutralization_T -> neutr_value = true
                R.id.neutralization_F -> neutr_value = false
            }

        }

        dogprofilestate.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.state_true -> state_value = true // 미분실
                R.id.state_false -> state_value = false // 분실
            }
        }

        val dog = DogProfile(uid, dogname, downloadUri, dogage, dogbreed, sex_value, size_value,neutr_value,state_value)
        Log.d("ybyb","ybyb DogProfile ->${dog}")

        db.collection("users").document(uid).collection("dogprofiles").document(docid).set(dog).addOnSuccessListener {
            Log.d(TAG,"ybyb 업데이트 성공 ")
            val intent = Intent(this,UserProfileActivity::class.java)
            intent.putExtra("uid",uid)
            overridePendingTransition(R.anim.slide_up_enter, R.anim.slide_up_eixt)
            startActivity(intent)
            finish()
        }.addOnFailureListener {
            Log.d(TAG,"ybyb 업데이트 실패 "+it.toString() )
        }



    }
}