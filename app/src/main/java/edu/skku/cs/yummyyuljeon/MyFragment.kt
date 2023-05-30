package edu.skku.cs.yummyyuljeon

import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import edu.skku.cs.yummyyuljeon.databinding.FragmentMyBinding

class MyFragment : Fragment() {

    private var _binding: FragmentMyBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyBinding.inflate(inflater, container, false)

        val inputStream = requireActivity().openFileInput("favorite.json")
        val json = inputStream.bufferedReader().use { it.readText() }
        inputStream.close()
        val data = Gson().fromJson(json, StorePlace::class.java)
        Log.i("MyFragment", "data: $data")
        val adapter = CardAdapter(requireContext(), data.favorites)
        binding.gridCard.adapter = adapter

        // update height of gridview
        val numRows = (adapter.count + 1) / 2
        val cardHeight = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            230f * numRows + 40f,
            resources.displayMetrics
        ).toInt()
        binding.gridCard.layoutParams.height = cardHeight
        binding.gridCard.requestLayout()
        binding.gridCard.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}