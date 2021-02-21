package com.fctorial.api_linter;

import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.Notifications;
import com.intellij.openapi.ui.MessageType;

import java.io.IOException;
import java.util.List;

public class Utils {
    static NotificationGroup ng = new NotificationGroup("Case Convertor", NotificationDisplayType.BALLOON);
    static void notify(Object msg) {
        String content = msg.toString();
        System.out.println(content);
        Notifications.Bus.notify(ng.createNotification(content, MessageType.INFO));
    }

    public static void browse(String uri) {
        String os = System.getProperty("os.name").toLowerCase();
        boolean launched = false;
        if (os.contains("win")) {
            Runtime rt = Runtime.getRuntime();
            try {
                rt.exec("rundll32 url.dll,FileProtocolHandler " + uri);
                launched = true;
            } catch (IOException e) {
                launched = false;
            }
        } else if (os.contains("mac")) {
            Runtime rt = Runtime.getRuntime();
            try {
                rt.exec("open " + uri);
                launched = true;
            } catch (IOException e) {
                launched = false;
            }
        } else if (os.contains("nix") || os.contains("nux")) {
            Runtime rt = Runtime.getRuntime();
            String[] browsers = { "xdg-open", "google-chrome", "google-chrome-stable", "chromium", "epiphany", "firefox", "mozilla", "konqueror",
                    "netscape", "opera", "links", "lynx" };

            StringBuffer cmd = new StringBuffer();
            for (int i = 0; i < browsers.length; i++)
                if(i == 0)
                    cmd.append(String.format(    "%s \"%s\"", browsers[i], uri));
                else
                    cmd.append(String.format(" || %s \"%s\"", browsers[i], uri));
            // If the first didn't work, try the next browser and so on

            try {
                rt.exec(new String[] { "sh", "-c", cmd.toString() });
                launched = true;
            } catch (IOException e) {
                launched = false;
            }
        }
        if (!launched) {
            Utils.notify("Couldn't open browser, goto url: " + uri);
        }
    }

    static String[] listToArray(List<String> l) {
        String[] res = new String[l.size()];
        for (int i = 0; i < l.size(); i++) {
            res[i] = l.get(i);
        }
        return res;
    }
}
