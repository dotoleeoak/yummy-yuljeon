package edu.skku.cs.yummyyuljeon

import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.util.concurrent.TimeUnit

class DetailActivity : AppCompatActivity() {
    companion object {
        const val name = "name"
        const val x = "x"
        const val y = "y"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val id = intent.getStringExtra("id")
        val name = intent.getStringExtra("name")
        val address = intent.getStringExtra("address")
        val phone = intent.getStringExtra("phone")
        val distance = intent.getStringExtra("distance")
        val image = intent.getStringExtra("image")
        val x = intent.getStringExtra("x")
        val y = intent.getStringExtra("y")

        val nameView = findViewById<TextView>(R.id.detailTitle)
        val addressView = findViewById<TextView>(R.id.address)
        val imageView = findViewById<ImageView>(R.id.detailImage)
        val phoneView = findViewById<TextView>(R.id.phoneNumber)

        nameView.text = name
        addressView.text = address
        if (phone?.isNotBlank() == true) {
            phoneView.text = phone
            phoneView.setOnClickListener {
                val target = "tel:" + phone.replace("-", "")
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse(target))
                startActivity(intent)
            }
        }
        if (image?.isNotBlank() == true) Picasso.get().load(image).into(imageView)

        // check if item is in favorite
        val inputStream = openFileInput("favorite.json")
        val json = inputStream.bufferedReader().use { it.readText() }
        Log.i("favorite", json)
        inputStream.close()
        val store = Gson().fromJson(json, StorePlace::class.java)
        // if item is in favorite, change button image
        if (store.favorites.any { it.id == id }) {
            val favoriteButton = findViewById<Button>(R.id.buttonFavorite)
            favoriteButton.setBackgroundResource(R.drawable.rounded_button_color)
            favoriteButton.setTextColor(resources.getColor(R.color.white, null))
            val colorStateList = ColorStateList.valueOf(
                resources.getColor(R.color.white, null)
            )
            favoriteButton.compoundDrawableTintList = colorStateList
        }

        // add item to favorite
        val favoriteButton = findViewById<Button>(R.id.buttonFavorite)
        favoriteButton.setOnClickListener {
            val inputStream = openFileInput("favorite.json")
            val json = inputStream.bufferedReader().use { it.readText() }
            inputStream.close()
            val data = Gson().fromJson(json, StorePlace::class.java)
            // if item is in favorite, remove item
            if (data.favorites.any { it.id == id }) {
                data.favorites.removeIf { it.id == id }
                favoriteButton.setBackgroundResource(R.drawable.rounded_button)
                favoriteButton.setTextColor(resources.getColor(R.color.button_main, null))
                val colorStateList = ColorStateList.valueOf(
                    resources.getColor(R.color.button_main, null)
                )
                favoriteButton.compoundDrawableTintList = colorStateList
                Toast.makeText(this, "Removed from Favorites 🗑️", Toast.LENGTH_SHORT).show()
            } else {
                data.favorites.add(Place(id, name, address, phone, distance, image, x, y))
                favoriteButton.setBackgroundResource(R.drawable.rounded_button_color)
                favoriteButton.setTextColor(resources.getColor(R.color.white, null))
                val colorStateList = ColorStateList.valueOf(
                    resources.getColor(R.color.white, null)
                )
                favoriteButton.compoundDrawableTintList = colorStateList
                Toast.makeText(
                    this, "Added to Favorites! ❤️", Toast.LENGTH_SHORT
                ).show()
            }
            val outputStream = openFileOutput("favorite.json", MODE_PRIVATE)
            val newData = Gson().toJson(data)
            outputStream.write(newData.toByteArray())
            outputStream.close()
        }

        // open map activity on click map button
        val mapButton = findViewById<Button>(R.id.buttonMap)
        mapButton.setOnClickListener {
            val intent = intent
            intent.setClass(this, MapActivity::class.java).apply {
                putExtra("name", name)
                putExtra("x", x)
                putExtra("y", y)
            }
            startActivity(intent)
        }

        val url = getString(R.string.base_url) + "/place/" + id
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                val json = response.body!!.string()
                Log.i(localClassName, json)
                if (json.contains("502 Bad Gateway")) {
                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(
                            this@DetailActivity,
                            "Server is down, please try again later",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    return
                }
                val data = Gson().fromJson(json, ApiDetail::class.java)

                CoroutineScope(Dispatchers.Main).launch {
                    val openingHourView = findViewById<TextView>(R.id.openingHours)
                    val reviewList = findViewById<ListView>(R.id.reviewList)

                    openingHourView.text = data.openHour

                    val progressBar = findViewById<View>(R.id.progressBar)
                    progressBar.visibility = View.GONE
                    reviewList.visibility = View.VISIBLE

                    val reviews = data.reviews
                    reviewList.adapter = ReviewAdapter(this@DetailActivity, reviews!!)

                    // Update height of listview depending on the number of cards
                    var totalHeight = 0
                    for (i in 0 until reviewList.adapter.count) {
                        val view = reviewList.adapter.getView(i, null, reviewList)
                        view.measure(0, 0)
                        totalHeight += view.measuredHeight
                        // add vertical padding
                        totalHeight += TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            20f,
                            resources.displayMetrics
                        ).toInt()
                    }
                    reviewList.layoutParams.height = totalHeight
                    reviewList.requestLayout()
                }
            }
        })
    }
}