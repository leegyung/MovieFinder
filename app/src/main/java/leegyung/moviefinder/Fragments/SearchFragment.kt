package leegyung.moviefinder.Fragments

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.KeyEvent.KEYCODE_ENTER
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import leegyung.moviefinder.Adapter.MovieRecyclerViewAdapter
import leegyung.moviefinder.MainActivity
import leegyung.moviefinder.ViewModel.SearchViewModel
import leegyung.moviefinder.databinding.FragmentSearchBinding


class SearchFragment : Fragment() {
    //Naver api 호출시 사용할 유저 id,passcode (사용하진 않으실 거죠?.....ㅠ)
    private val mClientId = "3t6zOLNt2kwc2gwiuHsS"
    private val mClientPwd = "S746jPMCtc"

    //onDestroyView 에서 binding release
    private var _binding : FragmentSearchBinding? = null
    private val mBinding get() = _binding!!

    //영화 정보 저장과 요청을 위한 viewModel
    private lateinit var mViewModel: SearchViewModel
    private lateinit var mAdapter : MovieRecyclerViewAdapter
    // 인테넷 연결 확인을 위한 ConnectivityManager
    private lateinit var mInternetManager : ConnectivityManager

    // 리사이클러뷰 마지막원소 도달시 다중 호출 방지용
    private var mPageNum = 0
    // 검색한 검색어 목록
    private val mSearchedList = arrayListOf<String>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //사용할 viewmodel 설정
        mViewModel = ViewModelProvider(this)[SearchViewModel::class.java]
        // mViewModel 정보 초기화
        mViewModel.initializer(mClientId, mClientPwd)
        // ConnectivityManager 를 CONNECTIVITY_SERVICE 로 초기화
        mInternetManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        // mBinding.MovieList adapter 설정
        mAdapter = MovieRecyclerViewAdapter(context)
        mBinding.MovieList.adapter = mAdapter

