package com.scj.text.generator.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @Description 文字转手写体图片
 * @Author LaoQin
 * @Date 2020/03/15 22:40
 **/
@Component
public class TextToImg {
    @Autowired
    RestTemplate restTemplate;

    @Value("${url}")
    String url;
    /**
     * @Author 覃岭
     * @Description //TODO 将文字转成手写文字并返回
     * @Date 22:47 2020/03/15
     * @Param [text] 要转写的文字
     * @return java.io.File 返回的文件
     **/
    public InputStream textToImg(String text,Integer fontInfoId,Integer fontSize,Integer imageWidth,Integer ImageHeight,String fontColor) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String,Object> map = new LinkedMultiValueMap<>();
        map.add("Content",text);
        map.add("FontInfoId",fontInfoId);
        map.add("ActionCategory",1);
        map.add("FontSize",fontSize);
        map.add("ImageWidth",imageWidth);
        map.add("ImageHeight",ImageHeight);
        map.add("FontColor",fontColor);
        map.add("ImageBgColor","");
        System.out.println("请求参数:"+map);
        HttpEntity entity = new HttpEntity<>(map,headers);
        String result = restTemplate.postForObject(url, entity, String.class);
        Document document = Jsoup.parse(result);
        Element element = document.getElementById("imgResult");
        String src = element.attr("src");
        //new一个URL对象
        URL url = new URL(src);
        //打开链接
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        //设置请求方式为"GET"
        conn.setRequestMethod("GET");
        //超时响应时间为5秒
        conn.setConnectTimeout(5 * 1000);
        //通过输入流获取图片数据
        InputStream inStream = conn.getInputStream();
        return inStream;
    }
}
