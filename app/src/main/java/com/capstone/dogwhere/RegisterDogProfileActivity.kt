package com.capstone.dogwhere

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.text.Layout
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.core.view.setPadding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.capstone.dogwhere.DTO.DogProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_check.*
import kotlinx.android.synthetic.main.activity_matching_registration.*
import kotlinx.android.synthetic.main.activity_register_dog_profile.*
import kotlinx.android.synthetic.main.activity_register_user_profile.*
//import kotlinx.android.synthetic.main.activity_register_user_profile.btn_selectPhoto
import kotlinx.android.synthetic.main.activity_register_user_profile.btn_upload
import kotlinx.android.synthetic.main.activity_register_user_profile.userProfilePhoto
import org.jetbrains.anko.padding
import org.jetbrains.anko.px2sp
import java.io.File

class RegisterDogProfileActivity : AppCompatActivity() {
    private val FLAG_GALLERY_CODE: Int = 10
    private val FLAG_GALLERY_CODE2: Int = 11
    private val TAG = RegisterDogProfileActivity::class.java.simpleName
    private lateinit var auth: FirebaseAuth
    private lateinit var rdb: FirebaseDatabase
    private lateinit var storage: FirebaseStorage
    private lateinit var ImagePath: String
    private var dogsex: String = ""
    private var dogneneutralization: String = ""
    private var dogSize =""
    val breeds = arrayOf(
        "요크셔테리어", "세인트버나드", "푸들", "포메라니안", "웰시코기",
        "페키니즈", "잉글리쉬 쉽독", "빠삐용", "슈나우저", "말티즈",
        "이탈리안 골든 리트리버", "저먼 셰퍼드", "달마시안", "닥스훈트", "차우차우",
        "샤페이", "치와와", "불독", "불 테리어", "복서"
        , "닥스훈트", "차우차우",
        "비숑프리제", "비글", "말라뮤트", "시바견"
    )
    var dogbreed =""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_dog_profile)

        auth = FirebaseAuth.getInstance()
        rdb = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()

        DogProfilePhoto.setOnClickListener {
            selectPhoto()
        }
        DogProfilePhoto2.setOnClickListener {
            selectPhoto2()
        }

        val myAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, breeds)
        dogprofilebreed.adapter = myAdapter
        dogprofilebreed.prompt = "견종을 선택하세요"
        dogprofilebreed.setSelection(5)
        dogprofilebreed.gravity = Gravity.CENTER_HORIZONTAL
        dogprofilebreed.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                Log.d("ybyb","position ->${position}")
                Log.d("ybyb","id ->${id}")
                Log.d("ybyb","view ->${view.toString()}")
                Log.d("ybyb","이름? ->${breeds[id.toInt()]}")
                dogbreed = breeds[id.toInt()]

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Log.d("ybyb","선택 안 됨")
            }
        }

        radio_size()
        radio_sex()
        radio_dogneneutralization()

        btn_add.setOnClickListener {
            upload(ImagePath)
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
                R.id.neutralization_T -> {
                    checked_dogneneutralization_T()
                    dogneneutralization = "예"
                }
                R.id.neutralization_F -> {
                    checked_dogneneutralization_F()
                    dogneneutralization = "아니요"
                }


            }
        }
    }

    private fun radio_size() {
        dogprofilesize.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.size_small ->let {
                    checked_small()
                    dogSize ="small"
                }
                R.id.size_middle ->let {
                    checked_middle()
                    dogSize ="middle"
                }
                R.id.size_big ->let {
                    checked_big()
                    dogSize ="big"
                }
            }
        }
    }
    private fun checked_small() {
        val size_small =findViewById<RadioButton>(R.id.size_small)
        val size_middle =findViewById<RadioButton>(R.id.size_middle)
        val size_big =findViewById<RadioButton>(R.id.size_big)
        size_small.setBackgroundResource(R.drawable.backgroundgreencircle)
        size_small.setTextColor(Color.parseColor("#00C09F"))
        size_middle.setBackgroundResource(R.drawable.backgroundgraycircle)
        size_middle.setTextColor(Color.parseColor("#52443C3C"))
        size_big.setBackgroundResource(R.drawable.backgroundgraycircle)
        size_big .setTextColor(Color.parseColor("#52443C3C"))
    }
    private fun checked_middle() {
        val size_small =findViewById<RadioButton>(R.id.size_small)
        val size_middle =findViewById<RadioButton>(R.id.size_middle)
        val size_big =findViewById<RadioButton>(R.id.size_big)
        size_middle.setBackgroundResource(R.drawable.backgroundgreencircle)
        size_middle.setTextColor(Color.parseColor("#00C09F"))
        size_small.setBackgroundResource(R.drawable.backgroundgraycircle)
        size_small.setTextColor(Color.parseColor("#52443C3C"))
        size_big.setBackgroundResource(R.drawable.backgroundgraycircle)
        size_big.setTextColor(Color.parseColor("#52443C3C"))
    }
    private fun checked_big() {
        val size_small =findViewById<RadioButton>(R.id.size_small)
        val size_middle =findViewById<RadioButton>(R.id.size_middle)
        val size_big =findViewById<RadioButton>(R.id.size_big)
        size_big.setBackgroundResource(R.drawable.backgroundgreencircle)
        size_big.setTextColor(Color.parseColor("#00C09F"))
        size_middle.setBackgroundResource(R.drawable.backgroundgraycircle)
        size_middle.setTextColor(Color.parseColor("#52443C3C"))
        size_small.setBackgroundResource(R.drawable.backgroundgraycircle)
        size_small.setTextColor(Color.parseColor("#52443C3C"))
    }

    private fun checked_male() {
        sex_male.setBackgroundResource(R.drawable.backgroundgreencircle)
        sex_male.setTextColor(Color.parseColor("#00C09F"))
        sex_female.setBackgroundResource(R.drawable.backgroundgraycircle)
        sex_female.setTextColor(Color.parseColor("#52443C3C"))

    }

    private fun checked_female() {
        sex_female.setBackgroundResource(R.drawable.backgroundgreencircle)
        sex_female.setTextColor(Color.parseColor("#00C09F"))
        sex_male.setBackgroundResource(R.drawable.backgroundgraycircle)
        sex_male.setTextColor(Color.parseColor("#52443C3C"))

    }

    private fun checked_dogneneutralization_T() {
        neutralization_T.setBackgroundResource(R.drawable.backgroundgreencircle)
        neutralization_T.setTextColor(Color.parseColor("#00C09F"))
        neutralization_F.setBackgroundResource(R.drawable.backgroundgraycircle)
        neutralization_F.setTextColor(Color.parseColor("#52443C3C"))

    }

    private fun checked_dogneneutralization_F() {
        neutralization_F.setBackgroundResource(R.drawable.backgroundgreencircle)
        neutralization_F.setTextColor(Color.parseColor("#00C09F"))
        neutralization_T.setBackgroundResource(R.drawable.backgroundgraycircle)
        neutralization_T.setTextColor(Color.parseColor("#52443C3C"))

    }

    private fun selectPhoto() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE)
        startActivityForResult(intent, FLAG_GALLERY_CODE)
    }

    private fun selectPhoto2() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE)
        startActivityForResult(intent, FLAG_GALLERY_CODE2)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FLAG_GALLERY_CODE) {
            if (data != null) {
                Log.d(TAG, getImageFilePath(data!!.data!!))

                ImagePath = getImageFilePath(data!!.data!!)

                var file = Uri.fromFile(File(getImageFilePath(data!!.data!!)))
                Glide.with(this).load(file).placeholder(R.drawable.zzarri).apply(RequestOptions())
                    .circleCrop().into(DogProfilePhoto)
            } else {
                Log.d(TAG, "가져온 데이터 없음")
                ImagePath = ""
            }
        } else if (requestCode == FLAG_GALLERY_CODE2) {
            if (data != null) {
                Log.d(TAG, getImageFilePath(data!!.data!!))
                ImagePath = getImageFilePath(data!!.data!!)
                var file = Uri.fromFile(File(getImageFilePath(data!!.data!!)))
                Glide.with(this).load(file).placeholder(R.drawable.zzarri).apply(RequestOptions())
                    .circleCrop().into(DogProfilePhoto2)
            } else {
                Log.d(TAG, "가져온 데이터 없음")
                ImagePath = ""
            }
        } else {
            Log.d(TAG, "가져온 데이터 없음")
            ImagePath = ""
        }
    }

    private fun getImageFilePath(contentUri: Uri): String {
        var columIndex = 0
        val projection: Array<String> = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(contentUri, projection, null, null, null)
        if (cursor!!.moveToFirst()) {
            columIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        }
        return cursor.getString(columIndex)
    }

    private fun upload(uri: String) {
        var file = Uri.fromFile(File(uri))
        val storageRef: StorageReference = storage.getReference("gs:/dogwhere-ea26c.appspot.com")
        val ref = storageRef.child("DogProfile/${file.lastPathSegment}")

        val uploadTask = ref.putFile(file)

        val urlTask = uploadTask.continueWithTask { it ->
            if (!it.isSuccessful) {
                it.exception?.let {
                    throw it
                }
            }
            ref.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                Log.d("yb","dogprofile img -> ${downloadUri}")
                val uid = auth.currentUser!!.uid
                val dogname = findViewById<EditText>(R.id.dogprofilename).getText().toString()
                val dogage = findViewById<EditText>(R.id.dogprofileage).getText().toString()
                if (sex_male.isChecked) {
                    dogsex = "수컷"
                } else if (sex_female.isChecked) {
                    dogsex = "암컷"
                } else {
                    dogsex = "선택안함"
                }
                val dogneu:Boolean = dogneneutralization =="예"
                Log.e("yy", "true나오라고"+dogneu.toString())
                val dog = DogProfile(uid, dogname, downloadUri.toString(), dogage, dogbreed, dogsex, dogSize,dogneu)
                Log.d("yb","ybyb ->dogprofile ->${dog}")
                val db = Firebase.firestore
                db.collection("users").document(uid).collection("dogprofiles").add(dog)
                    .addOnSuccessListener { documentReference ->

                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error adding document", e)
                    }
                val intent = Intent(this, DogProfileActivity::class.java)
                startActivity(intent)
                finish()

            } else {
                Log.w("실패", "업로드 실패", task.exception)
            }
        }
    }
}
//user - user(id) - dogprofile - 아래 여러 마리의 강아지 등록할 수 있게 수정했음 (08-27)