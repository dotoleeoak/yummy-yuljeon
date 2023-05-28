package edu.skku.cs.yummyyuljeon

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class DetailActivity : AppCompatActivity() {
    companion object {
        const val name = "name"
        const val x = "x"
        const val y = "y"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val name = intent.getStringExtra("name")
        val address = intent.getStringExtra("address")
        val x = intent.getDoubleExtra("x", 126.974579152201)
        val y = intent.getDoubleExtra("y", 37.2940144502368)

        val nameView = findViewById<TextView>(R.id.detailTitle)
        val addressView = findViewById<TextView>(R.id.address)

        nameView.text = name
        addressView.text = address

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
    }
}