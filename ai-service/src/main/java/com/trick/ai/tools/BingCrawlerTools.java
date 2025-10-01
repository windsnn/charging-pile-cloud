package com.trick.ai.tools;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class BingCrawlerTools {
    // --- 配置常量 ---
    private static final String BING_SEARCH_URL_TEMPLATE = "https://cn.bing.com/search?q=%s&setlang=zh-CN&ensearch=0";
    private static final String BING_RESULT_SELECTOR = "#b_results > li.b_algo";
    private static final String BING_RESULT_TITLE_SELECTOR = "h2 a";
    private static final String BING_RESULT_LINK_SELECTOR = "h2 a";
    private static final String BING_RESULT_SNIPPET_SELECTOR = ".b_caption p";
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.110 Safari/537.36";
    private static final int TIMEOUT_MS = 10000;
    private static final int MIN_PARAGRAPH_LENGTH = 30;

    /**
     * 根据关键词进行必应搜索。
     *
     * @param query      搜索关键词
     * @param numResults 希望返回的结果数量
     * @return 搜索结果列表。如果搜索失败或没有结果，则返回空列表。
     */
    @Tool(description = "根据关键词进行必应搜索，并指定返回的结果条数")
    public List<SearchResult> bingSearch(String query, int numResults) {
        List<SearchResult> results = new ArrayList<>();
        try {
            // 对查询参数进行URL编码
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            String url = String.format(BING_SEARCH_URL_TEMPLATE, encodedQuery);

            Document doc = buildJsoupConnection(url).get();

            Elements items = doc.select(BING_RESULT_SELECTOR);
            for (Element item : items) {
                if (results.size() >= numResults) break;

                String title = item.select(BING_RESULT_TITLE_SELECTOR).text();
                String link = item.select(BING_RESULT_LINK_SELECTOR).attr("href");
                String snippet = item.select(BING_RESULT_SNIPPET_SELECTOR).text();

                if (!title.isEmpty() && !link.isEmpty()) {
                    results.add(new SearchResult(title, link, snippet));
                }
            }
        } catch (UnsupportedEncodingException e) {
            log.error("UTF-8 encoding not supported, this should never happen.", e);
        } catch (IOException e) {
            // 记录详细的错误日志
            log.error("Failed to perform Bing search for query: '{}'", query, e);
            // 搜索失败时返回空列表，而不是一个包含错误信息的结果
        }
        return results;
    }

    /**
     * 根据URL抓取网页主要内容。
     *
     * @param url 目标网页的URL
     * @return 网页标题和提取的正文内容。抓取失败则返回错误信息。
     */
    @Tool(description = "根据网页URL抓取其主要文本内容")
    public String fetchWebpage(String url) {
        if (url == null || url.isBlank()) {
            return "错误：URL不能为空。";
        }
        try {
            Document doc = buildJsoupConnection(url).get();

            // 尝试更智能地提取内容
            Element mainContent = doc.selectFirst("article, [role=main], #main, #content");
            Elements paragraphs;
            if (mainContent != null) {
                paragraphs = mainContent.select("p");
            } else {
                // 如果找不到主要内容容器，则回退到原始逻辑
                paragraphs = doc.select("p");
            }

            String bodyText = paragraphs.stream()
                    .map(Element::text)
                    .map(String::trim)
                    .filter(text -> text.length() > MIN_PARAGRAPH_LENGTH)
                    .collect(Collectors.joining("\n\n"));

            if (bodyText.isEmpty()) {
                return "无法从页面提取有效内容。页面可能需要JavaScript渲染，或者格式不标准。";
            }

            return "标题: " + doc.title() + "\n\n" + bodyText;

        } catch (IOException e) {
            log.error("Failed to fetch webpage from URL: {}", url, e);
            return "抓取失败: " + e.getMessage();
        } catch (IllegalArgumentException e) {
            log.error("Invalid URL provided: {}", url, e);
            return "无效的URL: " + e.getMessage();
        }
    }

    private Connection buildJsoupConnection(String url) {
        return Jsoup.connect(url)
                .userAgent(USER_AGENT)
                .timeout(TIMEOUT_MS);
    }

    public record SearchResult(String title, String link, String snippet) {
    }
}
