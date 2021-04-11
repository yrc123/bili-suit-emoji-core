package com.yrc.pageprocessor;

import com.alibaba.fastjson.JSON;
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
	static final String dataJsonPath = "$.data.suit_items.emoji_package[0].items";
	static final String bgJsonPath = "$.data.suit_items.space_bg[0].properties";
	static final String itemIdJsonPath = "$.item_id";
	static final String nameIdJsonPath = "$.name";
	static final String imageIdJsonPath = "$.properties.image";
	String dirPath;
	public BiliSuitDetailPageProcessor(String dirPath){
		this.dirPath=dirPath;
	}
	@Override
	public void process(Page page) {
		List<String> items = new JsonPathSelector(dataJsonPath).selectList(page.getRawText());
		List<Item> emojyItems= new ArrayList<>();
		for (String item : items) {
			String itemId = new JsonPathSelector(itemIdJsonPath).select(item);
			String name = new JsonPathSelector(nameIdJsonPath).select(item);
			String image = new JsonPathSelector(imageIdJsonPath).select(item);
			EmojyItem emojyItem = new EmojyItem(itemId, name, image);
			emojyItems.add(emojyItem);
		}
		Map bg = (Map) JSON.parse(page.getRawText());
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
		final ImageDownloadTool imageDownloadTool = new ImageDownloadTool(emojyItems, dirPath);
		imageDownloadTool.startDownload();
	}

	@Override
	public Site getSite() {
		Site site = new Site();
		site=site.setRetryTimes(3)
				.setSleepTime(1000);
		return site;
	}
}
