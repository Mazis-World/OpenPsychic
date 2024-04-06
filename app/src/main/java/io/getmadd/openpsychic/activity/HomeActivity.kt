package io.getmadd.openpsychic.activity

import android.app.AlertDialog
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import io.getmadd.openpsychic.R
import io.getmadd.openpsychic.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_home)?.let {
                navHostFragment -> navHostFragment.findNavController()
        }

        navController?.let {
            it.navigateUp()
            it.navigate(R.id.explore_fragment)
        }

        val builder: AlertDialog.Builder =
            AlertDialog.Builder(ContextThemeWrapper(this, R.style.AlertDialogCustom))

        binding.include.topNavOpLogoImageView.setOnClickListener {
            builder
                .setMessage("Are you sure you want to logout?")
                .setTitle("Logout")
                .setCancelable(false)
                .setPositiveButton("Logout") { dialog, which ->
                    FirebaseAuth.getInstance().signOut()
                }
                .setNegativeButton("Cancel") { dialog, which ->
                    dialog.cancel()
                }.create().show()
        }

        binding.include.topNavBellIconImageView.setOnClickListener {
            navController?.navigate(R.id.notification_fragment)
        }

        binding.includeBottomNavigationView.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_explore -> {
                    navController?.navigate(R.id.explore_fragment)
                }
                R.id.navigation_dreams -> {
                    navController?.navigate(R.id.dreams_fragment)
                }
                R.id.navigation_history -> {
                    navController?.navigate(R.id.history_fragment)
                }
                R.id.navigation_profile -> {
                    navController?.navigate(R.id.profile_fragment)
                }
            }
            return@setOnItemSelectedListener true
        }

        mediaPlayer = MediaPlayer.create(this, R.raw.abundance)
        mediaPlayer.isLooping = true
        mediaPlayer.start()
    }

    override fun onResume() {
        super.onResume()
        mediaPlayer.start()
    }

    override fun onPause() {
        super.onPause()
        mediaPlayer.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }
}