package com.mobtech.mtplayer

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.request.CachePolicy


class AppInitializer : Application(), ImageLoaderFactory {

    override fun onCreate() {
        super.onCreate()
        AppPreferences.initPrefs(applicationContext)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    }

    override fun newImageLoader() = ImageLoader.Builder(this)
        .diskCachePolicy(CachePolicy.DISABLED)
        .crossfade(true)
        .build()
}
