package com.example.messengerapp.Fragments

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import com.example.messengerapp.ModelClasses.Users
import com.example.messengerapp.R
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import com.squareup.picasso.Request
import kotlinx.android.synthetic.main.fragment_settings.view.*

class SettingsFragment : Fragment() {

    var usersRefrence: DatabaseReference? = null
    var firebaseUser: FirebaseUser? = null
    private val RequesteCode = 483
    private var imageeUri: Uri? = null
    private var storgeRef: StorageReference? = null
    private var coverChecker: String? = ""
    private var socialChecker: String? = ""

    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        firebaseUser = FirebaseAuth.getInstance().currentUser
        usersRefrence = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)
        storgeRef = FirebaseStorage.getInstance().reference.child("User Images")

        usersRefrence!!.addValueEventListener(object : ValueEventListener{

            override fun onDataChange(p0: DataSnapshot) {

                if (p0.exists()){

                    val user: Users? = p0.getValue(Users::class.java)

                    if (context!=null){

                        view.username_settings.text = user!!.getUserName()
                        Picasso.get().load(user.getProfile()).into(view.profile_image_settings)
                        Picasso.get().load(user.getCover()).into(view.cover_image_setting)

                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }

        })
        view.profile_image_settings.setOnClickListener {

            PickImage()

        }
        view.cover_image_setting.setOnClickListener {
            coverChecker = "cover"

            PickImage()

        }

        view.set_facebook.setOnClickListener {
            socialChecker = "facebook"
            setSocialLinks()

        }

        view.set_instagram.setOnClickListener {
            socialChecker = "instagram"
            setSocialLinks()

        }

        view.set_website.setOnClickListener {
            socialChecker = "website"
            setSocialLinks()

        }

        view.set_leader.setOnClickListener {
            socialChecker = "leader"
            setSocialLinks()

        }

        view.username_settings.setOnClickListener {
            socialChecker = "username"
            setSocialLinks()

        }


        return view
    }

    private fun setSocialLinks() {
        val builder: AlertDialog.Builder =
            AlertDialog.Builder(context, R.style.Theme_AppCompat_DayNight_Dialog_Alert)

        if (socialChecker == "leader") {

            builder.setTitle("コーチ登録をしますか？「はい」または「いいえ」でお答え下さい。")

        }
            else if (socialChecker == "website") {

                builder.setTitle("URLを入力して下さい。")


            } else {

                builder.setTitle("ユーザー名を入力して下さい。")

            }

            val editText = EditText(context)

        if (socialChecker == "leader") {

            editText.hint = "はい　いいえ"

        }
        else if (socialChecker == "website") {

                editText.hint = "www.google.com"

            } else {

                editText.hint = "ユーザー名を入力して下さい"

            }

            builder.setView(editText)

            builder.setPositiveButton("決定", DialogInterface.OnClickListener {

                    dialog, which ->

                val str = editText.text.toString()

                if (str == "") {
                    Toast.makeText(context, "項目を入力して下さい。", Toast.LENGTH_LONG).show()
                } else {
                    saveSocialLink(str)
                }
            })

            builder.setNegativeButton("キャンセル", DialogInterface.OnClickListener {

                    dialog, which ->

                dialog.cancel()

            })

            builder.show()
    }

    private fun saveSocialLink(str: String) {

        val  mapSocial = HashMap<String,Any>()

        when(socialChecker){

            "facebook" -> {

                mapSocial["facebook"] = "https://m.facebook.com/$str"
            }

            "instagram" -> {

                mapSocial["instagram"] = "https://m.instagram.com/$str"
            }

            "website" -> {

                mapSocial["website"] = "https://$str"
            }

            "leader" -> {

                mapSocial["leader"] = "$str"
            }


            "username" -> {

                mapSocial["username"] = "$str"
                mapSocial["search"] = "$str"
            }

        }

        usersRefrence!!.updateChildren(mapSocial).addOnCompleteListener {
            task ->
            if (task.isSuccessful){
                Toast.makeText(context, "アップデートが完了しました。",Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun PickImage() {
        val intent = Intent()
        intent.type = "image/"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent,RequesteCode)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RequesteCode && resultCode == Activity.RESULT_OK && data!!.data != null){

            imageeUri = data.data
            Toast.makeText(context, "アップロード中",Toast.LENGTH_LONG).show()
            uploadImageToDatabase()

        }
    }

    private fun uploadImageToDatabase() {

        val progressBar = ProgressDialog(context)
        progressBar.setMessage("画像アップロード中です。しばらくお待ちください。")
        progressBar.show()

        if (imageeUri!=null){

            val fileRef = storgeRef!!.child(System.currentTimeMillis().toString()+ ".jpg")
            var uploadTask: StorageTask<*>
            uploadTask = fileRef.putFile(imageeUri!!)

            uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot, Task<Uri>>{ task ->
                if (!task.isSuccessful){
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation fileRef.downloadUrl
            }).addOnCompleteListener { task ->
                if (task.isSuccessful){

                    val downloadUrl = task.result
                    val url = downloadUrl.toString()

                    if (coverChecker == "cover"){

                        val  mapCoverImg = HashMap<String,Any>()
                        mapCoverImg["cover"] = url
                        usersRefrence!!.updateChildren(mapCoverImg)
                        coverChecker = ""

                    } else {

                        val  mapProfileImg = HashMap<String,Any>()
                        mapProfileImg["profile"] = url
                        usersRefrence!!.updateChildren(mapProfileImg)
                        coverChecker = ""

                    }
                    progressBar.dismiss()
                }
            }
        }

    }

}
