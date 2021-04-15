package com.capstone.dogwhere

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.util.Log
import android.widget.EditText
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
import kotlinx.android.synthetic.main.activity_register_user_profile.btn_selectPhoto
import kotlinx.android.synthetic.main.activity_register_user_profile.btn_upload
import kotlinx.android.synthetic.main.activity_register_user_profile.userProfilePhoto
import java.io.File

class RegisterDogProfileActivity : AppCompatActivity() {
    private val FLAG_GALLERY_CODE: Int = 10
    private val TAG = RegisterDogProfileActivity::class.java.simpleName
    private lateinit var auth: FirebaseAuth
    private lateinit var rdb: FirebaseDatabase
    private lateinit var storage: FirebaseStorage
    private lateinit var ImagePath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_dog_profile)

        auth = FirebaseAuth.getInstance()
        rdb = FirebaseDatabase.getInstance()
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

    private fun upload(uri: String) {
        var file = Uri.fromFile(File(uri))
        val storageRef: StorageReference = storage.getReference("gs:/dogwhere-ea26c.appspot.com")
        val ref = storageRef.child("images/")

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
                val uid = auth.currentUser.uid
                val dogname = findViewById<EditText>(R.id.dogprofilename).getText().toString()
                val dogsex = findViewById<EditText>(R.id.dogprofilensex).getText().toString()
                val dogage = findViewById<EditText>(R.id.dogprofileage).getText().toString()
                val dogbreed = findViewById<EditText>(R.id.dogprofilebreed).getText().toString()

                val dog = DogProfile(uid, dogname, downloadUri.toString(), dogage, dogbreed, dogsex)

                val db = Firebase.firestore

                // rdb.getReference(uid).child("userprofiles").setValue()

                db.collection("dogprofiles")
                    .add(dog)
                    .addOnSuccessListener { documentReference ->
                        Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error adding document", e)
                    }

//                rdb.getReference().child("dogprofiles").setValue(dog)
                //       rdb.getReference().child("userprofiles").child(uid).setValue(dog)

                val profileRef = rdb.getReference("userprofiles")
                profileRef.child(uid).child(uid).child("dogprofiles").push().setValue(dog)

                val intent = Intent(this, MainMenuActivity::class.java)
                startActivity(intent)
                finish()

            } else {
                Log.w("실패", "업로드 실패", task.exception)
            }
        }

    }
}