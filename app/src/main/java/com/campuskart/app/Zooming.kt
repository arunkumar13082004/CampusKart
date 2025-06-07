package com.campuskart.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.campuskart.app.databinding.ActivityZoomingBinding

class Zooming : AppCompatActivity() {

    private lateinit var binding: ActivityZoomingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityZoomingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val primaryImage = intent.getStringExtra("PrimaryImage")
        val image0 = intent.getStringExtra("Image0")
        val image1 = intent.getStringExtra("Image1")
        val image2 = intent.getStringExtra("Image2")

        if (!primaryImage.isNullOrEmpty()) {
            Glide.with(this).load(primaryImage).into(binding.primaryImage)
        } else {
            Glide.with(this).load(R.drawable.no_image).into(binding.primaryImage)
            binding.primaryImage.isZoomable = false
            binding.primaryImage.doubleTapToZoom = false
            binding.previous.visibility = View.GONE
            binding.next.visibility = View.GONE
        }

        if (!image0.isNullOrEmpty()) {
            Glide.with(this).load(image0).into(binding.image0)
        } else {
            binding.viewFlipper.removeView(binding.image0)
        }

        if (!image1.isNullOrEmpty()) {
            Glide.with(this).load(image1).into(binding.image1)
        } else {
            binding.viewFlipper.removeView(binding.image1)
        }

        if (!image2.isNullOrEmpty()) {
            Glide.with(this).load(image2).into(binding.image2)
        } else {
            binding.viewFlipper.removeView(binding.image2)
        }

        binding.previous.setOnClickListener {
            binding.viewFlipper.showPrevious()
        }
        binding.next.setOnClickListener {
            binding.viewFlipper.showNext()
        }
    }
}
