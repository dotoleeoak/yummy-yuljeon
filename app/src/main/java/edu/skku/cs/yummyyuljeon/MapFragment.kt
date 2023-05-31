package edu.skku.cs.yummyyuljeon

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import edu.skku.cs.yummyyuljeon.databinding.FragmentMapBinding
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView

class MapFragment : Fragment() {
    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)

        val mapView = MapView(requireActivity())
        val mapViewContainer = binding.mapView
        mapViewContainer.addView(mapView)

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
            return binding.root
        }

        fun drawableToBitmap(id: Int): Bitmap {
            val drawable = ResourcesCompat.getDrawable(resources, id, null)!!
            val bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            return bitmap
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            val y = location.latitude
            val x = location.longitude
            mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(y, x), true)

            val marker = MapPOIItem()
            marker.itemName = "Current Position"
            marker.tag = 0
            marker.mapPoint = MapPoint.mapPointWithGeoCoord(y, x)
            marker.markerType = MapPOIItem.MarkerType.CustomImage
            marker.customImageBitmap = drawableToBitmap(R.drawable.fa6solidlocationcrosshairs)
            marker.isCustomImageAutoscale = false
            marker.setCustomImageAnchor(0.5f, 0.5f)
            mapView.addPOIItem(marker)
        }

        val model = ViewModelProvider(requireActivity())[PlaceViewModel::class.java]
        model.places.observe(viewLifecycleOwner) { places ->
            for (place in places) {
                val marker = MapPOIItem()
                marker.itemName = place.name
                marker.mapPoint =
                    MapPoint.mapPointWithGeoCoord(place.y!!.toDouble(), place.x!!.toDouble())
                marker.markerType = MapPOIItem.MarkerType.CustomImage
                marker.customImageBitmap = drawableToBitmap(R.drawable.fa6solidlocationdot)
                marker.isCustomImageAutoscale = false
                marker.setCustomImageAnchor(0.5f, 0.5f)
                mapView.addPOIItem(marker)
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}