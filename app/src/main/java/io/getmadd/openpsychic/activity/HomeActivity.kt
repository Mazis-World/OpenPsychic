package io.getmadd.openpsychic.activity

import android.app.AlertDialog
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.getmadd.openpsychic.OpenPsychicApp
import io.getmadd.openpsychic.R
import io.getmadd.openpsychic.databinding.ActivityHomeBinding
import io.getmadd.openpsychic.fragments.main.LaunchFragment
import io.getmadd.openpsychic.utils.Firebase

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var firestore: FirebaseFirestore
    private lateinit var firebase: Firebase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firestore = FirebaseFirestore.getInstance()
        firebase = Firebase.getInstance()
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

        binding.include.messagesIconImageView.setOnClickListener {
            navController?.navigate(R.id.messages_fragment)
        }

        binding.includeBottomNavigationView.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_explore -> {
                    navController?.navigate(R.id.explore_fragment)
                }
                R.id.navigation_dreams -> {
                    navController?.navigate(R.id.dreams_fragment)
                }
                R.id.navigation_request -> {
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
        updateOnlineUsersCount()

    }

    private fun updateOnlineUsersCount() {
        val onlineUsersRef = firestore.collection("onlineUsers")

        onlineUsersRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.w(LaunchFragment.TAG, "Listen failed", error)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val count = snapshot.size()
                // Update UI with the count
                binding.include.topNavUsersOnlineTextView.text = "Users Online: $count"
            } else {
                Log.d(LaunchFragment.TAG, "Current data: null")
                binding.include.topNavUsersOnlineTextView.text = "Users Online: 0"
            }
        }
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