package com.mobtech.mtplayer.preferences

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.mobtech.mtplayer.AppConstants
import com.mobtech.mtplayer.AppPreferences
import com.mobtech.mtplayer.databinding.NotificationActionsItemBinding
import com.mobtech.mtplayer.models.NotificationAction
import com.mobtech.mtplayer.utils.Theming


class NotificationActionsAdapter(private val ctx: Context) :
    RecyclerView.Adapter<NotificationActionsAdapter.CheckableItemsHolder>() {

    var selectedActions = AppPreferences.getPrefsInstance().notificationActions

    private val mActions = listOf(
        NotificationAction(AppConstants.REPEAT_ACTION, AppConstants.CLOSE_ACTION), // default
        NotificationAction(AppConstants.REWIND_ACTION, AppConstants.FAST_FORWARD_ACTION),
        NotificationAction(AppConstants.FAVORITE_ACTION, AppConstants.CLOSE_ACTION),
        NotificationAction(AppConstants.FAVORITE_POSITION_ACTION, AppConstants.CLOSE_ACTION)
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckableItemsHolder {
        val binding = NotificationActionsItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CheckableItemsHolder(binding)
    }

    override fun getItemCount() = mActions.size

    override fun onBindViewHolder(holder: CheckableItemsHolder, position: Int) {
        holder.bindItems()
    }

    inner class CheckableItemsHolder(private val binding: NotificationActionsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindItems() {

            with(binding) {

                notifAction0.setImageResource(
                    Theming.getNotificationActionIcon(
                        mActions[absoluteAdapterPosition].first,
                        isNotification = false
                    )
                )
                notifAction1.setImageResource(
                    Theming.getNotificationActionIcon(
                        mActions[absoluteAdapterPosition].second,
                        isNotification = false
                    )
                )
                radio.isChecked = selectedActions == mActions[absoluteAdapterPosition]

                root.contentDescription =
                    ctx.getString(Theming.getNotificationActionTitle(mActions[absoluteAdapterPosition].first))

                root.setOnClickListener {
                    notifyItemChanged(mActions.indexOf(selectedActions))
                    selectedActions = mActions[absoluteAdapterPosition]
                    notifyItemChanged(absoluteAdapterPosition)
                    AppPreferences.getPrefsInstance().notificationActions = selectedActions
                }

                root.setOnLongClickListener {
                    Toast.makeText(
                        ctx,
                        Theming.getNotificationActionTitle(mActions[absoluteAdapterPosition].first),
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnLongClickListener true
                }
            }
        }
    }
}
