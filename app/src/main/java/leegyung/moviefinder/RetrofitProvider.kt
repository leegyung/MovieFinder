package leegyung.moviefinder

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitProvider {
    private val mBaseURL = "https://openapi.naver.com/v1/search/"

    fun getMovieListRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(mBaseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

}