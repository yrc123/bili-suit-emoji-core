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
import java.util.function.BiConsumer;

public class BiliSuitListPageProcessor implements PageProcessor {
	//获取套装列表数据的正则表达式
	final static String dataRegex="((?<=window.__INITIAL_STATE__=)\\{.*\\})";
	//获取套装id与对应name的正则表达式
	final static String itemRegex="(\"item_id\":[0-9]*,\"name\":\".*?\")";
	//获取套装id的正则表达式
	final static String idRegex="((?<=\"item_id\":)[0-9]*)";
	//获取套装name的正则表达式
	final static String nameRegex="((?<=\"name\":)\".*?\")";
	//要执行的策略
	BiConsumer<? super Map,? super List> func;
	//如果是使用搜索，表示要搜索的字符串
	List<String> searchWords;

	public BiliSuitListPageProcessor(BiConsumer<? super Map,? super List> func){
		this.func=func;
	}

	public BiliSuitListPageProcessor(BiConsumer<? super Map,? super List> func, List<String> searchWords) {
		this.func = func;
		this.searchWords = searchWords;
	}

	@Override
	public void process(Page page) {
		Map<Integer, String> idNameMap = getIdNameMapByString(page.toString());
		System.out.println("共找到"+idNameMap.size()+"套套装");

		func.accept(idNameMap,searchWords);
	}

	/**
	 * 输出所有的套装信息
	 * @param idNameMap 所有的套装组成的Map
	 * @param searchWords 无意义
	 */
	public static void printAllSuit(Map<Integer, String> idNameMap,List<String> searchWords){
		idNameMap.forEach((id,name)->{
			System.out.printf("套装id: %s\t  套装名: %s\n",id,name);
		});
	}
	/**
	 * 输出包含关键词的套装信息
	 * @param idNameMap 所有的套装组成的Map
	 * @param searchWords 要搜索的关键词列表
	 */
	public static void searchSuit(Map<Integer,String> idNameMap,List<String> searchWords){
		System.out.println("以下为套装名中包含："+searchWords.toString()+" 的套装");
		for (Map.Entry<Integer, String> entry : idNameMap.entrySet()) {
			Integer id = entry.getKey();
			String name = entry.getValue();
			for (String searchWord : searchWords) {
				if(name.indexOf(searchWord)!=-1){
					System.out.printf("套装id: %s\t  套装名: %s\n",id,name);
					break;
				}
			}
		}
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

	public BiConsumer getFunc() {
		return func;
	}

	public void setFunc(BiConsumer func) {
		this.func = func;
	}

	public List<String> getSearchWords() {
		return searchWords;
	}

	public void setSearchWords(List<String> searchWords) {
		this.searchWords = searchWords;
	}
}
