package com.reptile.biggestbird

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Rect
import androidx.core.graphics.drawable.toBitmap
import com.reptile.biggestbird.R
import kotlinx.coroutines.delay

class PlayerPlane(private val context: Context, x: Float, y: Float) {
	private val bitmap: Bitmap = context.getDrawable(R.drawable.player_plane)?.toBitmap()!!
	private val interpolationFactor = 0.2f
	var x: Float = x
		private set
	var y: Float = y
		private set

	fun draw(canvas: Canvas) {
		canvas.drawBitmap(bitmap, x, y, null)
	}

	fun update(touchX: Float, touchY: Float) {
		val targetX = touchX - bitmap.width / 2
		val targetY = touchY - bitmap.height / 2

		x += (targetX - x) * interpolationFactor
		y += (targetY - y) * interpolationFactor
	}

	fun getBounds(): Rect {
		return Rect(x.toInt(), y.toInt(), x.toInt() + bitmap.width, y.toInt() + bitmap.height)
	}
	fun shoot(): Bullet {
		return Bullet(context, x + bitmap.width / 2, y + bitmap.height)
	}
	fun setPosition(x: Float, y: Float) {
		this.x = x
		this.y = y
	}

}

