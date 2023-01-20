package leegyung.moviefinder.RoomComponents

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface SearchWordDAO {
    //DB에 entity가 하나라도 있는지 확인
    @Query("SELECT (SELECT COUNT(*) FROM SearchWordsEntity) == 0")
    fun isDBEmpty() : Boolean
    //저장하는 entity는 검색어 리스트 하나 뿐이니까 리턴
    @Query("SELECT * FROM SearchWordsEntity Where id == 1")
    fun getWords() : SearchWordsEntity
    //DB에 새로운 entity 추가
    @Insert
    fun newWords(wordsEntity: SearchWordsEntity)
    //DB에 entity 변경
    @Update
    fun updateWords(wordsEntity: SearchWordsEntity)
}