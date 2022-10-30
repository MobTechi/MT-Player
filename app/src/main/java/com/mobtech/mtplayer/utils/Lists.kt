package com.mobtech.mtplayer.utils

import android.annotation.SuppressLint
import android.content.Context
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.mobtech.mtplayer.AppConstants
import com.mobtech.mtplayer.AppPreferences
import com.mobtech.mtplayer.R
import com.mobtech.mtplayer.extensions.toFormattedDuration
import com.mobtech.mtplayer.extensions.toSavedMusic
import com.mobtech.mtplayer.models.Music
import java.util.*

@SuppressLint("DefaultLocale")
object Lists {

    @JvmStatic
    fun processQueryForStringsLists(
        query: String?,
        list: List<String>?
    ): List<String>? {
        // In real app you'd have it instantiated just once
        val filteredStrings = mutableListOf<String>()

        return try {
            // Case insensitive search
            list?.iterator()?.let { iterate ->
                while (iterate.hasNext()) {
                    val filteredString = iterate.next()
                    if (filteredString.lowercase().contains(query?.lowercase()!!)) {
                        filteredStrings.add(filteredString)
                    }
                }
            }
            return filteredStrings
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    @JvmStatic
    fun processQueryForMusic(query: String?, musicList: List<Music>?): List<Music>? {
        // In real app you'd have it instantiated just once
        val filteredSongs = mutableListOf<Music>()
        val isShowDisplayName =
            AppPreferences.getPrefsInstance().songsVisualization == AppConstants.FN
        return try {
            // Case insensitive search
            musicList?.iterator()?.let { iterate ->
                while (iterate.hasNext()) {
                    val filteredSong = iterate.next()
                    val toFilter = if (isShowDisplayName) {
                        filteredSong.displayName
                    } else {
                        filteredSong.title
                    }
                    if (toFilter?.lowercase()!!.contains(query?.lowercase()!!)) {
                        filteredSongs.add(filteredSong)
                    }
                }
            }
            return filteredSongs
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    @JvmStatic
    fun getSortedList(
        id: Int,
        list: MutableList<String>?
    ) = when (id) {
        AppConstants.ASCENDING_SORTING -> list?.apply {
            Collections.sort(this, String.CASE_INSENSITIVE_ORDER)
        }

        AppConstants.DESCENDING_SORTING -> list?.apply {
            Collections.sort(this, String.CASE_INSENSITIVE_ORDER)
        }?.asReversed()
        else -> list
    }

    @JvmStatic
    fun getSortedListWithNull(
        id: Int,
        list: MutableList<String?>?
    ): MutableList<String>? {
        val withoutNulls = list?.map {
            transformNullToEmpty(it)
        }?.toMutableList()
        return getSortedList(id, withoutNulls)
    }

    private fun transformNullToEmpty(toTrans: String?): String {
        if (toTrans == null) {
            return ""
        }
        return toTrans
    }

    @JvmStatic
    fun getSelectedSorting(sorting: Int, menu: Menu): MenuItem {
        return when (sorting) {
            AppConstants.ASCENDING_SORTING -> menu.findItem(R.id.ascending_sorting)
            AppConstants.DESCENDING_SORTING -> menu.findItem(R.id.descending_sorting)
            else -> menu.findItem(R.id.default_sorting)
        }
    }

    @JvmStatic
    fun getSelectedSortingForAllMusic(sorting: Int, menu: Menu): MenuItem {
        return when (sorting) {
            AppConstants.ASCENDING_SORTING -> menu.findItem(R.id.ascending_sorting)
            AppConstants.DESCENDING_SORTING -> menu.findItem(R.id.descending_sorting)
            AppConstants.DATE_ADDED_SORTING -> menu.findItem(R.id.date_added_sorting)
            AppConstants.DATE_ADDED_SORTING_INV -> menu.findItem(R.id.date_added_sorting_inv)
            AppConstants.ARTIST_SORTING -> menu.findItem(R.id.artist_sorting)
            AppConstants.ARTIST_SORTING_INV -> menu.findItem(R.id.artist_sorting_inv)
            AppConstants.ALBUM_SORTING -> menu.findItem(R.id.album_sorting)
            AppConstants.ALBUM_SORTING_INV -> menu.findItem(R.id.album_sorting_inv)
            else -> menu.findItem(R.id.default_sorting)
        }
    }

    @JvmStatic
    fun getSortedMusicList(
        id: Int,
        list: MutableList<Music>?
    ): List<Music>? {

        return when (id) {
            AppConstants.ASCENDING_SORTING -> getSortedListBySelectedVisualization(list)
            AppConstants.DESCENDING_SORTING -> getSortedListBySelectedVisualization(list)?.asReversed()
            AppConstants.TRACK_SORTING -> list?.sortedBy { it.track }
            AppConstants.TRACK_SORTING_INVERTED -> list?.sortedBy { it.track }?.asReversed()
            else -> list
        }
    }

    @JvmStatic
    fun getSortedMusicListForAllMusic(
        id: Int,
        list: List<Music>?
    ): List<Music>? {

        return when (id) {
            AppConstants.ASCENDING_SORTING -> getSortedListBySelectedVisualization(list)
            AppConstants.DESCENDING_SORTING -> getSortedListBySelectedVisualization(list)?.asReversed()
            AppConstants.TRACK_SORTING -> list?.sortedBy { it.track }
            AppConstants.TRACK_SORTING_INVERTED -> list?.sortedBy { it.track }?.asReversed()
            AppConstants.DATE_ADDED_SORTING -> list?.sortedBy { it.dateAdded }?.asReversed()
            AppConstants.DATE_ADDED_SORTING_INV -> list?.sortedBy { it.dateAdded }
            AppConstants.ARTIST_SORTING -> list?.sortedBy { it.artist }
            AppConstants.ARTIST_SORTING_INV -> list?.sortedBy { it.artist }?.asReversed()
            AppConstants.ALBUM_SORTING -> list?.sortedBy { it.album }
            AppConstants.ALBUM_SORTING_INV -> list?.sortedBy { it.album }?.asReversed()
            else -> list
        }
    }

    private fun getSortedListBySelectedVisualization(list: List<Music>?) = list?.sortedBy {
        if (AppPreferences.getPrefsInstance().songsVisualization == AppConstants.FN) {
            it.displayName
        } else {
            it.title
        }
    }

    @JvmStatic
    fun getSortedMusicListForFolder(
        id: Int,
        list: MutableList<Music>?
    ): List<Music>? {
        return when (id) {
            AppConstants.ASCENDING_SORTING -> list?.sortedBy { it.displayName }
            AppConstants.DESCENDING_SORTING -> list?.sortedBy { it.displayName }?.asReversed()
            AppConstants.DATE_ADDED_SORTING -> list?.sortedBy { it.dateAdded }?.asReversed()
            AppConstants.DATE_ADDED_SORTING_INV -> list?.sortedBy { it.dateAdded }
            AppConstants.ARTIST_SORTING -> list?.sortedBy { it.artist }
            AppConstants.ARTIST_SORTING_INV -> list?.sortedBy { it.artist }?.asReversed()
            else -> list
        }
    }

    @JvmStatic
    fun getSongsSorting(currentSorting: Int) = when (currentSorting) {
        AppConstants.TRACK_SORTING -> AppConstants.TRACK_SORTING_INVERTED
        AppConstants.TRACK_SORTING_INVERTED -> AppConstants.ASCENDING_SORTING
        AppConstants.ASCENDING_SORTING -> AppConstants.DESCENDING_SORTING
        else -> AppConstants.TRACK_SORTING
    }

    @JvmStatic
    fun getSongsDisplayNameSorting(currentSorting: Int) =
        if (currentSorting == AppConstants.ASCENDING_SORTING) {
            AppConstants.DESCENDING_SORTING
        } else {
            AppConstants.ASCENDING_SORTING
        }

    fun hideItems(items: List<String>) {
        val hiddenArtistsFolders = AppPreferences.getPrefsInstance().filters?.toMutableList()
        hiddenArtistsFolders?.addAll(items)
        AppPreferences.getPrefsInstance().filters = hiddenArtistsFolders?.toSet()
    }

    @JvmStatic
    fun addToFavorites(
        context: Context,
        song: Music?,
        canRemove: Boolean,
        playerPosition: Int,
        launchedBy: String
    ) {
        val favorites =
            AppPreferences.getPrefsInstance().favorites?.toMutableList() ?: mutableListOf()
        song?.toSavedMusic(playerPosition, launchedBy)?.let { savedSong ->
            if (!favorites.contains(savedSong)) {
                favorites.add(savedSong)
                Toast.makeText(
                    context,
                    context.getString(
                        R.string.favorite_added,
                        savedSong.title,
                        playerPosition.toLong().toFormattedDuration(
                            isAlbum = false,
                            isSeekBar = false
                        )
                    ),
                    Toast.LENGTH_SHORT
                ).show()
            } else if (canRemove) {
                favorites.remove(savedSong)
            }
            AppPreferences.getPrefsInstance().favorites = favorites
        }
    }
}
