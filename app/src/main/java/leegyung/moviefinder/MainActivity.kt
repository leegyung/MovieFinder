package leegyung.moviefinder

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import leegyung.moviefinder.Fragments.RecentSearchFragment
import leegyung.moviefinder.Fragments.SearchFragment

class MainActivity : AppCompatActivity() {

    private val mSearchingFrag = SearchFragment()
    private val mRecentSearchFrag = RecentSearchFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //ActionBar 제거
        supportActionBar?.hide()

        requestPermissions()
        initFragments()


    }

    /**
     * INTERNET 과 ACCESS_NETWORK_STATE 권한을 요청
     */
    private fun requestPermissions(){
        // 버전 6.0 미만인 기기는 스킵
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            return
        }

        val permissions = arrayOf(
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE)

        for (permission : String in permissions){
            val check = checkCallingOrSelfPermission(permission)
            if(check == PackageManager.PERMISSION_DENIED){
                //거부된 권한 허용여부를 확인하는 창을 띄운다
                requestPermissions(permissions, 0)
            }
        }
    }

    private fun initFragments(){
        supportFragmentManager
            .beginTransaction()
            .add(R.id.FragmentFrame, mRecentSearchFrag)
            .commit()

        supportFragmentManager
            .beginTransaction()
            .add(R.id.FragmentFrame, mSearchingFrag)
            .commit()

        supportFragmentManager.beginTransaction().show(mSearchingFrag).commit()
        supportFragmentManager.beginTransaction().hide(mRecentSearchFrag).commit()

    }

    fun switchFragment(fragNum : Int, title : String?){
        when(fragNum){
            //mSearchingFrag 를 표시
            1 -> {
                supportFragmentManager.beginTransaction().show(mSearchingFrag).commit()
                supportFragmentManager.beginTransaction().hide(mRecentSearchFrag).commit()
            }
            //mRecentSearchFrag 를 표시
            else -> {
                supportFragmentManager.beginTransaction().show(mRecentSearchFrag).commit()
                supportFragmentManager.beginTransaction().hide(mSearchingFrag).commit()

            }
        }
    }


}