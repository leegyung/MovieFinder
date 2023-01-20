package leegyung.moviefinder.RoomComponents

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [SearchWordsEntity::class],
    version = 1
)

abstract class SearchWordsDB: RoomDatabase() {
    abstract fun searchWordDao() : SearchWordDAO

    companion object{
        private var instance: SearchWordsDB? = null

        @Synchronized
        fun getInstance(context : Context) : SearchWordsDB? {
            if(instance == null){
                synchronized(SearchWordsDB::class){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        SearchWordsDB::class.java,
                        "words-database"
                    ).build()
                }
            }
            return instance
        }
    }



}