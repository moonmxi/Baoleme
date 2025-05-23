package org.demo.baoleme.pojo;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
public class Page<E> {
    //当前页码
    private Integer currPage;
    //上一页
    private Integer prePage;
    //下一页
    private Integer nextPage;
    //查询的数据总条数
    private Integer count;
    //当前页的数据内容
    private List<E> list;
    //每页的数据条数
    private Integer pageSize;
    //总页数
    private Integer pageCount;
    //过滤条数
    private Integer filterCount;
}
