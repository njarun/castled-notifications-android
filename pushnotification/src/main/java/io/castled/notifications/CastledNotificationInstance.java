package io.castled.notifications;

import android.content.Context;

import com.google.firebase.messaging.FirebaseMessaging;

import io.castled.notifications.logger.CastledLogger;
import io.castled.notifications.store.CastledInstancePrefStore;
import io.castled.notifications.tasks.ServerTaskHandler;
import io.castled.notifications.tasks.ServerTaskListener;
import io.castled.notifications.tasks.ServerTaskQueue;
import io.castled.notifications.tasks.models.TokenUploadServerTask;
import io.castled.notifications.tasks.models.UserIdSetTask;

public class CastledNotificationInstance {

    private static final String CASTLED_SERVER_TASK_DIR = "castled-notifications";
    private static final String CASTLED_PUSH_TASK_FILE = "push";

    private final CastledLogger logger;
    private final String instanceId;
    private final ServerTaskQueue serverTaskQueue;

    public String getInstanceId() {
        return instanceId;
    }

    CastledNotificationInstance(Context context, String instanceId) {
        this.logger = CastledLogger.getInstance();
        this.instanceId = instanceId;
        CastledInstancePrefStore.init(context, instanceId);

        this.serverTaskQueue = new ServerTaskQueue(CASTLED_SERVER_TASK_DIR, CASTLED_PUSH_TASK_FILE);
        ServerTaskHandler serverTaskHandler = ServerTaskHandler.getInstance(serverTaskQueue);
        ServerTaskListener listener = new ServerTaskListener(serverTaskHandler);
        this.serverTaskQueue.register(listener);
    }

    public void start() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                logger.warning("Fetching FCM registration token failed!");
                return;
            }
            // Get new FCM registration token
            String token = task.getResult();
            logger.debug(token);
            handleTokenFetch(task.getResult());
        });
    }

    public void setUserId(String userId) {
        UserIdSetTask task = new UserIdSetTask(userId);
        serverTaskQueue.add(task);
    }

    private void handleTokenFetch(String token) {
        TokenUploadServerTask tokenUploadServerTask = new TokenUploadServerTask(token);
        serverTaskQueue.add(tokenUploadServerTask);
    }

}