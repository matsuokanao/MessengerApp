package com.example.messengerapp

import android.content.Intent
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.TableLayout
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.activity_main.*
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.messengerapp.Fragments.ChatsFragment
import com.example.messengerapp.Fragments.SearchFragment
import com.example.messengerapp.Fragments.SettingsFragment
import com.example.messengerapp.ModelClasses.Chat
import com.example.messengerapp.ModelClasses.Chatlist
import com.example.messengerapp.ModelClasses.Users
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import java.text.FieldPosition



@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    //データベースでデータの読み書きを行う
    var refUsers: DatabaseReference? = null
    var firebaseUser: FirebaseUser? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //スクリーン画面にViewを設定
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar_main)

        firebaseUser = FirebaseAuth.getInstance().currentUser
        refUsers = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)


        val toolbar: Toolbar = findViewById(R.id.toolbar_main)
        //ツールバー
        setSupportActionBar(toolbar)
        supportActionBar!!.title=""

        val tabLayout: TabLayout = findViewById(R.id.tab_layout)
        val viewPager: ViewPager = findViewById(R.id.view_page)

        //チャット内容を表示する
        val ref = FirebaseDatabase.getInstance().reference.child("Chats")
        ref!!.addValueEventListener(object : ValueEventListener{
            //Chatsデータが変更されるたびに呼び出す
            override fun onDataChange(p0: DataSnapshot) {

                //adapterのインスタンス生成
                val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)

                var countUnreadMessages = 0

                for (dataSnapshot in p0.children){
                    val chat = dataSnapshot.getValue(Chat::class.java)
                    if (chat!!.getReceiver().equals(firebaseUser!!.uid) && !chat.isIsSeen()){

                        countUnreadMessages += 1

                    }
                }

                if (countUnreadMessages == 0){

                    //トーク画面　フラグメント作成
                    viewPagerAdapter.addFragment(ChatsFragment(),"トーク画面")

                } else {

                    viewPagerAdapter.addFragment(ChatsFragment(),"($countUnreadMessages)トーク")
                }
                //コーチ検索画面　フラグメント作成
                viewPagerAdapter.addFragment(SearchFragment(),"コーチを探す")
                //ユーザー設定画面　フラグメント作成
                viewPagerAdapter.addFragment(SettingsFragment(),"ユーザー設定")
                /// adapterをセット
                viewPager.adapter = viewPagerAdapter
                //TabLayoutにViewPagerを設定
                tabLayout.setupWithViewPager(viewPager)

            }
            override fun onCancelled(error: DatabaseError) {

            }
        })


        //ユーザー名とプロフィール写真を表示する
        refUsers!!.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    val user: Users? = p0.getValue(Users::class.java)

                    user_name.text = user!!.getUserName()

                    //getProfileの情報をprofile_imageに設定
                    Picasso.get().load(user.getProfile()).placeholder(R.drawable.profile).into(profile_image)
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                //エラー表示
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        // Inflate the menu; this adds items to the action bar if it is present.

        menuInflater.inflate(R.menu.menu_main, menu)

        return true
    }

    //メニューのアイテムを押下した時の処理の関数
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
         when (item.itemId) {

             //ログアウトボタンを押した時
            R.id.action_logout -> {

                //ログアウト処理を行う
                FirebaseAuth.getInstance().signOut()

                val intent = Intent(this@MainActivity,WelcomeActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()

                return  true
            }
        }
        return false
    }

    //ページの情報を設定
    internal  class ViewPagerAdapter(fragmentManager: FragmentManager):

            FragmentPagerAdapter(fragmentManager)

    {
        private val fragments: ArrayList<Fragment>
        private val titles: ArrayList<String>

        init {
            fragments = ArrayList<Fragment>()
            titles = ArrayList<String>()
        }

        override fun getItem(position: Int): Fragment{
            return fragments[position]
        }

        override fun getCount(): Int {
          return fragments.size
        }
        fun addFragment(fragment: Fragment,title: String){
            fragments.add(fragment)
            titles.add(title)
        }

        override fun getPageTitle(i: Int): CharSequence? {
            return titles[i]
        }
    }
    //onlineかofflineの判断処理を行う
    private  fun updateStatus(status: String){

        val ref= FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)

        val hashMap = HashMap<String,Any>()

        hashMap["status"] = status

        //データの更新　
        ref!!.updateChildren(hashMap)
    }

    //スリープから回復した時に呼び出される処理
    override fun onResume() {
        super.onResume()

        updateStatus("online")
    }

    //ユーザーがアクティビティを離れていることを示す
    override fun onPause() {

        super.onPause()

        updateStatus("offline")

    }
}