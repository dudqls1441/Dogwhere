package com.capstone.dogwhere

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.marginBottom
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.capstone.dogwhere.Chat.UserItem
import com.capstone.dogwhere.DTO.UserProfile
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_chat_list.*
import kotlinx.android.synthetic.main.activity_register_user_profile.*
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import org.jetbrains.anko.padding
import java.io.File

class RegisterUserProfileActivity : AppCompatActivity() {
    private val FLAG_GALLERY_CODE: Int = 10
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

        userProfilePhoto.setOnClickListener {
            selectPhoto()
        }
        radio_btm()
        btn_check_name.setOnClickListener {
//            Name_FLAG = true
////            check_name()

            CoroutineScope(Dispatchers.IO).launch {
                runBlocking {
                    check_name2()
                }
            }

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

        startActivityForResult(intent, FLAG_GALLERY_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FLAG_GALLERY_CODE) {
            if (data != null) {
                Log.d(TAG, getImageFilePath(data!!.data!!))

                ImagePath = getImageFilePath(data!!.data!!)

                var file = Uri.fromFile(File(getImageFilePath(data!!.data!!)))
                Glide.with(this).load(file).placeholder(R.drawable.zzarri).apply(RequestOptions())
                    .circleCrop().into(userProfilePhoto)

            } else {
                Log.d(TAG, "가져온 데이터 없음")
                ImagePath = ""

            }

            //circleCrop() : 이거 이미지 둥글게 표현하는 거
            // centerCrop() : 외부에서 받아온 이미지가 있다면, 가운데에서 이미지를 잘라 보여주는 함수
            //  fitCenter() : 외부에서 받아온 이미지를 가운데에서 사이즈를 조절하여 이미지 전체를 받아오는 함수
            // placeholder(로딩 이미지) // ex) placeholder(R.drawable.loading) : 이미지가 로딩하는 동안 보여질 이미지를 정함
            //     error(실패 이미지) // ex) error(R.drawable.error) : 이미지를 불러오는데 실패 했을때 보여질 이미지를
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
                    val uid = auth.currentUser.uid
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

                    val intent = Intent(this, RegisterDogProfileActivity::class.java)
                    startActivity(intent)
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

//                Log.d(TAG, usersex + " < -- 값2 ")
//                Toast.makeText(this, usersex, Toast.LENGTH_LONG).show()

//                val userhobby = findViewById<EditText>(R.id.userprofileHobby).getText().toString()
//
//                Toast.makeText(this, uid + "랑" + username, Toast.LENGTH_SHORT).show()
//                Log.d(TAG, uid + "---" + username)
//                val profileRef = rdb.getReference("userprofiles").child(uid)
//
//                //  rdb.getReference().child("userprofiles").push().child(uid).setValue(user)
//                profileRef.child(uid).push().setValue(user)


//    fun check_name() {
//        val username = findViewById<EditText>(R.id.userprofileName).getText().toString()
//        db.collection("users")
//            .get()
//            .addOnSuccessListener { result ->
//                while (Name_FLAG) {
//                    for (document in result) { //이게 왜 아 document 이름이? 아니 이거 ㄱㄷ 생각좀 봐봐 저기 밑에 포문 다돌아야 여기포문 한번임 아 머리안
//                        val uids = document.get("uid").toString()
//                        db.collection("users").document(uids).collection("userprofiles").get()
//                            .addOnSuccessListener { result ->
//                                val names = document.get("userName")
//                                if (username == names) { // 이걸 while 문으로 써야되나
//                                    Log.d(TAG, "해당 닉네임이 이미 존재함")
//                                    Toast.makeText(this, "이미 존재하는 닉네임입니다.", Toast.LENGTH_SHORT)
//                                        .show()
//                                    Name_FLAG = false // ㅇㅇㅇㅇ 무한루프임 ㅈ거 ㅓ어디 로근데  다시 해봐?ㅇㅇ
//                                    //정렬한번만
//                                }
//                            }
//                    }
//                    if (Name_FLAG) {
//                        Toast.makeText(this, "사용 가능한 닉네임입니다.", Toast.LENGTH_SHORT)
//                            .show()
//                    }
//                    break
//                }
//            }
//    }

    fun check_name2() {
        val username = findViewById<EditText>(R.id.userprofileName).getText().toString()
        CoroutineScope(Dispatchers.IO).launch {
            runBlocking {
                check_name3(username)
            }
        }

    }

    suspend fun check_name3(username : String) :Boolean{
        return try{
            db.collection("users")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        val uids = document.get("uid").toString()
                        db.collection("users").document(uids).collection("userprofiles").get()
                            .addOnSuccessListener{ result ->
                                val names = document.get("userName")
                                Log.d(TAG,"기존 닉네임   :" + names.toString())
                                Log.d(TAG,"입력 닉네임   :" + username)
                                if (username == names) {
                                    Log.d(TAG, "해당 닉네임이 이미 존재함")
                                    Toast.makeText(
                                        this@RegisterUserProfileActivity,
                                        "이미 존재하는 닉네임입니다.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }else{
                                    Name_FLAG = true
                                    Log.d(TAG, "사용 가능한 닉네임")
                                    Toast.makeText(
                                        this@RegisterUserProfileActivity,
                                        "사용 가능한 닉네임임.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    }
                }.await()
            true
        } catch (e :FirebaseException){
            Log.e("error:","erroe:"+e.message.toString())
            false

        }

    }
}