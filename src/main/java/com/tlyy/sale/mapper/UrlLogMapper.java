package com.tlyy.sale.mapper;

import com.tlyy.sale.entity.UrlLog;
import org.apache.ibatis.annotations.Insert;

/**
 * @author LeiDongxing
 * created on 2021/6/21
 */
public interface UrlLogMapper {
    @Insert("insert into url_log(id,short_url,ip,create_time) values(#{id},#{shortUrl},#{ip},#{createTime}})")
    int insert(UrlLog urlLog);
}
