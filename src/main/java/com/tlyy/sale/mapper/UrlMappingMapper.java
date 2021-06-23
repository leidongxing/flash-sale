package com.tlyy.sale.mapper;

import com.tlyy.sale.entity.UrlMapping;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

/**
 * @author LeiDongxing
 * created on 2021/6/21
 */
public interface UrlMappingMapper {
    @Insert("insert into url_mapping(id,short_url,url,total_visits,ip_visits,expire_time,name,create_id,update_id," +
            "create_time,update_time) values (#{id},#{shortUrl},#{url},#{totalVisits},#{ipVisits},#{expireTime},#{name}," +
            "#{createId},#{updateId},#{createTime},#{updateTime})")
    int insert(UrlMapping urlMapping);

    @Select("select url from url_mapping where short_url=#{shortUrl}")
    String selectUrlByShortUrl(String shortUrl);
}
