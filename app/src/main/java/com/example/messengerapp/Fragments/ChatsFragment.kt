package com.example.messengerapp.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.messengerapp.AdapterClasses.UserAdapter
import com.example.messengerapp.ModelClasses.Chatlist
import com.example.messengerapp.ModelClasses.Users
import com.example.messengerapp.Notifications.Token
import com.example.messengerapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.iid.FirebaseInstanceId


class ChatsFragment : Fragment() {

    private var userAdapter: UserAdapter? = null
    private var mUsers: List<Users>? = null
    private var usersChatList: List<Chatlist>? = null
    //1行分のデータを1行分のViewに設定して生成
    lateinit var recycler_view_chatlist: RecyclerView
    private var firebaseUser: FirebaseUser? = null

    override fun onCreateView(

        //Fragmentが初めてUIを描画する時にシステムが呼び出す
        inflater: LayoutInflater, container: ViewGroup?,
        //Fragment生成時にシステムが呼び出す
        savedInstanceState: Bundle?
    ): View? {
        // Fragmentとlayoutを紐付ける
        val view = inflater.inflate(R.layout.fragment_chats, container, false)

        recycler_view_chatlist = view.findViewById(R.id.recycler_view_chatlist)
        recycler_view_chatlist.setHasFixedSize(true)
        recycler_view_chatlist.layoutManager = LinearLayoutManager(context)

        firebaseUser = FirebaseAuth.getInstance().currentUser

        usersChatList = ArrayList()

        val ref = FirebaseDatabase.getInstance().reference.child("ChatList").child(firebaseUser!!.uid)
        ref!!.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {

                (usersChatList as ArrayList).clear()

                for (dataSnapshot in p0.children){

                    val chatlist = dataSnapshot.getValue(Chatlist::class.java)

                    (usersChatList as ArrayList).add(chatlist!!)
                }

                retrieveChatList()

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        updateToken(FirebaseInstanceId.getInstance().token)

        return  view
    }

    private fun updateToken(token: String?) {

        val ref = FirebaseDatabase.getInstance().reference.child("Tokens")
        val token1 = Token(token!!)
        ref.child(firebaseUser!!.uid).setValue(token1)

    }

    private fun retrieveChatList(){

        mUsers = ArrayList()

        val ref = FirebaseDatabase.getInstance().reference.child("Users")
        ref!!.addValueEventListener(object : ValueEventListener{

            override fun onDataChange(p0: DataSnapshot) {

                (mUsers as ArrayList).clear()

                for (dataSnapshot in p0.children){

                    val user = dataSnapshot.getValue(Users::class.java)

                    for (eachChatList in usersChatList!!){

                        if (user!!.getUID().equals(eachChatList.getId())){

                            (mUsers as ArrayList).add(user!!)
                        }
                    }
                }
                userAdapter = UserAdapter(context!!,(mUsers as ArrayList<Users>), true)
                recycler_view_chatlist.adapter = userAdapter
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }
}

