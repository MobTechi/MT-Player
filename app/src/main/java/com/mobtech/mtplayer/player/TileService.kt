package com.mobtech.mtplayer.player

import android.content.Intent
import android.os.Build
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi
import com.mobtech.mtplayer.AppConstants
import com.mobtech.mtplayer.ui.MainActivity

@RequiresApi(Build.VERSION_CODES.N)
class PlayerTileService : TileService() {

    override fun onClick() {
        super.onClick()
        with(Intent(this, MainActivity::class.java)) {
            putExtra(AppConstants.LAUNCHED_BY_TILE, AppConstants.LAUNCHED_BY_TILE)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivityAndCollapse(this)
        }
    }
}
