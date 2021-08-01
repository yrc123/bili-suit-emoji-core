package com.yrc.pageprocessor;

import com.alibaba.fastjson.JSONObject;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.CssSelector;
import us.codecraft.webmagic.selector.RegexSelector;
import us.codecraft.webmagic.selector.Selectable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BiliSuitListPageProcessor implements PageProcessor {
	//获取套装列表数据的正则表达式
	final static String dataRegex="((?<=window.__INITIAL_STATE__=)\\{.*\\})";
	//获取套装id与对应name的正则表达式
	final static String itemRegex="(\"item_id\":[0-9]*,\"name\":\".*?\")";
	//获取套装id的正则表达式
	final static String idRegex="((?<=\"item_id\":)[0-9]*)";
	//获取套装name的正则表达式
	final static String nameRegex="((?<=\"name\":)\".*?\")";

	@Override
	public void process(Page page) {
		Map<Integer, String> idNameMap = getIdNameMapByString(page.toString());
		System.out.println("共找到"+idNameMap.size()+"套套装");
		idNameMap.forEach((id,name)->{
			System.out.printf("套装id: %s\t  套装名: %s\n",id,name);
		});
	}

	/**
	 * 通过返回的页面字符串来获取id，name的map
	 * @return
	 */
	private Map<Integer,String> getIdNameMapByString(String html){
		Map<Integer, String> idNameMap = new HashMap<>(200);
		RegexSelector idRegexSelector = new RegexSelector(idRegex);
		RegexSelector nameRegexSelector = new RegexSelector(nameRegex);

		String jsonString = new RegexSelector(dataRegex).select(html);
		List<String> itemList = new RegexSelector(itemRegex).selectList(jsonString);
		for (String item : itemList) {
			Integer id = Integer.valueOf(idRegexSelector.select(item));
			String name = nameRegexSelector.select(item);
			idNameMap.put(id,name);
		}

		return idNameMap;
	}

	@Override
	public Site getSite() {
		Site site = new Site();
		site=site.setRetryTimes(3)
				.setSleepTime(500);
		return site;
	}
}
