package com.mobtech.mtplayer.ui

import com.mobtech.mtplayer.models.Music

interface MediaControlInterface {
    fun onSongSelected(song: Music?, songs: List<Music>?, songLaunchedBy: String)
    fun onSongsShuffled(
        songs: List<Music>?,
        songLaunchedBy: String
    )

    fun onAddToQueue(song: Music?)
    fun onAddAlbumToQueue(
        songs: List<Music>?,
        // first: force play, second: restore song
        forcePlay: Pair<Boolean, Music?>
    )

    fun onUpdatePlayingAlbumSongs(songs: List<Music>?)
    fun onPlaybackSpeedToggled()
    fun onHandleCoverOptionsUpdate()
    fun onUpdatePositionFromNP(position: Int)
}
