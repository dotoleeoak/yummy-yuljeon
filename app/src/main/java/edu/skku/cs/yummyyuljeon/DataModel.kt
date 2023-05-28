package edu.skku.cs.yummyyuljeon

data class Place(
    val name: String? = null,
    val address: String? = null,
    val distance: String? = null,
    val image: String? = null,
    val x: Number? = null,
    val y: Number? = null
)

//data class SignIn(val username: String? = null)
//
//data class SignInResponse(val success: Boolean? = null)
//
//data class MazeEntry(val name: String? = null, val size: Int? = null)
//
//data class MazeMap(val maze: String? = null)
//
//data class MazeCell(
//    val wall: Int? = null,
//    val size: Int? = null,
//    var person: Direction? = null,
//    var hint: Boolean = false
//)
//
//enum class Direction {
//    UP,
//    LEFT,
//    DOWN,
//    RIGHT
//}
