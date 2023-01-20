package leegyung.moviefinder.Data

// 영화 검색 api response data model
data class MovieListResponse (
    val items : ArrayList<Movie>,
    val total : Int,
    val errorMessage : String,
    val errorCode : String
)

data class Movie(
    var title : String,
    val link : String,
    val image : String,
    val pubDate : String,
    val userRating : String
)
