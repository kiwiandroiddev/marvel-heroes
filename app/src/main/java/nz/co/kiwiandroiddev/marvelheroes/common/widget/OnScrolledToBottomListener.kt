package nz.co.kiwiandroiddev.marvelheroes.common.widget

import androidx.recyclerview.widget.RecyclerView

abstract class OnScrolledToBottomListener : RecyclerView.OnScrollListener() {

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
        if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
            onScrolledToBottom()
        }
    }

    abstract fun onScrolledToBottom()
}