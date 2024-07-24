package io.getmadd.openpsychic.activity

import android.media.MediaPlayer
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import io.getmadd.openpsychic.R
import io.getmadd.openpsychic.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController = findNavController(R.id.nav_host_fragment_content_main)

        mediaPlayer = MediaPlayer.create(this, R.raw.zen)
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