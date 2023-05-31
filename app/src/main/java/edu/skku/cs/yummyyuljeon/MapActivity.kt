package edu.skku.cs.yummyyuljeon

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.res.ResourcesCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView

class MapActivity : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        val name = intent.getStringExtra("name")
        val x = intent.getStringExtra("x")!!.toDouble()
        val y = intent.getStringExtra("y")!!.toDouble()

        val mapView = MapView(this)
        val mapViewContainer = findViewById<RelativeLayout>(R.id.map_view)
        mapViewContainer.addView(mapView)

        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(y, x), true)

        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(
                applicationContext,
                "Need to Access Location Permission!",
                Toast.LENGTH_SHORT
            ).show()
            return
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

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            val marker = MapPOIItem()
            marker.itemName = "Current Position"
            marker.tag = 0
            marker.mapPoint = MapPoint.mapPointWithGeoCoord(location.latitude, location.longitude)
            marker.markerType = MapPOIItem.MarkerType.CustomImage
            marker.customImageBitmap = drawableToBitmap(R.drawable.fa6solidlocationcrosshairs)
            marker.isCustomImageAutoscale = false
            marker.setCustomImageAnchor(0.5f, 0.5f)
            mapView.addPOIItem(marker)
        }

        val marker = MapPOIItem()
        marker.itemName = name
        marker.mapPoint = MapPoint.mapPointWithGeoCoord(y, x)
        marker.markerType = MapPOIItem.MarkerType.CustomImage
        marker.customImageBitmap = drawableToBitmap(R.drawable.fa6solidlocationdot)
        marker.isCustomImageAutoscale = false
        marker.setCustomImageAnchor(0.5f, 0.5f)
        mapView.addPOIItem(marker)
    }
}