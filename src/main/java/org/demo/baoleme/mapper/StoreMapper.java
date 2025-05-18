package org.demo.baoleme.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.demo.baoleme.pojo.Store;

import java.util.List;

@Mapper
public interface StoreMapper {

    @Select("""
    SELECT id, name, description, location, rating, balance, status, created_at, image
    FROM store
    ORDER BY created_at DESC
    LIMIT #{offset}, #{limit}
""")
    List<Store> selectStoresPaged(@Param("offset") int offset, @Param("limit") int limit);
}
