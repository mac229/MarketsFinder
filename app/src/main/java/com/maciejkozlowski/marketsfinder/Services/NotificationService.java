package com.maciejkozlowski.marketsfinder.Services;

import android.app.IntentService;
import android.content.Intent;

public class NotificationService extends IntentService {

    public static final String NOTIFICATION_SERVICE_NAME =
            "com.maciejkozlowski.marketsfinder.NOTIFICATION_SERVICE_NAME";

    public NotificationService() {
        super(NOTIFICATION_SERVICE_NAME);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }
}
