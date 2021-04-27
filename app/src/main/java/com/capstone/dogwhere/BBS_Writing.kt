package com.capstone.dogwhere

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import com.capstone.dogwhere.DTO.BBS_Free
import com.capstone.dogwhere.DTO.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_b_b_s__writing.*
import kotlinx.android.synthetic.main.activity_register_user_profile.*
import java.io.File
import java.time.LocalDateTime

class BBS_Writing : AppCompatActivity() {
    private val FLAG_GALLERY_CODE: Int = 10
    private val TAG = BBS_Writing::class.java.simpleName
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var ImagePath: String
    lateinit var name: String
    private lateinit var name1: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_b_b_s__writing)
        storage = FirebaseStorage.getInstance()
        name = ""

        btn_gallary.setOnClickListener {
            selectPhoto()
        }
        btn_post.setOnClickListener {
            post(ImagePath)
            Thread.sleep(8000)

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

    private fun post(uri: String) {
        auth = FirebaseAuth.getInstance()
        var file = Uri.fromFile(File(uri))
        val storageRef: StorageReference = storage.getReference("gs:/dogwhere-ea26c.appspot.com")
        val ref = storageRef.child("images/${file.lastPathSegment}")
        val uid = auth.currentUser.uid
        val title = edit_title.text.toString()
        val content = edit_content.text.toString()
        val db = Firebase.firestore
        val time = 122

        val uploadTask = ref.putFile(file)

//        uploadTask.addOnFailureListener {
//
//        }.addOnSuccessListener {uploadTask ->
//            @SuppressWarnings("VisibleForTests")
//            val downloadURL = uploadTask.uploadSessionUri
//        }


        uploadTask.continueWith { taskSnapshot ->
            if (taskSnapshot.isSuccessful) {
                taskSnapshot.exception?.let {
                    throw it
                }
            }
            return@continueWith ref.downloadUrl
        }.addOnCompleteListener { it ->
            @SuppressWarnings
            if (it.isSuccessful) {
                val downloadURL = it.result
                val uid = auth.currentUser.uid
                val db = Firebase.firestore
                db.collection("users").whereEqualTo("uid", uid).get()
                    .addOnSuccessListener { result ->
                        val result = result.toObjects<User>()
                        for (document in result) {
                            name = document.userName
                            Log.d("joo", "name1 " + name)
                        }
                        val post = BBS_Free(uid, title, content, name, time.toString())
                        upload(post)
                        Log.d("joo", "name2 " + name)
                    }
                Log.d("joo", "name3 =" + name)
            } else {
                it.exception
            }
        }.addOnFailureListener {
            it.localizedMessage
        }
    }

    private fun upload(post : BBS_Free) {
        auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser.uid
        val title = edit_title.text.toString()
        val content = edit_content.text.toString()
        val db = Firebase.firestore
        val time = 122


        Log.d("joo", "name0 =" + name)


        db.collection("free_bbs").add(post)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                Log.d("joo", "hello : "+name)
                val intent = Intent(this, BBSActivity::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e ->
                Log.w(TAG,  "Error adding document", e)
            }
    }

}


//    val urlTask = uploadTask.continueWithTask { task ->
//        if (!task.isSuccessful) {
//            task.exception?.let {
//                throw it
//            }
//            Log.d(TAG, "name5 =" + name)
//
//        }
//        ref.downloadUrl
//    }.addOnCompleteListener { task ->
//        if (task.isSuccessful) {
//            val downloadUri = task.result
//            auth = FirebaseAuth.getInstance()
//            val uid = auth.currentUser.uid
//            val title = edit_title.text.toString()
//            val content = edit_content.text.toString()
//            val db = Firebase.firestore
//            val time = 122
//            Log.d(TAG, "name0 " + name)
//
//
//            db.collection("users").whereEqualTo("uid", uid).get()
//                .addOnSuccessListener { result ->
//                    val result = result.toObjects<User>()
//                    for (document in result) {
//                        name = document.userName
//                        Log.d(TAG, "name1 " + name)
//                    }
//                    Log.d(TAG, "name2 " + name)
//                }
//
////                Handler().post({},10000)
////                Thread.sleep(5000)
//            Log.d(TAG, "name3 =" + name)
//            val post = BBS_Free(uid, title, content, name, time.toString())
//
//
//            db.collection("free_bbs").add(post)
//                .addOnSuccessListener { documentReference ->
//                    Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
//                    Log.d(TAG, name)
//                    val intent = Intent(this, BBSActivity::class.java)
//                    startActivity(intent)
//                    finish()
//                }
//                .addOnFailureListener { e ->
//                    Log.w(TAG, "Error adding document", e)
//                }
//        }
//    }
//}
//
//}