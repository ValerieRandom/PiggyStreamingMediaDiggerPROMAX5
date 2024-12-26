package com.xxx.UI;


public interface NotificationManager {

    // 顯示一般資訊通知
    void showInfoNotification(String title, String content);


    // 顯示錯誤通知
    void showErrorNotification(String title, String content);

}