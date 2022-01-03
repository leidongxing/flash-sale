package com.tlyy.sale.api.mapper;

import com.tlyy.sale.api.entity.ItemOrder;
import org.apache.ibatis.annotations.Insert;

public interface ItemOrderMapper {
    @Insert("insert into item_order(id,user_id,item_id,item_price,amount,order_price,gmt_create,gmt_modified,is_deleted) " +
            "values (#{id},#{userId},#{itemId},#{itemPrice},#{amount},#{orderPrice},#{gmtCreate},#{gmtModified},#{deleted})")
    int insert(ItemOrder record);
}