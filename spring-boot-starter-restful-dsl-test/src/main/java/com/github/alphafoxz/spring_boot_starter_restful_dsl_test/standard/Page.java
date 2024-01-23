package com.github.alphafoxz.spring_boot_starter_restful_dsl_test.standard;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Page<T> {
    /**
     * 当前分页数据泪飙
     */
    private List<T> data;
    /**
     * 当前页码
     */
    private int pageNum;
    /**
     * 每页条数
     */
    private int pageSize;
    /**
     * 数据总数
     */
    private int total;
}