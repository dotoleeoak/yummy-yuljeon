package edu.skku.cs.yummyyuljeon.ui.home

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import edu.skku.cs.yummyyuljeon.Place
import edu.skku.cs.yummyyuljeon.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var adapter: CardAdapter? = null

    companion object {
        const val EXT_NAME = "name"
        const val EXT_ADDRESS = "address"
        const val EXT_IMAGE = "image"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root = binding.root

        // TODO: receive data from API
        val places = ArrayList<Place>()
        places.add(Place("봉수육", "경기 수원시 장안구 율전로 108번길 11 1층", "500m", "image_url"))
        places.add(Place("쟈스민", "경기 수원시 장안구 율전로 108번길 11 1층", "500m", "image_url"))
        places.add(Place("보리네 주먹고기", "경기 수원시 장안구 율전로 108번길 11 1층", "500m", "image_url"))
        places.add(Place("이라면 본점", "경기 수원시 장안구 율전로 108번길 11 1층", "500m", "image_url"))
        places.add(Place("옥집", "경기 수원시 장안구 율전로 108번길 11 1층", "500m", "image_url"))

        val gridView = binding.gridCard
        adapter = CardAdapter(requireContext(), places)
        gridView.adapter = adapter

        // Update height of gridview depending on the number of cards
        val numRows = (places.size + 1) / 2
        val pxHeight = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            260f * numRows,
            requireContext().resources.displayMetrics
        ).toInt()

        val gridParams = gridView.layoutParams
        gridParams.height = pxHeight
        gridView.layoutParams = gridParams

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