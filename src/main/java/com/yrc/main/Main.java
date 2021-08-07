package com.yrc.main;

import com.yrc.pageprocessor.BiliSuitDetailPageProcessor;
import com.yrc.pageprocessor.PrintAllBiliSuitListProcessor;
import com.yrc.pageprocessor.SearchBiliSuitListProcessor;
import org.apache.commons.cli.*;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.pipeline.ConsolePipeline;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
	//获取url中的套装id的正则表达式
	static final String regex="((?<=id=)[0-9]+)";
	//套装详情页api
	static final String emojyApi="https://api.bilibili.com/x/garb/mall/item/suit/v2?item_id=";
	//获取套装列表api
	static final String suitListApi="https://www.bilibili.com/h5/mall/home";
	static Options options ;
	public static void main(String[] args) {
		CommandLine commandLine = initArgs(args);
		if(commandLine==null){
			return;
		}
		HelpFormatter helpFormatter = new HelpFormatter();
		Pattern pattern = Pattern.compile(regex);

		String dirPath=null;
		String url=null;
		String itemId=null;

		//获取itemId
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

		if(itemId!=null||commandLine.hasOption("a")){
			//设置保存文件夹
			if(commandLine.hasOption("a")){
				dirPath = "./suitImage";
			}
			if (commandLine.hasOption("d")) {
				dirPath = commandLine.getOptionValue("d");
			}
			if (dirPath!=null&&dirPath.charAt(dirPath.length() - 1) != '/') {
				dirPath += '/';
			}

			if(commandLine.hasOption("u")||commandLine.hasOption("i")) {
				Spider.create(new BiliSuitDetailPageProcessor(dirPath))
						.addUrl(emojyApi + itemId)
						.addPipeline(new ConsolePipeline())
						.setDownloader(new HttpClientDownloader())
						.thread(5)
						.run();
			}else if(commandLine.hasOption("a")){
				System.out.println("暂未完成");
			}
		}else if(commandLine.hasOption("l")){
			Spider.create(new PrintAllBiliSuitListProcessor())
					.addUrl(suitListApi)
					.addPipeline(new ConsolePipeline())
					.setDownloader(new HttpClientDownloader())
					.thread(5)
					.run();
		}else if(commandLine.hasOption("f")){
			List<String> searchWords = new ArrayList<>();
			searchWords.add(commandLine.getOptionValue("f"));
			Spider.create(new SearchBiliSuitListProcessor(searchWords))
					.addUrl(suitListApi)
					.addPipeline(new ConsolePipeline())
					.setDownloader(new HttpClientDownloader())
					.thread(5)
					.run();
		}else{
			helpFormatter.printHelp("bili套装表情下载器",options);
		}
	}
	public static CommandLine initArgs(String[] args) {
		options=new Options();
		HelpFormatter helpFormatter = new HelpFormatter();
		options.addOption("h","help",false,"将此帮助消息输出到输出流");
		options.addOption("l","list",false,"获取套装列表");
		options.addOption("i","id",true,"待爬取的主题item_id（即分享链接后的item_id的值）");
		options.addOption("u","url",true,"待爬取的主题分享链接url");
		options.addOption("a","all",false,"爬取所有套装");
		options.addOption("f","find",true,"搜索套装id");
		options.addOption("d","directory",true,"指定放置生成的类文件的位置");
		CommandLine commandLine= null;
		try {
			DefaultParser parser = new DefaultParser();
			commandLine = parser.parse(options, args);
		} catch (MissingArgumentException e){
			System.out.println("参数不完整");
			helpFormatter.printHelp("bili套装表情下载器",options);
		} catch (ParseException e) {
			System.out.println("参数解析失败");
			helpFormatter.printHelp("bili套装表情下载器",options);
		}
		return commandLine;
	}
}
