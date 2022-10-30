@file:Suppress("DEPRECATION")

package com.mobtech.mtplayer.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.google.android.material.appbar.MaterialToolbar
import com.mobtech.mtplayer.AppConstants
import com.mobtech.mtplayer.AppPreferences
import com.mobtech.mtplayer.R
import com.mobtech.mtplayer.extensions.setIconTint
import com.mobtech.mtplayer.extensions.toSavedMusic
import com.mobtech.mtplayer.player.MediaPlayerHolder
import com.mobtech.mtplayer.ui.MainActivity


object Theming {

    @JvmStatic
    fun applyChanges(activity: Activity, currentViewPagerItem: Int) {
        with(activity) {
            finishAfterTransition()
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra(AppConstants.RESTORE_FRAGMENT, currentViewPagerItem)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }

    fun getSortIconForSongs(sort: Int): Int {
        return when (sort) {
            AppConstants.ASCENDING_SORTING -> R.drawable.ic_sort_alphabetical_descending
            AppConstants.DESCENDING_SORTING -> R.drawable.ic_sort_alphabetical_ascending
            AppConstants.TRACK_SORTING -> R.drawable.ic_sort_numeric_descending
            else -> R.drawable.ic_sort_numeric_ascending
        }
    }

    fun getSortIconForSongsDisplayName(sort: Int): Int {
        return when (sort) {
            AppConstants.ASCENDING_SORTING -> R.drawable.ic_sort_alphabetical_descending
            else -> R.drawable.ic_sort_alphabetical_ascending
        }
    }

    @ColorInt
    @JvmStatic
    fun resolveThemeColor(resources: Resources): Int {
        return resources.getColor(R.color.gray)
    }

    @ColorInt
    @JvmStatic
    fun resolveWidgetsColorNormal(context: Context) = resolveColorAttr(
        context,
        android.R.attr.colorButtonNormal
    )

    @ColorInt
    @JvmStatic
    fun resolveColorAttr(context: Context, @AttrRes colorAttr: Int): Int {
        val resolvedAttr: TypedValue =
            resolveThemeAttr(
                context,
                colorAttr
            )
        // resourceId is used if it's a ColorStateList, and data if it's a color reference or a hex color
        val colorRes =
            if (resolvedAttr.resourceId != 0) {
                resolvedAttr.resourceId
            } else {
                resolvedAttr.data
            }
        return ContextCompat.getColor(context, colorRes)
    }

    @JvmStatic
    private fun resolveThemeAttr(context: Context, @AttrRes attrRes: Int) =
        TypedValue().apply { context.theme.resolveAttribute(attrRes, this, true) }

    @JvmStatic
    fun getAlbumCoverAlpha(): Int {
        return 25
    }

    @JvmStatic
    fun getTabIcon(tab: String) = when (tab) {
        AppConstants.SONGS_TAB -> R.drawable.ic_music_note
        AppConstants.FOLDERS_TAB -> R.drawable.ic_folder_music
        else -> R.drawable.ic_settings
    }

    @JvmStatic
    fun getNotificationActionTitle(action: String) = when (action) {
        AppConstants.REPEAT_ACTION -> R.string.notification_actions_repeat
        AppConstants.REWIND_ACTION -> R.string.notification_actions_fast_seeking
        AppConstants.FAVORITE_ACTION -> R.string.notification_actions_favorite
        else -> R.string.notification_actions_favorite_position
    }

    @JvmStatic
    fun getNotificationActionIcon(action: String, isNotification: Boolean): Int {
        val mediaPlayerHolder = MediaPlayerHolder.getInstance()
        return when (action) {
            AppConstants.PLAY_PAUSE_ACTION -> if (mediaPlayerHolder.state == AppConstants.PLAYING || mediaPlayerHolder.state == AppConstants.RESUMED) {
                R.drawable.ic_pause
            } else {
                R.drawable.ic_play
            }
            AppConstants.REPEAT_ACTION -> if (isNotification) {
                getRepeatIcon(mediaPlayerHolder, isNotification = true)
            } else {
                R.drawable.ic_repeat
            }
            AppConstants.PREV_ACTION -> R.drawable.ic_skip_previous
            AppConstants.NEXT_ACTION -> R.drawable.ic_skip_next
            AppConstants.CLOSE_ACTION -> R.drawable.ic_close
            AppConstants.FAST_FORWARD_ACTION -> R.drawable.ic_fast_forward
            AppConstants.REWIND_ACTION -> R.drawable.ic_fast_rewind
            AppConstants.FAVORITE_ACTION -> if (isNotification) {
                getFavoriteIcon(mediaPlayerHolder, isNotification = true)
            } else {
                R.drawable.ic_favorite
            }
            else -> R.drawable.ic_favorite
        }
    }

    @JvmStatic
    fun getRepeatIcon(mediaPlayerHolder: MediaPlayerHolder, isNotification: Boolean) = when {
        mediaPlayerHolder.isRepeat1X -> R.drawable.ic_repeat_one
        mediaPlayerHolder.isLooping -> R.drawable.ic_repeat
        else -> if (isNotification) {
            R.drawable.ic_repeat_one_disabled_alt
        } else {
            R.drawable.ic_repeat_one_disabled
        }
    }

    @JvmStatic
    fun getFavoriteIcon(mediaPlayerHolder: MediaPlayerHolder, isNotification: Boolean): Int {
        val favorites = AppPreferences.getPrefsInstance().favorites
        val isFavorite = favorites != null && favorites.contains(
            mediaPlayerHolder.currentSong?.toSavedMusic(0, mediaPlayerHolder.launchedBy)
        )
        return if (isFavorite) {
            R.drawable.ic_favorite
        } else {
            if (isNotification) {
                R.drawable.ic_favorite_empty_alt
            } else {
                R.drawable.ic_favorite_empty
            }
        }
    }

    @JvmStatic
    fun tintSleepTimerMenuItem(tb: MaterialToolbar, isEnabled: Boolean) {
        tb.menu.findItem(R.id.sleeptimer).setIconTint(
            if (isEnabled) {
                tb.resources.getColor(R.color.silver_gray)
            } else {
                ContextCompat.getColor(tb.context, R.color.white)
            }
        )
    }
}
