package com.yqg.manage.dal.order;


import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.order.entity.OrdHistory;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @author alan
 */
@Mapper
public interface ManOrderHistoryDao extends BaseMapper<OrdHistory> {

    @Select("<script>"
            + " select o.status,o.orderId,o.statusChangeTime,o.id from ordHistory o where o.disabled = 0 and o.orderId in  "
            + " <foreach collection='orderUUIDs' item='item' separator=',' open='(' close=')'>"
            + " #{item}"
            + " </foreach>"
            + "</script>")
    List<OrdHistory> getOrderHistoryByOrderUUIDs(@Param("orderUUIDs") List<String> orderUUIDs);
}
