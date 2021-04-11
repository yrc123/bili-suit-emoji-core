package com.yrc.tools;

import com.yrc.pojo.EmojyItem;
import com.yrc.pojo.Item;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;

import java.io.*;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ImageDownloadTool {
	String dirPath;
	List<Item> items = new ArrayList<>();
	ThreadPoolExecutor poolExecutor=null;

	public ImageDownloadTool(List<Item> items,String dirPath) {
		this.items = items;
		this.dirPath=dirPath;
		poolExecutor=new ThreadPoolExecutor(5,5,1, TimeUnit.HOURS,new LinkedBlockingQueue<>());
//		poolExecutor=new ThreadPoolExecutor(5,5,1, TimeUnit.HOURS,new ArrayBlockingQueue<Runnable>(2));
	}
	public void startDownload(){
		for (Item item : items) {
			File dir = new File(dirPath);
			if(!dir.exists()){
				dir.mkdirs();
			}
			System.out.println("开始下载: " + item.getName());
			Runnable imageDownloader = new ImageDownloader(dirPath+item.getName()+".png", item.getImage());
			poolExecutor.execute(imageDownloader);
//			imageDownloader.run();

		}
		poolExecutor.shutdown();
	}
	class ImageDownloader extends Thread{
		String savePath;
		String url;

		public ImageDownloader(String savePath, String url) {
			this.savePath = savePath;
			this.url = url;
		}

		@Override
		public void run() {
			HttpClient client = HttpClientBuilder.create().build();
			HttpGet httpGet = new HttpGet(url);
			HttpResponse response = null;
			InputStream is = null;
			try {
				response = client.execute(httpGet);
				HttpEntity entity = response.getEntity();
				is = entity.getContent();
			} catch (IOException e) {
				System.out.println("连接失败");
				e.printStackTrace();
			}

			BufferedInputStream bis = new BufferedInputStream(is);
			File file = new File(savePath);

			FileOutputStream fos = null;
			byte[] bytes = new byte[4098];
			try {
				fos = new FileOutputStream(file);
				int n=0;
				while((n=bis.read(bytes))!=-1)
				fos.write(bytes,0,n);
				bis.close();
				fos.close();
			} catch (FileNotFoundException e) {
				System.out.println("输出文件夹不存在或权限错误");
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("无法读入图片");
				e.printStackTrace();
			}
		}
	}
}
