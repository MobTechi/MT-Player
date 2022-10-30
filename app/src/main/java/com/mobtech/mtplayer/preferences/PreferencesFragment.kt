package com.mobtech.mtplayer.preferences

import android.content.Context
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.mobtech.mtplayer.AppPreferences
import com.mobtech.mtplayer.R
import com.mobtech.mtplayer.dialogs.RecyclerSheet
import com.mobtech.mtplayer.player.MediaPlayerHolder
import com.mobtech.mtplayer.ui.MediaControlInterface
import com.mobtech.mtplayer.ui.UIControlInterface
import com.mobtech.mtplayer.utils.Theming


class PreferencesFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceClickListener {

    private lateinit var mUIControlInterface: UIControlInterface
    private lateinit var mMediaControlInterface: MediaControlInterface

    private val mMediaPlayerHolder get() = MediaPlayerHolder.getInstance()
    private val mAppPreferences get() = AppPreferences.getPrefsInstance()

    override fun setDivider(divider: Drawable?) {
        super.setDivider(null)
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences?.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences?.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mUIControlInterface = activity as UIControlInterface
            mMediaControlInterface = activity as MediaControlInterface
        } catch (e: ClassCastException) {
            e.printStackTrace()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        findPreference<Preference>(getString(R.string.filter_pref))?.onPreferenceClickListener =
            this@PreferencesFragment

        findPreference<Preference>(getString(R.string.notif_actions_pref))?.run {
            summary =
                getString(Theming.getNotificationActionTitle(mAppPreferences.notificationActions.first))
            onPreferenceClickListener = this@PreferencesFragment
        }

        findPreference<Preference>(getString(R.string.filter_pref))?.run {
            AppPreferences.getPrefsInstance().filters?.let { ft ->
                summary = ft.size.toString()
                isEnabled = ft.isNotEmpty()
            }
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }

    override fun onPreferenceClick(preference: Preference): Boolean {
        Toast.makeText(activity, "preference " + preference.key, Toast.LENGTH_SHORT).show()
        when (preference.key) {
            getString(R.string.filter_pref) -> if (!mAppPreferences.filters.isNullOrEmpty()) {
                RecyclerSheet.newInstance(RecyclerSheet.FILTERS_TYPE)
                    .show(requireActivity().supportFragmentManager, RecyclerSheet.TAG_MODAL_RV)
            }
            getString(R.string.notif_actions_pref) -> RecyclerSheet.newInstance(RecyclerSheet.NOTIFICATION_ACTIONS_TYPE)
                .show(requireActivity().supportFragmentManager, RecyclerSheet.TAG_MODAL_RV)
        }
        return false
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            getString(R.string.precise_volume_pref) -> mMediaPlayerHolder.run {
                setPreciseVolume(
                    if (!mAppPreferences.isPreciseVolumeEnabled) {
                        mAppPreferences.latestVolume = currentVolumeInPercent
                        100
                    } else {
                        mAppPreferences.latestVolume
                    }
                )
            }
            getString(R.string.playback_vel_pref) -> mMediaControlInterface.onPlaybackSpeedToggled()
            getString(R.string.eq_pref) -> if (mAppPreferences.isEqForced) {
                mMediaPlayerHolder.onBuiltInEqualizerEnabled()
            } else {
                mMediaPlayerHolder.releaseBuiltInEqualizer()
            }
            getString(R.string.focus_pref) -> mMediaPlayerHolder.run {
                if (mAppPreferences.isFocusEnabled) {
                    tryToGetAudioFocus()
                    return
                }
                giveUpAudioFocus()
            }
            getString(R.string.covers_pref) -> mMediaControlInterface.onHandleCoverOptionsUpdate()
            getString(R.string.notif_actions_pref) ->
                findPreference<Preference>(getString(R.string.notif_actions_pref))?.summary =
                    getString(Theming.getNotificationActionTitle(mAppPreferences.notificationActions.first))
            getString(R.string.song_visual_pref) -> {
                mMediaPlayerHolder.updateMediaSessionMetaData()
                mMediaControlInterface.onUpdatePlayingAlbumSongs(null)
            }
        }
    }

    fun enableEqualizerOption() {
        val eqEnabled = mAppPreferences.isEqForced
        findPreference<SwitchPreferenceCompat>(getString(R.string.eq_pref))?.run {
            isChecked = eqEnabled
            isEnabled = eqEnabled
            summary = if (!eqEnabled) {
                getString(R.string.error_builtin_eq)
            } else {
                getString(R.string.eq_pref_sum)
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment PreferencesFragment.
         */
        @JvmStatic
        fun newInstance() = PreferencesFragment()
    }
}
