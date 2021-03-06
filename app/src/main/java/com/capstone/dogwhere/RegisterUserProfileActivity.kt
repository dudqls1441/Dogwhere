package com.capstone.dogwhere

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
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
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
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
    private val FLAG_GALLERY_CODE2: Int = 11
    private val TAG = RegisterUserProfileActivity::class.java.simpleName
    private val RECORD_REQUEST_CODE = 1000
    private lateinit var auth: FirebaseAuth
    private lateinit var rdb: FirebaseDatabase
    private lateinit var database: DatabaseReference
    private lateinit var storage: FirebaseStorage
    private lateinit var ImagePath: String
    private var usersex: String = ""
    var Name_FLAG = true
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

        //????????? ?????? ??? ??? ????????? CoroutineScope //

        btn_check_name.setOnClickListener {
            val username = findViewById<EditText>(R.id.userprofileName).getText().toString()
            check_name(username)
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


            }
        }
    }

    private fun checked_sex_man() {
        sex_man.setBackgroundResource(R.drawable.backgroundgreencircle)
        sex_man.setTextColor(Color.parseColor("#00C09F"))
        sex_woman.setBackgroundResource(R.drawable.backgroundgraycircle)
        sex_woman.setTextColor(Color.parseColor("#52443C3C"))

    }

    private fun checked_sex_woman() {
        sex_woman.setBackgroundResource(R.drawable.backgroundgreencircle)
        sex_woman.setTextColor(Color.parseColor("#00C09F"))
        sex_man.setBackgroundResource(R.drawable.backgroundgraycircle)
        sex_man.setTextColor(Color.parseColor("#52443C3C"))

    }

    private fun selectPhoto() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE)
        startActivityForResult(intent, FLAG_GALLERY_CODE)
    }

    // * Glide //

    //circleCrop() : ?????? ????????? ????????? ???????????? ???
    // centerCrop() : ???????????? ????????? ???????????? ?????????, ??????????????? ???????????? ?????? ???????????? ??????
    //  fitCenter() : ???????????? ????????? ???????????? ??????????????? ???????????? ???????????? ????????? ????????? ???????????? ??????
    // placeholder(?????? ?????????) // ex) placeholder(R.drawable.loading) : ???????????? ???????????? ?????? ????????? ???????????? ??????
    //     error(?????? ?????????) // ex) error(R.drawable.error) : ???????????? ??????????????? ?????? ????????? ????????? ????????????
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
                Log.d(TAG, "????????? ????????? ??????")
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
                Log.d(TAG, "????????? ????????? ??????")
                ImagePath = ""
            }
        } else {
            Log.d(TAG, "????????? ????????? ??????")
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


    private fun launchImageCrop(uri: Uri?) {
        CropImage.activity(uri).setGuidelines(CropImageView.Guidelines.ON)
            .setCropShape(CropImageView.CropShape.RECTANGLE)
            .start(this)

    }

    private fun setupPermissions() {
        //???????????? ?????? ???????????? permission ????????? ?????????
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
                if (Name_FLAG == true) {
                    val downloadUri = task.result
                    val uid = auth.currentUser!!.uid
                    val username = findViewById<EditText>(R.id.userprofileName).getText().toString()
                    val userage = findViewById<EditText>(R.id.userprofileAge).getText().toString()
                    if (sex_man.isChecked) {
                        usersex = "man"
                    } else if (sex_woman.isChecked) {
                        usersex = "wm"
                    }
                    // ?????? ?????? ?????? ?????? ????????? ??? ?????? ????????? ?????????
                    FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            Log.d("ybyb", "Fetching FCM registration token failed", task.exception)
                            return@OnCompleteListener
                        }
                        // Get new FCM registration token
                        val token = task.result.toString()
                        // Log and toast
                        Log.d("ybyb", "?????? ->" + token)

                        val user = UserProfile(
                            uid,
                            downloadUri.toString(),
                            userage,
                            username,
                            usersex,
                            token
                        )
                        db.collection("users").document(uid).collection("userprofiles")
                            .document(uid)
                            .set(user)
                            .addOnSuccessListener { documentReference ->
                            }
                            .addOnFailureListener { e ->
                                Log.w("Ybyb", "Error adding document", e)
                            }

                        val intent = Intent(this, DogProfileActivity::class.java)
                        startActivity(intent)
                        overridePendingTransition(R.anim.slide_up_enter, R.anim.slide_up_eixt)
                        finish()
                        //} else {
                        //  Toast.makeText(this, "????????? ?????? ?????? ??????", Toast.LENGTH_SHORT).show()
                        // }
                    })
                } else {
                    Toast.makeText(this, "????????? ?????? ?????? ??????", Toast.LENGTH_SHORT)
                        .show() //?????? ???????????? ????????? ?????????  ????????? ????????? ?????????
                }
            } else {
                Log.w("ybyb", "????????? ??????", task.exception)
                Toast.makeText(
                    baseContext, "?????? ????????? ????????? ??????",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    private fun check() {
        if (Name_FLAG == true) {
            Toast.makeText(
                this@RegisterUserProfileActivity,
                "?????? ????????? ??????????????????..",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(
                this@RegisterUserProfileActivity,
                "?????? ???????????? ??????????????????.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun check_name(username: String) {
        Name_FLAG = true
        db.collection("users").get().addOnSuccessListener { result ->
            for (document in result) {
                val uids = document.get("uid").toString()
                db.collection("users").document(uids).collection("userprofiles").document(uids)
                    .get().addOnSuccessListener { result ->
                        val names = result.get("userName").toString()
                        if (username == names) {
                            Name_FLAG = false
                            Log.d(
                                "ybyb",
                                "?????? ???????????????. Name_FLAG ->${Name_FLAG}---documentId->${document.id}"
                            )
                        }
                    }
            }
            val handler = Handler()
            handler.postDelayed(java.lang.Runnable {
                Log.d("ybyb", "????????? Name_FLAG ->${Name_FLAG}")
                check()
            }, 1500) //????????? ?????? ??????
        }
    }
}
