package leegyung.moviefinder.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import leegyung.moviefinder.MainActivity
import leegyung.moviefinder.R

open class WordsRecyclerViewAdapter(private val mContext : Context?, private val mMainActivity: MainActivity)
    : RecyclerView.Adapter<WordsRecyclerViewAdapter.ViewHolder>() {

    var mWordList = ArrayList<String>()
    var mCallback = mMainActivity


    /**
     * 아이템 뷰를 위한 뷰홀더 객체 생성후 리턴
     *
     * return ViewHolder : recyclerview_layout 의 viewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordsRecyclerViewAdapter.ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.search_words_layout, parent, false)
        return ViewHolder(view)
    }

    /**
     * 뷰홀더가 재활용될 떄 실행
     * inner class ViewHolder 의 bind 를 호출해 뷰홀더 content 설정
     */
    override fun onBindViewHolder(holder: WordsRecyclerViewAdapter.ViewHolder, position: Int) {
        holder.bind(mWordList[position])
    }

    override fun getItemCount(): Int = mWordList.size

    inner class ViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        private val wordText = itemView.findViewById<TextView>(R.id.SearchWord)

        /**
         * 뷰홀더 content 설정
         * param item : 표시할 영화 정보
         */
        fun bind(item : String){
            wordText.text = item
            itemView.setOnClickListener {
                mCallback.onSearchWordClicked(item)
            }

        }
    }


}