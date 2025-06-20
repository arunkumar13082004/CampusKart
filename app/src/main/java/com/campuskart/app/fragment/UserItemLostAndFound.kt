package com.campuskart.app.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager

import com.campuskart.app.adapters.userItemLostFoundAdapter
import com.campuskart.app.data.LostAndFoundData

import com.campuskart.app.databinding.FragmentUserItemLostAndFoundBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase


class UserItemLostAndFound : Fragment() {


    val itemList = ArrayList<LostAndFoundData>()
    lateinit var itemsAdapter: userItemLostFoundAdapter


    private var viewBinding: FragmentUserItemLostAndFoundBinding? = null
    private val binding get() = viewBinding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewBinding = FragmentUserItemLostAndFoundBinding.inflate(inflater, container, false)
        retriveDataFromDatabase()
        return binding.root

    }


    private fun retriveDataFromDatabase() {
        val database: FirebaseDatabase =
            FirebaseDatabase.getInstance("https://campusx-a99d3-default-rtdb.us-central1.firebasedatabase.app")
        val myReference: DatabaseReference = database.reference.child("LostAndFound")

        myReference.get().addOnSuccessListener {
            val user = Firebase.auth.currentUser?.uid.toString()

            itemList.clear()   //For clearing when data gets added to database.

            for (eachItem in it.children) {
                val item = eachItem.getValue(LostAndFoundData::class.java)
                if (item != null && item.uid == user) {
                    itemList.add(item)

                }

            }

            if(itemList.isEmpty()){
                binding.emptylostAndFound.visibility=View.VISIBLE
            }

            itemsAdapter = userItemLostFoundAdapter(requireContext(), itemList)
            binding.recyclerView.layoutManager = GridLayoutManager(activity,2)
            binding.recyclerView.adapter = itemsAdapter

        }.addOnFailureListener {

        }
    }

}