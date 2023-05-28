package edu.skku.cs.yummyyuljeon

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class DetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val name = intent.getStringExtra("name")
        val address = intent.getStringExtra("address")

        val nameView = findViewById<TextView>(R.id.detailTitle)
        val addressView = findViewById<TextView>(R.id.address)

        nameView.text = name
        addressView.text = address
    }
}