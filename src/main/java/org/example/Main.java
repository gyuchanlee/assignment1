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

        // 인수 유효성 체크
        checkArgs(args);

        // 시작 URL, 최대 깊이
        String startUrl = args[0];
        maxDepth = Integer.parseInt(args[1]);

        // 경로 추적을 위한 리스트 초기화 > 탐색 경로 저장
        List<String> currentPath = new ArrayList<>();

        crawlingDFS(startUrl, 0, currentPath);
    }

    public static void crawlingDFS(String url, int depth, List<String> currentPath) {

        // 현재 깊이가 최대 깊이와 같거나 클 경우
        if (depth >= maxDepth) {
            System.out.println(url + " ( 현재 " + depth + " depth)");
            System.out.println("----->  " + depth + " depth");

            // 현재 경로 출력
            for (int i = 0; i < currentPath.size(); i++) {
                System.out.println(currentPath.get(i) + " ( 현재 " + i + " depth)");
            }
            return;

        }


        // 방문했던 URL 스킵
        if (visitedUrls.contains(url)) {
            return;
        }

        // 현재 URL 출력
        System.out.println(url + " ( 현재 " + depth + " depth)");
        // 방문한 URL 기록
        visitedUrls.add(url);

        try {
            Document doc = Jsoup.connect(url).timeout(5000).get();
            Elements links = doc.select("a[href]");

            // 하위 유효 링크 존재 여부
            for (Element link : links) {
                // 절대 주소 값 -> 일관성 유지
                String href = link.attr("abs:href");
                if (isValidUrl(href)) {
                    // 현재 경로에 링크 추가
                    List<String> newPath = new ArrayList<>(currentPath);
                    newPath.add(url);

                    // 다음 깊이 이동 -> 재귀함수 호출
                    crawlingDFS(href, depth + 1, newPath);
                }
            }

            // 분기 별 최대 깊이 탐색 완료 출력문 -> 재귀함수 완료 시점
            System.out.println("-----> " + " " + depth + " depth");

        } catch (IOException e) {
            System.out.println("접속 에러 발생 " + url + e.getMessage());
        } catch (Exception e) {
            System.out.println("예상치 못한 에러 발생 " + url + e.getMessage());
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