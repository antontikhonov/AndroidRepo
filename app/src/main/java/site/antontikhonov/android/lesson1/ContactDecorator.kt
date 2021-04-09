package site.antontikhonov.android.lesson1

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.Px
import androidx.recyclerview.widget.RecyclerView

class ContactDecorator(private val dividerDrawable: Drawable,
                       @Px private val offsetPx: Int) : RecyclerView.ItemDecoration() {

       override fun getItemOffsets(rect: Rect, view: View, parent: RecyclerView, s: RecyclerView.State) {
              if (parent.getChildAdapterPosition(view) != RecyclerView.NO_POSITION) {
                     rect.set(offsetPx, offsetPx, offsetPx, offsetPx)
              }
       }

       override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
              repeat(parent.childCount) {
                     val top = parent.getChildAt(it).bottom + offsetPx
                     val bottom = top + dividerDrawable.intrinsicHeight
                     dividerDrawable.setBounds(offsetPx, top, parent.width - offsetPx, bottom)
                     dividerDrawable.draw(canvas)
              }
       }
}