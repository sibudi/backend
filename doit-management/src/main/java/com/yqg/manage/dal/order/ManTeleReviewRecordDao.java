package com.yqg.manage.dal.order;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.manage.entity.check.ManTeleReviewQuestion;
import com.yqg.manage.entity.check.ManTeleReviewRecord;
import com.yqg.manage.service.check.response.TeleReviewQuestionResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @Author Jacob
 */
@Mapper
public interface ManTeleReviewRecordDao extends BaseMapper<ManTeleReviewRecord> {

    @Update("update manTeleReviewRecord set disabled = 1 where orderNo = #{orderNo} and disabled = 0 " +
            "and langue = #{langue} and userUuid = #{userUuid} ")
    Integer deleteManTeleResult(@Param("orderNo") String orderNo,
                                @Param("userUuid") String userUuid, @Param("langue") Integer langue);

    @Select("select question, answer, result from manTeleReviewRecord where userUuid = #{uuid} and langue = #{langue} " +
            "and orderNo = #{orderNo} and disabled = 0")
    List<TeleReviewQuestionResponse> getTeleRecord(@Param("uuid") String uuid, @Param("langue") Integer langue,
                                                   @Param("orderNo") String orderNo);

    @Update("update manTeleReviewRecord set disabled = 1 where manOrderRemarkId = #{manOrderRemarkId} ")
    Integer deleteCompanyTele(@Param("manOrderRemarkId") Integer manOrderRemarkId);
}
