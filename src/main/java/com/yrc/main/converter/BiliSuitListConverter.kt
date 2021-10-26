package com.yrc.main.converter

import com.yrc.pageprocessor.AbstractBiliSuitListPageProcessor
import com.yrc.pojo.SuitListItem
import com.yrc.service.BiliSuitService
import us.codecraft.webmagic.selector.RegexSelector

class BiliSuitListConverter(val service:BiliSuitService) {

    companion object{
        //获取套装列表数据的正则表达式
        val jsonRegex = "((?<=window.__INITIAL_STATE__=)\\{.*\\})"

        //获取套装id与对应name的正则表达式
        val itemRegex = "(\"item_id\":[0-9]*,\"name\":\".*?\")"

        //获取套装id的正则表达式
        val itemIdRegex = "((?<=\"item_id\":)[0-9]*)"

        //获取套装name的正则表达式
        val nameRegex = "((?<=\"name\":)\".*?\")"
    }
    suspend fun start():Map<Int, String>{
        var html = service.getSuitList().string()
        val idNameMap = getIdNameMapByString(html)
        println("共找到" + idNameMap.size + "套套装")

        return idNameMap
    }

    /**
     * 通过返回的页面字符串来获取id，name的map
     * @return
     */
    private fun getIdNameMapByString(html: String): Map<Int, String> {
        val idNameMap: MutableMap<Int, String> = HashMap(200)
        //获取数据
        val json = Regex(jsonRegex).find(html)?.value?:""
        val itemIdRegex = Regex(itemIdRegex)
        val nameRegex = Regex(nameRegex)
        val itemList = Regex(itemRegex)
            .findAll(json)
            .map {
                it.value
            }
            .map {
                SuitListItem(
                    itemIdRegex.find(it)?.value?.toInt()?:Unit,
                    nameRegex.find(it)?.value?:""
            ) }
        for (item in itemList) {
            val id = Integer.valueOf(idRegexSelector.select(item))
            val name = nameRegexSelector.select(item)
            idNameMap[id] = name
        }
        return idNameMap
    }
}