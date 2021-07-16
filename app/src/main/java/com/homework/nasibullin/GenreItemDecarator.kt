package com.homework.nasibullin

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class GenreItemDecarator(val topBottom: Int = 0, val leftRight: Int): RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.bottom = topBottom
        outRect.top = leftRight
        outRect.right = leftRight
        outRect.left = leftRight
    }
}