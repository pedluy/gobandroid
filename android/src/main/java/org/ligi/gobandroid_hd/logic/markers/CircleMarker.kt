package org.ligi.gobandroid_hd.logic.markers

import android.graphics.Canvas
import android.graphics.Paint
import org.ligi.gobandroid_hd.logic.Cell

class CircleMarker(cell: Cell) : BaseShapeMarker(cell) {
    override fun draw(c: Canvas, size: Float, x: Float, y: Float, paint: Paint) {
        super.draw(c, size, x, y, paint)
        c.drawCircle(x, y, size / 4, localPaint)
    }

    override fun equals(other: Any?): Boolean {
        return super.equals(other) && other is CircleMarker
    }
}
