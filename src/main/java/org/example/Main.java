package org.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;

public class Main {

    // 방문 중복 방지 기록용 HashSet
    private static Set<String> visitedUrls = new HashSet<>();
    private static int maxDepth;

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
         *
         * 깊이 제한 크롤링 -> 너비 우선 검색 BFS
         * BFS -> 깊이별 순서 보장 : Queue 활용
         *
         * 깊이 제한 검색 DFS -> 최대 깊이까지 한번 끝까지 탐색하고, 계속 반복
         * DFS : Stack 활용 or 재귀 함수 구현 및 활용
         */

        // 인수 유효성 체크
        checkArgs(args);

        // 시작 URL, 최대 깊이
        String startUrl = args[0];
        maxDepth = Integer.parseInt(args[1]);

        crawlingDFS(startUrl, 0);
    }

    public static void crawlingDFS(String url, int depth) {
        // 현재 깊이가 최대 깊이보다 클 경우
        if (depth > maxDepth) {
            return;
        }

        // 방문했던 URL 스킵
        if (visitedUrls.contains(url)) {
            return;
        }

        // 현재 URL 출력
        String indent = "    ".repeat(depth);
        System.out.println(indent + url + " ( 현재 " + depth + " depth)");

        // 방문한 URL 기록
        visitedUrls.add(url);

        // 현재 URL의 HTML 문서를 파싱하여 a 태그로 된 링크를 찾는다.
        try {
            Document doc = Jsoup.connect(url).timeout(5000).get();
            Elements links = doc.select("a[href]");

            for (Element link : links) {
                // 절대 주소 값 -> 일관성 유지
                String href = link.attr("abs:href");
                if (isValidUrl(href)) {
                    // 다음 깊이 이동 -> 재귀함수 호출
                    crawlingDFS(href, depth + 1);
                }
            }
        } catch (IOException e) {
            System.out.println(indent + "접속 에러 발생 " + url + e.getMessage());
        } catch (Exception e) {
            System.out.println(indent + "예상치 못한 에러 발생 " + url + e.getMessage());
        }
    }

    private static void checkArgs(String[] args) {
        // 명령행 인수 검증
        if (args.length != 2) {
            System.out.println("사용법: java -jar Crawling.jar <시작URL> <최대깊이>");
            System.out.println("예시: java -jar Crawling.jar https://www.khan.co.kr 2");
            System.exit(1);
        }

        String startUrl;
        int maxDepth;

        // 인수들 검사
        try {
            startUrl = args[0];
            maxDepth = Integer.parseInt(args[1]);

            // URL 유효성 검사
            if (!isValidUrl(startUrl)) {
                System.out.println("Error: 유효하지 않은 URL 입니다.");
                System.exit(1);
            }

            // 깊이 범위 검사
            if (maxDepth < 0 || maxDepth > 100) {
                System.out.println("Error: 최대 깊이는 0~100 사이의 값이어야 합니다.");
                System.exit(1);
            }

        } catch (NumberFormatException e) {
            System.out.println("Error: 최대 깊이는 정수여야 합니다.");
            System.exit(1);
        }
    }

    // 긁어온 a 링크가 유효한 href 인지 확인
    private static boolean isValidUrl(String url) {
        return url != null && 
               !url.isEmpty() && 
               (url.startsWith("http://") || url.startsWith("https://")) &&
               !url.contains("#") && // 앵커 링크 제외
               !url.contains("javascript:") && // 자바스크립트 링크 제외
               !url.endsWith(".pdf") && // PDF 파일 제외
               !url.endsWith(".jpg") && // 이미지 파일 제외
               !url.endsWith(".png") &&
               !url.endsWith(".gif");
    }
}