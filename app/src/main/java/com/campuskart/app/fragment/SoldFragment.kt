package com.campuskart.app.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.campuskart.app.adapters.SoldAdapter
import com.campuskart.app.data.SellData
import com.campuskart.app.databinding.FragmentSoldBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase


class SoldFragment : Fragment() {


    val itemList = ArrayList<SellData>()
    lateinit var itemsAdapter: SoldAdapter

    private var viewBinding: FragmentSoldBinding? = null
    private val binding get() = viewBinding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewBinding = FragmentSoldBinding.inflate(inflater, container, false)
        retriveDataFromDatabase()
        return binding.root

    }

    private fun retriveDataFromDatabase() {

        val database: FirebaseDatabase =FirebaseDatabase.getInstance("https://campusx-a99d3-default-rtdb.us-central1.firebasedatabase.app")
        val myReference: DatabaseReference =database.reference.child("Items")

        myReference.get().addOnSuccessListener {
            val user=Firebase.auth.currentUser?.uid.toString()
            itemList.clear()   //For clearing when data gets added to database.

            for(eachItem in it.children){
                val item=eachItem.getValue(SellData::class.java)
                if(item!=null && item.userUID==user&&item.sold!!){
                    itemList.add(item)
                }

                itemsAdapter= SoldAdapter(requireContext(),itemList)
                binding.recyclerView.layoutManager=GridLayoutManager(context,2)
                binding.recyclerView.adapter=itemsAdapter
                binding.emptyState.visibility = if (itemList.isEmpty()) View.VISIBLE else View.GONE
                binding.recyclerView.visibility = if (itemList.isEmpty()) View.GONE else View.VISIBLE
            }

        }.addOnFailureListener {

        }

    }


}