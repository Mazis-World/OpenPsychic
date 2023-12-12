package io.getmadd.openpsychic.activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.ContextThemeWrapper
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.firebase.auth.FirebaseAuth
import io.getmadd.openpsychic.R
import io.getmadd.openpsychic.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController = findNavController(R.id.nav_host_fragment_content_home)
        navController.navigateUp()
        navController.navigate(R.id.explore_fragment)

        val builder: AlertDialog.Builder =
            AlertDialog.Builder(ContextThemeWrapper(this, R.style.AlertDialogCustom))

        binding.include.topNavOpLogoImageView.setOnClickListener {
            builder
                .setMessage("Are you sure you want to logout?")
                .setTitle("Logout")
                .setPositiveButton("Logout") { dialog, which ->
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
                .setNegativeButton("Cancel") { dialog, which ->
                    dialog.cancel()
                }.create().show()

        }


        binding.includeBottomNavigationView.bottomNavigationView.setOnItemSelectedListener { item ->
            // do stuff
            when (item.itemId) {
                R.id.navigation_explore -> {
                    navController.navigate(R.id.explore_fragment)
                }
                R.id.navigation_history -> {
                    navController.navigate(R.id.history_fragment)
                }
                R.id.navigation_profile -> {
                    navController.navigate(R.id.history_fragment)
                }
            }
            return@setOnItemSelectedListener true
        }
    }
}