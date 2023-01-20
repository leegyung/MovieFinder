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

    private var _binding : FragmentRecentSearchBinding? = null
    private val mBinding get() = _binding!!
    private lateinit var mAdapter : WordsRecyclerViewAdapter

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

        mAdapter = WordsRecyclerViewAdapter(context, mMainActivityContext)
        mBinding.WordListRecyclerView.adapter = mAdapter

        initializeButtons()
    }

    private fun initializeButtons(){
        mBinding.BackToSearch.setOnClickListener {
            (activity as MainActivity).switchFragment(1,null)
        }

        mBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                (activity as MainActivity).switchFragment(1, null)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, mBackPressedCallback)
    }


    fun updateWordList(words : ArrayList<String>?){
        if(words != null && words.isNotEmpty()){
            for(word:String in words!!){
                mAdapter.mWordList.remove(word)
                mAdapter.mWordList.add(0, word)
            }
            if(mAdapter.mWordList.size > 10){
                mAdapter.mWordList = mAdapter.mWordList.slice(0..9) as ArrayList<String>
            }
            mAdapter.notifyDataSetChanged()
        }
    }

    fun getSearchWordList() : ArrayList<String> {
        return mAdapter.mWordList
    }

    fun restoreSearchWordList(words : ArrayList<String>){
        mAdapter.mWordList = words
        mAdapter.notifyDataSetChanged()
    }

    override fun onDetach() {
        super.onDetach()
        mBackPressedCallback.remove()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}