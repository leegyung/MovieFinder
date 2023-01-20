package leegyung.moviefinder.Fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import leegyung.moviefinder.Adapter.WordsRecyclerViewAdapter
import leegyung.moviefinder.MainActivity
import leegyung.moviefinder.databinding.FragmentRecentSearchBinding
import leegyung.moviefinder.databinding.FragmentSearchBinding


class RecentSearchFragment(private val mMainActivityContext : MainActivity) : Fragment() {
    //onDestroyView 에서 binding release
    private var _binding : FragmentRecentSearchBinding? = null
    private val mBinding get() = _binding!!

    //검색한 영화목록 recyclerView adapter
    private lateinit var mAdapter : WordsRecyclerViewAdapter
    //뒤로가기 버튼 사용을 위한 callback
    private lateinit var mBackPressedCallback: OnBackPressedCallback

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecentSearchBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //WordListRecyclerView adapter 설정
        mAdapter = WordsRecyclerViewAdapter(context, mMainActivityContext)
        mBinding.WordListRecyclerView.adapter = mAdapter

        initializeButtons()
    }

    //버튼에 리스너 추가
    private fun initializeButtons(){
        //SearchFragment 로 돌아가기 위한 버튼 리스너
        mBinding.BackToSearch.setOnClickListener {
            (activity as MainActivity).switchFragment(1,null)
        }
        //뒤로가기 버튼 callback 등록으로 SearchFragment 전환
        mBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                (activity as MainActivity).switchFragment(1, null)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, mBackPressedCallback)
    }

    /**
     * 표시할 최근 검색어 목록 추가
     * param words: 최근 검색어 목록
     */
    fun updateWordList(words : ArrayList<String>?){
        if(words != null && words.isNotEmpty()){
            //전에 검색한 중복 검색어 제거 후 정렬
            for(word:String in words!!){
                mAdapter.mWordList.remove(word)
                mAdapter.mWordList.add(0, word)
            }
            //검색어 목록 최대 10개로 조정
            if(mAdapter.mWordList.size > 10){
                mAdapter.mWordList = mAdapter.mWordList.slice(0..9) as ArrayList<String>
            }
            mAdapter.notifyDataSetChanged()
        }
    }

    /**
     * 최근 검색 목록 리스트 return
     */
    fun getSearchWordList() : ArrayList<String> {
        return mAdapter.mWordList
    }

    /**
     * 표시할 최근 검색어 목록 설정. 앱 제시작 시 저장 정보 restore
     * param words : restore 할 목록
     */
    fun restoreSearchWordList(words : ArrayList<String>){
        mAdapter.mWordList = words
        mAdapter.notifyDataSetChanged()
    }

    //mBackPressedCallback release
    override fun onDetach() {
        super.onDetach()
        mBackPressedCallback.remove()
    }

    //binding release
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}