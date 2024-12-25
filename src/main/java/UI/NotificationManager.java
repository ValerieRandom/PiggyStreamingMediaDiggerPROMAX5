package UI;

import org.controlsfx.control.Notifications;

public class NotificationManager {

    /**
     * 顯示一般資訊通知
     */
    public static void showInfoNotification(String title, String content) {
        Notifications.create()
                .title(title)
                .text(content)
                .showInformation();
    }

    /**
     * 顯示錯誤通知
     */
    public static void showErrorNotification(String title, String content) {
        Notifications.create()
                .title(title)
                .text(content)
                .showError();
    }
}
