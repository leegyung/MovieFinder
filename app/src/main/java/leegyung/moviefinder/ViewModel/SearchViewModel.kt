package leegyung.moviefinder.ViewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import leegyung.moviefinder.Data.Movie
import leegyung.moviefinder.Repository.MovieSearchRepository

class SearchViewModel : ViewModel(){

    var mCurrentTitle = ""
    // Response 의 Load Error 메시지
    lateinit var mLoadError : MutableLiveData<String>
    // 로딩 한 영화 목록
    lateinit var mCurrentPageMovies : MutableLiveData<ArrayList<Movie>>
    // 특정 영화를 로딩한 총 목록
    private val mAllMovies = ArrayList<Movie>()
    // 검색 한 영화의 총 갯수
    lateinit var mTotalItemNum : MutableLiveData<Int>
    // 영화 검색을 위한 repository
    private lateinit var mMovieRepository : MovieSearchRepository

    fun initializer(clientId : String, clientPwd : String) {
        mMovieRepository = MovieSearchRepository(clientId, clientPwd)
        mLoadError = mMovieRepository.mLoadError
        mCurrentPageMovies = mMovieRepository.mMovieList
        mTotalItemNum = mMovieRepository.mTotalItemNum



    }

    fun loadMovieList(title : String, pageNum : Int){
        when(title){
            mCurrentTitle -> {
                viewModelScope.launch {
                    if(pageNum < mTotalItemNum.value!!){
                        mMovieRepository.updateMovieData(title, pageNum)


                    }
                }
            }
            else -> {
                mAllMovies.clear()
                mCurrentTitle = title
                mMovieRepository.clearInfo()
                viewModelScope.launch {
                    mMovieRepository.updateMovieData(title, 1)

                }

            }
        }
    }

    fun cleanTitles(){
        for(movie : Movie in mCurrentPageMovies.value!!){
            movie.title = movie.title.replace("<b>", "").replace("</b>", "")
        }
    }

    fun getMovieList():ArrayList<Movie> {
        mAllMovies.addAll(mCurrentPageMovies.value!!.toCollection(ArrayList()))
        return mAllMovies
    }






}