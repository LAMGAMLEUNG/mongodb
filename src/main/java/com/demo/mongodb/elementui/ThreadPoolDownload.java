package com.demo.mongodb.elementui;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 多线程下载 ElementUI 离线包
 */
public class ThreadPoolDownload implements Callable<Boolean> {

    private List<String> urls;

    public ThreadPoolDownload(List<String> urls) {
        this.urls = urls;
    }

    @Override
    public Boolean call() throws Exception {
        ElementUIDownload.downLoad(urls);
        return true;
    }

    public static void main(String[] args) {
        ElementUIDownload.mkDirsByUrls(ElementUIDownload.baseUrls, ElementUIDownload.savePath);
        List<ThreadPoolDownload> downloadThreadList = new ArrayList<ThreadPoolDownload>();
        for (String baseUrl : ElementUIDownload.baseUrls) {
            downloadThreadList.add(new ThreadPoolDownload(ElementUIDownload.getAllJS(baseUrl)));
        }
        ExecutorService executorService = Executors.newFixedThreadPool(downloadThreadList.size());
        for (ThreadPoolDownload threadPoolDownload : downloadThreadList) {
            Future<Boolean> f = executorService.submit(threadPoolDownload);
            try {
                if (f.get()) {
                    System.out.println("OK---------------------");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        executorService.shutdown();
    }
}


