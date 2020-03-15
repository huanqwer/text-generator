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
    public String textToImg(String text){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String,Object> map = new LinkedMultiValueMap<>();
        map.add("Content",text);
        map.add("FontInfoId",462);
        map.add("ActionCategory",1);
        map.add("FontSize",24);
        map.add("ImageWidth",300);
        map.add("ImageHeight",100);
        map.add("FontColor","#0000CD");
        map.add("ImageBgColor","#FFFFFF");
        HttpEntity entity = new HttpEntity<>(map,headers);
        String result = restTemplate.postForObject(url, entity, String.class);
        Document document = Jsoup.parse(result);
        Element element = document.getElementById("imgResult");
        String src = element.attr("src");
        return src;
    }
}
