package io.castled.notifications;

import android.content.Context;

public class CastledNotifications {

    private static CastledNotificationInstance instance;

    public static void initialize(Context context, String instanceId) {

        if (instance == null) {

            instance = new CastledNotificationInstance(context, instanceId);
        }
        else if (!instance.getInstanceId().equals(instanceId)) {

            String errorMessage =
                    "CastledNotifications.start has been called before with a different instanceId! (before: "
                            + instance.getInstanceId() + ", now: " + instanceId + ").";

            throw new IllegalStateException(errorMessage);
        }

        //instance.start();
    }

    static CastledNotificationInstance getInstance() {

        if (instance == null) {

            String errorMessage = "CastledNotifications is not initialized, try calling initialize";
            throw new IllegalStateException(errorMessage);
        }

        return instance;
    }

    public static void setUserId(String userId) {
        instance.setUserId(userId);
    }

    public static void setToken(String token) {
        instance.handleTokenFetch(token);
    }

}