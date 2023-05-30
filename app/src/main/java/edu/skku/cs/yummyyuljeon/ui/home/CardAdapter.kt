package edu.skku.cs.yummyyuljeon.ui.home

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation
import edu.skku.cs.yummyyuljeon.*

class CardAdapter(private val context: Context, private val items: List<Place>) :
    BaseAdapter() {

    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(p0: Int): Any {
        return items[p0]
    }

    override fun getItemId(p0: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflater = LayoutInflater.from(context)
        val view = convertView ?: inflater.inflate(R.layout.card, parent, false)

        val image = view.findViewById<ImageView>(R.id.card_image)
        val name = view.findViewById<TextView>(R.id.name)
        val address = view.findViewById<TextView>(R.id.address)

        val imageUrl = items[position].image
        if (imageUrl?.isNotBlank() == true)
            Picasso.get().load(items[position].image).into(image)
        name.text = items[position].name
        address.text = items[position].address

        // if item is clicked, show detail page
        view.setOnClickListener {
            val intent = Intent(
                context,
                DetailActivity::class.java
            ).apply {
                putExtra(HomeFragment.EXT_ID, items[position].id)
                putExtra(HomeFragment.EXT_NAME, items[position].name)
                putExtra(HomeFragment.EXT_ADDRESS, items[position].address)
                putExtra(HomeFragment.EXT_PHONE, items[position].phone)
                putExtra(HomeFragment.EXT_IMAGE, items[position].image)
                putExtra(HomeFragment.EXT_X, items[position].x)
                putExtra(HomeFragment.EXT_Y, items[position].y)
            }
            context.startActivity(intent)
        }

        // set width of the card depending on the screen size
        val pxMargin = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            30f * 2 + 10f,
            context.resources.displayMetrics
        ).toInt()

        val pxWidth = context.resources.displayMetrics.widthPixels

        val viewParams = view.layoutParams
        viewParams.width = (pxWidth - pxMargin) / 2
        view.layoutParams = viewParams

        return view
    }
}
