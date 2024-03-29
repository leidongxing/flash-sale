package com.tlyy.sale.api.mapper;

import com.tlyy.sale.api.entity.ItemStock;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface ItemStockMapper {
    String COLUMNS = " id,stock,item_id itemId,gmt_create gmtCreate,gmt_modified gmtModified ";


    @Select("select" + COLUMNS + "from item_stock where item_id =#{itemId} and is_deleted=0")
    ItemStock selectByItemId(@Param("itemId") Long itemId);

    @Select("select" + COLUMNS + "from item_stock where is_deleted=0")
    List<ItemStock> selectAll();

    @Update("update item_stock set stock= stock-#{amount} where item_id=#{itemId} and stock>=#{amount} ")
    int decreaseStockWithVersion(@Param("itemId") Long itemId, @Param("amount") Long amount);

    @Update("update item_stock set stock= stock-#{amount} where item_id=#{itemId}")
    int decreaseStock(@Param("itemId") Long itemId, @Param("amount") Long amount);
}