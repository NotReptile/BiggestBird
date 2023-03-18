package com.reptile.biggestbird;

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import kotlinx.coroutines.delay
import kotlin.random.Random


class GameView(context: Context) : SurfaceView(context), SurfaceHolder.Callback {
	private val gameThread: GameThread = GameThread(holder, this)
	private var playerPlane: PlayerPlane = PlayerPlane(context, width / 2f, height * 0.85f)
	private val enemyPlanes: MutableList<EnemyPlane> = mutableListOf()
	private val enemyBullets: MutableList<Bullet> = mutableListOf()
	private val bullets: MutableList<Bullet> = mutableListOf()
	private val startButton = Rect()
	private var lastSpawnTime: Long = System.currentTimeMillis()
	private var touchX: Float = 0f
	private var touchY: Float = 0f
	private var playerLives: Int = 3
	private var isGameOver = false
	private var lastShotTime: Long = 0
	private val shotDelay: Long = 300
	private var isGameStarted = false

	init {
		holder.addCallback(this)
	}

	override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
		super.onSizeChanged(w, h, oldw, oldh)
		playerPlane.setPosition(w / 2f, h * 0.85f)
		startButton.set(w / 2 - 200, h / 2 - 100, w / 2 + 200, h / 2 + 100)
	}




	override fun surfaceCreated(holder: SurfaceHolder) {
		gameThread.setRunning(true)
		gameThread.start()
	}

	override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

	override fun surfaceDestroyed(holder: SurfaceHolder) {
		gameThread.setRunning(false)
		gameThread.join()
	}

	fun update() {
		// Update game logic here
		playerPlane.update(touchX, touchY)
		spawnEnemyPlanes()
		updateEnemyPlanes()
		updateBullets()
		// Add collision detection and scoring logic
	}
	private fun updateBullets() {
		val playerBulletIterator = bullets.iterator()
		while (playerBulletIterator.hasNext()) {
			val bullet = playerBulletIterator.next()
			bullet.update()

			if (bullet.isOffScreen()) {
				playerBulletIterator.remove()
			}

			// Check for collisions between player bullets and enemy planes
			val enemyPlaneIterator = enemyPlanes.iterator()
			while (enemyPlaneIterator.hasNext()) {
				val enemyPlane = enemyPlaneIterator.next()
				if (Rect.intersects(bullet.getBounds(), enemyPlane.getBounds())) {
					playerBulletIterator.remove()
					enemyPlaneIterator.remove()
					break
				}
			}
		}
		val enemyBulletIterator = enemyBullets.iterator()
		while (enemyBulletIterator.hasNext()) {
			val bullet = enemyBulletIterator.next()
			bullet.update()

			if (bullet.isOffScreen()) {
				enemyBulletIterator.remove()
			}

			// Check for collisions between enemy bullets and the player
			if (Rect.intersects(bullet.getBounds(), playerPlane.getBounds())) {
				enemyBulletIterator.remove()
				playerLives--
				if (playerLives <= 0) {
					isGameOver = true
				}
			}
		}
	}

	private fun updateEnemyPlanes() {
		val iterator = enemyPlanes.iterator()
		while (iterator.hasNext()) {
			val enemyPlane = iterator.next()
			enemyPlane.update()
			if (enemyPlane.isOffScreen(height)) {
				iterator.remove()
			}
			// Enemy plane shooting
			if (Random.nextInt(100) < 2) {
				enemyBullets.add(enemyPlane.shoot())
			}
			// Check for collisions between enemy planes and the player
			if (Rect.intersects(enemyPlane.getBounds(), playerPlane.getBounds())) {
				iterator.remove()
				playerLives--
				if (playerLives <= 0) {
					isGameOver = true
				}
			}
		}
	}
	private fun spawnEnemyPlanes() {
		val currentTime = System.currentTimeMillis()
		if (currentTime - lastSpawnTime > 1000) {
			enemyPlanes.add(EnemyPlane(context, width))
			lastSpawnTime = currentTime
		}
	}


	override fun draw(canvas: Canvas) {
		super.draw(canvas)
		canvas.drawColor(Color.GRAY)

		if (!isGameStarted) {
			drawStartButton(canvas)
		} else {
			playerPlane.draw(canvas)
			enemyPlanes.forEach { it.draw(canvas) }
			bullets.forEach { it.draw(canvas) }
		}

		if (isGameOver) {
			drawGameOver(canvas)
		}
	}

	private fun drawStartButton(canvas: Canvas) {
		val paint = Paint()
		paint.color = Color.WHITE
		paint.textSize = 150f
		paint.textAlign = Paint.Align.CENTER
		canvas.drawText("Start", width / 2f, height / 2f, paint)
	}

	private fun drawGameOver(canvas: Canvas) {
		val paint = Paint()
		paint.color = Color.WHITE
		paint.textSize = 100f
		paint.textAlign = Paint.Align.CENTER
		canvas.drawText("Game Over", width / 2f, height / 2f, paint)

		paint.textSize = 50f
		canvas.drawText("Tap to restart", width / 2f, height / 2f + 100, paint)
	}

	override fun onTouchEvent(event: MotionEvent): Boolean {
		if (isGameOver) {
			if (event.action == MotionEvent.ACTION_DOWN) {
				resetGame()
				return true
			}
		} else {
			when (event.action) {
				MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
					touchX = event.x
					touchY = event.y

					if (!isGameStarted && startButton.contains(touchX.toInt(), touchY.toInt())) {
						isGameStarted = true
						return true
					}

					if (isGameStarted) {
						val currentTime = System.currentTimeMillis()
						if (currentTime - lastShotTime > shotDelay) {
							bullets.add(playerPlane.shoot())
							lastShotTime = currentTime
						}
					}

					return true
				}
			}
		}
		return super.onTouchEvent(event)
	}



	private fun resetGame() {
		playerLives = 3
		isGameOver = false
		isGameStarted = false
		enemyPlanes.clear()
		bullets.clear()
		enemyBullets.clear()
	}


}


