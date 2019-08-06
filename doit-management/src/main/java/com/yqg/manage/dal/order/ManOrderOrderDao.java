package com.yqg.manage.dal.order;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.manage.dal.provider.OrderOrderSqlProvider;
import com.yqg.manage.service.order.request.AssignableOrderRequest;
import com.yqg.manage.service.order.request.CompletedOrderRequest;
import com.yqg.manage.service.order.request.D0OrderRequest;
import com.yqg.manage.service.order.request.ManAllOrdListSearchResquest;
import com.yqg.manage.service.order.request.ManOrderListSearchResquest;
import com.yqg.manage.service.order.request.OrderSearchRequest;
import com.yqg.manage.service.order.request.OverdueOrderRequest;
import com.yqg.manage.service.order.request.PaidOrderRequest;
import com.yqg.manage.service.order.response.AssignableOrderResponse;
import com.yqg.manage.service.order.response.CompletedOrderResponse;
import com.yqg.manage.service.order.response.D0OrderResponse;
import com.yqg.manage.service.order.response.IssueFailedOrderResponse;
import com.yqg.manage.service.order.response.ManAllOrdListResponse;
import com.yqg.manage.service.order.response.ManOrderListResponse;
import com.yqg.manage.service.order.response.OverdueOrderResponse;
import com.yqg.manage.service.order.response.PaidOrderResponse;
import com.yqg.order.entity.OrdOrder;
import org.apache.ibatis.annotations.*;

import java.util.List;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author alan
 */
@Mapper
public interface ManOrderOrderDao extends BaseMapper<OrdOrder> {
//    /*订单列表*/
//    @SelectProvider(type = OrderOrderSqlProvider.class, method = "orderList")
//    @Options(useGeneratedKeys = true)
//    public List<ManOrderListResponse> orderList(@Param("ManOrderListSearchResquest") ManOrderListSearchResquest searchResquest);

    /*全部订单列表*/
    @SelectProvider(type = OrderOrderSqlProvider.class, method = "allOrdList")
    @Options(useGeneratedKeys = true)
    List<ManAllOrdListResponse> allOrdList(@Param("ManAllOrdListSearchResquest") ManAllOrdListSearchResquest searchResquest);

    /*订单列表记数*/
    @SelectProvider(type = OrderOrderSqlProvider.class, method = "orderListCount")
    @Options(useGeneratedKeys = true)
    Integer orderListCount(@Param("ManAllOrdListSearchResquest") ManAllOrdListSearchResquest searchResquest);

    /*计算订单是第几次申请*/
    @Select("select count(1) from ordOrder where userUuid = #{user} and id <= #{id} and disabled = 0 ")
    public Integer orderApplyCountByUser(@Param("user") String userUuid,@Param("id") Integer id);

    @SelectProvider(type = OrderOrderSqlProvider.class, method = "getAssignableOrderList")
    List<AssignableOrderResponse> getAssignableOrderList(
            @Param("AssignableOrderRequest") AssignableOrderRequest request);

    @SelectProvider(type = OrderOrderSqlProvider.class, method = "getCompletedOrderList")
    List<CompletedOrderResponse> getCompletedOrderList(
            @Param("CompletedOrderRequest") CompletedOrderRequest request);


    @SelectProvider(type = OrderOrderSqlProvider.class, method = "getD0OrderList")
    List<D0OrderResponse> getD0OrderList(@Param("D0OrderRequest") D0OrderRequest request);

    @SelectProvider(type = OrderOrderSqlProvider.class, method = "getOverdueOrderList")
    List<OverdueOrderResponse> getOverdueOrderList(
            @Param("OverdueOrderRequest") OverdueOrderRequest request);

    @SelectProvider(type = OrderOrderSqlProvider.class, method = "getPaidOrderList")
    List<PaidOrderResponse> getPaidOrderList(@Param("PaidOrderRequest") PaidOrderRequest request);


    @Select("SELECT "
            + "    o.uuid, "
            + "    o.amountApply, "
            + "    o.borrowingTerm,"
            + "    o.userBankUuid,"
            + "    u.realName,"
            + "    u.userRole,"
            + "    ub.bankNumberNo,"
            + "    ub.bankCardName," +
            "   u.uuid as userUuid "
            + "  FROM "
            + "    ordOrder o,"
            + "    usrUser u,"
            + "    usrBank ub "
            + " where u.uuid = o.userUuid "
            + " and ub.uuid = o.userBankUuid "
            + " and o.status = 16"
            + " and o.disabled=0 ")
    List<IssueFailedOrderResponse> getIssueFailedOrderList();

    @Select("select * from ordOrder where userUuid = #{userUuid} and refundTime < now()")
    List<OrdOrder> getOverDayOrder(@Param("userUuid") String uuid);

    @Select("select * from ordOrder where userUuid = #{userUuid} and disabled=0 order by createTime DESC")
    List<OrdOrder> getOrdersByUserUuid(@Param("userUuid") String uuid);

    @Select("select * from ordOrder where userUuid = (SELECT userUuid from ordOrder where uuid = #{uuid}) and disabled = '0' order by id asc")
    List<OrdOrder> getOrderNoByUserUuid(@Param("uuid") String uuid);

    @Select("select count(*) from doit.manOrderCheckRemark where remark like '%[测试100单免复审]%'")
    Integer getNoCheckCount();
}
