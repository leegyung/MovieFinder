package leegyung.moviefinder.Fragments

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import leegyung.moviefinder.Adapter.MovieRecyclerViewAdapter
import leegyung.moviefinder.MainActivity
import leegyung.moviefinder.ViewModel.SearchViewModel
import leegyung.moviefinder.databinding.FragmentSearchBinding


class SearchFragment : Fragment() {
    private val mClientId = "3t6zOLNt2kwc2gwiuHsS"
    private val mClientPwd = "S746jPMCtc"

    private lateinit var mBinding : FragmentSearchBinding
    private lateinit var mViewModel: SearchViewModel
    private lateinit var mAdapter : MovieRecyclerViewAdapter
    // 인테넷 연결 확인을 위한 ConnectivityManager
    private lateinit var mInternetManager : ConnectivityManager





    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mBinding = FragmentSearchBinding.inflate(inflater, container, false)

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

    }

    private fun buttonsInit(){
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        mBinding.RecentBtn.setOnClickListener {
            (activity as MainActivity).switchFragment(11,"")
        }

        mBinding.SearchBtn.setOnClickListener {
            if(checkInternetConnection()){
                mViewModel.loadMovieList(mBinding.MovieTitleText.text.toString())
                imm.hideSoftInputFromWindow(view?.windowToken, 0)
            }else{
                Toast.makeText(context, "인터넷 연결 없음", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun observeViewModelInit(){
        mViewModel.mCurrentPageMovies.observe(viewLifecycleOwner){
            mViewModel.cleanTitles()
            val temp = mViewModel.getMovieList()
            Log.v("size", temp.toString())
            mAdapter.mMovieList = temp
            mAdapter.notifyDataSetChanged()

        }

        mViewModel.mLoadError.observe(viewLifecycleOwner){
            if(mViewModel.mLoadError.value != ""){
                Toast.makeText(context, mViewModel.mLoadError.value, Toast.LENGTH_LONG).show()
            }

        }
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


}