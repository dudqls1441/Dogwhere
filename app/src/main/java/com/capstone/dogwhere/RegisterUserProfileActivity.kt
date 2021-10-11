package com.capstone.dogwhere

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.capstone.dogwhere.DTO.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_chat_list.*
import kotlinx.android.synthetic.main.activity_register_dog_profile.*
import kotlinx.android.synthetic.main.activity_register_user_profile.*
import kotlinx.android.synthetic.main.activity_register_user_profile.btn_back
import kotlinx.coroutines.*
import java.io.File

class RegisterUserProfileActivity : AppCompatActivity() {
    private val FLAG_GALLERY_CODE: Int = 10
    private val FLAG_IMAGECROP_CODE: Int = 11
    private val TAG = RegisterUserProfileActivity::class.java.simpleName
    private val RECORD_REQUEST_CODE = 1000
    private lateinit var auth: FirebaseAuth
    private lateinit var rdb: FirebaseDatabase
    private lateinit var database: DatabaseReference
    private lateinit var storage: FirebaseStorage
    private lateinit var ImagePath: String
    private var usersex: String = ""
    var Name_FLAG = false
    val db = Firebase.firestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_user_profile)
        setupPermissions()

        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        btn_back.setOnClickListener {
            auth.signOut()
            this?.let { MySharedPreferences.clearUser(it) }
            startActivity(Intent(this, LoginActivity::class.java))

        }

        userProfilePhoto.setOnClickListener {
            selectPhoto()
//            ImageCrop()
        }
        radio_btm()

        //코루틴 적용 안 될 거예여 CoroutineScope //

        btn_check_name.setOnClickListener {
            check_name2()
        }

        btn_upload.setOnClickListener {
            upload(ImagePath)
        }
    }

    private fun radio_btm() {
        userprofilesex.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.sex_man -> checked_sex_man()
                R.id.sex_woman -> checked_sex_woman()
                R.id.Nondisclosure -> checked_Nondisclosure()


            }
        }
    }

    private fun checked_sex_man() {
        sex_man.setBackgroundResource(R.drawable.backgroundgreencircle)
        sex_man.setTextColor(Color.parseColor("#00C09F"))
        sex_woman.setBackgroundResource(R.drawable.backgroundgraycircle)
        sex_woman.setTextColor(Color.parseColor("#52443C3C"))
        Nondisclosure.setBackgroundResource(R.drawable.backgroundgraycircle)
        Nondisclosure.setTextColor(Color.parseColor("#52443C3C"))
    }

    private fun checked_sex_woman() {
        sex_woman.setBackgroundResource(R.drawable.backgroundgreencircle)
        sex_woman.setTextColor(Color.parseColor("#00C09F"))
        sex_man.setBackgroundResource(R.drawable.backgroundgraycircle)
        sex_man.setTextColor(Color.parseColor("#52443C3C"))
        Nondisclosure.setBackgroundResource(R.drawable.backgroundgraycircle)
        Nondisclosure.setTextColor(Color.parseColor("#52443C3C"))
    }

    private fun checked_Nondisclosure() {
        Nondisclosure.setBackgroundResource(R.drawable.backgroundgreencircle)
        Nondisclosure.setTextColor(Color.parseColor("#00C09F"))
        sex_man.setBackgroundResource(R.drawable.backgroundgraycircle)
        sex_man.setTextColor(Color.parseColor("#52443C3C"))
        sex_woman.setBackgroundResource(R.drawable.backgroundgraycircle)
        sex_woman.setTextColor(Color.parseColor("#52443C3C"))
    }


    private fun selectPhoto() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE)
        intent.type = "image/*"
        intent.putExtra("crop", true)
        intent.putExtra("aspectX", 1)
        intent.putExtra("aspectY", 1)
        intent.putExtra("outputX", 280)
        intent.putExtra("outputY", 280)
        intent.putExtra("return-data", true)


        startActivityForResult(intent, FLAG_GALLERY_CODE)
    }


    // * Glide //

    //circleCrop() : 이거 이미지 둥글게 표현하는 거
    // centerCrop() : 외부에서 받아온 이미지가 있다면, 가운데에서 이미지를 잘라 보여주는 함수
    //  fitCenter() : 외부에서 받아온 이미지를 가운데에서 사이즈를 조절하여 이미지 전체를 받아오는 함수
    // placeholder(로딩 이미지) // ex) placeholder(R.drawable.loading) : 이미지가 로딩하는 동안 보여질 이미지를 정함
    //     error(실패 이미지) // ex) error(R.drawable.error) : 이미지를 불러오는데 실패 했을때 보여질 이미지를
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FLAG_GALLERY_CODE) {
            //if문에 &&resultCode ==Activity.RESULT_OK 넣어주기
            if (data != null) {
//                Log.d(TAG, getImageFilePath(data!!.data!!))
                Log.d(TAG, "확인 원래 data ${data}")
                Log.d("ImageCrop", "확인 원래 data!!.data!!  ${data!!.data!!}")

                ImagePath = getImageFilePath(data!!.data!!)

                Log.d(TAG, "확인 원래 ImagePath ${ImagePath}")

//                ImageCrop(data!!.data!!)

                //a0929@naver.com
                data?.data?.let { it ->
                    launchImageCrop(it)
                }
//                ImageCrop()
                var file = Uri.fromFile(File(getImageFilePath(data!!.data!!)))
                Glide.with(this).load(file).placeholder(R.drawable.zzarri).apply(RequestOptions())
                    .circleCrop().into(userProfilePhoto)

            } else {
                Log.d(TAG, "가져온 데이터 없음")
                ImagePath = ""
            }
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    Log.d("ImageCrop", "확인 ImageCrop path  result!!.uri.path ${result!!.uri.path}")
                    Log.d("ImageCrop", "확인 ImageCrop uri ${result.uri}")
                    Log.d(
                        "ImageCrop",
                        "확인 ImageCrop result!!.uri.authority ${result!!.uri.authority}"
                    )

                    Log.d("ImageCrop", "확인 result.uri!! ${result.uri!!}")
                    Log.d("ImageCrop", "확인 data ${data}")
                    Log.d("ImageCrop", "확인 result.originalUri ${result!!.uri}")

                    val resultUri = result.uri!!

                    Log.d(TAG, "확인 crop 이미지 패스 ${ImagePath}")
                    ImagePath = resultUri!!.path!!
                    Log.d(TAG, "확인 croped 이미지 패스 ${ImagePath}")


                    Glide.with(this).load(resultUri).placeholder(R.drawable.zzarri)
                        .apply(RequestOptions())
                        .circleCrop().into(userProfilePhoto)
                } else {
                    Log.d("ImageCrop", "확인 ImageCrop : 데이터 없음")
                }


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
                Log.d("ImageCrop", "ImageCrop error ${error.message}")
            }

        } else if (requestCode == FLAG_IMAGECROP_CODE) {
            val result = CropImage.getActivityResult(data)
            Log.d("ImageCrop", "99999  result ${result}")
            Log.d("ImageCrop", "99999  data ${data}")
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
//                    val resultUri = result.uri!!
//                    Log.d("ImageCrop", "99999 ${resultUri}")

                    ImagePath = data!!.data!!.path!!

//                    var file = Uri.fromFile(File(getImageFilePath(data!!.data!!)))
                    Glide.with(this).load(data!!.data!!).placeholder(R.drawable.zzarri)
                        .apply(RequestOptions())
                        .circleCrop().into(DogProfilePhoto)

                } else {
                    Log.d("ImageCrop", "확인 ImageCrop : 데이터 없음")
                }
            }

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


    private fun launchImageCrop(uri: Uri?) {
        CropImage.activity(uri).setGuidelines(CropImageView.Guidelines.ON)
            .setCropShape(CropImageView.CropShape.RECTANGLE)
            .start(this)

    }

    //aaa0930@naver.com

