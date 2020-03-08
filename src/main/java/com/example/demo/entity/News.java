package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.frameworkset.orm.annotation.Column;
import com.frameworkset.orm.annotation.ESId;
import lombok.Data;

import java.time.LocalDate;

/**
 * 新闻
 *
 * @author ywb
 * @date 2020/3/8 21:35
 */
@Data
public class News {
    /**
     * 设定文档标识字段
     */
    @ESId(persistent = false)
    private Long id;
    /**
     * 内容
     */
    private String content;
    /**
     * 标题
     */
    private String title;
    /**
     * 作者
     */
    private String author;
    /**
     * 发布时间
     */
    @JsonFormat(pattern = "yyyy/MM/dd")
    @Column(dataformat = "yyyy/MM/dd")
    @JsonProperty(value = "publish_date", access = JsonProperty.Access.WRITE_ONLY)
    private LocalDate publishDate;
    /**
     * 类别
     */
    private String category;
}
