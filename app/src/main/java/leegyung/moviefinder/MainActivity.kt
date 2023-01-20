package leegyung.moviefinder

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.room.Room
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import leegyung.moviefinder.Fragments.RecentSearchFragment
import leegyung.moviefinder.Fragments.SearchFragment
import leegyung.moviefinder.ListenerInterface.OnSearchWordClick
import leegyung.moviefinder.RoomComponents.SearchWordsDB
import leegyung.moviefinder.RoomComponents.SearchWordsEntity

class MainActivity : AppCompatActivity(), OnSearchWordClick {

    private val mSearchingFrag = SearchFragment()
    private val mRecentSearchFrag = RecentSearchFragment(this)
    private lateinit var mSearchWordsDB : SearchWordsDB
    private lateinit var mWordsFromRoom : SearchWordsEntity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //ActionBar 제거
        supportActionBar?.hide()

        mSearchWordsDB = SearchWordsDB.getInstance(this)!!

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

    fun switchFragment(fragNum : Int, searchList : ArrayList<String>?){
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

                mRecentSearchFrag.updateWordList(searchList)

            }
        }
    }

    override fun onSearchWordClicked(title: String) {
        if(title.isNotEmpty()){
            supportFragmentManager.beginTransaction().show(mSearchingFrag).commit()
            supportFragmentManager.beginTransaction().hide(mRecentSearchFrag).commit()
            mSearchingFrag.searchWordSelected(title)
        }


    }

    override fun onStop() {
        super.onStop()
        val wordsFromSearchFrag = mSearchingFrag.getSearchWordList()
        var recentWords = mRecentSearchFrag.getSearchWordList()

        if(wordsFromSearchFrag.isNotEmpty()){
            for(word:String in wordsFromSearchFrag){
                recentWords.add(0, word)
            }
            if(recentWords.size > 10){
                recentWords = recentWords.slice(0..9) as ArrayList<String>
            }
        }

        mWordsFromRoom = SearchWordsEntity(Gson().toJson(recentWords))
        CoroutineScope(Dispatchers.IO).launch {
            mSearchWordsDB.searchWordDao().newWords(mWordsFromRoom)
        }
    }

    /*
    override fun onRestart() {
        super.onRestart()
        CoroutineScope(Dispatchers.IO).launch {
            mWordsFromRoom = mSearchWordsDB.searchWordDao().getWords()
        }

        val type = object : TypeToken<ArrayList<String>>(){}.type
        mRecentSearchFrag.restoreSearchWordList(Gson().fromJson(mWordsFromRoom.wordList, type))
    }*/



}