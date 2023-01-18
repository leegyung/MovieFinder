package leegyung.moviefinder

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import leegyung.moviefinder.databinding.FragmentRecentSearchBinding
import leegyung.moviefinder.databinding.FragmentSearchBinding


class RecentSearchFragment : Fragment() {
    private lateinit var mBinding : FragmentRecentSearchBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentRecentSearchBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.BackToSearch.setOnClickListener {
            (activity as MainActivity).switchFragment(1,"hi")
        }
    }


}