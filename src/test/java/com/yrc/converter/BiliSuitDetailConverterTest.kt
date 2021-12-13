package com.yrc.converter

import com.yrc.utils.UrlUtil
import org.apache.commons.io.IOUtils
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.nio.file.Paths
import kotlin.io.path.reader
import kotlin.test.assertEquals

internal class BiliSuitDetailConverterTest {
    val apiUrl = "https://api.bilibili.com/x/garb/mall/item/suit/v2"
    val params = mapOf("suitId" to "123")
    val mock:UrlUtil
    val converter: BiliSuitDetailConverter
    init {
        val file = Paths.get("src","test", "resources", "converter", "suitDetail.json")
        val json = IOUtils.toString(file.reader(Charsets.UTF_8))
        mock = Mockito.mock(UrlUtil::class.java)
        Mockito.`when`(mock.getString(mock.buildUrl(apiUrl, params))).thenReturn(json)
        converter = BiliSuitDetailConverter(apiUrl, mock)
    }
    @Test
    fun getItemList(){
        val itemList = converter.getItemList(params, {it["suitId"].orEmpty()})
        assertEquals(24, itemList.size)
    }
}