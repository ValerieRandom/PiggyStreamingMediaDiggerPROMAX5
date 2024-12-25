# 大壞豬影音挖掘機 PRO - MAX 超強五代

> **版本特色**：本版採用 `bestvideo[ext=mp4] + bestaudio[ext=m4a]` 的方式下載，以確保取得同時包含音訊與影像的 MP4 檔案，再視用戶需求轉檔成不同格式（WMV / MOV）。

## 專案目的

- 讓使用者透過輸入影片連結 (YouTube 等)，並選擇想要的最終格式（MP4、WMV、MOV），系統將自動下載影片並進行轉檔。  
- 解決原本只下載到單一軌（純影像或純音訊）而導致轉檔失敗的問題。  

## 開發環境與技術

- **JavaFX**：用於 GUI 開發。  
- **yt-dlp**：負責從各種影音平台下載影音串流。  
- **ffmpeg**：負責轉檔（支援 MP4、WMV、MOV 等）。  
- **Maven / Gradle**（取決於您實際使用的建置工具）：做為專案管理與依賴管理。  
- **OS 支援**：Windows、macOS （預設偵測 `os.name` 以切換相對應執行檔）。

## 專案結構

```
PiggyStreamingMediaDiggerPROMAX5/
 ├─ TOOLS/                        # 放置 yt-dlp、ffmpeg 等執行檔
 │   ├─ yt-dlp.exe                # Windows 平台用 (若有 mac 則相對應)
 │   ├─ ffmpeg.exe                # Windows 平台用
 │   └─ ... (其他平台可放相應可執行檔)
 ├─ src/
 │   ├─ main/java/
 │   │   ├─ UI/
 │   │   │   ├─ NotificationManager.java  # 統一做提示訊息
 │   │   │   └─ UIBuilder.java            # 架構 GUI 介面
 │   │   └─ com/xxx/piggystreamingmediadiggerpromax5/
 │   │       ├─ Main.java                # 程式進入點
 │   │       ├─ Downloader.java          # 下載&轉檔主要邏輯
 │   │       ├─ Utils.java               # OS 偵測、工具檢查等雜項
 │   │       └─ ...
 │   └─ main/resources/
 │       ├─ com/xxx/piggystreamingmediadiggerpromax5/hello-view.fxml # 若有 FXML
 │       └─ ...
 ├─ .gitignore
 ├─ pom.xml / build.gradle
 ├─ mvnw / mvnw.cmd
 ├─ ...
 └─ README.md
```

## 程式碼邏輯簡介

1. **Main.java**
   - JavaFX Application 進入點：  
     - `start(Stage primaryStage)` 中先偵測作業系統 `osType`。  
     - 檢查 `yt-dlp`、`ffmpeg` 是否存在（藉由 `Utils.ensureDependencies(...)`）。  
     - 建立唯一的 `TextArea logOutput`，傳入 `UIBuilder` 建置主介面。  
   - `primaryStage.show()` 顯示視窗。

2. **UIBuilder.java**
   - 建構使用者介面 (VBox 或其他容器)：  
     - 提供 TextField 讓使用者輸入 `URL`。  
     - 提供 ComboBox 讓使用者選擇最終的影片格式（MP4、WMV、MOV）。  
     - 綁定按鈕事件：  
       - 先呼叫 `Downloader.downloadVideo(...)`：  
         - 下載最佳 MP4 + M4A （`bestvideo[ext=mp4]+bestaudio[ext=m4a] --merge-output-format mp4`）。  
       - 若使用者選擇的格式不是 MP4，則呼叫 `Downloader.convertFormat(...)` 將 MP4 轉成對應格式。

3. **Downloader.java**
   - **`downloadVideo(...)`**：  
     - 執行 `yt-dlp` 指令，強制 `-f "bestvideo[ext=mp4]+bestaudio[ext=m4a]" --merge-output-format mp4`，並輸出至 `PiggyStreamingMediaDigger.mp4`。  
     - 若正常下載，印出「影片下載完成！」；否則印出錯誤代碼。  
   - **`convertFormat(...)`**：  
     - 執行 `ffmpeg -i "input.mp4" -c:v libx264 -c:a aac "output.wmv/mov"`。  
     - 轉檔成功，印出「已完成轉檔」；失敗則顯示 `exit code`。

