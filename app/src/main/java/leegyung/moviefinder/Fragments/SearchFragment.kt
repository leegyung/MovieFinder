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
    private val mClientId = "3t6zOLNt2kwc2gwiuHsS"
    private val mClientPwd = "S746jPMCtc"

    private var _binding : FragmentSearchBinding? = null
    private val mBinding get() = _binding!!


    private lateinit var mViewModel: SearchViewModel
    private lateinit var mAdapter : MovieRecyclerViewAdapter
    // 인테넷 연결 확인을 위한 ConnectivityManager
    private lateinit var mInternetManager : ConnectivityManager

    // 리사이클러 마지막원소 도달시 다중 호출 방지용
    private var mPageNum = 0

    private val mSearchedList = arrayListOf<String>()




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    //초기값을 설정해주거나 LiveData 옵저빙, RecyclerView 또는 ViewPager2 에 사용될 Adapter 세팅
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mViewModel = ViewModelProvider(this)[SearchViewModel::class.java]
        mViewModel.initializer(mClientId, mClientPwd)
        // ConnectivityManager 를 CONNECTIVITY_SERVICE 로 초기화
        mInternetManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        mAdapter = MovieRecyclerViewAdapter(context)
        mBinding.MovieList.adapter = mAdapter

        buttonsInit()
        observeViewModelInit()
        setMovieRecyclerViewListener()

    }

    private fun buttonsInit(){

        mBinding.RecentBtn.setOnClickListener {
            (activity as MainActivity).switchFragment(2, mSearchedList)
            mSearchedList.clear()
        }

        mBinding.SearchBtn.setOnClickListener {
            searchMovie()
        }

        mBinding.MovieTitleText.setOnKeyListener { _, keyCode, keyEvent ->
            if(keyEvent.action == KeyEvent.ACTION_DOWN && keyCode == KEYCODE_ENTER){
                searchMovie()
            }
            false
        }
    }

    private fun searchMovie(){
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if(checkInternetConnection()){
            if(mBinding.MovieTitleText.text.toString() != mViewModel.mCurrentTitle){
                if(mBinding.MovieTitleText.text.toString().isEmpty()){
                    Toast.makeText(context, "검색어를 입력 하세요.", Toast.LENGTH_SHORT).show()
                }else{
                    mViewModel.loadMovieList(mBinding.MovieTitleText.text.toString(), 1)
                    imm.hideSoftInputFromWindow(view?.windowToken, 0)
                    mPageNum = 0
                    addSearchWord()
                }
            }else{
                imm.hideSoftInputFromWindow(view?.windowToken, 0)
            }
        }else{
            Toast.makeText(context, "인터넷 연결 없음", Toast.LENGTH_SHORT).show()
        }

    }

    private fun addSearchWord(){
        if(mSearchedList.size == 10){
            mSearchedList.removeAt(0)
            mSearchedList.add(mBinding.MovieTitleText.text.toString())
        }else{
            mSearchedList.add(mBinding.MovieTitleText.text.toString())
        }
    }


    private fun observeViewModelInit(){
        mViewModel.mCurrentPageMovies.observe(viewLifecycleOwner){
            mViewModel.cleanTitles()
            mAdapter.mMovieList = mViewModel.getMovieList()
            mAdapter.notifyDataSetChanged()
        }

        mViewModel.mLoadError.observe(viewLifecycleOwner){
            if(mViewModel.mLoadError.value != null && mViewModel.mLoadError.value != ""){
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

    fun getSearchWordList() : ArrayList<String> {
        return mSearchedList
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}