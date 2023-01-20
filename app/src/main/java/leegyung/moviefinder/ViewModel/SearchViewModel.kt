package leegyung.moviefinder.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import leegyung.moviefinder.Data.Movie
import leegyung.moviefinder.Repository.MovieSearchRepository

class SearchViewModel : ViewModel(){
    // 현제 검색중인 검색어
    var mCurrentTitle = ""
    // Response 의 Load Error 메시지
    lateinit var mLoadError : MutableLiveData<String>
    // 로딩 한 영화 목록
    lateinit var mCurrentPageMovies : MutableLiveData<ArrayList<Movie>>
    // 특정 영화를 로딩한 총 목록
    private val mAllMovies = ArrayList<Movie>()
    // 검색 한 영화의 총 갯수
    private lateinit var mTotalItemNum : MutableLiveData<Int>
    // 영화 검색 Retrofit 통신을 위한 repository
    private lateinit var mMovieRepository : MovieSearchRepository

    //목록 초기화
    fun initializer(clientId : String, clientPwd : String) {
        mMovieRepository = MovieSearchRepository(clientId, clientPwd)
        mLoadError = mMovieRepository.mLoadError
        mCurrentPageMovies = mMovieRepository.mMovieList
        mTotalItemNum = mMovieRepository.mTotalItemNum
    }

    /**
     * mMovieRepository에 데이터 요청
     *
     * param title: 검색한 영화의 제목
     * param pageNum: 검색목록중 받아올 페이지 번호
     */
    fun loadMovieList(title : String, pageNum : Int){
        when(title){
            //같은 검색어의 다음 페이지 요청시
            mCurrentTitle -> {
                viewModelScope.launch {
                    // 다음 페이지가 있는지 확인후 요청
                    if(pageNum < mTotalItemNum.value!!){
                        mMovieRepository.updateMovieData(title, pageNum)
                    }
                }
            }
            //다른 검색어 검색 요청시 첫 페이지 요청
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

    /**
     * 한국어로 검색 시 제목에 추가된 <b></b> 제거
     */
    fun cleanTitles(){
        for(movie : Movie in mCurrentPageMovies.value!!){
            movie.title = movie.title.replace("<b>", "").replace("</b>", "")
        }
    }

    /**
     * 현제 검색어로 로드한 페이지의 모든 영화 정보 목록 return
     */
    fun getMovieList():ArrayList<Movie> {
        mAllMovies.addAll(mCurrentPageMovies.value!!.toCollection(ArrayList()))
        return mAllMovies
    }






}