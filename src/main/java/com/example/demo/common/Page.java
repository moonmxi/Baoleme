package com.example.demo.common;

import java.util.List;

public class Page<T> {

    // 请求参数
    private int pageNum;     // 当前页码
    private int pageSize;    // 每页数量

    // 响应参数
    private List<T> list;    // 当前页数据列表
    private long total;      // 总记录数
    private int pages;       // 总页数

    // 排序字段（示例："createTime,desc"）
    private String orderBy;

    // 默认构造函数（防止直接实例化）
    public Page() {
    }

    // 分页请求构造函数
    public Page(int pageNum, int pageSize) {
        this(pageNum, pageSize, "");
    }

    public Page(int pageNum, int pageSize, String orderBy) {
        this.pageNum = Math.max(pageNum, 1);
        this.pageSize = pageSize <= 0 ? 10 : pageSize;
        this.orderBy = orderBy;
    }

    // 计算起始行（用于SQL的limit）
    public int getStartRow() {
        return (pageNum - 1) * pageSize;
    }

    // 设置总记录数时自动计算总页数
    public void setTotal(long total) {
        this.total = total;
        this.pages = (int) (total / pageSize + ((total % pageSize == 0) ? 0 : 1));
    }

    // Getters and Setters
    public int getPageNum() {
        return pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public long getTotal() {
        return total;
    }

    public int getPages() {
        return pages;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }
}
