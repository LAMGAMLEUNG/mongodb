package com.demo.mongodb.elementui;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * 用于下载 ElementUI 离线包
 * 注意：theme-chalk/fonts 目录下的字体图标无法通过此方法无法下载, 需要到 GitHub 下载字体图标
 * 因为该目录下的字体图标文件无法在线预览, 也就无法下载, GitHub 下载地址见下(需要选择对应版本)
 * https://github.com/ElemeFE/element/tree/v2.15.6/packages/theme-chalk/src/fonts
 */
public class ElementUIDownload {

    static Document document;
    static String savePath = "D:/";
    static Connection connect;
    static String version = "2.15.6";
    static List<String> baseUrls;

    static {
        baseUrls = new ArrayList<String>();
        baseUrls.add("https://unpkg.com/browse/element-ui@" + version + "/lib/");
        baseUrls.add("https://unpkg.com/browse/element-ui@" + version + "/lib/directives/");
        baseUrls.add("https://unpkg.com/browse/element-ui@" + version + "/lib/locale/");
        baseUrls.add("https://unpkg.com/browse/element-ui@" + version + "/lib/locale/lang/");
        baseUrls.add("https://unpkg.com/browse/element-ui@" + version + "/lib/mixins/");
        baseUrls.add("https://unpkg.com/browse/element-ui@" + version + "/lib/theme-chalk/");
        baseUrls.add("https://unpkg.com/browse/element-ui@" + version + "/lib/theme-chalk/fonts/");
        baseUrls.add("https://unpkg.com/browse/element-ui@" + version + "/lib/umd/locale/");
        baseUrls.add("https://unpkg.com/browse/element-ui@" + version + "/lib/utils/");
        baseUrls.add("https://unpkg.com/browse/element-ui@" + version + "/lib/utils/menu/");
        baseUrls.add("https://unpkg.com/browse/element-ui@" + version + "/lib/utils/popup/");
    }

    public static void downLoad(List<String> urls) {
        for (String url : urls) {
            File file = new File(savePath + url.substring(url.indexOf("/element-ui@") + 1));
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                Response response = Jsoup.connect(url).ignoreContentType(true).timeout(100000).execute();
                FileWriter writer = new FileWriter(file);
                writer.write(response.body());
                writer.flush();
                writer.close();
                System.out.println("download OK｡" + url);
            } catch (IOException e) {
                System.err.println("download Failed｡" + url);
                e.printStackTrace();
            }
        }
        System.out.println("download All OK｡");
    }

    public static List<String> getAllJS(String url) {
        List<String> downLoadUrls = new ArrayList<String>();
        try {
            connect = Jsoup.connect(url).ignoreContentType(true);
            document = connect.get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Elements elements = document.select("td .css-xt128v");
        for (Element element : elements) {

            if (!element.absUrl("href").endsWith("/")) {
                downLoadUrls.add(element.absUrl("href").replace("/browse", ""));
            } else {

            }
        }
        return downLoadUrls;
    }

    public static boolean mkDirsByUrls(List<String> urls, String savePath) {
        File file = new File(savePath);
        if (!file.exists()) {
            file.mkdirs();
        }

        for (String url : urls) {
            String dir = savePath + url.substring(url.indexOf("/element-ui"), url.lastIndexOf("/"));
            File saveDir = new File(dir);
            if (!saveDir.exists()) {
                saveDir.mkdirs();
            }
        }
        return true;
    }
}
