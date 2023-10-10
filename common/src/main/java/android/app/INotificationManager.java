package android.app;

import android.content.pm.ParceledListSlice;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.service.notification.StatusBarNotification;

import androidx.annotation.RequiresApi;

public interface INotificationManager extends IInterface {
    abstract class Stub extends Binder implements INotificationManager {
        public static INotificationManager asInterface(IBinder obj)
        {
            throw new RuntimeException("Stub!");
        }
    }

    void enqueueNotificationWithTag(String pkg, String opPkg, String tag, int id, Notification notification, int userId);

    @RequiresApi(30)
    void cancelNotificationWithTag(String pkg, String opPkg, String tag, int id, int userId);

    void cancelNotificationWithTag(String pkg, String tag, int id, int userId);

    boolean areNotificationsEnabledForPackage(String pkg, int uid);

    void createNotificationChannelsForPackage(String pkg, int uid, ParceledListSlice<NotificationChannel> channelsList);

    @RequiresApi(30)
    NotificationChannel getNotificationChannelForPackage(String pkg, int uid, String channelId, String conversationId, boolean includeDeleted);
    NotificationChannel getNotificationChannelForPackage(String pkg, int uid, String channelId, boolean includeDeleted);
    ParceledListSlice<NotificationChannel> getNotificationChannelsForPackage(String pkg, int uid, boolean includeDeleted);
    NotificationChannelGroup getNotificationChannelGroupForPackage(String groupId, String pkg, int uid);
    ParceledListSlice<NotificationChannelGroup> getNotificationChannelGroupsForPackage(String pkg, int uid, boolean includeDeleted);

    void deleteNotificationChannel(String pkg, String channelId);

    void deleteNotificationChannelGroup(String pkg, String channelGroupId);

    void updateNotificationChannelGroupForPackage(String pkg, int uid, NotificationChannelGroup group);

    ParceledListSlice<StatusBarNotification> getAppActiveNotifications(String callingPkg, int userId);

}
