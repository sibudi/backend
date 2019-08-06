package com.yqg.drools.extract;

import com.yqg.common.enums.user.CertificationEnum;
import com.yqg.drools.beans.JobData;
import com.yqg.drools.beans.JobData.WorkingExperience;
import com.yqg.drools.beans.JuXinLiData.DataDetail;
import com.yqg.drools.beans.SnsData;
import com.yqg.drools.beans.SnsData.TimeLine;
import com.yqg.drools.model.FaceBookModel;
import com.yqg.drools.model.KeyConstant;
import com.yqg.drools.model.base.RuleSetEnum;
import com.yqg.drools.service.OrderThirdDataService;
import com.yqg.drools.service.UserService;
import com.yqg.drools.utils.DateUtil;
import com.yqg.order.entity.OrdOrder;
import com.yqg.user.entity.UsrStudentDetail;
import com.yqg.user.entity.UsrUser;
import com.yqg.user.entity.UsrWorkDetail;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/*****
 * @Author zengxiangcai
 * Created at 2018/1/31
 * @Email zengxiangcai@yishufu.com
 *
 ****/

@Service
@Slf4j
public class FaceBookExtractor implements BaseExtractor<FaceBookModel> {

    @Autowired
    private OrderThirdDataService orderThirdDataService;

    @Autowired
    private UserService userService;

    @Override
    public boolean filter(RuleSetEnum ruleSet) {
        return RuleSetEnum.FACEBOOK.equals(ruleSet);
    }

