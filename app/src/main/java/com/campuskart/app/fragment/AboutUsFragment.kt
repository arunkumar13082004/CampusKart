package com.campuskart.app.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.campuskart.app.R
import com.campuskart.app.databinding.FragmentAboutUsBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class AboutUsFragment : Fragment() {

    private var viewBinding: FragmentAboutUsBinding? = null
    private val binding get() = viewBinding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentAboutUsBinding.inflate(inflater, container, false)

        // Only your profile button remains
        binding.Arun.setOnClickListener {
            bottomSheetFunction(
                facebookUrl = "https://www.facebook.com/arun.kmtadda",
                instaUrl = "https://www.instagram.com/arunkumar_jagadi",
                linkedInUrl = "https://www.linkedin.com/in/arunkumar-24486b264",
                githubUrl = "https://github.com/arunkumar13082004"
            )
        }

        return binding.root
    }

    private fun gotoUrl(url: String) {
        var URL = url
        if (!url.startsWith("http://") && !url.startsWith("https://"))
            URL = "http://$url"
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(URL))
        startActivity(browserIntent)
    }

    private fun bottomSheetFunction(
        facebookUrl: String?,
        instaUrl: String?,
        linkedInUrl: String?,
        githubUrl: String?
    ) {
        val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialog)
        val bottomsheet: View = LayoutInflater.from(context).inflate(
            R.layout.fragment_bottom_sheet,
            requireView().findViewById<View>(R.id.bottom_sheet_container) as? ConstraintLayout
        )

        val github = bottomsheet.findViewById<ImageView>(R.id.github_icon)
        val linkedin = bottomsheet.findViewById<ImageView>(R.id.linked_icon)
        val instagram = bottomsheet.findViewById<ImageView>(R.id.instagram_icon)
        val facebook = bottomsheet.findViewById<ImageView>(R.id.facebook_icon)

        github.setOnClickListener {
            githubUrl?.let { gotoUrl(it) } ?: Toast.makeText(activity, "Not Available", Toast.LENGTH_SHORT).show()
        }
        linkedin.setOnClickListener {
            linkedInUrl?.let { gotoUrl(it) } ?: Toast.makeText(activity, "Not Available", Toast.LENGTH_SHORT).show()
        }
        instagram.setOnClickListener {
            instaUrl?.let { gotoUrl(it) } ?: Toast.makeText(activity, "Not Available", Toast.LENGTH_SHORT).show()
        }
        facebook.setOnClickListener {
            facebookUrl?.let { gotoUrl(it) } ?: Toast.makeText(activity, "Not Available", Toast.LENGTH_SHORT).show()
        }

        bottomSheetDialog.setContentView(bottomsheet)
        bottomSheetDialog.show()
    }
}
