package com.mobtech.mtplayer.equalizer

import android.os.Build
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.core.view.WindowCompat
import com.mobtech.mtplayer.BaseActivity
import com.mobtech.mtplayer.R
import com.mobtech.mtplayer.databinding.ActivityEqualizerBinding
import com.mobtech.mtplayer.extensions.applyEdgeToEdge
import com.mobtech.mtplayer.utils.Theming


class EqualizerActivity : BaseActivity() {

    private lateinit var mEqualizerBinding: ActivityEqualizerBinding

    private val onBackPressedCallback: OnBackPressedCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finishAndRemoveTask()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        setTheme(Theming.resolveTheme(this))

        mEqualizerBinding = ActivityEqualizerBinding.inflate(layoutInflater)
        setContentView(mEqualizerBinding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            window?.navigationBarColor = Theming.resolveColorAttr(this, R.attr.main_bg)
            WindowCompat.setDecorFitsSystemWindows(window, true)
            mEqualizerBinding.root.applyEdgeToEdge()
        }

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }
}
