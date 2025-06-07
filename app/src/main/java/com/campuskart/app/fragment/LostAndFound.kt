package com.campuskart.app.fragment

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.campuskart.app.LostAndFoundInput
import com.campuskart.app.adapters.LostAndFoundAdapter
import com.campuskart.app.data.LostAndFoundData
import com.campuskart.app.databinding.FragmentLostAndFoundBinding
import com.campuskart.app.utils.CheckInternet
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.firebase.database.*

class LostAndFound : Fragment() {

    private lateinit var binding: FragmentLostAndFoundBinding
    private lateinit var emptyH: ConstraintLayout
    private lateinit var refreshLostAndFound: SwipeRefreshLayout
    private lateinit var goToTopButton: ExtendedFloatingActionButton

    private var database: FirebaseDatabase? = null
    private val objectList = ArrayList<LostAndFoundData>()
    private val foundList = ArrayList<LostAndFoundData>()
    private val lostList = ArrayList<LostAndFoundData>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLostAndFoundBinding.inflate(inflater)
        activity?.title = "Lost And Found"

        setupFabButton()
        setupRecyclerScrollListener()
        setupFirebase()
        setupSwipeRefresh()

        return binding.root
    }

    private fun setupFabButton() {
        val listIt = binding.lostandfoundFab
        goToTopButton = binding.topScrollButton

        listIt.setOnClickListener {
            val intent = Intent(context, LostAndFoundInput::class.java)
            startActivity(intent)
        }

        goToTopButton.setOnClickListener {
            binding.lostandfoundRecycler.smoothScrollToPosition(0)
        }
    }

    private fun setupRecyclerScrollListener() {
        binding.lostandfoundRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val fab = binding.lostandfoundFab
                if (dy > 10 && fab.isShown) fab.hide()
                if (dy < -10 && !fab.isShown) fab.show()
                if (dy < -10 && goToTopButton.isShown) goToTopButton.hide()
                if (dy > 10 && !goToTopButton.isShown) goToTopButton.show()
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    Handler(Looper.getMainLooper()).postDelayed({
                        if (goToTopButton.isShown &&
                            binding.lostandfoundRecycler.scrollState == RecyclerView.SCROLL_STATE_IDLE
                        ) {
                            goToTopButton.hide()
                        }
                    }, 1000)
                }
            }
        })
    }

    private fun setupFirebase() {
        emptyH = binding.emptyhome
        database = FirebaseDatabase.getInstance("https://campusx-a99d3-default-rtdb.us-central1.firebasedatabase.app")

        database!!.reference.child("LostAndFound")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    objectList.clear()
                    foundList.clear()
                    lostList.clear()

                    binding.loadingAnimation.visibility = View.GONE
                    binding.lostandfoundFab.visibility = View.VISIBLE

                    for (item in snapshot.children) {
                        val obj = item.getValue(LostAndFoundData::class.java)
                        if (obj != null) {
                            objectList.add(obj)
                            when (obj.lostOrFound) {
                                "FOUND" -> foundList.add(obj)
                                "LOST" -> lostList.add(obj)
                            }
                        }
                    }

                    emptyH.visibility = if (objectList.isEmpty()) View.VISIBLE else View.GONE

                    objectList.sortBy { it.dateAdded }
                    foundList.sortBy { it.dateAdded }
                    lostList.sortBy { it.dateAdded }

                    binding.lostandfoundRecycler.adapter =
                        context?.let { LostAndFoundAdapter(it, objectList) }

                    updateFilterButtons()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Failed to load data", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun setupSwipeRefresh() {
        refreshLostAndFound = binding.lostandfoundSwipeRefresh
        refreshLostAndFound.setOnRefreshListener {
            if (!CheckInternet.isConnectedToInternet(requireContext())) {
                Toast.makeText(
                    context, "Couldn't refresh! Check your network...",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                binding.lostandfoundRecycler.adapter?.notifyDataSetChanged()
            }
            refreshLostAndFound.isRefreshing = false
        }
    }

    private fun updateFilterButtons() {
        binding.lostandfoundFilterFound.setOnClickListener {
            binding.lostandfoundRecycler.adapter =
                context?.let { LostAndFoundAdapter(it, foundList) }

            setFilterColors("#23ba29", "#d1cfcf", "#d1cfcf")
            updateSwipeRefresh(foundList)
        }

        binding.filterlost.setOnClickListener {
            binding.filterlost.isSelected = !binding.filterlost.isSelected // ✅ FIXED LINE

            binding.lostandfoundRecycler.adapter =
                context?.let { LostAndFoundAdapter(it, lostList) }

            setFilterColors("#d1cfcf", "#F44336", "#d1cfcf")
            updateSwipeRefresh(lostList)
        }

        binding.lostandfoundFilterAll.setOnClickListener {
            binding.lostandfoundRecycler.adapter =
                context?.let { LostAndFoundAdapter(it, objectList) }

            setFilterColors("#d1cfcf", "#d1cfcf", "#FEC202")
            updateSwipeRefresh(objectList)
        }
    }

    private fun updateSwipeRefresh(list: List<LostAndFoundData>) {
        refreshLostAndFound.setOnRefreshListener {
            binding.lostandfoundRecycler.adapter =
                context?.let { LostAndFoundAdapter(it, list as MutableList<LostAndFoundData>) }
            refreshLostAndFound.isRefreshing = false
        }
    }

    private fun setFilterColors(found: String, lost: String, all: String) {
        binding.lostandfoundFilterFound.backgroundTintList = ColorStateList.valueOf(Color.parseColor(found))
        binding.filterlost.backgroundTintList = ColorStateList.valueOf(Color.parseColor(lost))
        binding.lostandfoundFilterAll.backgroundTintList = ColorStateList.valueOf(Color.parseColor(all))
    }
}
