package edu.skku.cs.yummyyuljeon

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import java.io.IOException

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
        val image = intent.getStringExtra("image")
        val x = intent.getStringExtra("x")
        val y = intent.getStringExtra("y")

        val nameView = findViewById<TextView>(R.id.detailTitle)
        val addressView = findViewById<TextView>(R.id.address)
        val imageView = findViewById<ImageView>(R.id.detailImage)
        val phoneView = findViewById<TextView>(R.id.phoneNumber)

        nameView.text = name
        addressView.text = address
        phoneView.text = phone
        Picasso.get().load(image).into(imageView)

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

        val url = getString(R.string.base_url) + "/place" + id
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                val gson = Gson()
                val data = gson.fromJson(body, ApiDetail::class.java)

                CoroutineScope(Dispatchers.Main).launch {
                    val openingHourView = findViewById<TextView>(R.id.openingHours)

                    openingHourView.text = data.openHour

                    // update recyclerview


//                            binding.progressBar.visibility = View.GONE
//                            val gridView = binding.gridCard
//                            adapter = CardAdapter(requireContext(), places)
//                            gridView.adapter = adapter
//
//                            // Update height of gridview depending on the number of cards
//                            val numRows = (places.size + 1) / 2
//                            val cardHeight = TypedValue.applyDimension(
//                                TypedValue.COMPLEX_UNIT_DIP,
//                                230f * numRows + 40f,
//                                resources.displayMetrics
//                            ).toInt()
//                            gridView.layoutParams.height = cardHeight
//                            gridView.requestLayout()
//                            gridView.visibility = View.VISIBLE
                }
            }
        })
    }
}