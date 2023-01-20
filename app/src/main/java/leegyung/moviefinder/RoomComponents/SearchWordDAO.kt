package leegyung.moviefinder.RoomComponents

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface SearchWordDAO {
    @Query("SELECT (SELECT COUNT(*) FROM SearchWordsEntity) == 0")
    fun isDBEmpty() : Boolean

    @Query("SELECT * FROM SearchWordsEntity Where id == 1")
    fun getWords() : SearchWordsEntity

    @Insert
    fun newWords(wordsEntity: SearchWordsEntity)

    @Update
    fun updateWords(wordsEntity: SearchWordsEntity)
}