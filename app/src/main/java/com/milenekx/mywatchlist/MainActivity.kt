package com.milenekx.mywatchlist

import android.os.Bundle
import android.view.View // Import View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var bottomNavView: BottomNavigationView // Declare the property

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Set up Navigation Controller
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        navController = navHostFragment.navController // Initialize navController

        // Connect BottomNavigationView with NavController
        bottomNavView = findViewById(R.id.bottom_nav_view) // Initialize bottomNavView
        bottomNavView.setupWithNavController(navController)

        // Observe NavController destination changes ---
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.itemDetailsFragment -> {
                    bottomNavView.visibility = View.GONE
                }
                R.id.searchFragment -> {
                    bottomNavView.visibility = View.GONE
                }
                else -> {
                    // Show bottom nav for all other fragments
                    bottomNavView.visibility = View.VISIBLE
                }
            }
        }
    }
}