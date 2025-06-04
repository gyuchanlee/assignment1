package org.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Main {

    public static void main(String[] args) {
        System.out.println("과제 1");


        try {
            Document doc = Jsoup.connect("https://www.khan.co.kr").get();
            doc.select("a").forEach(e -> {
                String href = e.attr("href");
                // 제대로 된 하이퍼링크가 아닌지 체크
                System.out.println("href = " + href);
            });
        } catch (Exception e) {
            System.out.println("연결 실패: " + e.getMessage());
        }
    }
}