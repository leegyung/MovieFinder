package leegyung.moviefinder.API

import leegyung.moviefinder.Data.MovieListResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

// 영화 정보 검색 api
interface MovieApi {
    @GET("movie.json")
    suspend fun getMovies(
        @Query("query") title : String,
        @Query("start") start : Int,
        @Header("X-Naver-Client-Id") clientId : String,
        @Header("X-Naver-Client-Secret") clientPassword : String
    ):Response<MovieListResponse>

}