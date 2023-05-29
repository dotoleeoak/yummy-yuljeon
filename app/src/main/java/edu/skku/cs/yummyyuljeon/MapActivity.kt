package edu.skku.cs.yummyyuljeon

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RelativeLayout
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView

class MapActivity : AppCompatActivity() {
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

        val marker = MapPOIItem()
        marker.itemName = name
        marker.mapPoint = MapPoint.mapPointWithGeoCoord(y, x)
        marker.markerType = MapPOIItem.MarkerType.BluePin
        marker.selectedMarkerType = MapPOIItem.MarkerType.RedPin
        mapView.addPOIItem(marker)
    }
}