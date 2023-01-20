package leegyung.moviefinder

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import leegyung.moviefinder.Fragments.RecentSearchFragment
import leegyung.moviefinder.Fragments.SearchFragment
import leegyung.moviefinder.ListenerInterface.OnSearchWordClick
import leegyung.moviefinder.RoomComponents.SearchWordsDB
import leegyung.moviefinder.RoomComponents.SearchWordsEntity

class MainActivity : AppCompatActivity(), OnSearchWordClick {
    // 영화 검색 Fragment
    private val mSearchingFrag = SearchFragment()
    // 검색 기록 fragment
    private val mRecentSearchFrag = RecentSearchFragment(this)
    // 검색기록 저장 Room database
    private lateinit var mSearchWordsDB : SearchWordsDB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //ActionBar 제거
        supportActionBar?.hide()

        requestPermissions()
        initFragments()

        // Database 가져오기
        mSearchWordsDB = SearchWordsDB.getInstance(this)!!

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

    /**
     * RecentSearchFrag 와 SearchingFrag 를 스텍에 저장
     */
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

    /**
     * RecentSearchFrag 와 SearchingFrag 변경
     *
     * param fragNum : 1 -> SearchingFrag 화면에 표시
     *                 2 -> RecentSearchFrag 화면에 표시
     * 
     * param searchList : RecentSearchFrag로 변경시 검색했던 검색어 리스트
     */
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
                //검색한 영화 목록을 RecentSearchFrag 에 주고 업데이트
                mRecentSearchFrag.updateWordList(searchList)

            }
        }
    }

    /**
     * 상속받은 OnSearchWordClick 의 onSearchWordClicked 가 호출됐을 시 실행
     * -> WordsRecyclerViewAdapter 에서 단어 선택 시 fragment 전환을 위해 사용
     *
     * param title: 선택한 최근 검색어
     */
    override fun onSearchWordClicked(title: String) {
        if(title.isNotEmpty()){
            supportFragmentManager.beginTransaction().show(mSearchingFrag).commit()
            supportFragmentManager.beginTransaction().hide(mRecentSearchFrag).commit()
            mSearchingFrag.searchWordSelected(title)
        }
    }

    /**
     * 앱 onStop 상태에서 최근 검색 목록 mSearchWordsDB에 저장
     */
    override fun onStop() {
        super.onStop()
        //전에 검색했던 검색어 리스트
        val wordsFromSearchFrag = mSearchingFrag.getSearchWordList()
        //현제 검색한 검색어 리스트
        var recentWords = mRecentSearchFrag.getSearchWordList()

        //두 리스트를 합치면서 순서 정렬
        if(wordsFromSearchFrag.isNotEmpty()){
            for(word:String in wordsFromSearchFrag){
                recentWords.remove(word)
                recentWords.add(0, word)
            }
            //전체 목록 수 10으로 조정
            if(recentWords.size > 10){
                recentWords = recentWords.slice(0..9) as ArrayList<String>
            }
        }
        
        //mSearchWordsDB에 저장을 위한 IO 코루틴
        CoroutineScope(Dispatchers.IO).launch {
            //DB가 비어있을 때 Entity 생성 후 검색목록 저장
            if(mSearchWordsDB.searchWordDao().isDBEmpty()){
                val newEntity = SearchWordsEntity(Gson().toJson(recentWords))
                mSearchWordsDB.searchWordDao().newWords(newEntity)
            }
            //DB에 있는 전에 저장한 검색어 목록 replace
            else{
                val entity = mSearchWordsDB.searchWordDao().getWords()
                entity.wordList = Gson().toJson(recentWords)
                mSearchWordsDB.searchWordDao().updateWords(entity)
            }
        }
    }

    /**
     * 앱이 종료후 제 시작됐을 시 mSearchWordsDB에서 검색어 리스트 가져오기
     */
    override fun onStart() {
        super.onStart()
        CoroutineScope(Dispatchers.IO).launch {
            //DB에 원소가 있을시 실행
            if(!mSearchWordsDB.searchWordDao().isDBEmpty()){
                val words = mSearchWordsDB.searchWordDao().getWords().wordList
                withContext(Dispatchers.Main){
                    val type = object : TypeToken<ArrayList<String>>(){}.type
                    //가져온 목록 mRecentSearchFrag에 전달
                    mRecentSearchFrag.restoreSearchWordList(Gson().fromJson(words, type))
                }
            }
        }
    }



}