package com.lovewall.app

import android.graphics.Canvas
import android.service.wallpaper.WallpaperService
import android.view.MotionEvent
import android.view.SurfaceHolder
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.lovewall.app.models.LocationUpdate
import com.lovewall.app.models.Message
import com.lovewall.app.viewmodels.EavesdropViewModel
import com.lovewall.app.viewmodels.LocationViewModel
import com.lovewall.app.viewmodels.MessagingViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class WallpaperService : WallpaperService() {
    override fun onCreateEngine(): Engine = LoveWallEngine()

    inner class LoveWallEngine : WallpaperService.Engine(), LifecycleOwner {
        private val lifecycleRegistry = LifecycleRegistry(this)
        override val lifecycle: Lifecycle = lifecycleRegistry

        @Inject lateinit var messagingViewModel: MessagingViewModel
        @Inject lateinit var locationViewModel: LocationViewModel
        @Inject lateinit var eavesdropViewModel: EavesdropViewModel

        private var job: Job? = null
        private val scope = CoroutineScope(Dispatchers.Main)
        private var lastDrawTime = 0L
        private val DRAW_THROTTLE_MS = 100L  // 10 FPS max
        private val bitmapCache = android.util.LruCache<String, android.graphics.Bitmap>(10 * 1024 * 1024)  // 10MB

        override fun onCreate(surfaceHolder: SurfaceHolder?) {
            super.onCreate(surfaceHolder)
            lifecycleRegistry.currentState = Lifecycle.State.CREATED
            // Listen for updates
            job = scope.launch {
                launch { messagingViewModel.messages.collect { updateOverlays(it) } }
                launch { locationViewModel.location.collect { updateLocationWidget(it) } }
                launch { eavesdropViewModel.sessionActive.collect { updateEavesdropIcon(it) } }
            }
        }

        override fun onDestroy() {
            job?.cancel()
            lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
            super.onDestroy()
        }

        override fun onTouchEvent(event: MotionEvent) {
            super.onTouchEvent(event)
            // Handle gestures: tap to send note, etc.
            if (event.action == MotionEvent.ACTION_DOWN) {
                // Launch transparent activity for input
                val intent = android.content.Intent(applicationContext, InputActivity::class.java)
                intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP)
                applicationContext.startActivity(intent)
            }
        }

        override fun onVisibilityChanged(visible: Boolean) {
            if (visible) {
                lifecycleRegistry.currentState = Lifecycle.State.RESUMED
            } else {
                lifecycleRegistry.currentState = Lifecycle.State.STARTED
            }
        }

        private fun updateOverlays(messages: List<Message>) {
            if (System.currentTimeMillis() - lastDrawTime < DRAW_THROTTLE_MS) return
            val canvas = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                surfaceHolder?.lockHardwareCanvas()
            } else {
                surfaceHolder?.lockCanvas()
            } ?: return
            
            try {
                drawBackground(canvas)
                messages.forEach { drawMessage(it, canvas) }
            } finally {
                surfaceHolder.unlockCanvasAndPost(canvas)
                lastDrawTime = System.currentTimeMillis()
            }
        }

        private fun drawBackground(canvas: Canvas) {
            // Draw custom background
            canvas.drawColor(0xFF000000.toInt()) // Placeholder black
        }

        private fun drawMessage(message: Message, canvas: Canvas) {
            // Render text/sticker/voice icon at position
        }

        private fun updateLocationWidget(location: LocationUpdate) {
            // Draw mini-map or icon
        }

        private fun updateEavesdropIcon(active: Boolean) {
            // Draw pulsing mic icon
        }
    }
}
