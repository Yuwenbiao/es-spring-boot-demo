package com.example.demo.entity;

import lombok.Data;

import java.util.List;

/**
 * 新闻搜索结果
 *
 * @author ywb
 * @date 2020/3/8 22:09
 */
@Data
public class NewsSearchResult {
    /**
     * 新闻列表
     */
    private List<News> newsList;
    /**
     * 总数
     */
    private Long totalSize;
}
