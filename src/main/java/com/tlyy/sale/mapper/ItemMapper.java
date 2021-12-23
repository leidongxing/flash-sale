package com.tlyy.sale.mapper;

import com.tlyy.sale.entity.Item;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface ItemMapper {
    String COLUMNS = " id,title,price,description,sales,img_url imgUrl,gmt_create gmtCreate,gmt_modified gmtModified ";

    @Select("select" + COLUMNS + "from item where id =#{id} and is_deleted=0")
    Item selectById(Long id);

    @Select("select" + COLUMNS + "from item where is_deleted=0")
    List<Item> selectAll();

    @Update("update item set sales= sales+#{amount} where id=#{id}")
    int increaseSales(@Param("id") Long id, @Param("amount") Long amount);
}