    @Override
    public Optional<FaceBookModel> extractModel(OrdOrder order, KeyConstant keyConstant)
        throws Exception {

        DataDetail data = orderThirdDataService
            .getThirdData(order, CertificationEnum.FACEBOOK_IDENTITY);
        if (data == null) {
            return Optional.empty();
        }

        if (data.getReportData() == null || data.getReportData().getSns() == null) {
            log.info("the sns info is empty, orderNo: {}", order.getUuid());
            return Optional.empty();
        }
        SnsData snsData = data.getReportData().getSns();

        FaceBookModel model = new FaceBookModel();

        if (!StringUtils.isEmpty(snsData.getTimeLines())) {

            model.setTotalCommentCount(
                snsData.getTimeLines().stream().filter(elem -> elem.getCommentCount() != null)
                    .mapToInt(
                        TimeLine::getCommentCount).sum());
            model.setTotalLikesCount(
                snsData.getTimeLines().stream().filter(elem -> elem.getLikesCount() != null)
                    .mapToInt(
                        TimeLine::getLikesCount).sum());

            model.setTotalPostCount(
                snsData.getTimeLines().stream().filter(elem -> elem.getPostCount() != null)
                    .mapToInt(
                        TimeLine::getPostCount).sum());

            Date recent2Month = DateUtil
                .addMonth(DateUtil.formatDate(order.getApplyTime(), DateUtil.FMT_MMYYYY), -1);
            List<TimeLine> recent2MonthList = snsData.getTimeLines().stream().filter(
                elem -> elem.getCommentCount() != null && elem.getTimeLineDate() != null
                    && elem.getTimeLineDate().compareTo(recent2Month) >= 0)
                .collect(Collectors.toList());

            model.setRecent2MonthCommentCount(
                recent2MonthList.stream().filter(elem -> elem.getCommentCount() != null)
                    .mapToInt(TimeLine::getCommentCount).sum());

            model.setRecent2MonthLikesCount(
                recent2MonthList.stream().filter(elem -> elem.getLikesCount() != null)
                    .mapToInt(
                        TimeLine::getLikesCount).sum());

            model.setRecent2MonthPostCount(
                recent2MonthList.stream().filter(elem -> elem.getPostCount() != null)
                    .mapToInt(
                        TimeLine::getPostCount).sum());

            Date currentMonth = DateUtil.formatDate(order.getApplyTime(), DateUtil.FMT_MMYYYY);

            List<TimeLine> currentMonthList = snsData.getTimeLines().stream().filter(
                elem -> elem.getCommentCount() != null && elem.getTimeLineDate() != null
                    && elem.getTimeLineDate().compareTo(currentMonth) == 0)
                .collect(Collectors.toList());

            model.setCurrentMonthCommentCount(
                currentMonthList.stream().filter(elem -> elem.getCommentCount() != null)
                    .mapToInt(TimeLine::getCommentCount).sum());

            model.setCurrentMonthLikesCount(
                currentMonthList.stream().filter(elem -> elem.getLikesCount() != null)
                    .mapToInt(
                        TimeLine::getLikesCount).sum());

            model.setCurrentMonthPostCount(
                currentMonthList.stream().filter(elem -> elem.getPostCount() != null)
                    .mapToInt(
                        TimeLine::getPostCount).sum());

            //月均：从第一笔有数据的记录开始
            BigDecimal commentMonthPeriod = BigDecimal.valueOf(snsData.getTimeLines().stream()
                .filter(elem -> elem.getCommentCount() != null && elem.getCommentCount() > 0).count());

            BigDecimal likesMonthPeriod = BigDecimal.valueOf(snsData.getTimeLines().stream()
                .filter(elem -> elem.getLikesCount() != null && elem.getLikesCount() > 0).count());

            BigDecimal postMonthPeriod = BigDecimal.valueOf(snsData.getTimeLines().stream()
                .filter(elem -> elem.getPostCount() != null && elem.getPostCount() > 0).count());

            model.setMonthAverageCommentCount(
                commentMonthPeriod.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO
                    : BigDecimal.valueOf(model.getTotalCommentCount())
                        .divide(commentMonthPeriod, 6, BigDecimal.ROUND_HALF_UP));

            model.setMonthAverageLikesCount(
                likesMonthPeriod.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO
                    : BigDecimal.valueOf(model.getTotalLikesCount())
                        .divide(likesMonthPeriod, 6, BigDecimal.ROUND_HALF_UP));

            model.setMonthAveragePostCount(
                postMonthPeriod.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO
                    : BigDecimal.valueOf(model.getTotalPostCount())
                        .divide(postMonthPeriod, 6, BigDecimal.ROUND_HALF_UP));

            Optional<TimeLine> firstTimeLine = snsData.getTimeLines().stream()
                .filter(elem -> elem.getTimeLineDate() != null && elem.getPostCount() != null
                    && elem.getPostCount() > 0).min(
                    Comparator.comparing(TimeLine::getTimeLineDate));

            if (firstTimeLine.isPresent()) {
                model.setMonthsWithPost(DateUtil
                    .getDiffMonthsIgnoreDays(firstTimeLine.get().getTimeLineDate(),
                        order.getApplyTime())+1);
                Long monthsWithoutPost = snsData.getTimeLines().stream().filter(
                    elem -> elem.getTimeLineDate() != null && elem.getTimeLineDate()
                        .compareTo(firstTimeLine.get().getTimeLineDate()) > 0)
                    .filter(elem -> elem.getPostCount() == null || elem.getPostCount() == 0)
                    .count();
                model.setMonthsWithoutPost(Integer.parseInt(String.valueOf(monthsWithoutPost)));
            }

        } else {
            log.info("the sns timeline is empty, orderNo: {}", order.getUuid());
        }

        if (data.getReportData().getJob() != null) {
            JobData job = data.getReportData().getJob();
            UsrUser user = userService.getUserInfo(order.getUserUuid());
            UsrStudentDetail studentDetail = null;
            UsrWorkDetail workDetail = null;
            if (user.getUserRole() == null) {

            } else if (user.getUserRole() == 1) {
                studentDetail = userService.getUserStudentDetail(order.getUserUuid());
            } else if (user.getUserRole() == 2) {
                workDetail = userService.getUserWorkDetail(order.getUserUuid());
            }

            if (!CollectionUtils.isEmpty(job.getEducationBackGrounds())) {
                //学历匹配
                String academic = studentDetail != null ? studentDetail.getAcademic()
                    : (workDetail != null ? workDetail.getAcademic() : "");
                model.setAcademicDegreeNotSame(academicMapping.get(academic) == null);
            }
            if (!CollectionUtils.isEmpty(job.getWorkingExperiences()) && workDetail != null) {
                //公司名称匹配
                String orderCompany = workDetail.getCompanyName();
                long findCount = job.getWorkingExperiences().stream().filter(
                    elem -> !StringUtils.isEmpty(elem.getCompanyName()) && elem.getCompanyName()
                        .equalsIgnoreCase(orderCompany)).count();
                model.setCompanyNameNotContain(findCount == 0);
                //最早工作时间匹配

                //公司名称相同的最早记录，
                //忽略公司名称的最早记录
                Optional<WorkingExperience> earliestOptional = job.getWorkingExperiences().stream()
                    .filter(elem -> elem.getJobStartDate() != null && elem.getCompanyName() != null
                        && elem.getCompanyName().equalsIgnoreCase(orderCompany))
                    .min(Comparator.comparing(
                        WorkingExperience::getJobStartDate));
                if (!earliestOptional.isPresent()) {
                    earliestOptional = job.getWorkingExperiences().stream()
                        .filter(elem -> elem.getJobStartDate() != null)
                        .min(Comparator.comparing(
                            WorkingExperience::getJobStartDate));

                }
                if (earliestOptional.isPresent()) {
                    model.setDiffDaysBetweenWorkStartAndOrderApply(DateUtil
                        .getDiffDaysIgnoreHours(earliestOptional.get().getJobStartDate(),
                            order.getApplyTime()));
                }

            }

        }

        return Optional.of(model);
    }

    /****
     * facebook学历信息和系统学历映射
     */
    private static Map<String, String> academicMapping = new ConcurrentHashMap<>();

    static {
        academicMapping.put("Sekolah Menengah Atas", "High School");//高中
        academicMapping.put("Sarjana", "College");//本科
        academicMapping.put("Pascasarjana", "Graduate School");//研究生

    }
}
