package edu.skku.cs.yummyyuljeon

data class Place(
    val id: String? = null,
    val name: String? = null,
    val address: String? = null,
    val phone: String? = null,
    val distance: String? = null,
    val image: String? = null,
    val x: String? = null,
    val y: String? = null
)

data class ApiPlace(
    val meta: Meta? = null,
    val places: ArrayList<Place>? = null
) {
    data class Meta(
        val total_count: Number? = null,
        val is_end: Boolean? = null,
    )
}

data class ApiDetail(
    val openHour: String? = null,
    val reviews: ArrayList<Review>? = null
) {
    data class Review(
        val content: String? = null
    )
}