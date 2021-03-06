package com.capstone.dogwhere

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.capstone.dogwhere.DTO.BBS_Transaction
import com.capstone.dogwhere.DTO.User
import com.capstone.dogwhere.DTO.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_b_b_s__transaction__writing.*
import kotlinx.android.synthetic.main.activity_register_dog_profile.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class BBS_Transaction_Writing : AppCompatActivity() {

    private val FLAG_GALLERY_CODE: Int = 10
    private val TAG = BBS_Common_Writing::class.java.simpleName
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private var ImagePath = "null"
    lateinit var name: String
    private lateinit var name1: String
    private var photoselect = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_b_b_s__transaction__writing)
        storage = FirebaseStorage.getInstance()
        name = ""

        btn_gallary.setOnClickListener {
            selectPhoto()
        }
        btn_post.setOnClickListener {
            post(ImagePath)

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
            if (data != null) {
                Log.d(TAG, getImageFilePath(data!!.data!!))
                ImagePath = getImageFilePath(data!!.data!!)
                var file = Uri.fromFile(File(getImageFilePath(data!!.data!!)))
                img_product.visibility= View.VISIBLE
                Glide.with(this).load(file).placeholder(R.drawable.backgroundgray)
                    .apply(RequestOptions()).into(img_product)

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

    private fun post(uri: String) {
        auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser!!.uid
        val title = edit_title.text.toString()
        val content = edit_content.text.toString()
        val price = edit_price.text.toString()
        val db = Firebase.firestore
        val doc = db.collection(intent.getStringExtra("tab").toString()).document()
        val time = currenttime()

        if (uri == "null") {
            db.collection("users").document(uid).collection("userprofiles").document(uid).get()
                .addOnSuccessListener { result ->

                    val result = result.toObject<UserProfile>()

                    name = result?.userName.toString()
                    val post = BBS_Transaction(
                        uid,
                        title,
                        price,
                        content,
                        "null",
                        name,
                        doc.id,
                        0,
                        0,
                        time.toString()
                    )
                    Log.d("joo", "tab : " + intent.getStringExtra("tab"))
                    upload(post)
                    Log.d("joo", "tab1 : " + intent.getStringExtra("tab"))
                }

        } else {
            var file = Uri.fromFile(File(uri))
            val storageRef: StorageReference =
                storage.getReference("gs:/dogwhere-ea26c.appspot.com")
            val ref = storageRef.child("images/${file.lastPathSegment}")
            val uploadTask = ref.putFile(file)

            uploadTask.continueWithTask { taskSnapshot ->
                if (taskSnapshot.isSuccessful) {
                    taskSnapshot.exception?.let {
                        throw it
                    }
                }
                return@continueWithTask ref.downloadUrl
            }.addOnCompleteListener { task ->
//                @SuppressWarnings
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    Log.d("yb","transactionuri img -> ${downloadUri}")
                    val uid = auth.currentUser!!.uid
                    val db = Firebase.firestore
                    db.collection("users").whereEqualTo("uid", uid).get()
                        .addOnSuccessListener { result ->
                            val result = result.toObjects<User>()
                            for (document in result) {
                                name = document.userName
                            }
                            val post = BBS_Transaction(
                                uid,
                                title,
                                price,
                                content,
                                downloadUri.toString(),
                                name,
                                doc.id,
                                0,
                                0,
                                time.toString()
                            )
                            upload(post)
                        }
                } else {
                    task.exception
                }
            }.addOnFailureListener {
                it.localizedMessage
            }
        }
    }


    private fun upload(post: BBS_Transaction) {
        val db = Firebase.firestore

        Log.d("joo", "name0 =" + name)

        val doc = db.collection(intent.getStringExtra("tab").toString()).document(post.oid)

        doc.set(post)
            .addOnSuccessListener { documentReference ->
                Log.d("joo", "tab2 : " + intent.getStringExtra("tab"))
                val intent = Intent(this, MainMenuActivity::class.java)
                intent.putExtra("state", intent.getStringExtra("tab").toString())
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }

    }

    private fun currenttime(): String? {
        val time = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd kk:mm:ss")
        val curTime = dateFormat.format(Date(time))
        Log.d("check", curTime)
        return curTime
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