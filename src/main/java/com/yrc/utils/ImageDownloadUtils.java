package com.yrc.utils;

import com.yrc.pojo.Item;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

public class ImageDownloadUtils {
	String dirPath;
	List<Item> items = new ArrayList<>();
	ThreadPoolExecutor poolExecutor = null;

	public ImageDownloadUtils(List<Item> items, String dirPath) {
		this.items = items;
		this.dirPath = dirPath;
		poolExecutor = new ThreadPoolExecutor(5, 5, 1, TimeUnit.HOURS, new LinkedBlockingQueue<>());
//		poolExecutor=new ThreadPoolExecutor(5,5,1, TimeUnit.HOURS,new ArrayBlockingQueue<Runnable>(2));
	}

	public void startDownload() {
		for (Item item : items) {
			File dir = new File(dirPath);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			System.out.println("开始下载: " + item.getName());
			Runnable imageDownloader = new ImageDownloader(dirPath + filterSpecialChar(item.getName()) + ".png", item.getImage());
			poolExecutor.execute(imageDownloader);
//			imageDownloader.run();

		}
		poolExecutor.shutdown();
	}

	class ImageDownloader extends Thread {
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
				int n = 0;
				while ((n = bis.read(bytes)) != -1)
					fos.write(bytes, 0, n);
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

	public static String filterSpecialChar(String fileName) {
		String s = new String(fileName);
		Pattern pattern = Pattern.compile("[\\s\\\\/:\\*\\?\\\"<>\\|]");
		Matcher matcher = pattern.matcher(s);
		s = matcher.replaceAll("[char]"); // 将匹配到的非法字符以空替换
		return s;
	}
}