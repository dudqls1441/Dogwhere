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
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
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

        radio_sex()
        radio_dogneneutralization()

        btn_add.setOnClickListener {
            upload(ImagePath)
        }

//        adddog.setOnClickListener {
//            AddDog()
//        }

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

        val urlTask = uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            ref.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                val uid = auth.currentUser!!.uid
                val dogname = findViewById<EditText>(R.id.dogprofilename).getText().toString()
                val dogage = findViewById<EditText>(R.id.dogprofileage).getText().toString()
                val dogbreed = findViewById<EditText>(R.id.dogprofilebreed).getText().toString()
                if (sex_male.isChecked) {
                    dogsex = "수컷"
                } else if (sex_female.isChecked) {
                    dogsex = "암컷"
                } else {
                    dogsex = "선택안함"
                }
                dogpprofileneutralization.setOnCheckedChangeListener { group, i ->
                    when (i) {
                        R.id.neutralization_T -> dogneneutralization = "예"
                        R.id.neutralization_F -> dogneneutralization = "아니요"
                    }
                    Log.d(TAG, dogsex)
                }
                Log.d(TAG, dogsex)
                val dog = DogProfile(uid, dogname, downloadUri.toString(), dogage, dogbreed, dogsex)
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