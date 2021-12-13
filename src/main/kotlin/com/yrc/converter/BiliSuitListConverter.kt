package com.yrc.converter

import com.yrc.utils.UrlUtil

class BiliSuitListConverter(private val apiUrl: String, private val urlUtil: UrlUtil = UrlUtil()) {

    companion object{
        //获取套装列表数据的正则表达式
        const val jsonRegex = "((?<=window.__INITIAL_STATE__=)\\{.*\\})"

        //获取套装id与对应name的正则表达式
        const val itemRegex = "(\"item_id\":[0-9]*,\"name\":\".*?\")"

        //获取套装id的正则表达式
        const val itemIdRegex = "((?<=\"item_id\":)[0-9]*)"

        //获取套装name的正则表达式
        const val nameRegex = "((?<=\"name\":)\".*?\")"
    }
    private fun getItemMap():Map<Int, String>{
        val html = urlUtil.getString(apiUrl)
        val idNameMap = getIdNameMapByString(html)
        println("共找到${idNameMap.size}套套装")
        return idNameMap
    }

    /**
     * 通过返回的页面字符串来获取id，name的map
     * @return
     */
    private fun getIdNameMapByString(html: String): Map<Int, String> {
        //获取json数据
        val json = Regex(jsonRegex).find(html)?.value?:""
        val itemIdRegex = Regex(itemIdRegex)
        val nameRegex = Regex(nameRegex)
        return Regex(itemRegex)
            .findAll(json)
            .map {
                it.value
            }
            .map {
                (itemIdRegex.find(it)?.value?.toInt()) to (nameRegex.find(it)?.value)
            }.filter {
                it.first!=null && it.second!=null
            }.associate {
                (it.first!!) to (it.second!!)
            }
    }
    fun printItemList(itemList:List<Pair<Int,String>>){
        itemList.map {
                (it.first.toString().padStart(5,' ')) to it.second
            }
            .forEach{
            println("套装id: ${it.first}\t  套装名: ${it.second}")
        }
    }
    fun getRegexItemList(regex:String): List<Pair<Int, String>> {
        val itemList = getItemMap().toList()
        val findRegex = Regex(regex)
        return itemList.filter {
                findRegex.containsMatchIn(it.second)
            }.toList()
    }
    fun getItems(): List<Pair<Int, String>> {
        return getItemMap().toList()
    }
}