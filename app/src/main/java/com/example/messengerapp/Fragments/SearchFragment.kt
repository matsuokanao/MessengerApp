package com.example.messengerapp.Fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.messengerapp.AdapterClasses.UserAdapter
import com.example.messengerapp.ModelClasses.Users
import com.example.messengerapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.fragment_settings.view.*


class SearchFragment : Fragment() {

    private var userAdapter: UserAdapter? = null
    private var mUsers: List<Users>? = null
    //1行分のデータを1行分のViewに設定して生成
    private var recyclerView: RecyclerView? = null
    //テキストボックス作成
    private var searchEditText: EditText? = null


    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_search, container, false)

        recyclerView = view.findViewById(R.id.searchList)
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager = LinearLayoutManager(context)
        searchEditText = view.findViewById(R.id.searchUsersET)

        mUsers = ArrayList()
        retrieveAllUsers()

        searchEditText!!.addTextChangedListener(object : TextWatcher{

            override fun beforeTextChanged(p0: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(cs: CharSequence?, start: Int, befor: Int, count: Int) {

                searchForUsers(cs.toString().toLowerCase())
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        return view
    }

    private fun retrieveAllUsers() {

        var firebaseUserID = FirebaseAuth.getInstance().currentUser!!.uid
        val refUsers = FirebaseDatabase.getInstance().reference.child("Users")

        refUsers.addValueEventListener(object : ValueEventListener{

            //変更の読み取りとリッスン
            override fun onDataChange(p0: DataSnapshot) {

                    if (searchEditText!!.text.toString() == "" ) {

                        for (snapshot in p0.children) {

                            val user: Users? = snapshot.getValue(Users::class.java)

                            if (!(user!!.getUID()).equals(firebaseUserID)) {
                                if (user!!.getLeader().equals("はい")) {
                                    (mUsers as ArrayList<Users>).add(user)
                                }
                            }
                        }
                        userAdapter = UserAdapter(context!!, mUsers!!, false)
                        recyclerView!!.adapter = userAdapter
                             }
                       }

                override fun onCancelled(error: DatabaseError) {
            }
        })
    }


    private fun searchForUsers(str: String){


        var firebaseUserID = FirebaseAuth.getInstance().currentUser!!.uid
        val queryUsers = FirebaseDatabase.getInstance().reference
            .child("Users").orderByChild("search")
            .startAt(str)
            .endAt(str + "\uf8ff")

        queryUsers.addValueEventListener(object : ValueEventListener{

            override fun onDataChange(p0: DataSnapshot) {

                    (mUsers as ArrayList<Users>).clear()

                    for (snapshot in p0.children) {
                        val user: Users? = snapshot.getValue(Users::class.java)

                        if (!(user!!.getUID()).equals(firebaseUserID)) {
                            if (user!!.getLeader().equals("はい")) {

                                (mUsers as ArrayList<Users>).add(user)

                                }
                             }
                        }

                    userAdapter = UserAdapter(context!!, mUsers!!, false)
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }
}