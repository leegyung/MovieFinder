package leegyung.moviefinder.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import leegyung.moviefinder.Data.Movie
import leegyung.moviefinder.R


open class MovieRecyclerViewAdapter(
    private val context : Context?)
    : RecyclerView.Adapter<MovieRecyclerViewAdapter.ViewHolder>() {

    var mMovieList = ArrayList<Movie>()


    /**
     * 아이템 뷰를 위한 뷰홀더 객체 생성후 리턴
     *
     * return ViewHolder : recyclerview_layout 의 viewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.movie_info_layout, parent, false)
        return ViewHolder(view)
    }

    /**
     * 뷰홀더가 재활용될 떄 실행
     * inner class ViewHolder 의 bind 를 호출해 뷰홀더 content 설정
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mMovieList[position])
    }

    override fun getItemCount(): Int = mMovieList.size



    inner class ViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        // 영화 포스터를 표시하는 imageView
        private val mPoster = itemView.findViewById<ImageView>(R.id.PosterImageView)
        // 영화 정보를 표시하는 textView
        private val mTitle = itemView.findViewById<TextView>(R.id.Title)
        private val mPubDate = itemView.findViewById<TextView>(R.id.PubDate)
        private val mRate = itemView.findViewById<TextView>(R.id.Rate)

        /**
         * 뷰홀더 content 설정
         * param item : 표시할 영화 정보
         */
        fun bind(item: Movie){
            // 포스터 URL 을 mPoster 에 연결시켜주기 위해 Glide 사용
            // 이미지 URL 정보 없을 시 R.drawable.movie 로 설정
            Glide.with(context!!).load(item.image).error(R.drawable.movie).into(mPoster)
            // 영화 제목, 평점, 출시일 정보 설정
            mTitle.text = mTitle.text.toString().plus(item.title)
            mRate.text = mRate.text.toString().plus(item.userRating)
            mPubDate.text = mPubDate.text.toString().plus(item.pubDate)


        }
    }

}