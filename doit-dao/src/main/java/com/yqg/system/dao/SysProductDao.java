package com.yqg.system.dao;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.system.entity.SysPaymentChannel;
import com.yqg.system.entity.SysProduct;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Didit Dwianto on 2017/12/5.
 */
@Mapper
public interface SysProductDao extends BaseMapper<SysProduct> {

    @Select("select * from sysProduct where disabled = 0 and productCode = #{productUuid}")
    SysProduct getProductInfo(@Param("productUuid") String productUuid);

    @Select("select * from sysProduct where  productCode = #{productUuid}")
    SysProduct getProductInfoIgnorDisabled(@Param("productUuid") String productUuid);

    @Select("select * from sysProduct where disabled = 1 and productLevel = #{productLevel} and dueFeeRate = #{dueFeeRate} ")
    List<SysProduct> getProductConfWithProductLevelAndDuefeeRate(@Param("productLevel") Integer productLevel,@Param("dueFeeRate") String dueFeeRate);

    @Select("select * from sysProduct where disabled = 0  and borrowingAmount =#{borrowingAmount} and borrowingTerm=#{borrowingTerm} and dueFeeRate = #{dueFeeRate}")
    List<SysProduct> getProductByAmountAndTermWithDuefeeRate(@Param("borrowingAmount") BigDecimal borrowingAmount,
                                               @Param("borrowingTerm") int borrowingTerm,@Param("dueFeeRate") String dueFeeRate);

    @Select("select * from sysProduct where borrowingAmount =#{borrowingAmount} and dueFeeRate = #{dueFeeRate} and borrowingTerm=#{borrowingTerm} limit 1")
    SysProduct getProductByForDecreamentCreditLimit(@Param("borrowingAmount") BigDecimal borrowingAmount,
                                                    @Param("dueFeeRate") String dueFeeRate,
                                                    @Param("borrowingTerm") int borrowingTerm);

    @Select("select * from sysProduct where disabled = 0 and  productLevel in (#{productLevel},#{userLevel}) and productLevel != 0 and dueFeeRate = #{dueFeeRate}")
    List<SysProduct> getProductWithProductLevelAndUserLevel(@Param("productLevel") Integer productLevel,@Param("userLevel") Integer userLevel,@Param("dueFeeRate") String dueFeeRate);

    @Select("select * from sysProduct where disabled = 0 and  productLevel in (#{userLevel}) and dueFeeRate = #{dueFeeRate}")
    List<SysProduct> getProductWithProductLevel(@Param("userLevel") Integer userLevel,@Param("dueFeeRate") String dueFeeRate);

    @Select("select * from sysProduct where  productLevel in (#{productLevel},#{userLevel}) and productLevel != 0 and dueFeeRate = #{dueFeeRate}")
    List<SysProduct> getProductWithProductLevelAndUserLevelIgnorDisabled(@Param("productLevel") Integer productLevel,@Param("userLevel") Integer userLevel,@Param("dueFeeRate") String dueFeeRate);

    @Select("select distinct(borrowingAmount) from sysProduct where disabled = 0 order by borrowingAmount; ")
    List<BigDecimal> allSysProduct();

    @Select("select * from sysProduct where  productCode = '' and borrowingAmount<= #{borrowingAmount} order by borrowingAmount DESC limit 1;")
    SysProduct getBlankProductInfoIgnoreDisabled(@Param("borrowingAmount") BigDecimal borrowingAmount);
}
