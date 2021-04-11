package com.yrc.tools;

import com.yrc.pojo.EmojyItem;
import com.yrc.pojo.Item;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Collections;


class ImageDownloadToolTest {
	@Test
	void downloadTest() throws IOException {
		Item item = new EmojyItem("1","test", "http://i0.hdslb.com/bfs/emote/502817b928fde334b35425827064258a604579c4.png");
		ImageDownloadTool downloadTool= new ImageDownloadTool(Collections.singletonList(item), "./img/");
		downloadTool.startDownload();
	}

}