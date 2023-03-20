package com.reptile.biggestbird

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import androidx.core.graphics.drawable.toBitmap
import kotlin.random.Random


class EnemyPlane(private val context: Context, private val screenWidth: Int) {
	private val bitmap: Bitmap = context.getDrawable(R.drawable.enemy_plane)?.toBitmap()!!
	private var x: Float = Random.nextInt(screenWidth - bitmap.width).toFloat()
	private var y: Float = -bitmap.height.toFloat()
	private val speed: Float = Random.nextFloat() * 5 + 1

	fun draw(canvas: Canvas) {
		canvas.drawBitmap(bitmap, x, y, null)
	}
	fun update() {
		y += speed
	}

	fun isOffScreen(screenHeight: Int): Boolean {
		return y > screenHeight
	}

	fun getBounds(): Rect {
		return Rect(x.toInt(), y.toInt(), x.toInt() + bitmap.width, y.toInt() + bitmap.height)
	}
}
