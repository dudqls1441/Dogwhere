package com.capstone.dogwhere

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.capstone.dogwhere.DTO.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_register_user_profile.*
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_user_profile)
        setupPermissions()

        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()

        btn_selectPhoto.setOnClickListener {
            selectPhoto()
        }

        btn_upload.setOnClickListener {
            upload(ImagePath)
        }
    }

    private fun selectPhoto() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE)

        startActivityForResult(intent, FLAG_GALLERY_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FLAG_GALLERY_CODE) {
            Log.d(TAG, getImageFilePath(data!!.data!!))

            ImagePath = getImageFilePath(data!!.data!!)

            var file = Uri.fromFile(File(getImageFilePath(data!!.data!!)))
            userProfilePhoto.setImageURI(file)
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
                val downloadUri = task.result
                val uid = auth.currentUser.uid
                val username = findViewById<EditText>(R.id.userprofileName).getText().toString()
                val userage = findViewById<EditText>(R.id.userprofileAge).getText().toString()
                if(sex_man.isChecked){
                    usersex = "남자"
                }else if (sex_woman.isChecked){
                    usersex = "여자"
                }else{

                }

                userprofilesex.setOnCheckedChangeListener { radioGroup, i ->


                    Toast.makeText(this, usersex, Toast.LENGTH_LONG).show()
                }


//                userprofilesex.setOnCheckedChangeListener { group, i ->
//                    Log.d(TAG, sex_man.text.toString() + " < -- 값 ")
//                    if (i == R.id.sex_male) {
//                        usersex = "남자"
//                    } else {
//                        usersex = "여자"
//                    }
//                    Log.d(TAG, usersex + " < -- 값1 ")
//                    Toast.makeText(this, usersex    , Toast.LENGTH_LONG).show()
//                }
                Log.d(TAG, usersex + " < -- 값2 ")
                Toast.makeText(this, usersex, Toast.LENGTH_LONG).show()


                val userhobby = findViewById<EditText>(R.id.userprofileHobby).getText().toString()

                Toast.makeText(this, uid + "랑" + username, Toast.LENGTH_SHORT).show()
                Log.d(TAG, uid + "---" + username)

                val user = UserProfile(uid, downloadUri.toString(), userage, username, usersex)

                Log.d(
                    TAG,
                    uid + "랑" + downloadUri + "랑" + username + "랑" + userage + "랑" + usersex + "랑" + userhobby
                )
//

                val db = Firebase.firestore
//                db.collection("userprofiles")
//                    .add(user)
//                    .addOnSuccessListener { documentReference ->
//                        Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
//                    }
//                    .addOnFailureListener { e ->
//                        Log.w(TAG, "Error adding document", e)
//                    }




                db.collection("users").document(uid).collection("userprofiles").document(uid).set(user)
                    .addOnSuccessListener { documentReference ->

                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error adding document", e)
                    }

//                val profileRef = rdb.getReference("userprofiles").child(uid)
//
//                //  rdb.getReference().child("userprofiles").push().child(uid).setValue(user)
//                profileRef.child(uid).push().setValue(user)

                val intent = Intent(this, RegisterDogProfileActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Log.w("실패", "업로드 실패", task.exception)
                Toast.makeText(
                    baseContext, "유저 프로필 업로드 실패",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
