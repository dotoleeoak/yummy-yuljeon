package edu.skku.cs.yummyyuljeon

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class ReviewAdapter(private val context: Context, private val items: List<ApiDetail.Review>) : BaseAdapter() {
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

        val review = view.findViewById<TextView>(R.id.review)
        review.text = items[position].content

        return view
    }
}
