package com.reptile.biggestbird

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import androidx.core.graphics.drawable.toBitmap

class Bullet(private val context: Context, x: Float, y: Float) {
	private val bitmap: Bitmap = context.getDrawable(R.drawable.bullet)?.toBitmap()!!
	private val speed: Float = 35f
	var x: Float = x
		private set
	var y: Float = y
		private set

	fun draw(canvas: Canvas) {
		canvas.drawBitmap(bitmap, x, y, null)
	}

	fun update() {
		y -= speed
	}

	fun isOffScreen(): Boolean {
		return y + bitmap.height < 0
	}

	fun getBounds(): Rect {
		return Rect(x.toInt(), y.toInt(), x.toInt() + bitmap.width, y.toInt() + bitmap.height)
	}
}
