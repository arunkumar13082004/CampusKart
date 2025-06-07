package com.campuskart.app

import NetworkChangeReceiver
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.campuskart.app.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import okhttp3.OkHttpClient
import java.net.InetSocketAddress
import java.net.Proxy

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var hideIcon = true
    private var doubleBackToExitPressedOnce = false

    private var networkChangeReceiver: NetworkChangeReceiver? = null

    // Firebase
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var authStateListener: FirebaseAuth.AuthStateListener

    // Optional proxy (if required in your network)
    val proxy = Proxy(Proxy.Type.HTTP, InetSocketAddress("172.16.199.20", 8080))
    val okHttpClient = OkHttpClient.Builder().proxy(proxy).build()

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main_screen, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.aboutUsMenu -> {
                val i = Intent(applicationContext, MainFragmentHolder::class.java)
                i.putExtra("aboutUs", "aboutUs")
                startActivity(i)
            }
            R.id.reportUsMenu -> {
                val i = Intent(applicationContext, MainFragmentHolder::class.java)
                i.putExtra("reportUs", "reportUs")
                startActivity(i)
            }
            // You can uncomment logout logic when needed
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#ffffff")))
        supportActionBar?.setDisplayUseLogoEnabled(true)

        firebaseAuth = FirebaseAuth.getInstance()

        // Setup Firebase AuthStateListener
        authStateListener = FirebaseAuth.AuthStateListener { auth ->
            val user: FirebaseUser? = auth.currentUser
            if (user != null) {
                // Firebase ready — continue app
                setupNavigation()
            } else {
                // Not signed in — redirect to AuthActivity
                startActivity(Intent(this, AuthActivity::class.java))
                finish()
            }
        }
        firebaseAuth.addAuthStateListener(authStateListener)

        // Register network change receiver
        networkChangeReceiver = NetworkChangeReceiver(supportFragmentManager)
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(networkChangeReceiver, filter)
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
        val navController = navHostFragment!!.findNavController()

        val popupMenu = PopupMenu(this, null)
        popupMenu.inflate(R.menu.bottom_nav)
        binding.bottomBar.setupWithNavController(popupMenu.menu, navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            title = when (destination.id) {
                R.id.cartFragment -> "My Cart"
                R.id.profile -> "Profile"
                R.id.lostAndFound -> "Lost/Found"
                else -> "CampusKart"
            }
            when (destination.id) {
                R.id.profile -> {
                    hideIcon = false
                    invalidateOptionsMenu()
                }
                else -> {
                    hideIcon = true
                    invalidateOptionsMenu()
                }
            }
        }
    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }
        doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show()
        Handler(Looper.getMainLooper()).postDelayed({
            doubleBackToExitPressedOnce = false
        }, 2000)
    }

    override fun onDestroy() {
        unregisterReceiver(networkChangeReceiver)
        firebaseAuth.removeAuthStateListener(authStateListener)
        super.onDestroy()
    }
}
