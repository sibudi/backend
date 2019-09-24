package com.yqg.system.dao;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.system.entity.SysPaymentChannel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created by Didit Dwianto on 2017/11/26.
 */
@Mapper
public interface SysPaymentChannelDao extends BaseMapper<SysPaymentChannel> {

    //  安卓还款(全部通过DOKU)  包括 Alfamart  BCA  PERMATA 和 OtherBanks（走PERMATA）
    // show all syspayment channel for show CIMB
    @Select("SELECT * FROM doit.sysPaymentChannel where disabled = 0 order by remark") //and type in(9,12,14,15)
    List<SysPaymentChannel> getRepaymentChanelListForDOKUNew();

    // 新版p2p还款 不同的放款渠道获取不同的还款渠道 包括BCA->BCA,BNI->BNI,CIMB,BRI和其他银行->CIMB  ,其中BNI使用自己的渠道，其他都使用DOKU
    @Select("SELECT * FROM doit.sysPaymentChannel where disabled = 0 and paymentChannelCode = #{paymentChannelCode}  and  paymentChannelName = #{paymentChannelName} ")
    List<SysPaymentChannel> getRepaymentChanelWithBankCode(@Param("paymentChannelCode")  String paymentChannelCode,@Param("paymentChannelName")  String paymentChannelName);

    // 根据type获取还款渠道
    @Select("SELECT * FROM doit.sysPaymentChannel where disabled = 0 and  type = #{type} ")
    SysPaymentChannel getRepaymentChanelWithType(@Param("type")  String type);
}
