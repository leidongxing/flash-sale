package com.tlyy.sale.mapper;

import com.tlyy.sale.entity.Item;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface ItemMapper {
    @Select("select * from item where id =#{id}")
    Item selectById(Long id);

    @Update("update item set sales= sales+#{amount} where id=#{id}")
    int increaseSales(@Param("id")Long id, @Param("amount")Long amount);
}