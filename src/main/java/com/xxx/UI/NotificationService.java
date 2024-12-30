package com.xxx.UI;

import org.controlsfx.control.Notifications;

public class NotificationService implements NotificationManager {
    @Override
    public void showInfoNotification(String title, String content) {

        switch (title) {

            case "SUPPORTED_SITES":
                title = "可支援網站列表";
                content = "點擊字母即可快速查詢此工具是否支援該字母開頭之所有網站。\n"
                        + "例如：P：支援的網站包含\n"
                        + "PornTube PornTop..etc\n";
                break;

            case "CACHE_CLEARED":
                title = "暫存已清空";
                content = "字母列表的資料已刷新，請重新選擇字母按鈕加載最新網站清單！";
                break;

            case "DIRECT_DOWNLOAD":
                title = "直接下載方案，不能選畫質";
                content = "嘗試點擊直接下載，這是這個工具最直接簡單的辦法\n"
                        + "如果這也失敗，最佳解：買些烤鴨及炒鴨架去找小豬\n";
                break;

            case "BEST_COMBINATION":
                title = "系統自動解析最頂下載方案";
                content = "大頭熊一放 URL ，小豬就會拿放大鏡東看看西看看，\n"
                        + "然後抓出針對這個 URL 最頂的音訊與影音結合，下載時長可能有點點慢\n"
                        + "但你真的也不能拿小豬怎麼樣\n"
                        + "小豬是覺得這個等待空檔大頭熊可以去抱抱小豬或繼續寫其他頁 deck\n" ;
                break;

            case "START_FORMAT":
                title = "點擊開始解析後才能選擇畫質與影片格式\n";
                content = "如果想要下載自己理想的畫質與格式，大頭熊就先放 URL ! \n"
                        + "然後先點開始解析! 解析完才能下載ㄟ! \n"
                        + "這是規定! (豬蹄揮舞)";
                break;

            case "":
                title = "";
                content = "\n"
                        + "";
                break;

            default:
                break;
        }

        Notifications.create()
                .title(title)
                .text(content)
                .showInformation();
    }
    @Override
    public void showErrorNotification(String title, String content) {

    }

}