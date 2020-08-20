package com.example.messengerapp.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.messengerapp.AdapterClasses.UserAdapter
import com.example.messengerapp.ModelClasses.Users
import com.example.messengerapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class SearchFragment : Fragment() {

    private var userAdapter: UserAdapter? = null
    private var mUsers: List<Users>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_search, container, false)

        mUsers = ArrayList()
        retrieveAllUsers()
        return view
    }

    private fun retrieveAllUsers() {

        var firebaseUserID = FirebaseAuth.getInstance().currentUser!!.uid
        val refUsers = FirebaseDatabase.getInstance().reference.child("Users")

        refUsers.addValueEventListener(object : ValueEventListener{

            override fun onDataChange(p0: DataSnapshot) {

                (mUsers as ArrayList<Users>).clear()
                for (snapshot in p0.children){
                   val user: Users? = p0.getValue(Users::class.java)

                    if(!(user!!.getUID()).equals(firebaseUserID)){
                        (mUsers as ArrayList<Users>).add(user)
                    }
                }
                userAdapter = UserAdapter(context!!,mUsers!!,false)
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

                for (snapshot in p0.children){
                    val user: Users? = p0.getValue(Users::class.java)

                    if(!(user!!.getUID()).equals(firebaseUserID)){
                        (mUsers as ArrayList<Users>).add(user)
                    }
                }
                userAdapter = UserAdapter(context!!,mUsers!!,false)
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }
}