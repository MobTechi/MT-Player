package com.mobtech.mtplayer.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.mobtech.mtplayer.AppConstants
import com.mobtech.mtplayer.AppPreferences
import com.mobtech.mtplayer.MusicViewModel
import com.mobtech.mtplayer.R
import com.mobtech.mtplayer.databinding.FragmentMusicContainersBinding
import com.mobtech.mtplayer.databinding.GenericItemBinding
import com.mobtech.mtplayer.extensions.handleViewVisibility
import com.mobtech.mtplayer.extensions.setTitleColor
import com.mobtech.mtplayer.player.MediaPlayerHolder
import com.mobtech.mtplayer.ui.MediaControlInterface
import com.mobtech.mtplayer.ui.UIControlInterface
import com.mobtech.mtplayer.utils.Lists
import com.mobtech.mtplayer.utils.Theming
import me.zhanghai.android.fastscroll.FastScrollerBuilder
import me.zhanghai.android.fastscroll.PopupTextProvider


/**
 * A simple [Fragment] subclass.
 * Use the [MusicContainersFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MusicContainersFragment : Fragment(),
    SearchView.OnQueryTextListener {

    private var _musicContainerListBinding: FragmentMusicContainersBinding? = null

    // View model
    private lateinit var mMusicViewModel: MusicViewModel

    private var mLaunchedBy = AppConstants.FOLDER_VIEW

    private var mList: MutableList<String>? = null

    private lateinit var mListAdapter: MusicContainersAdapter

    private lateinit var mUiControlInterface: UIControlInterface
    private lateinit var mMediaControlInterface: MediaControlInterface

    private lateinit var mSortMenuItem: MenuItem
    private var mSorting = AppConstants.DESCENDING_SORTING

    private val sIsFastScrollerPopup get() = mSorting == AppConstants.ASCENDING_SORTING || mSorting == AppConstants.DESCENDING_SORTING

    private var actionMode: ActionMode? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        arguments?.getString(TAG_LAUNCHED_BY)?.let { launchedBy ->
            mLaunchedBy = launchedBy
        }

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mUiControlInterface = activity as UIControlInterface
            mMediaControlInterface = activity as MediaControlInterface
        } catch (e: ClassCastException) {
            e.printStackTrace()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _musicContainerListBinding = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _musicContainerListBinding =
            FragmentMusicContainersBinding.inflate(inflater, container, false)
        return _musicContainerListBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mMusicViewModel =
            ViewModelProvider(requireActivity())[MusicViewModel::class.java].apply {
                deviceMusic.observe(viewLifecycleOwner) { returnedMusic ->
                    if (!returnedMusic.isNullOrEmpty()) {
                        mSorting = getSortingMethodFromPrefs()
                        mList = getSortedList()
                        finishSetup()
                    }
                }
            }
    }

    private fun finishSetup() {

        _musicContainerListBinding?.artistsFoldersRv?.run {
            setHasFixedSize(true)
            itemAnimator = null
            mListAdapter = MusicContainersAdapter()
            adapter = mListAdapter
            FastScrollerBuilder(this).useMd2Style().build()
        }

        _musicContainerListBinding?.searchToolbar?.let { stb ->

            stb.inflateMenu(R.menu.menu_search)
            stb.title = getFragmentTitle()
            stb.overflowIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_sort)

            stb.setNavigationOnClickListener {
                mUiControlInterface.onCloseActivity()
            }

            with(stb.menu) {

                mSortMenuItem = Lists.getSelectedSorting(mSorting, this).apply {
                    setTitleColor(Theming.resolveThemeColor(resources))
                }

                with(findItem(R.id.action_search).actionView as SearchView) {
                    setOnQueryTextListener(this@MusicContainersFragment)
                    setOnQueryTextFocusChangeListener { _, hasFocus ->
                        stb.menu.setGroupVisible(R.id.sorting, !hasFocus)
                        stb.menu.findItem(R.id.sleeptimer).isVisible = !hasFocus
                    }
                }
                setMenuOnItemClickListener(this)
            }
        }

        tintSleepTimerIcon(enabled = MediaPlayerHolder.getInstance().isSleepTimer)
    }

    fun tintSleepTimerIcon(enabled: Boolean) {
        _musicContainerListBinding?.searchToolbar?.run {
            Theming.tintSleepTimerMenuItem(this, enabled)
        }
    }

    private fun getSortedList(): MutableList<String>? {
        return when (mLaunchedBy) {
            AppConstants.FOLDER_VIEW ->
                Lists.getSortedList(
                    mSorting,
                    mMusicViewModel.deviceMusicByFolder?.keys?.toMutableList()
                )
            else ->
                Lists.getSortedListWithNull(
                    mSorting,
                    mMusicViewModel.deviceMusicByFolder?.keys?.toMutableList()
                )
        }
    }

    private fun getSortingMethodFromPrefs(): Int {
        return when (mLaunchedBy) {
            AppConstants.FOLDER_VIEW ->
                AppPreferences.getPrefsInstance().foldersSorting
            else ->
                AppPreferences.getPrefsInstance().allMusicSorting
        }
    }

    private fun getFragmentTitle(): String {
        val stringId = when (mLaunchedBy) {
            AppConstants.FOLDER_VIEW ->
                R.string.folders
            else ->
                R.string.songs
        }
        return getString(stringId)
    }

    private fun setListDataSource(selectedList: List<String>?) {
        if (!selectedList.isNullOrEmpty()) {
            mListAdapter.swapList(selectedList)
        }
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        setListDataSource(Lists.processQueryForStringsLists(newText, getSortedList()) ?: mList)
        return false
    }

    override fun onQueryTextSubmit(query: String?) = false

    private fun setMenuOnItemClickListener(menu: Menu) {
        _musicContainerListBinding?.searchToolbar?.setOnMenuItemClickListener {

            when (it.itemId) {
                R.id.sleeptimer -> mUiControlInterface.onOpenSleepTimerDialog()
                else -> if (it.itemId != R.id.action_search) {
                    mSorting = it.order

                    mList = getSortedList()
                    setListDataSource(mList)

                    mSortMenuItem.setTitleColor(
                        Theming.resolveColorAttr(
                            requireContext(),
                            android.R.attr.textColorPrimary
                        )
                    )

                    mSortMenuItem = Lists.getSelectedSorting(mSorting, menu).apply {
                        setTitleColor(Theming.resolveThemeColor(resources))
                    }

                    saveSortingMethodToPrefs(mSorting)
                }
            }
            return@setOnMenuItemClickListener true
        }
    }

    private fun saveSortingMethodToPrefs(sortingMethod: Int) {
        if (mLaunchedBy === AppConstants.FOLDER_VIEW) {
            AppPreferences.getPrefsInstance().foldersSorting = sortingMethod
        }
    }

    companion object {

        private const val TAG_LAUNCHED_BY = "SELECTED_FRAGMENT"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment MusicContainersListFragment.
         */
        @JvmStatic
        fun newInstance(launchedBy: String) = MusicContainersFragment().apply {
            arguments = bundleOf(TAG_LAUNCHED_BY to launchedBy)
        }
    }

    private inner class MusicContainersAdapter :
        RecyclerView.Adapter<MusicContainersAdapter.ArtistHolder>(), PopupTextProvider {

        @SuppressLint("NotifyDataSetChanged")
        fun swapList(newItems: List<String>?) {
            mList = newItems?.toMutableList()
            notifyDataSetChanged()
        }

        override fun getPopupText(position: Int): String {
            if (sIsFastScrollerPopup) {
                mList?.get(position)?.run {
                    if (isNotEmpty()) {
                        return first().toString()
                    }
                }
            }
            return ""
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistHolder {
            val binding =
                GenericItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ArtistHolder(binding)
        }

        override fun getItemCount(): Int {
            return mList?.size!!
        }

        override fun onBindViewHolder(holder: ArtistHolder, position: Int) {
            holder.bindItems(mList?.get(holder.absoluteAdapterPosition)!!)
        }

        inner class ArtistHolder(private val binding: GenericItemBinding) :
            RecyclerView.ViewHolder(binding.root) {

            fun bindItems(item: String) {

                with(binding) {
                    albumCover.handleViewVisibility(show = false)

                    title.text = item
                    subtitle.text = getItemsSubtitle(item)

                    root.setOnClickListener {
                        mUiControlInterface.onArtistOrFolderSelected(item, mLaunchedBy)
                    }
                    root.setOnLongClickListener {
                        return@setOnLongClickListener true
                    }
                }
            }
        }

        private fun getItemsSubtitle(item: String): String? {
            return when (mLaunchedBy) {
                AppConstants.FOLDER_VIEW ->
                    getString(
                        R.string.folder_info,
                        mMusicViewModel.deviceMusicByFolder?.getValue(item)?.size
                    )
                else -> mMusicViewModel.deviceMusicByFolder?.get(item)?.first()?.artist
            }
        }

    }
}
