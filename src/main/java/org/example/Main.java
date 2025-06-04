package org.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        /**
         * java 언어를 사용하여  웹 Crawling 프로그램 구현.
         * 요구사항 - Start URL 과 Depth 를 파라메터로 입력받아 Start URL HTML 을 분석하여 다른 URL 로 연결되는 링크를 찾으면 해당 URL 로 접근하여 HTML 을 분석하며,
         * 최대 Depth 까지 반복하여 탐색한다. 단 기존에 방문한 URL 이라면 Skip 한다.
         *
         * ex)java -jar Crawling.jar https://www.khan.co.kr 10
         *
         * output
         * https://www.khan.co.kr
         * https://a.b.c
         * https://d.e.f
         * ….. 10 depth
         * https://aa.bb.cc
         * https://dd.ee.ff
         * ….. 10 depth
         *
         * (jsoup 라이브러리를 사용하는 것은 허용 하며, 그 외 다른 라이브러리는 사용불가)
         */

        int currentDepth = 0; // 현재 깊이
        // 이미 한번 접근했던 상위 링크들 저장
        List<String> parentHrefList = new ArrayList<>();
        String startUrl = "https://www.khan.co.kr"; // 나중에 입력받기
        int depth = 3; // 깊이 -> 나중에 입력받기
        
        try {
            
            Document doc = Jsoup.connect(startUrl).get();
            parentHrefList.add(startUrl);
            doc.select("a").forEach(e -> {
                String href = e.attr("href");
                // 제대로 된 하이퍼링크가 아닌지 체크
                if (href.startsWith("http")) {
                    if (!parentHrefList.contains(href)) {
                        System.out.println("href = " + href);
                        parentHrefList.add(href);
                    }
                }
            });
        } catch (Exception e) {
            System.out.println("연결 실패: " + e.getMessage());
        }
    }
}