package com.yqg.manage.dal.collection;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.manage.dal.provider.QualityCheckSqlProvider;
import com.yqg.manage.entity.collection.ManQualityCheckConfig;
import com.yqg.manage.service.collection.request.ManQualityRecordRequest;
import com.yqg.manage.service.collection.response.ManQualityCountResponse;
import com.yqg.manage.service.collection.response.OutCollectionResponse;
import com.yqg.manage.service.order.request.OverdueOrderRequest;
import com.yqg.order.entity.OrdOrder;
import com.yqg.user.entity.UsrUser;
import org.apache.ibatis.annotations.*;

import java.util.List;

/*****
 * @Author Jeremy Lawrence
 * created at ${date}
 *
 ****/

@Mapper
public interface ManQualityCheckConfigDao extends BaseMapper<ManQualityCheckConfig> {


    /**
     * 获得逾期订单（只查催收人员的订单i）
     * @param searchResquest
     * @return
     */
    @SelectProvider(type = QualityCheckSqlProvider.class, method = "listQualityChecks")
    @Options(useGeneratedKeys = true)
    List<OutCollectionResponse> listQualityChecks(@Param("OverdueOrderRequest") OverdueOrderRequest searchResquest);

    /**
     * 统计数据汇总
     * @return
     */
    @SelectProvider(type = QualityCheckSqlProvider.class, method = "getTotalData")
    @Options(useGeneratedKeys = true)
    List<ManQualityCountResponse> getTotalData(@Param("collectors") String collectors,
                                               @Param("checkerId") Integer checkerId,
                                               @Param("startTime") String startTime, @Param("endTime") String endTime);

    /**
     * 获得详细数据
     * @return
     */
    @SelectProvider(type = QualityCheckSqlProvider.class, method = "getDetailData")
    @Options(useGeneratedKeys = true)
    List<ManQualityCountResponse> getDetailData(@Param("collectors") String collectors,
                                                @Param("checkerId") Integer checkerId,
                                                @Param("startTime") String startTime, @Param("endTime") String endTime);


    /**
     * 获取质检报表sheet3数据
     * @return
     */
    @SelectProvider(type = QualityCheckSqlProvider.class, method = "getQualityDataSheet3")
    @Options(useGeneratedKeys = true)
    List<ManQualityCountResponse> getQualityDataSheet3(@Param("collectors") String collectors,
                                                @Param("checkerId") Integer checkerId,
                                                @Param("startTime") String startTime, @Param("endTime") String endTime);

    @Select("<script> select * from usrUser where uuid in " +
            "<foreach collection='ids' item='item' separator=',' open='(' close=')'> #{item} </foreach> and disabled = 0; </script>")
    List<UsrUser> batchGetUsers(@Param("ids") List<String> ids);

    @Select("<script>select * from ordOrder where uuid in " +
            "<foreach collection='ids' item='item' separator=',' open='(' close=')'> #{item} </foreach> and disabled = 0;</script>")
    List<OrdOrder> batchGetOrders(@Param("ids") List<String> ids);
}
