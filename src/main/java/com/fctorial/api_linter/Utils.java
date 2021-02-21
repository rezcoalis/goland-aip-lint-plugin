package com.fctorial.api_linter;

import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.Notifications;
import com.intellij.openapi.ui.MessageType;

public class Utils {
    static NotificationGroup ng = new NotificationGroup("Case Convertor", NotificationDisplayType.BALLOON);
    static void notify(Object msg) {
        String content = msg.toString();
        System.out.println(content);
        Notifications.Bus.notify(ng.createNotification(content, MessageType.INFO));
    }

}
