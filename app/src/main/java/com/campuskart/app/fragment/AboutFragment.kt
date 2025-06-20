package com.campuskart.app.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.campuskart.app.MainFragmentHolder
import com.campuskart.app.databinding.FragmentAboutBinding


class AboutFragment : Fragment() {

    private var viewBinding:FragmentAboutBinding?=null
    private val binding get()= viewBinding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding= FragmentAboutBinding.inflate(inflater,container,false)

        binding.developersButton.setOnClickListener {
            val i = Intent(activity, MainFragmentHolder::class.java)
            i.putExtra("developers", "developers")
            startActivity(i)
        }
        return binding.root
    }

}