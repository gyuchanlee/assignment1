package org.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;

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
         *
         * 깊이 제한 크롤링 -> 너비 우선 검색 BFS
         * BFS -> 깊이별 순서 보장 : Queue 활용
         *
         * 깊이 제한 검색 DFS -> 최대 깊이까지 한번 끝까지 탐색하고, 계속 반복
         * DFS : Stack 활용
         */


        // 테스트용으로 시작 URL, 최대 깊이를 설정 > 추후 args로 받는걸로 변경
//        String startUrl = "https://www.khan.co.kr";
//        int maxDepth = 2;

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
            return;
        }


        // BFS 를 위한 큐 자료구조
//        Queue<UrlWithDepth> queue = new LinkedList<>();

        // DFS > 스택 구조
        // 스택에서 맨 위에 있는 URL을 가져옴 (LIFO)
        Stack<UrlWithDepth> stack = new Stack<>();



        // 방문한 URL을 저장하기 위한 Set : 중복 방지
        Set<String> visited = new HashSet<>();

        // 시작 URL을 큐에 추가
//        queue.offer(new UrlWithDepth(startUrl, 0));

        // 시작 URL을 스택에 추가
        stack.push(new UrlWithDepth(startUrl, 0));

        visited.add(startUrl);

//        while (!queue.isEmpty()) {
        while (!stack.isEmpty()) {
            // 큐에서 맨 앞에 있는 URL을 가져옴
//            UrlWithDepth current = queue.poll();

            // 스택에서 맨 위의 URL 가져오기
            UrlWithDepth current = stack.pop();

            // 최대 깊이 체크
            if (current.depth > maxDepth) {
                continue;
            }

            // 들여쓰기로 계층표현
            String indent = "  ".repeat(current.depth);
            System.out.println(indent + current.url);

            // 최대 깊이에 도달하면 더 이상 크롤링하지 않음
            if (current.depth >= maxDepth) {
                // 최대 깊이 도달 출력문
                continue;
            }

            try {
                /**
                 * 방문 URL의 연결 링크들 (깊이+1) 수집
                 */

                Document doc = Jsoup.connect(current.url)
                        .timeout(5000) // 5초 타임아웃
                        .get();

                Elements links = doc.select("a[href]");

                for (Element link : links) {
                    String href = link.attr("abs:href");
                    /**
                     * 절대 URL로 변환
                     * 모든 링크가 `http://` 또는 `https://`로 시작하는 완전한 URL 형태로 고정
                     */

                    // 유효한 HTTP/HTTPS URL인지 확인
                    if (isValidUrl(href) && !visited.contains(href)) {
                        visited.add(href);
//                        queue.offer(new UrlWithDepth(href, current.depth + 1));
                        stack.push(new UrlWithDepth(href, current.depth + 1));
                    }
                }

            } catch (IOException e) {
                System.out.println("연결 실패: " + current.url + " - " + e.getMessage());
            }
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
    
    // URL과 깊이를 함께 저장
    static class UrlWithDepth {
        String url;
        int depth;
        
        UrlWithDepth(String url, int depth) {
            this.url = url;
            this.depth = depth;
        }
    }
}