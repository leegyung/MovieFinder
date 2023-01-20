package leegyung.moviefinder.RoomComponents

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SearchWordsEntity (
    var wordList : String
){
        @PrimaryKey(autoGenerate = true) var id : Int = 0
}