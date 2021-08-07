package com.yrc.pageprocessor;

import java.util.List;
import java.util.Map;

public class SearchBiliSuitListProcessor extends AbstractBiliSuitListPageProcessor{
	List<String> searchWords;
	public SearchBiliSuitListProcessor(List<String> searchWords){
		this.searchWords=searchWords;
	}
	@Override
	void accept(Map<Integer, String> idNameMap) {
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
}
