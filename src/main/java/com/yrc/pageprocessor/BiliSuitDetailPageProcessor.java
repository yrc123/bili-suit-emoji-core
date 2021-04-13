package com.yrc.pageprocessor;

import com.alibaba.fastjson.JSON;
import com.jayway.jsonpath.PathNotFoundException;
import com.yrc.pojo.EmojyItem;
import com.yrc.pojo.Item;
import com.yrc.tools.ImageDownloadTool;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.JsonPathSelector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BiliSuitDetailPageProcessor implements PageProcessor {
	static final String itemDataJsonPath = "$.data.suit_items.emoji_package[0].items";
	static final String bgJsonPath = "$.data.suit_items.space_bg[0].properties";
	static final String itemIdJsonPath = "$.item_id";
	static final String nameIdJsonPath = "$.name";
	static final String imageIdJsonPath = "$.properties.image";
	static final String suitStateJsonPath = "$.data.item.state";
	static final String suitNameJsonPath = "$.data.item.name";
	static final String dataJsonPath = "$.data";
	static final String regex="((?<=id=)[0-9]+)";
	static final String emojyApi="https://api.bilibili.com/x/garb/mall/item/suit/v2?item_id=";
	String dirPath;
	String itemId;
	boolean findAll;
	boolean first=true;

	public BiliSuitDetailPageProcessor(String dirPath){
		this.dirPath=dirPath;
		this.findAll=false;
	}
	public BiliSuitDetailPageProcessor(String dirPath,boolean findAll){
		this.dirPath=dirPath;
		this.findAll=findAll;
	}
	@Override
	public void process(Page page) {
		if(findAll==true&&first==true){
			first=false;
			for(int i=1;i<10000;i++){
				page.addTargetRequest(emojyApi+i);
			}
			return;
		}
		String raw = page.getRawText();

		//无套装
		String id=page.getUrl().regex(regex).toString();
		String data = new JsonPathSelector(dataJsonPath).select(raw);
		if(data==null){
			System.out.println("无对应套装：id="+id);
			return;
		}

		String suitState= new JsonPathSelector(suitStateJsonPath).select(raw);
		String suitName=new JsonPathSelector(suitNameJsonPath).select(raw);
		if(suitState.isEmpty()){
			System.out.println("无对应套装：id="+id);
			return;
		}else if(suitState.equals("1")){
			System.out.println("套装失效：id="+id+" name="+suitName);
		}else{
			System.out.println("发现套装：id="+id+" name="+suitName);
		}

		List<String> items = null;
		List<Item> emojyItems= new ArrayList<>();
		try{
			items = new JsonPathSelector(itemDataJsonPath).selectList(raw);
			for (String item : items) {
				String itemId = new JsonPathSelector(itemIdJsonPath).select(item);
				String name = new JsonPathSelector(nameIdJsonPath).select(item);
				String image = new JsonPathSelector(imageIdJsonPath).select(item);
				EmojyItem emojyItem = new EmojyItem(itemId, name, image);
				emojyItems.add(emojyItem);
			}
		}catch (PathNotFoundException e){
			System.out.println("该套装无表情包");
		}
		Map bg = null;
		try {
			bg = (Map) JSON.parse(raw);
			bg= (Map) bg.get("data");
			bg= (Map) bg.get("suit_items");
			List list= (List) bg.get("space_bg");
			bg= (Map) list.get(0);
			bg= (Map) bg.get("properties");
			int cnt=0;
			for (Object o : bg.entrySet()) {
				Map.Entry entry = (Map.Entry) o;
				String key = (String) entry.getKey();
				String value= (String) entry.getValue();
				if(key.matches(".*portrait.*")){
					emojyItems.add(new EmojyItem(null,"bg"+cnt,value));
					cnt++;
				}
			}
		}catch (NullPointerException e){
			System.out.println("该套装无背景图");
		}
		ImageDownloadTool imageDownloadTool =null;
		if(findAll==false){
			if(dirPath==null){
				dirPath="./"+suitName+"/";
			}
			imageDownloadTool=new ImageDownloadTool(emojyItems, dirPath);
		}else{
			imageDownloadTool=new ImageDownloadTool(emojyItems, dirPath+suitName+'/');

		}
		imageDownloadTool.startDownload();
	}

	@Override
	public Site getSite() {
		Site site = new Site();
		site=site.setRetryTimes(3)
				.setSleepTime(500);
		return site;
	}
}
