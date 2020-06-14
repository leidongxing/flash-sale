package com.tlyy.sale.mapper;

import com.tlyy.sale.entity.ItemStock;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface ItemStockMapper {
    @Select("select * from item_stock where item_id =#{itemId}")
    ItemStock selectByItemId(@Param("itemId") Long itemId);

    @Update("update item_stock set stock= stock-#{amount} where item_id=#{itemId} and stock>=#{amount} ")
    int decreaseStock(@Param("itemId") Long itemId, @Param("amount") Long amount);
}