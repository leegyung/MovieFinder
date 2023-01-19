package leegyung.moviefinder.Repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*
import leegyung.moviefinder.API.MovieApi
import leegyung.moviefinder.Data.Movie
import leegyung.moviefinder.RetrofitProvider

class MovieSearchRepository(
    private val mClientId : String,
    private val mClientPass : String) {


    // 코루틴 exception 발생 시 알려줄 handler
    private val mExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.v("Coroutine Exception", throwable.toString()) }
    // 현재 로딩 중인지 확인할 boolean
    private var mLoading = false
    // 영화 목록 로딩 취소를 위한 변수
    private var mJob : Job? = null
    // RetrofitProvider 에서 받아 온 Retrofit
    private val mMovieApi = RetrofitProvider.getMovieListRetrofit().create(MovieApi::class.java)



    // Response 의 Load Error 메시지
    val mLoadError = MutableLiveData("")
    // 로딩 한 영화 목록
    val mMovieList = MutableLiveData<ArrayList<Movie>>()
    // 검색 한 영화의 총 갯수
    val mTotalItemNum = MutableLiveData(0)




    fun updateMovieData(title : String, page : Int) {

        // 로딩중 설정
        mLoading = true

        // Coroutine Scope, Dispatcher IO 스레드에서 API 통신
        mJob = CoroutineScope(Dispatchers.IO + mExceptionHandler).launch {
            // 검색어와 페이지의 영화 목록 로드
            val response = mMovieApi.getMovies(title, page, mClientId, mClientPass)
            withContext(Dispatchers.Main) {
                if(response.isSuccessful){
                    mMovieList.value = response.body()?.items
                    mTotalItemNum.value = response.body()?.total
                    mLoadError.value = response.body()?.errorMessage
                    mLoading = false
                }
                else{
                    // 로딩중 애러 발생시 onError 실행
                    onError(response.message())
                }
            }
        }
    }

    fun clearInfo(){
        mLoadError.value = ""
        mMovieList.value = ArrayList()
        mTotalItemNum.value = 0
    }


    /**
     * 로딩중 애러 발생 시 애러 메세지 값 설정, 로딩중 false 로 설정
     *
     * param message : 애러 메세지
     */
    private fun onError(message: String) {
        mLoadError.value = message
        mLoading = false
    }

    /**
     * 지금 로딩 하고 있는 Coroutine 종료
     */
    fun cancelLoading() {
        mJob?.cancel()
    }


}