package edu.skku.cs.yummyyuljeon

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PlaceViewModel: ViewModel() {
    val places = MutableLiveData<ArrayList<Place>>()
}