第二代 客戶端界面應用程式
===
需搭配採用第二代的[E-learning-Server](https://git.dblab.csie.chu.edu.tw/uelearning/e-learning-server)後端伺服器使用

## 簡介
提供給學生在Android裝置上的操作界面，讓學生能在博物館內利用此應用程式進行學習導引，除了提供教材外，要提供給學生下一個學習點。

## 使用需求
* 作業系統最低需求: Android 3.2
* Wifi or 3G <- 有網路即可
* 相機鏡頭（掃描QR Code時需要）

---

## 開發需求
* 開發採用版本為: Android ５.0 (API Lev 21)
* 採用Android Studio的Gradle架構
* 採用Git Flow進行專案版本控管
    * `develop`為預設開發線，平常開發請在此線進行開發
    * `master`為正式版釋出線
* 使用JavaDoc產生開發文件


## 開發文件
整份專案將用javadoc產生

## 匯入專案之後
1. 將`/app/src/main/java/tw/edu/chu/csie/dblab/uelearning/android/config/Config.java.sample`檔複製成同目錄下的`Config.java`
2. 開啟`Config.java`並根據自己的狀況來修改參數。
3. 看看能不能跑吧～

## 圖示來源
### Logout icon
* 下載來源 <https://www.iconfinder.com/icons/175529/logout_icon>
* By: [Visual Pharm](http://icons8.com/)
