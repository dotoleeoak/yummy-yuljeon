package edu.skku.cs.yummyyuljeon.ui.home

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    companion object {
        const val EXT_NAME = "name"
        const val EXT_ADDRESS = "address"
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

        val url = getString(R.string.base_url) + "/place"
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
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
                        200f,
                        resources.displayMetrics
                    ).toInt()
                    gridView.layoutParams.height = cardHeight * numRows
                    gridView.requestLayout()
                    gridView.visibility = View.VISIBLE
                }
            }
        })

        // shuffle list
        binding.buttonShuffle.setOnClickListener {
            places.shuffle()
            adapter?.notifyDataSetChanged()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}