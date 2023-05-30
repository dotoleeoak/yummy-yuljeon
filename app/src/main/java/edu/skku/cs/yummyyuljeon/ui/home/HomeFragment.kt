package edu.skku.cs.yummyyuljeon.ui.home

import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import edu.skku.cs.yummyyuljeon.*
import edu.skku.cs.yummyyuljeon.databinding.FragmentHomeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import java.io.IOException

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var adapter: CardAdapter? = null
    private var places = ArrayList<Place>()
    private var clicked = R.id.buttonDistance1000

    companion object {
        const val EXT_ID = "id"
        const val EXT_NAME = "name"
        const val EXT_ADDRESS = "address"
        const val EXT_PHONE = "phone"
        const val EXT_IMAGE = "image"
        const val EXT_X = "x"
        const val EXT_Y = "y"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root = binding.root

        // shuffle list
        binding.buttonShuffle.setOnClickListener {
            places.shuffle()
            adapter?.notifyDataSetChanged()
        }

        fun getPlaceList(distance: Int = 1000) {
            val url = getString(R.string.base_url) + "/place"
            val path = "?distance=$distance"
            val client = OkHttpClient()
            val request = Request.Builder().url(url + path).build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }

                override fun onResponse(call: Call, response: Response) {
                    val body = response.body?.string()
                    Log.i("response", body.toString())
                    val gson = Gson()
                    val data = gson.fromJson(body, ApiPlace::class.java)
                    places = data.places!!

                    CoroutineScope(Dispatchers.Main).launch {
                        binding.progressBar.visibility = View.GONE
                        val gridView = binding.gridCard
                        adapter = CardAdapter(requireContext(), places)
                        gridView.adapter = adapter

                        // Update height of gridview depending on the number of cards
                        val numRows = (places.size + 1) / 2
                        val cardHeight = TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            230f * numRows + 40f,
                            resources.displayMetrics
                        ).toInt()
                        gridView.layoutParams.height = cardHeight
                        gridView.requestLayout()
                        gridView.visibility = View.VISIBLE
                    }
                }
            })
        }

        fun selectDistance(view: Button, distance: Int) {
            view.background = resources.getDrawable(R.drawable.rounded_button_color, null)
            view.setTextColor(resources.getColor(R.color.white, null))

            val oldView = requireView().findViewById<Button>(clicked)
            oldView.background = resources.getDrawable(R.drawable.rounded_button, null)
            oldView.setTextColor(resources.getColor(R.color.button_main, null))

            clicked = view.id

            binding.progressBar.visibility = View.VISIBLE
            binding.gridCard.visibility = View.GONE

            getPlaceList(distance)
        }

        binding.buttonDistance300.setOnClickListener {
            selectDistance(it as Button, 300)
        }

        binding.buttonDistance500.setOnClickListener {
            selectDistance(it as Button, 500)
        }

        binding.buttonDistance1000.setOnClickListener {
            selectDistance(it as Button, 1000)
        }

        binding.buttonDistance3000.setOnClickListener {
            selectDistance(it as Button, 2000)
        }

        getPlaceList()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}