package leegyung.moviefinder

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * 특정 Retrofit 을 만드는 object class
 */
object RetrofitProvider {
    private val mBaseURL = "https://openapi.naver.com/v1/search/"

    /**
     * return: baseURL 을 포함한 Retrofit
     */
    fun getMovieListRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(mBaseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

}