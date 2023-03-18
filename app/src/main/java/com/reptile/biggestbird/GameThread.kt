package com.reptile.biggestbird;

import android.graphics.Canvas
import android.view.SurfaceHolder


class GameThread(private val surfaceHolder: SurfaceHolder, private val gameView: GameView) : Thread() {
	private var running: Boolean = false

	fun setRunning(isRunning: Boolean) {
		running = isRunning
	}

	override fun run() {
		while (running) {
			var canvas: Canvas? = null
			try {
				canvas = surfaceHolder.lockCanvas()
				synchronized(surfaceHolder) {
					gameView.update()
					gameView.draw(canvas)
				}
			} catch (e: Exception) {
				e.printStackTrace()
			} finally {
				if (canvas != null) {
					try {
						surfaceHolder.unlockCanvasAndPost(canvas)
					} catch (e: Exception) {
						e.printStackTrace()
					}
				}
			}
			try {
				sleep(1000 / 60.toLong())
			} catch (e: InterruptedException) {
				e.printStackTrace()
			}
		}
	}
}