4. **Utils.java**
   - `detectOS()`：判斷目前作業系統是 Windows、macOS 或其他。  
   - `getToolPath(toolName, osType)`：回傳對應之 `yt-dlp.exe`、`ffmpeg.exe` 檔案路徑。  
   - `ensureDependencies(osType)`：若找不到對應工具就拋出例外。  
   - `showAlert(...)`：在 JavaFX Thread 中顯示簡單警告或訊息。

5. **NotificationManager.java**
   - 使用 `org.controlsfx.control.Notifications` 做彈出訊息或錯誤提示。

## 用途

- **多平台免安裝**：使用者無須手動安裝 yt-dlp / ffmpeg，只需把附帶的執行檔放在 `TOOLS/` 目錄即可。  
- **一鍵下載並轉檔**：填入 YouTube (或其他支持的) 影片 URL；選擇想要的最終格式。  
- **同時下載聲音 + 影片軌**：減少空檔或只下載純音訊檔產生的錯誤。

## 目前遇到的瓶頸與問題

1. **對非 YouTube 平台的支援度**：  
   - `bestvideo[ext=mp4]+bestaudio[ext=m4a]` 主要適用於 YouTube 上常見的 MP4 + M4A 流。  
   - 若使用者想下載其他平台 (例如 NicoNico、Bilibili、Twitter 等)，不一定有相同的 `bestvideo[ext=mp4] + bestaudio[ext=m4a]` 流可用。  
   - 可能導致 `yt-dlp` 報錯「無法找到指定格式」。  
   - **解法**：增加額外判斷，若該平台檢測不到 MP4，就切換 `bestvideo + bestaudio` 或自動挑選可行的格式合併為 MKV。

2. **Exit code 1**：  
   - 有時 `yt-dlp` 或 `ffmpeg` 依然會拋出 `exit code 1`，通常出現在：  
     - 影片被版權保護，或區域封鎖。  
     - 使用者網路環境不穩。  
     - 嘗試下載的流實際上不可用 (空檔或被拒絕)。  
   - 可能需要更多錯誤處理，或允許使用者手動指定其他格式。

3. **畫質無法自訂**：  
   - 由於全部都用 `bestvideo[ext=mp4] + bestaudio[ext=m4a]`，無法讓使用者自選 720p、1080p、4K 等。  
   - 若真的想給使用者一個「畫質控制」的功能，需要把 UI 端與 `Downloader.downloadVideo(...)` 改成可以接收特定代碼 + audio 合併。

4. **其他平台可能要帶 cookies**：  
   - 若影片是私有的、或需要登入，`yt-dlp` 可能需要額外參數 `--cookies-from-browser` 或 `--cookies <cookies.txt>` 才能下載。

## 待辦事項

- [ ] 若要支援更多平台，增加自動檢測並選擇可行影片 + 音訊流的邏輯。  
- [ ] 在 UI 端提供更細緻的錯誤處理 (例如網路失敗時彈出通知，而不是只印日誌)。  
- [ ] 改善畫質選擇：可考慮在 `Downloader.getAvailableQualities()` 裡同時抓到音訊碼，以支援手動合併。  
- [ ] 在 `readme` 或 UI 中提醒使用者：某些影片可能存在版權限制或需要登入。

---

## 使用方式

1. **下載 / clone 專案**：將此專案下載到本地後，進入專案根目錄。  
2. **確認 `TOOLS/` 內有對應平臺的 `yt-dlp` 與 `ffmpeg`**：  
   - Windows：`yt-dlp.exe`、`ffmpeg.exe`  
   - macOS：`yt-dlp`、`ffmpeg` (無 `.exe`)  
3. **執行**：  
   - 以 IDE (IntelliJ/Eclipse/VSCode) 執行 `Main.java`；或編譯後產生 jar，再以 `java -jar` 方式啟動。  
4. **操作流程**：  
   - 輸入影片 URL (最建議 YouTube)  
   - 選擇最終格式 (MP4、WMV、MOV)  
   - 點擊「開始下載」  
   - 程式會先下載 MP4；若需要則轉檔。  
   - 在介面下方 (TextArea) 或 Notification 通知中顯示進度與結果。
