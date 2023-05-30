package edu.skku.cs.yummyyuljeon

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
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

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            val y = location.latitude
            val x = location.longitude
            mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(y, x), true)

            // convert drawable to bitmap
            val drawable =
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.fa6solidlocationcrosshairs,
                    null
                )!!
            val bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)

            val currentLocationMarker = MapPOIItem()
            currentLocationMarker.itemName = "Current Position"
            currentLocationMarker.tag = 0
            currentLocationMarker.mapPoint = MapPoint.mapPointWithGeoCoord(y, x)
            currentLocationMarker.markerType = MapPOIItem.MarkerType.CustomImage
            currentLocationMarker.customImageBitmap = bitmap
            currentLocationMarker.isCustomImageAutoscale = false
            currentLocationMarker.setCustomImageAnchor(0.5f, 0.5f)
            mapView.addPOIItem(currentLocationMarker)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}