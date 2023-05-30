package edu.skku.cs.yummyyuljeon

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import edu.skku.cs.yummyyuljeon.databinding.ActivityMainBinding
import java.io.FileNotFoundException

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navView.setupWithNavController(navController)

        // if favorite.json file does not exist, create file
        try {
            val stream = openFileInput("favorite.json")
            Log.i("favorite", "favorite.json exists")
            val data = stream.bufferedReader().use { it.readText() }
            Log.i("favorite", data)
            stream.close()
        } catch (e: FileNotFoundException) {
            val data = Gson().toJson(StorePlace(arrayListOf()))
            val outputStream = openFileOutput("favorite.json", MODE_PRIVATE)
            Log.i("favorite", "creating: " + data)
            outputStream.write(data.toByteArray())
            outputStream.close()
        }
    }
}