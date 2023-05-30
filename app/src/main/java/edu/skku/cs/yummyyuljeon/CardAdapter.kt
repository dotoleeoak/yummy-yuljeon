package edu.skku.cs.yummyyuljeon

import android.content.Context
import android.content.Intent
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso

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
        val distance = view.findViewById<TextView>(R.id.distance)

        Log.i("CardAdapter", "getView: ${items[position].image}")

        val imageUrl = items[position].image
        if (imageUrl?.isNotBlank() == true)
            Picasso.get().load(items[position].image).into(image)
        name.text = items[position].name
        address.text = items[position].address
        distance.text = items[position].distance + 'm'

        // if item is clicked, show detail page
        view.setOnClickListener {
            val intent = Intent(
                context,
                DetailActivity::class.java
            ).apply {
                putExtra(ListFragment.EXT_ID, items[position].id)
                putExtra(ListFragment.EXT_NAME, items[position].name)
                putExtra(ListFragment.EXT_ADDRESS, items[position].address)
                putExtra(ListFragment.EXT_PHONE, items[position].phone)
                putExtra(ListFragment.EXT_IMAGE, items[position].image)
                putExtra(ListFragment.EXT_DISTANCE, items[position].distance)
                putExtra(ListFragment.EXT_X, items[position].x)
                putExtra(ListFragment.EXT_Y, items[position].y)
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
