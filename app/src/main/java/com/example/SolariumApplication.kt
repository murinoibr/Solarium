package com.example

import android.app.Application
import android.graphics.Bitmap
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache

/**
 * Classe de aplicação central do Solarium.
 * Otimizada para estabilidade e fluidez máxima em emuladores e dispositivos:
 * Desativa Hardware Bitmaps (allowHardware=false) para erradicar falhas EGL e ashmem deprecation,
 * aplicando bitmap RGB_565 leve e responsivo.
 */
class SolariumApplication : Application(), ImageLoaderFactory {

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.15)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("image_cache"))
                    .maxSizePercent(0.02)
                    .build()
            }
            .allowHardware(false)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .crossfade(true)
            .build()
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        try {
            if (level >= android.content.ComponentCallbacks2.TRIM_MEMORY_BACKGROUND) {
                coil.Coil.imageLoader(this).memoryCache?.clear()
            }
        } catch (_: Exception) {
            // Silencioso em caso de ciclo de vida antecipado
        }
    }
}
