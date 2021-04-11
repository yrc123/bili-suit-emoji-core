package com.yrc.main;

import com.yrc.pageprocessor.BiliSuitDetailPageProcessor;
import org.apache.commons.cli.*;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.pipeline.ConsolePipeline;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
	static final String regex="((?<=id=)[0-9]+)";
	static final String emojyApi="https://api.bilibili.com/x/garb/mall/item/suit/v2?item_id=";
	static Options options ;
	public static void main(String[] args) {
		CommandLine commandLine = initArgs(args);
		HelpFormatter helpFormatter = new HelpFormatter();
		Pattern pattern = Pattern.compile(regex);

		String dirPath=null;
		String url=null;
		String itemId=null;

		if(commandLine.hasOption("u")||commandLine.hasOption("i")){
			if(commandLine.hasOption("u")){
				url=commandLine.getOptionValue("u");
				Matcher matcher = pattern.matcher(url);
				if(matcher.find()){
					itemId=matcher.group();
				}else{
					System.out.println("无法解析url");
				}
			}else{
				itemId=commandLine.getOptionValue("i");
			}
			if(itemId!=null){
				dirPath=itemId;
				if(commandLine.hasOption("d")){
					dirPath=commandLine.getOptionValue("d");
				}
				if(dirPath.charAt(dirPath.length()-1)!='/'){
					dirPath+='/';
				}
				Spider.create(new BiliSuitDetailPageProcessor(dirPath))
						.addUrl(emojyApi+itemId)
						.addPipeline(new ConsolePipeline())
						.setDownloader(new HttpClientDownloader())
						.thread(5)
						.run();
			}
		}else{
			helpFormatter.printHelp("bili套装表情下载器",options);
		}
	}
	public static CommandLine initArgs(String[] args){
		options=new Options();
		options.addOption("h","help",false,"将此帮助消息输出到输出流");
		options.addOption("i","id",true,"待爬取的主题item_id（即分享链接后的item_id的值）\nurl与id输入一个即可");
		options.addOption("u","url",true,"待爬取的主题分享链接url\nurl与id输入一个即可");
		options.addOption("d","directory",true,"指定放置生成的类文件的位置");
		DefaultParser parser = new DefaultParser();
		CommandLine commandLine= null;
		try {
			commandLine = parser.parse(options, args);
		} catch (ParseException e) {
			System.out.println("解析失败");
			e.printStackTrace();
		}
		return commandLine;
	}
}
