package com.campuskart.app.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.campuskart.app.LostAndFoundDescriptionPage
import com.campuskart.app.R
import com.campuskart.app.data.LostAndFoundData
import com.campuskart.app.databinding.LayoutLostandfoundItemBinding
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class LostAndFoundAdapter(val context: Context,val objectList:MutableList<LostAndFoundData> ):
    RecyclerView.Adapter<LostAndFoundAdapter.LostAndFoundViewHolder>() {




    inner class LostAndFoundViewHolder(val view: View):RecyclerView.ViewHolder(view){

        var binding : LayoutLostandfoundItemBinding = LayoutLostandfoundItemBinding.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LostAndFoundViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.layout_lostandfound_item,parent,false)
        return LostAndFoundViewHolder(view)
    }

    override fun onBindViewHolder(holder: LostAndFoundViewHolder, position: Int) {

        val obj = objectList[objectList.size-position-1]

        val databaseUser =
            FirebaseDatabase.getInstance("https://campusx-a99d3-default-rtdb.us-central1.firebasedatabase.app")
                .getReference("Users")
        databaseUser.child(obj.uid.toString()).get().addOnSuccessListener { snapshot ->

            if (snapshot.exists()) {
                val name = snapshot.child("name").value.toString()
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                holder.binding.lostandfoundPosterUserName.text = name
            }
        }.addOnFailureListener {
            TODO("Not yet implemented")
        }
        holder.binding.lostandfoundObject.text= obj.objectName
        holder.binding.lostandfoundLocation.text = obj.objectLocation

        if (obj.imagePrimary != "") {
            Glide.with(context).load(obj.imagePrimary).into(holder.binding.lostandfoundObjectimage)
        }
       else{
           Glide.with(context).load(R.drawable.no_image).into(holder.binding.lostandfoundObjectimage)

       }

        when (obj.lostOrFound) {
            "FOUND" -> {
                holder.binding.indicatorRed.setImageResource(R.drawable.found_newtag)
                println("THE OBJECT WAS : ${obj.lostOrFound} ${obj.objectName}" )
            }
            "LOST" -> {
                holder.binding.indicatorRed.setImageResource(R.drawable.lost_newtag)
                println("THE OBJECT WAS : ${obj.lostOrFound} ${obj.objectName}" )
            }
            else -> {
                //holder.binding.indicatorRed.visibility = View.VISIBLE
                println("THE OBJECT WAS end case : ${obj.lostOrFound} ${obj.objectName}" )
            }
        }

        holder.itemView.setOnClickListener {
            val value = obj.pid.toString()
            val i = Intent(context, LostAndFoundDescriptionPage::class.java)
            i.putExtra("key", value)
            context.startActivity(i)
        }
    }
    override fun getItemCount(): Int {
        return objectList.size
    }
}