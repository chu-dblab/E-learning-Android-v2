/**
 * FileName:	FileUtils.java
 * Description 一切對Android儲存裝置的存取都從這個類別呼叫
 */
package tw.edu.chu.csie.dblab.uelearning.android.util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import tw.edu.chu.csie.dblab.uelearning.android.config.Config;

/**
 * 對Android儲存裝置的檔案存取的專用類別
 * @author ~kobayashi();
 */
public class FileUtils 
{
	private File BasicSDPath;  //SD卡的根目錄（會依照Android版本不同而有所變化）
	private Context BasicInternalPath;		//內部儲存裝置的根目錄(預設是/data/data/[package.name]/files/)
	private ZipEntry entry;
	
	public FileUtils() 
	{
		BasicSDPath = Environment.getExternalStorageDirectory();
	}
	
	/**
	 * 偵測這個裝置有沒有插入記憶卡
	 * @return <code>true</code> 有偵測到記憶卡
	 */
	public boolean isSDCardInsert() 
	{
		if(!BasicSDPath.equals(Environment.MEDIA_REMOVED))
		{
			return true;  //SD卡已經插上去了
		}
		else return false;	//沒插入SD卡
	}
	
	/**
	 * 取得在SD卡上的教材路徑
	 * @return 學習教材在SD卡上的路徑
	 */
	public String getPath()
	{
		if(isSDCardInsert())
		{
			File path = new File(BasicSDPath+"/"+ Config.APP_DIRECTORY);
			if(!path.exists()) 
			{
				path.mkdirs();
				return path.getAbsolutePath();
			}
			else return BasicSDPath+"/"+Config.APP_DIRECTORY;
		}
		else return BasicInternalPath+"/";
	}
	
	// ------------------------------------------------------------------------------------
	/**
	 * 取得此"學習地圖圖檔"路徑
	 * @param context 帶入Android基底Context
	 * @param materialId 此標地的編號
	 * @return 此"學習地圖圖檔"路徑
	 */
	public String getMapFilePath(Context context, int materialId)
	{
		/*ClientDBProvider db = new ClientDBProvider(context);
		
		String query[] = db.search("chu_target", "MapID", "TID="+materialId);
		
		// 如果有任何東西的話
		if(query.length > 0) {
			String fileName = query[0];
			
			return this.getMaterialPath()+fileName;
		}
		else {
			// 沒有查詢到，回傳null
			return null;
		}*/
        return null;
    }
	
	/**
	 * 取得在儲存裝置上的教材路徑
	 * @return		學習教材在儲存裝置上的路徑
	 */
	public String getMaterialPath()
	{
		return this.getPath()+Config.MATERIAL_DIRECTORY;
	}
	
	/**
	 * 取得此"學習點教材"路徑
	 * @param context 帶入Android基底Context
	 * @param materialId 此標地的編號
	 * @return 此"學習點教材"路徑
	 */
	public String getMaterialFilePath(Context context, int materialId)
	{
		/*ClientDBProvider db = new ClientDBProvider(context);
		
		String query[] = db.search("chu_target", "MaterialID", "TID="+materialId);
		
		// 如果有任何東西的話
		if(query.length > 0) {
			String fileName = query[0];
			
			return this.getMaterialPath()+fileName;
		}
		else {
			// 沒有查詢到，回傳null
			return null;
		}*/
        return null;
    }
	
	// ====================================================================================
	/**
	 * 下載檔案時存檔用
	 * @param path 存檔路徑
	 * @param is 檔案輸入串流
	 * @param con 下載檔案的網路連線
	 * @throws java.io.IOException
	 */
	public void saveFile(String path,InputStream is,HttpURLConnection con) throws IOException
	{
		File savePath = new File(path);
		FileOutputStream write = new FileOutputStream(path);
		int str1 = 0;;
		byte[] data = new byte[1024];
		while((str1=is.read(data)) != -1)
		{
			write.write(data,0,str1);
		}
		write.flush();
		write.close();
		is.close();
		con.disconnect();
	}

	/**
	 * 解壓縮檔案
	 * @throws java.io.IOException
	 */
	public void decompressFile() throws IOException
	{
		InputStream is = null;
       BufferedInputStream bi = null;
       BufferedOutputStream bo = null;
		File zipFile = new File(getPath()+Config.ZIP_FILE_NAME_OF_MATERIAL); //取得壓縮檔
		ZipFile unzip = new ZipFile(zipFile);
		Enumeration<? extends ZipEntry> entryEnum  = unzip.entries();   //取得壓縮黨內的第一個目錄或檔案
		
		//如果壓縮黨內還有目錄或檔案的話
		while(entryEnum.hasMoreElements()) {
			entry = entryEnum.nextElement(); //取得下一個檔案或目錄
			//存檔程序
			File outFile = new File(getPath(), entry.getName()); //開啟要存的檔案或目錄
          if(entry.isDirectory()) {  //如果entry的值是一個目錄 
              Log.d("decompress", "Add a folder: " + outFile.getAbsolutePath());
              outFile.mkdir();	//建立資料夾
              if (!outFile.exists()) //確認資料夾是否有建立成功
            	  	Log.e("decompress", "Can't create this path: " + outFile.getAbsolutePath());
            }
          else {	//entry的值是一個檔案
                Log.d("decompress", "Add a file: " + outFile.getAbsolutePath());
                is = unzip.getInputStream(entry);	//取得壓縮檔的輸入串流
                bi = new BufferedInputStream(is);
                bo = new BufferedOutputStream(new FileOutputStream(outFile));
                int data = 0;
                while ((data = bi.read()) != -1) bo.write(data);
                bo.flush();		//將Buffer的空間清空
                bo.close(); bi.close(); is.close();  //關閉所有的I/O串流
            }
		}
		zipFile.delete(); //將壓縮檔刪除
	}
}