        buttonsInit()
        observeViewModelInit()
        setMovieRecyclerViewListener()

    }

    /**
     * 검색, 최근검색 버튼 리스너 추가
     * 키보트 엔터키 리스너 추가
     */
    private fun buttonsInit(){
        mBinding.RecentBtn.setOnClickListener {
            // 최근검색 버튼 클릭시 fragment 전환
            (activity as MainActivity).switchFragment(2, mSearchedList)
            // 검색목록 초기화
            mSearchedList.clear()
        }
        
        mBinding.SearchBtn.setOnClickListener {
            //검색버튼 클릭시 데이터 요청 실행
            searchMovie()
        }

        mBinding.MovieTitleText.setOnKeyListener { _, keyCode, keyEvent ->
            if(keyEvent.action == KeyEvent.ACTION_DOWN && keyCode == KEYCODE_ENTER){
                //키보드의 엔터키 누를시 데이터 요청 실행
                searchMovie()
            }
            false
        }
    }

    /**
     * 영화 검색 요청 mViewModel에 전달
     */
    private fun searchMovie(){
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        //인터넷 연결시 실행
        if(checkInternetConnection()){
            //전이랑 같은 검색어가 아니거나 검색어가 비어있을 시 실행
            if(mBinding.MovieTitleText.text.toString() != mViewModel.mCurrentTitle
                || mBinding.MovieTitleText.text.toString() == "")
            {
                //검색어가 비어있다면 Toast 표시
                if(mBinding.MovieTitleText.text.toString() == ""){
                    imm.hideSoftInputFromWindow(view?.windowToken, 0)
                    Toast.makeText(context, "검색어를 입력 하세요.", Toast.LENGTH_SHORT).show()
                }else{
                    //mViewModel에 현제 검색어 첫 페이지 요청
                    mViewModel.loadMovieList(mBinding.MovieTitleText.text.toString(), 1)
                    //키보드 내리기
                    imm.hideSoftInputFromWindow(view?.windowToken, 0)
                    mPageNum = 0
                    //최근 검색어 목록 추가
                    addSearchWord()
                }
            }else{
                //검색어 같을 시 키보드 내리기
                imm.hideSoftInputFromWindow(view?.windowToken, 0)
            }
        }else{
            Toast.makeText(context, "인터넷 연결 없음", Toast.LENGTH_SHORT).show()
        }

    }

    /**
     * 검색한 검색어 mSearchedList에 추가
     */
    private fun addSearchWord(){
        //동일 검색어 제거
        mSearchedList.remove(mBinding.MovieTitleText.text.toString())
        //사이즈 10개 이하로 조절
        if(mSearchedList.size == 10){
            mSearchedList.removeAt(0)
            mSearchedList.add(mBinding.MovieTitleText.text.toString())
        }else{
            mSearchedList.add(mBinding.MovieTitleText.text.toString())
        }
    }

    /**
     * mViewModel 의 mCurrentPageMovies 와 mLoadError 변화 확인을 위한 observer 설정
     */
    private fun observeViewModelInit(){
        //요청한 영화 데이터가 mCurrentPageMovies 추가됐을 시 실행
        mViewModel.mCurrentPageMovies.observe(viewLifecycleOwner){
            mViewModel.cleanTitles()
            //mAdapter에 변경사항 notice
            mAdapter.mMovieList = mViewModel.getMovieList()
            mAdapter.notifyDataSetChanged()

            if(mAdapter.mMovieList.isEmpty()){
                mBinding.NoResultText.visibility = View.VISIBLE
            }
            else{
                mBinding.NoResultText.visibility = View.INVISIBLE
            }
        }

        //영화 로드중 애러 발생 시 실행
        mViewModel.mLoadError.observe(viewLifecycleOwner){
            if(mViewModel.mLoadError.value != null && mViewModel.mLoadError.value != ""){
                //애러 메세지 toast
                Toast.makeText(context, mViewModel.mLoadError.value, Toast.LENGTH_LONG).show()
            }
        }
    }

    /**
     * RecyclerView 에 ScrollListener 를 추가하여 마지막 item 이 보였는지 판별
     */
    private fun setMovieRecyclerViewListener(){
        mBinding.MovieList.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val lastItemPosition = (mBinding.MovieList.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                val totalItemCount = mAdapter.itemCount - 1
                // 마지막 item 이 보였으면 실행
                if(lastItemPosition == totalItemCount && mPageNum != totalItemCount){
                    mPageNum = totalItemCount
                    // ViewModel 에 다음 페이지 로드 요청
                    mViewModel.loadMovieList(mViewModel.mCurrentTitle, mPageNum + 2)
                }
            }
        })
    }


    /**
     * 인터넷 연결을 확인
     *
     * return Boolean : 연결됐다면 true, 아니면 false
     */
    private fun checkInternetConnection() : Boolean{
        // 버전 6.0 이상인 기기일 시 실행
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nw = mInternetManager.activeNetwork ?: return false
            val actNw = mInternetManager.getNetworkCapabilities(nw) ?: return false

            return when {
                // 와이파이 연됐을 시 return true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                // Cellular 연됐을 시 return true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                // 연결 없으면 return false
                else -> false
            }
        }
        // 버전 6.0 이하인 기기일 시 실행
        else {
            return mInternetManager.activeNetworkInfo?.isConnected ?: false
        }
    }

    /**
     * RecentSearchFragment 에서 최근 검색어중 하나 선택시 검색어 정보 로드
     * param title: 선택한 검색어
     */
    fun searchWordSelected(title : String){
        if(checkInternetConnection()){
            mViewModel.loadMovieList(title, 1)
            mPageNum = 0
            mBinding.MovieTitleText.setText(title)
            addSearchWord()
        }else{
            Toast.makeText(context, "인터넷 연결 없음", Toast.LENGTH_SHORT).show()
        }
    }

    //검색어 목록 return
    fun getSearchWordList() : ArrayList<String> {
        return mSearchedList
    }

    //binding release
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}