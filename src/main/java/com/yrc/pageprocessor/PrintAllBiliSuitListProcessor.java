package com.yrc.pageprocessor;

import java.util.Map;

public class PrintAllBiliSuitListProcessor extends AbstractBiliSuitListPageProcessor{
	@Override
	void accept(Map<Integer, String> idNameMap) {
		idNameMap.forEach((id,name)->{
			System.out.printf("套装id: %s\t  套装名: %s\n",id,name);
		});
	}
}
