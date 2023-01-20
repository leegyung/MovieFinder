package leegyung.moviefinder.ListenerInterface

/**
 * 커스텀 override function
 */
interface OnSearchWordClick {
    /**
     * WordsRecyclerViewAdapter 에서 단어 선택시 fragment 전환 요청을 위한 function
     * param title: 선택한 최근 검색어
     */
    fun onSearchWordClicked(title : String)
}