package com.ralphevmanzano.awssmsgateway.utils

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.recyclerview.widget.RecyclerView

class ItemDecoration(private var mDivider: Drawable) : RecyclerView.ItemDecoration() {

  override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
    super.onDrawOver(c, parent, state)

    val left = parent.paddingLeft
    val right = parent.width - parent.paddingRight

    val childCount = parent.childCount
    for (i in 0 until childCount) {
      val child = parent.getChildAt(i)
      val params = child.layoutParams as RecyclerView.LayoutParams
      val top = child.bottom + params.bottomMargin
      val bottom = top + mDivider.intrinsicHeight

      mDivider.let {
        it.setBounds(left, top, right, bottom)
        it.draw(c)
      }

    }
  }
}