package com.briup.environment.util;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XMLParse {
    public static Map<String, Map<String, String>> parse() throws DocumentException {
        //容器 数组 集合
        //存储从xml中解析到的数据
        Map<String, Map<String, String>> map = new HashMap<>();
        //1.获取Document对象
        SAXReader reader = new SAXReader();
        //2.读取XML文件
        Document document=
                reader.read(new File("src/main/resources/config.xml"));
        //3.获取根结点
        Element rootElement=document.getRootElement();
        //4.获取根结点下的所有子节点
        List<Element> list=rootElement.elements();
        //往map集合中添加数据
        for(Element element:list){
            String elementName = element.getName();
            String aClass = element.attributeValue("class");
            List<Element> dataElement = element.elements();
            Map<String, String> dataMap = new HashMap<>();
            dataMap.put("class", aClass);
            for (Element data : dataElement) {
                String labelName=data.getName();
                String value=data.getTextTrim();
                dataMap.put(labelName, value);
            }
            map.put(elementName, dataMap);
        }

//        for(Map.Entry<String,Map<String, String>> entry:map.entrySet()){
//            String key = entry.getKey();
//            Map<String, String> value = entry.getValue();
//            System.out.println(key + ":");
//            for (Map.Entry<String, String> stringStringEntry : value.entrySet()) {
//                System.out.println(stringStringEntry.getKey() + ":" + stringStringEntry.getValue());
//            }
//        }

        return map;
    }
//    public static void main(String[] args) throws DocumentException {
//        XMLParse.parse();
//    }
}
