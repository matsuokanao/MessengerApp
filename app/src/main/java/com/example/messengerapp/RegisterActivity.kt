package com.example.messengerapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var refUsers: DatabaseReference
    private var firebaseUserID: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)


        val toolbar: Toolbar = findViewById(R.id.toolbar_register)
        setSupportActionBar(toolbar)
        supportActionBar!!.title="登録"
        //アプリバーのアップボタンを有効に
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {

            val intent = Intent(this@RegisterActivity,WelcomeActivity::class.java)

            startActivity(intent)

            finish()
        }

        mAuth = FirebaseAuth.getInstance()

        register_btn.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser() {
        val username: String = username_register.text.toString()
        val email: String = email_register.text.toString()
        val password: String = password_register.text.toString()

        if(username == ""){
            Toast.makeText(this@RegisterActivity,"名前を入力して下さい。"
                ,Toast.LENGTH_LONG).show()

        } else if(email == ""){
            Toast.makeText(this@RegisterActivity,"メールアドレスを入力して下さい。"
                ,Toast.LENGTH_LONG).show()

        } else if(password == ""){
            Toast.makeText(this@RegisterActivity,"パスワードを入力して下さい。"
                ,Toast.LENGTH_LONG).show()

        } else {
            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener{
                task ->
                if(task.isSuccessful){
                    firebaseUserID = mAuth.currentUser!!.uid
                    refUsers = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUserID)

                    val userHashMap = HashMap<String,Any>()
                    userHashMap["uid"] = firebaseUserID
                    userHashMap["username"] = username
                    userHashMap["profile"] = "https://firebasestorage.googleapis.com/v0/b/messengerapp-43583.appspot.com/o/profile.png?alt=media&token=e35fc315-f6d9-4fef-8d74-2ceb41410f85"
                    userHashMap["cover"] = "https://firebasestorage.googleapis.com/v0/b/messengerapp-43583.appspot.com/o/cover.jpeg?alt=media&token=81bb04fb-e081-4b0d-81b7-8f3c19a3de61"
                    userHashMap["status"] = "offline"
                    //toLowerCase() 小文字に変換
                    userHashMap["search"] = username.toLowerCase()
                    userHashMap["facebook"] = "https://m.facebook.com"
                    userHashMap["instagram"] = "https://m.instagram.com"
                    userHashMap["website"] = "https://m.google.com"
                    userHashMap["leader"] = "いいえ"
                    userHashMap["introduction"] = "紹介文を入力"

                    refUsers.updateChildren(userHashMap)
                        .addOnCompleteListener { task ->

                            if (task.isSuccessful){

                                val intent = Intent(this@RegisterActivity,MainActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                                finish()

                            }
                        }

                } else {

                    Toast.makeText(this@RegisterActivity,"エラーが発生しました。"
                        ,Toast.LENGTH_LONG).show()

                }
            }
        }
    }
}