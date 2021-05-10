package com.capstone.dogwhere

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import com.capstone.dogwhere.DTO.BBS_Free
import com.capstone.dogwhere.DTO.BBS_Imformation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_b_b_s__writing__imformation.*
import java.io.File
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class BBS_Writing_Imformation : AppCompatActivity() {
    private val FLAG_GALLERY_CODE: Int = 10
    private val TAG = BBS_Writing_Imformation::class.java.simpleName
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var ImagePath: String
    private lateinit var name: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_b_b_s__writing__imformation)

        storage = FirebaseStorage.getInstance()
        ImagePath = ""
        btn_gallaryImformation.setOnClickListener {
            selectPhoto()
        }
        btn_postImformation.setOnClickListener {
            post(ImagePath)
//            startActivity(Intent(this,BBSActivity::class.java))
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
        val title = edit_title_imformation.text.toString()
        val content = edit_content_imformation.text.toString()
        val time = currenttime()

        val uploadTask = ref.putFile(file)

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
                if (it.isSuccessful) {
                    val downloadURL = it.result
                    val db = Firebase.firestore

                    db.collection("users").whereEqualTo("uid", uid).get()
                        .addOnSuccessListener { result ->
                            for (document in result) {
                                Log.d(TAG,document.get("userName").toString())
                                Log.d(TAG,document["userName1"].toString())
                                name = document.get("userName").toString()
                                Log.d("joo.", "name1111 " + name)
                            }
                            val post = BBS_Imformation(
                                uid,
                                title,
                                content,
                                downloadURL.toString(),
                                name,
                                time.toString()
                            )
                            upload(post)
                            Log.d("joo", "name2 " + name)
                        }
                        .addOnFailureListener{
                            it.localizedMessage
                            it.cause
                            Log.d("dbfail","미션 ~~실패~~")
                        }
                }
            }
        }

    }

    private fun upload(post: BBS_Imformation) {
        auth = FirebaseAuth.getInstance()
        val db = Firebase.firestore

        db.collection("impormation_bbs").add(post)
            .addOnCompleteListener { documentreference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentreference.result}")
                val intent = Intent(this, BBSActivity::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }

}


private fun currenttime(): String? {

    val time = System.currentTimeMillis()
    val dateFormat = SimpleDateFormat("yyyy-mm-dd hh:mm:ss")
    val curTime = dateFormat.format(Date(time))
    Log.d("check",curTime)

//    val current = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//        LocalDateTime.now()
//    } else {
//
//    }
//    val now = LocalDate.now()
//    var formatter = DateTimeFormatter.ISO_DATE
//    val formatted = current.format(formatter)

    return curTime
}

