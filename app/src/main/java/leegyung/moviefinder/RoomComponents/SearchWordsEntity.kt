package leegyung.moviefinder.RoomComponents

import androidx.room.Entity
import androidx.room.PrimaryKey

// 검색어 목록 저장을 위한 entity
@Entity
data class SearchWordsEntity (
    //검색어 목록
    var wordList : String
){
        @PrimaryKey(autoGenerate = true) var id : Int = 0
}