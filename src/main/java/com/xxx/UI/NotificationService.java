package com.xxx.UI;

import org.controlsfx.control.Notifications;

public class NotificationService implements NotificationManager {
    @Override
    public void showInfoNotification(String title, String content) {
        switch (title) {
            case "SUPPORTED_SITES":
                title = "可支援網站列表";
                content = "點擊字母即可快速查詢此工具是否支援該字母開頭之所有網站。例如：\n"
                        + "- P：支援的網站包含 PornTube PornTop...etc\n";
                break;

            case "CACHE_CLEARED":
                title = "暫存已清空";
                content = "字母列表的資料已刷新，請重新選擇字母按鈕加載最新網站清單！";
                break;

            case "FEATURE_UPDATE":
                title = "功能更新通知";
                content = "我們新增了對更多網站的支援，並優化了下載速度！";
                break;

            default:

                break;
        }

        // 顯示通知
        Notifications.create()
                .title(title)
                .text(content)
                .showInformation();
    }
    @Override
    public void showErrorNotification(String title, String content) {

    }

}