//    private fun ImageCrop(mImageCaptureUri: Uri) {
//        val intent = Intent(Intent.ACTION_PICK)
//        intent.type = "image/*"
//        intent.setDataAndType(mImageCaptureUri, "image/*")
//        intent.putExtra("crop", true)
//        intent.putExtra("aspectX", 1)
//        intent.putExtra("aspectY", 1)
//        intent.putExtra("outputX", 280)
//        intent.putExtra("outputY", 280)
//        intent.action = Intent.ACTION_GET_CONTENT
//        startActivityForResult(intent, FLAG_IMAGECROP_CODE)
//    }

    private fun setupPermissions() {
        //스토리지 읽기 퍼미션을 permission 변수에 담는다
        val permission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission to record denied")
            makeRequest()
        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            RECORD_REQUEST_CODE
        )
    }

    private fun upload(uri: String) {
        var file = Uri.fromFile(File(uri))
        val storageRef: StorageReference = storage.getReference("gs:/dogwhere-ea26c.appspot.com")
        val riversRef = storageRef.child("UserProfile/${file.lastPathSegment}")
        Log.d("123", riversRef.toString())
        val uploadTask = riversRef.putFile(file)

        val urlTask = uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            riversRef.downloadUrl

        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                if (Name_FLAG == true) {
                    val downloadUri = task.result
                    val uid = auth.currentUser!!.uid
                    val username = findViewById<EditText>(R.id.userprofileName).getText().toString()
                    val userage = findViewById<EditText>(R.id.userprofileAge).getText().toString()
                    if (sex_man.isChecked) {
                        usersex = "남자"
                    } else if (sex_woman.isChecked) {
                        usersex = "여자"
                    } else {
                        usersex = "선택안함"
                    }
                    // 지금 저거 하면 버튼 눌렀을 때 중복 확인은 되는데
                    val user = UserProfile(uid, downloadUri.toString(), userage, username, usersex)
                    db.collection("users").document(uid).collection("userprofiles").document(uid)
                        .set(user)
                        .addOnSuccessListener { documentReference ->
                        }
                        .addOnFailureListener { e ->
                            Log.w(TAG, "Error adding document", e)
                        }

                    val intent = Intent(this, DogProfileActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_up_enter,R.anim.slide_up_eixt)
                    finish()
                    //} else {
                    //  Toast.makeText(this, "닉네임 중복 확인 바람", Toast.LENGTH_SHORT).show()
                    // }
                } else {
                    Toast.makeText(this, "닉네임 중복 확인 바람", Toast.LENGTH_SHORT)
                        .show() //이거 가능하지 않게냐 ㅇㅇ됨  안된다 ㅋㅋ왜 안되지
                }
            } else {
                Log.w("실패", "업로드 실패", task.exception)
                Toast.makeText(
                    baseContext, "유저 프로필 업로드 실패",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun check_name2() {
        val username = findViewById<EditText>(R.id.userprofileName).getText().toString()
        check_name3(username)

    }

//    fun check_name3(username: String) {
//        db.collection("users")
//            .get()
//            .addOnSuccessListener { result ->
//                for (document in result) {
//
//                    val uids = document.get("uid").toString()
//                    db.collection("users").document(uids).collection("userprofiles").get()
//                        .addOnSuccessListener { result ->
//                            val names = document.get("userName")
//                            if (username == names) {
//                                Log.d(TAG, "해당 닉네임이 이미 존재함")
//                            } else {
//                                Name_FLAG = true
//                                Log.d(TAG, "사용 가능한 닉네임")
//                            }
//                        }
//                }
//            }
//        check()
//    }

    fun check_name3(username: String) = runBlocking {
        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {

                    val uids = document.get("uid").toString()
                    db.collection("users").document(uids).collection("userprofiles").get()
                        .addOnSuccessListener { result ->
                            val names = document.get("userName")
                            if (username == names) {

                                Log.d(TAG, "해당 닉네임이 이미 존재함")
                            } else {
                                Name_FLAG = true
                                Log.d(TAG, "사용 가능한 닉네임")
                            }
                        }
                }
            }
        launch {
            delay(1000L)
            check()
        }


    }

    private fun check() {
        if (Name_FLAG == true) {
            Toast.makeText(
                this@RegisterUserProfileActivity,
                "사용 가능한 닉네임입니다..",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(
                this@RegisterUserProfileActivity,
                "이미 사용중인 닉네임입니다.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
