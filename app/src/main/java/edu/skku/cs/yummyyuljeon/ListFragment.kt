package edu.skku.cs.yummyyuljeon

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import edu.skku.cs.yummyyuljeon.databinding.FragmentListBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class ListFragment : Fragment() {
    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!
    private var adapter: CardAdapter? = null
    private var places = ArrayList<Place>()
    private var clicked = R.id.buttonDistance1000
    private var path: String = ""
    private var distance: Int = 1000
    private var page: Int = 1
    private lateinit var fusedLocationClient: FusedLocationProviderClient
//    private val model = ViewModelProvider(this).get(PlaceViewModel::class.java)

    companion object {
        const val EXT_ID = "id"
        const val EXT_NAME = "name"
        const val EXT_ADDRESS = "address"
        const val EXT_PHONE = "phone"
        const val EXT_IMAGE = "image"
        const val EXT_DISTANCE = "distance"
        const val EXT_X = "x"
        const val EXT_Y = "y"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        val root = binding.root

        // gps location service
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        // shuffle list
        binding.buttonShuffle.setOnClickListener {
            places.shuffle()
            adapter?.notifyDataSetChanged()
        }

        fun getPlaceList() {
            binding.progressBar.visibility = View.VISIBLE
            if (page > 1) {
                val height = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    60f,
                    resources.displayMetrics
                ).toInt()
                binding.progressBar.layoutParams.height = height
                binding.progressBar.requestLayout()
            }
            if (page == 1) binding.gridCard.visibility = View.GONE
            binding.moreButton.visibility = View.GONE

            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(
                    requireContext(),
                    "Need to Access Location Permission!",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }

            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                val url = getString(R.string.base_url) + "/place"
                path =
                    "?distance=$distance&x=${location.longitude}&y=${location.latitude}&page=$page"
                val client = OkHttpClient()
                val request = Request.Builder().url(url + path).build()

                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        e.printStackTrace()
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val body = response.body?.string()
                        val gson = Gson()
                        val data = gson.fromJson(body, ApiPlace::class.java)
                        val last = data.meta!!.is_end!!
                        if (page > 1) {
                            places.addAll(data.places!!)
                        } else {
                            places = data.places!!
                        }

                        val model =
                            ViewModelProvider(requireActivity())[PlaceViewModel::class.java]
                        model.places.postValue(places)

                        CoroutineScope(Dispatchers.Main).launch {
                            val gridView = binding.gridCard
                            adapter = CardAdapter(requireContext(), places)
                            gridView.adapter = adapter

                            // Update height of gridview depending on the number of cards
                            val numRows = (places.size + 1) / 2
                            val cardHeight = TypedValue.applyDimension(
                                TypedValue.COMPLEX_UNIT_DIP,
                                240f * numRows,
                                resources.displayMetrics
                            ).toInt()
                            gridView.layoutParams.height = cardHeight
                            gridView.requestLayout()
                            gridView.visibility = View.VISIBLE
                            if (!last) binding.moreButton.visibility = View.VISIBLE
                            binding.progressBar.visibility = View.GONE
                        }
                    }
                })
            }
        }

        fun selectDistance(view: Button) {
            view.background =
                ResourcesCompat.getDrawable(resources, R.drawable.rounded_button_color, null)
            view.setTextColor(resources.getColor(R.color.white, null))

            val oldView = requireView().findViewById<Button>(clicked)
            oldView.background =
                ResourcesCompat.getDrawable(resources, R.drawable.rounded_button, null)
            oldView.setTextColor(resources.getColor(R.color.button_main, null))

            clicked = view.id

            getPlaceList()
        }

        binding.buttonDistance300.setOnClickListener {
            distance = 300
            selectDistance(it as Button)
        }

        binding.buttonDistance500.setOnClickListener {
            distance = 500
            selectDistance(it as Button)
        }

        binding.buttonDistance1000.setOnClickListener {
            distance = 1000
            selectDistance(it as Button)
        }

        binding.buttonDistance3000.setOnClickListener {
            distance = 3000
            selectDistance(it as Button)
        }

        binding.moreButton.setOnClickListener {
            page += 1
            getPlaceList()
        }

        getPlaceList()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}