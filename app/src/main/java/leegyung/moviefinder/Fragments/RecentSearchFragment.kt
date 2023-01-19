package leegyung.moviefinder.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import leegyung.moviefinder.MainActivity
import leegyung.moviefinder.databinding.FragmentRecentSearchBinding


class RecentSearchFragment : Fragment() {
    private lateinit var mBinding : FragmentRecentSearchBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentRecentSearchBinding.inflate(inflater, container, false)
        mBinding.BackToSearch.setOnClickListener {
            (activity as MainActivity).switchFragment(1,"hi")
        }

        return mBinding.root
    }

    //초기값을 설정해주거나 LiveData 옵저빙, RecyclerView 또는 ViewPager2 에 사용될 Adapter 세팅
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }


}