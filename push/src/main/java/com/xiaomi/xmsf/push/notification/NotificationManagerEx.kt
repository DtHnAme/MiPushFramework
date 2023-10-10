package com.nihility.notification

import android.app.INotificationManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.NotificationManager
import android.content.Context
import android.content.pm.ParceledListSlice
import android.os.Build
import android.service.notification.StatusBarNotification

import com.elvishew.xlog.XLog

import com.xiaomi.channel.commonutils.reflect.JavaCalls

import com.xiaomi.xmsf.MiPushFrameworkApp

object NotificationManagerEx {
    private const val TAG = "NotificationManagerEx"

    private const val ANDROID = "android"

    val isSystemHookReady: Boolean by lazy {
        true == JavaCalls.callMethod(notificationManager, "isSystemConditionProviderEnabled", "is_system_hook_ready")
    }

    private val notificationManager: NotificationManager by lazy {
        MiPushFrameworkApp.getContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    private val iNotificationManager: INotificationManager by lazy {
        JavaCalls.callStaticMethod(NotificationManager::class.java.name, "getService")
    }

    private fun getUid(packageName: String): Int {
        return MiPushFrameworkApp.getContext().packageManager.getPackageUid(packageName, 0)
    }

    private fun getUserId(): Int {
        return JavaCalls.callMethod(MiPushFrameworkApp.getContext(), "getUserId") as Int? ?: 0
    }

    fun notify(
        packageName: String,
        tag: String?, id: Int, notification: Notification
    ) {
        XLog.d(TAG, "notify() called with: packageName = $packageName, tag = $tag, id = $id, notification = $notification")
        if (isSystemHookReady) {
            val opPkg = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) ANDROID else packageName
            iNotificationManager.enqueueNotificationWithTag(packageName, opPkg, tag, id, notification, getUserId())
        } else {
            notificationManager.notify(tag, id, notification)
        }
    }

    fun cancel(
        packageName: String,
        tag: String?, id: Int
    ) {
        XLog.d(TAG, "cancel() called with: packageName = $packageName, tag = $tag, id = $id")
        if (isSystemHookReady) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                iNotificationManager.cancelNotificationWithTag(packageName, ANDROID, tag, id, getUserId())
            } else {
                iNotificationManager.cancelNotificationWithTag(packageName, tag, id, getUserId())
            }
        } else {
            notificationManager.cancel(tag, id)
        }
    }

    fun createNotificationChannels(
        packageName: String,
        channels: List<NotificationChannel?>
    ) {
        XLog.d(TAG, "createNotificationChannels() called with: packageName = $packageName, channels = $channels")
        if (isSystemHookReady) {
            iNotificationManager.createNotificationChannelsForPackage(packageName, getUid(packageName), ParceledListSlice(channels))
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannels(channels)
        }
    }

    fun getNotificationChannel(
        packageName: String,
        channelId: String?
    ): NotificationChannel? {
        XLog.d(TAG, "createNotificationChannels() called with: packageName = $packageName, channelId = $channelId")
        if (isSystemHookReady) {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                iNotificationManager.getNotificationChannelForPackage(packageName, getUid(packageName), channelId, null, false)
            } else {
                iNotificationManager.getNotificationChannelForPackage(packageName, getUid(packageName), channelId, false)
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return notificationManager.getNotificationChannel(channelId)
        }
        return null
    }

    fun getNotificationChannels(
        packageName: String
    ): List<NotificationChannel?>? {
        XLog.d(TAG, "getNotificationChannels() called with: packageName = $packageName")
        if (isSystemHookReady) {
            return iNotificationManager.getNotificationChannelsForPackage(packageName, getUid(packageName), false).list
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return notificationManager.notificationChannels
        }
        return emptyList()
    }

    fun deleteNotificationChannel(
        packageName: String,
        channelId: String?
    ) {
        XLog.d(TAG, "deleteNotificationChannel() called with: packageName = $packageName, channelId = $channelId")
        if (isSystemHookReady) {
            iNotificationManager.deleteNotificationChannel(packageName, channelId)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.deleteNotificationChannel(channelId)
        }
    }


    fun createNotificationChannelGroups(
        packageName: String,
        groups: List<NotificationChannelGroup?>
    ) {
        XLog.d(TAG, "createNotificationChannelGroups() called with: packageName = $packageName, groups = $groups")
        if (isSystemHookReady) {
            groups.forEach {
                runCatching {
                    //JavaCalls.setField(it, "mName", "Mi Push")

                    iNotificationManager.updateNotificationChannelGroupForPackage(packageName, getUid(packageName), it)
                }
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannelGroups(groups)
        }
    }

    fun getNotificationChannelGroup(
        packageName: String,
        groupId: String?
    ): NotificationChannelGroup? {
        XLog.d(TAG, "getNotificationChannelGroup() called with: packageName = $packageName, groupId = $groupId")
        if (isSystemHookReady) {
            return iNotificationManager.getNotificationChannelGroupForPackage(groupId, packageName, getUid(packageName))
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            TODO("compile error")
            //return notificationManager.getNotificationChannelGroup(groupId)
        }
        return null
    }

    fun getNotificationChannelGroups(
        packageName: String
    ): List<NotificationChannelGroup?>? {
        XLog.d(TAG, "getNotificationChannelGroups() called with: packageName = $packageName")
        if (isSystemHookReady) {
            return iNotificationManager.getNotificationChannelGroupsForPackage(packageName, getUid(packageName), false).list
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.notificationChannelGroups
        }
        return emptyList()
    }

    fun deleteNotificationChannelGroup(
        packageName: String,
        groupId: String?
    ) {
        XLog.d(TAG, "deleteNotificationChannelGroup() called with: packageName = $packageName, groupId = $groupId")
        if (isSystemHookReady) {
            iNotificationManager.deleteNotificationChannelGroup(packageName, groupId)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.deleteNotificationChannelGroup(groupId)
        }
    }

    fun areNotificationsEnabled(
        packageName: String
    ): Boolean {
        XLog.d(TAG, "areNotificationsEnabled() called with: packageName = $packageName")
        if (isSystemHookReady) {
            return iNotificationManager.areNotificationsEnabledForPackage(packageName, getUid(packageName))
        }
        return notificationManager.areNotificationsEnabled()
    }

    fun getActiveNotifications(
        packageName: String
    ): Array<StatusBarNotification?>? {
        XLog.d(TAG, "getActiveNotifications() called with: packageName = $packageName")
        if (isSystemHookReady) {
            return iNotificationManager.getAppActiveNotifications(packageName, getUid(packageName)).list.toTypedArray()
        }
        return notificationManager.activeNotifications
    }

}