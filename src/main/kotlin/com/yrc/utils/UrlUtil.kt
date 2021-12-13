package com.yrc.utils

import org.apache.commons.io.IOUtils
import java.io.InputStream
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class UrlUtil {
    fun getString(url:String): String{
        val uri = URI.create(url)
        val response = doGet(uri)
        return IOUtils.toString(response.body(), Charsets.UTF_8)
    }
    fun doGet(uri:URI): HttpResponse<InputStream> {
        val request = HttpRequest
            .newBuilder()
            .GET()
            .uri(uri)
            .build()
        return HttpClient
            .newHttpClient()
            .send(request,
                HttpResponse.BodyHandlers.ofInputStream())
    }
    fun buildUrl(api: String, params: Map<String, String>): String {
        val sb = StringBuilder("$api?")
        params.forEach { k, v ->
            sb.append("${encode(k)}=${encode(v)}&")
        }
        return sb.toString()
    }
    private fun encode(url: String): String {
        return URLEncoder.encode(url, Charsets.UTF_8)
    }
}