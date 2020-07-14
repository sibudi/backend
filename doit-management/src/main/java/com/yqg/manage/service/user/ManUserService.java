package com.yqg.manage.service.user;

import com.yqg.common.constants.RedisContants;
import com.yqg.common.enums.system.ExceptionEnum;
import com.yqg.common.exceptions.BadRequestException;
import com.yqg.common.exceptions.ServiceExceptionSpec;
import com.yqg.common.models.PageData;
import com.yqg.common.redis.RedisClient;
import com.yqg.common.utils.GetIpAddressUtil;
import com.yqg.common.utils.JsonUtils;
import com.yqg.common.utils.SignUtils;
import com.yqg.common.utils.UUIDGenerateUtil;
import com.yqg.manage.dal.user.ManUserDao;
import com.yqg.manage.dal.user.ReviewerSchedulerDAO;
import com.yqg.manage.entity.user.ManSysRole;
import com.yqg.manage.entity.user.ManSysUserRole;
import com.yqg.manage.entity.user.ManUser;
import com.yqg.manage.enums.DictCollectionEnum;
import com.yqg.manage.enums.ReviewerPostEnum;
import com.yqg.manage.service.review.response.AutoReviewRuleResponse;
import com.yqg.manage.service.user.request.ManSysLoginRequest;
import com.yqg.manage.service.user.request.ManSysUserListRequest;
import com.yqg.manage.service.user.request.ManSysUserRequest;
import com.yqg.manage.service.user.response.*;
import com.yqg.service.system.response.SysDicItemModel;
import com.yqg.service.system.service.SysDicService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author alan
 */
@Component
@Slf4j
public class ManUserService {

    private Logger logger = LoggerFactory.getLogger(ManUserService.class);

    @Autowired
    private ManUserDao manUserDao;

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private ManUserRoleService manUserRoleService;

    @Autowired
    private SysRoleService sysRoleService;

    @Autowired
    private SysDicService sysDicService;

    @Autowired
    private ReviewerSchedulerDAO reviewerSchedulerDao;

    private static final String DEFAULT_PASSWORD = "doit2018";

    /**
     * 管理后台用户登录接口*/
    public ManSysLoginResponse sysLogin(HttpServletRequest request, ManSysLoginRequest sysLoginRequest) throws ServiceExceptionSpec,BadRequestException {

        //判断是否第一次登陆强制修改密码
        if ("doit2018".equals(sysLoginRequest.getPassword())) {
            ManUser search = new ManUser();
            String userName = sysLoginRequest.getUsername();
            search.setDisabled(0);
            search.setStatus(0);
            search.setUsername(userName);
            search.setPassword(SignUtils.generateMd5("doit2018"));
            List<ManUser> manUsers = this.manUserDao.scan(search);
            if (!CollectionUtils.isEmpty(manUsers)) {
                return null;
            }
        }

        //如果登录接口session不为空，判断session是否正确
        String sessionId = String.valueOf(request.getParameter("sessionId"));
        if (StringUtils.isNotBlank(sessionId) && !"null".equals(sessionId)) {

            ManSysLoginResponse response = getUserInfoBySession(sessionId);
            if (response != null ) {
                response.setSessionId(sessionId);
                return response;
            }
        }
        ManUser search = new ManUser();
        String userName = sysLoginRequest.getUsername();
        String passWord = sysLoginRequest.getPassword();
        search.setDisabled(0);
        search.setStatus(0);

        if(StringUtils.isEmpty(userName) || StringUtils.isEmpty(passWord)||
            sysLoginRequest.getThird() == null){
            throw new ServiceExceptionSpec(ExceptionEnum.MANAGE_LOGIN_ERROR);
        }
        //判断用户是否已经登录
//        if (judgeUserLoginOrNot(sysLoginRequest.getUsername())) {
//            throw new ServiceExceptionSpec(ExceptionEnum.MANAGE_HAS_LOGIN);
//        }
        //删除登录用户的token
        deleteUserLoginOrNot(sysLoginRequest.getUsername());

        search.setUsername(userName);
        search.setPassword(SignUtils.generateMd5(passWord));
//        search.setThird(sysLoginRequest.getThird());
        List<ManUser> result = this.manUserDao.scan(search);
        if (CollectionUtils.isEmpty(result) ||
                result.get(0).getThirdPlatform().equals(1)) {
            throw new ServiceExceptionSpec(ExceptionEnum.MANAGE_LOGIN_ERROR);
        }
        ManUser userInfo = result.get(0);

        ManUser updateUser = new ManUser();      //获取登录用户最近一次登录ip，以及登录时间
        updateUser.setLastLoginTime(new Date());
        updateUser.setUuid(userInfo.getUuid());
        String ipAddr = GetIpAddressUtil.getIpAddr(request);
        this.manUserDao.update(updateUser);

        ManSysLoginResponse response = new ManSysLoginResponse();
        response.setRealName(userInfo.getRealname());
        response.setUuid(userInfo.getUuid());
        response.setUserName(userInfo.getUsername());
        response.setId(userInfo.getId());
        response.setCountry(userInfo.getCountry());
        response.setEmployeeNumber(userInfo.getEmployeeNumber());
        response.setVoicePhone(userInfo.getVoicePhone());
        response.setSessionId(this.createLoginSession(response));

        return response;
    }

    /**
     * 管理后台用户退出
     * @param request
     * @return
     * @throws ServiceExceptionSpec
     * @throws BadRequestException
     */
    public void sysLoginOut(HttpServletRequest request) throws ServiceExceptionSpec,BadRequestException {

        String sessionId = request.getParameter("sessionId");

        if (StringUtils.isNotBlank(sessionId)) {
            redisClient.del(RedisContants.MANAGE_SESSION_PREFIX + sessionId);
        }
    }

    /**
     * 新增后台用户
     */
    @Transactional
    public ManUser addSysUser(ManSysUserRequest sysUserRequest) throws ServiceExceptionSpec,BadRequestException {
        if(StringUtils.isEmpty(sysUserRequest.getRealname()) ||
                StringUtils.isEmpty(sysUserRequest.getUsername()) ||
                StringUtils.isEmpty(sysUserRequest.getRoleIds()) ||
                StringUtils.isEmpty(sysUserRequest.getRemark()) ||
                StringUtils.isEmpty(sysUserRequest.getMobile()) ||
                sysUserRequest.getThird() == null||
                sysUserRequest.getStatus() == null){
            throw new ServiceExceptionSpec(ExceptionEnum.MANAGE_ADD_USER_ERROR);
        }
        ManUser addUser = new ManUser();
        addUser.setDisabled(0);
        addUser.setUsername(sysUserRequest.getUsername());

        List<ManUser> result = this.manUserDao.scan(addUser);
        if (!CollectionUtils.isEmpty(result)) {
            throw new ServiceExceptionSpec(ExceptionEnum.MANAGE_ADD_USER_ERROR);
        }

        addUser.setPassword(SignUtils.generateMd5(DEFAULT_PASSWORD));
        addUser.setCreateTime(new Date());
        addUser.setStatus(0);
        addUser.setRealname(sysUserRequest.getRealname());
        addUser.setRemark(sysUserRequest.getRemark());
        addUser.setThird(sysUserRequest.getThird());
        addUser.setUuid(UUIDGenerateUtil.uuid());
        addUser.setUpdateTime(new Date());
        addUser.setLastLoginTime(new Date());
        addUser.setMobile(sysUserRequest.getMobile());
        addUser.setThirdPlatform(sysUserRequest.getThirdPlatform() ? 1: 0);
        addUser.setCollectionPhone(sysUserRequest.getCollectionPhone());
        addUser.setCollectionWa(sysUserRequest.getCollectionWa());
        addUser.setEmployeeNumber(sysUserRequest.getEmployeeNumber());
        addUser.setVoicePhone(sysUserRequest.getVoicePhone());
        Integer success = this.manUserDao.insert(addUser);
        //rizky addUser updated immediately after insert is successful,get id afterward
        if(success.equals(1))
            this.manUserRoleService.addUserRoleLink(sysUserRequest.getRoleIds(), addUser.getId());
        else
            throw new ServiceExceptionSpec(ExceptionEnum.MANAGE_ADD_USER_ERROR);
        return addUser;
    }

    /**
     * 修改后台用户*/
    @Transactional
    public ManUser editSysUser(ManSysUserRequest sysUserRequest) throws ServiceExceptionSpec {
        if(StringUtils.isEmpty(sysUserRequest.getRealname()) ||
                StringUtils.isEmpty(sysUserRequest.getUsername()) ||
                StringUtils.isEmpty(sysUserRequest.getRoleIds()) ||
                sysUserRequest.getStatus() == null || sysUserRequest.getId() == null){
            throw new ServiceExceptionSpec(ExceptionEnum.MANAGE_ADD_USER_ERROR);
        }
        ManUser editUser = new ManUser();
        editUser.setUsername(sysUserRequest.getUsername());
        editUser.setRealname(sysUserRequest.getRealname());
        editUser.setStatus(sysUserRequest.getStatus());
        editUser.setId(sysUserRequest.getId());
        editUser.setUpdateTime(new Date());
        editUser.setMobile(sysUserRequest.getMobile());
        editUser.setRemark(sysUserRequest.getRemark());
        editUser.setCollectionWa(sysUserRequest.getCollectionWa());
        editUser.setCollectionPhone(sysUserRequest.getCollectionPhone());
        editUser.setEmployeeNumber(sysUserRequest.getEmployeeNumber());
        editUser.setVoicePhone(sysUserRequest.getVoicePhone());
        this.manUserDao.update(editUser);

        this.manUserRoleService.delUserRoleLink(editUser.getId());
        this.manUserRoleService.addUserRoleLink(sysUserRequest.getRoleIds(),editUser.getId());
        return editUser;
    }

    /**
     * 查询用户列表
     * */
    public PageData sysUserList(ManSysUserListRequest userListRequest)
            throws ServiceExceptionSpec {
        PageData response = new PageData();

        List<ManUser> result;
        //如果需要将是否在线作为查询条件，分开进行
        if (userListRequest.getOnlineOrNot() != null
                && !userListRequest.getOnlineOrNot().equals(0)) {
            result = this.manUserDao.sysUserList(userListRequest);
            List<String> loginNames = getAllUserName();
            if (userListRequest.getOnlineOrNot().equals(1)) {
                result = result.stream().filter(elem -> {
                    return loginNames.contains(elem.getUsername());
                }).collect(Collectors.toList());
            } else {
                result = result.stream().filter(elem -> {
                    return !loginNames.contains(elem.getUsername());
                }).collect(Collectors.toList());
            }
            response.setRecordsTotal(result.size());
            //取得分页数据
            result = result.stream().skip((userListRequest.getPageNo() -1) * userListRequest.getPageSize())
                    .limit(userListRequest.getPageSize()).collect(Collectors.toList());
        } else {
            result = this.manUserDao.sysUserList(userListRequest);
            Integer sysUserListCount = this.manUserDao.sysUserListCount(userListRequest);
            response.setRecordsTotal(sysUserListCount);
        }
        List<ManSysUserListResponse> responseData = getManSysUserListResponses(userListRequest, result);
        response.setData(responseData);
        response.setPageSize(userListRequest.getPageSize());
        response.setPageNo(userListRequest.getPageNo());
        return response;
    }

    private List<ManSysUserListResponse> getManSysUserListResponses(ManSysUserListRequest userListRequest, List<ManUser> result) {
        List<ManSysUserListResponse> responseData = new ArrayList<>();
        for(ManUser user:result){
            ManSysUserListResponse userCell = new ManSysUserListResponse();
            /*通过用户id查出对应的角色id*/
            List<ManSysUserRole> roleResult = this.manUserRoleService.userRoleListByUserId(user.getId());
            List<String> roleString = new ArrayList<>();
            /*通过角色id查出角色名称*/
            for(ManSysUserRole item:roleResult){
                Integer roleId = item.getRoleId();
                List<ManSysRole> roleName = this.sysRoleService.sysRoleListById(roleId);
                if(!CollectionUtils.isEmpty(roleName)){
                    String temp = "";
                    if (userListRequest.getLanuge() != null
                            && userListRequest.getLanuge().equals(2)) {
                        temp = String.valueOf(roleId)+"|"+roleName.get(0).getRemark();
                    } else {
                        temp = String.valueOf(roleId)+"|"+roleName.get(0).getRoleName();
                    }
                    roleString.add(temp);
                }
            }
            //判断是否在线
            if (userListRequest.getOnlineOrNot() != null
                    && !userListRequest.getOnlineOrNot().equals(0)) {
                userCell.setOnlineOrNot(userListRequest.getOnlineOrNot().equals(1)? true : false);
            } else {
                userCell.setOnlineOrNot(this.judgeUserLoginOrNot(user.getUsername()));
            }
            userCell.setCreateTime(user.getCreateTime());
            userCell.setId(user.getId());
            userCell.setRealname(user.getRealname());
            userCell.setUsername(user.getUsername());
            userCell.setUuid(user.getUuid());
            userCell.setRoles(StringUtils.join(roleString.toArray(),","));
            userCell.setStatus(user.getStatus());
            userCell.setMobile(user.getMobile());
            userCell.setRemark(user.getRemark());
            userCell.setCollectionPhone(user.getCollectionPhone());
            userCell.setCollectionWa(user.getCollectionWa());
            userCell.setEmployeeNumber(user.getEmployeeNumber());
            userCell.setVoicePhone(user.getVoicePhone());
            responseData.add(userCell);
        }
        return responseData;
    }

    /**
     * 修改用户的密码
     */
    public void updateSysUserPasswd(ManSysUserRequest userRequest)
            throws Exception {
        if(StringUtils.isEmpty(userRequest.getUuid()) || StringUtils.isEmpty(userRequest.getOldPassword()) ||
                StringUtils.isEmpty(userRequest.getNewPassword())){
            throw new ServiceExceptionSpec(ExceptionEnum.MANAGE_EDIT_PASSWORD_ERROR);
        }
        ManUser search = new ManUser();
        search.setUuid(userRequest.getUuid());
        search.setPassword(SignUtils.generateMd5(userRequest.getOldPassword()));
        List<ManUser> searchResult = this.manUserDao.scan(search);
        if(searchResult.size() <= 0){
            throw new ServiceExceptionSpec(ExceptionEnum.MANAGE_EDIT_PASSWORD_ERROR);
        }
        search.setPassword(SignUtils.generateMd5(userRequest.getNewPassword()));
        search.setUpdateTime(new Date());
        this.manUserDao.update(search);
    }

    /**
     * 查询所有用户列表*/
    public List<ManUser> manUserList(ManSysUserRequest userRequest) throws ServiceExceptionSpec {
        List<ManUser> userList = this.manUserDao.manUserTotalList();
        return userList;
    }

    /**
     * 通过remark查询后台用户列表*/
    public List<ManUser> manUserListByRemark(ManSysUserRequest userRequest) throws ServiceExceptionSpec {
        if(StringUtils.isEmpty(userRequest.getRemark())){
            throw new ServiceExceptionSpec(ExceptionEnum.MANAGE_SEARCH_ERROR);
        }

        ManUser search = new ManUser();
        search.setRemark(userRequest.getRemark());
        search.setDisabled(0);
        List<ManUser> result = this.manUserDao.scan(search);
        return result;
    }

    /**
     * 创建后台用户登陆sessionId
     */
    private String createLoginSession(ManSysLoginResponse response) throws ServiceExceptionSpec {
        String sessionId = UUIDGenerateUtil.uuid();
        redisClient.set(RedisContants.MANAGE_SESSION_PREFIX + sessionId,
                JsonUtils.serialize(response),RedisContants.MANAGE_SESSION_EXPIRE);
        return sessionId;
    }

    /**
     * 通过用户名称判断用户是否已经登录
     * @param userName
     * @return
     */
    public Boolean judgeUserLoginOrNot(String userName) {

        Set keys = redisClient.keys(RedisContants.MANAGE_SESSION_PREFIX +  "*");

        Iterator iterator = keys.iterator();
        while (iterator.hasNext()) {
            String elem = String.valueOf(iterator.next());
            String userInfo = redisClient.get(elem);
            if (StringUtils.isEmpty(userInfo)) {
                continue;
            }
            ManSysLoginResponse result = JsonUtils.deserialize(userInfo,ManSysLoginResponse.class);
            if (userName.equals(result.getUserName())) {
                return true;
            }
        }
        return false;
    }
    /**
     * 通过用户名称判断用户，然后将其删除
     * @param userName
     * @return
    */
    public void deleteUserLoginOrNot(String userName) {

        Set keys = redisClient.keys(RedisContants.MANAGE_SESSION_PREFIX +  "*");

        Iterator iterator = keys.iterator();
        while (iterator.hasNext()) {
            String elem = String.valueOf(iterator.next());
            String userInfo = redisClient.get(elem);
            if (StringUtils.isEmpty(userInfo)) {
                continue;
            }
            ManSysLoginResponse result = JsonUtils.deserialize(userInfo,ManSysLoginResponse.class);
            if (userName.equals(result.getUserName())) {
                redisClient.del(elem);
            }
        }
    }

    /**
     * 获得所有登录的用户名
     * @return
     */
    public List<String> getAllUserName() {

        List<String> names = new ArrayList<>();
        Set keys = redisClient.keys(RedisContants.MANAGE_SESSION_PREFIX +  "*");

        Iterator iterator = keys.iterator();
        while (iterator.hasNext()) {
            String elem = String.valueOf(iterator.next());
            String userInfo = redisClient.get(elem);
            if (StringUtils.isEmpty(userInfo)) {
                continue;
            }
            ManSysLoginResponse result = JsonUtils.deserialize(userInfo,ManSysLoginResponse.class);
            names.add(result.getUserName());
        }
        return names;
    }

    /**
     * 通过姓名查询当前登录的sessionId
     * @param userName
     * @return
     */
    public String getSessionIdByName(String userName) {

        Set keys = redisClient.keys(RedisContants.MANAGE_SESSION_PREFIX +  "*");

        Iterator iterator = keys.iterator();
        while (iterator.hasNext()) {
            String elem = String.valueOf(iterator.next());
            String userInfo = redisClient.get(elem);
            if (StringUtils.isEmpty(userInfo)) {
                continue;
            }
            ManSysLoginResponse result = JsonUtils.deserialize(userInfo,ManSysLoginResponse.class);
            if (userName.equals(result.getUserName())) {
                return elem;
            }
        }
        return "";
    }

    /**
     * 通过用户sessionId查询用户id*/
    public Integer getUserIdBySession(String sessionId) {
        String userInfo = redisClient.get(RedisContants.MANAGE_SESSION_PREFIX+sessionId);

        if (StringUtils.isBlank(userInfo)) {
            return null;
        }
        ManSysLoginResponse result = JsonUtils.deserialize(userInfo,ManSysLoginResponse.class);
        return result.getId();
    }

    /**
     * 通过用户session查询用户信息*/
    public ManSysLoginResponse getUserInfoBySession(String sessionId) throws ServiceExceptionSpec {
        String userInfo = redisClient.get(RedisContants.MANAGE_SESSION_PREFIX+sessionId);

        ManSysLoginResponse result = new ManSysLoginResponse();
        if(StringUtils.isNotBlank(userInfo)){
            result = JsonUtils.deserialize(userInfo,ManSysLoginResponse.class);
        }
        return result;
    }

    public List<ManSysUserResponse> getUsersByRole(int roleId){
        List<ManUser> userList = manUserDao.getSystemUsersByRole(roleId);
        if(CollectionUtils.isEmpty(userList)){
            return new ArrayList<>();
        }
        return userList.stream().map(elem->ManSysUserResponse.buildResponseUserProfileFromUserDetail(elem)).collect(Collectors.toList());
    }


    /**
     * 判断用户名称是否重复
     * @param userName
     * @return
     */
    public ManSysRoleNameResponse judgeUserName(String userName) throws ServiceExceptionSpec {

        if (StringUtils.isEmpty(userName)) {
            throw new ServiceExceptionSpec(ExceptionEnum.MANAGE_EDIT_PASSWORD_ERROR);
        }
        ManUser search = new ManUser();
        search.setUsername(userName);
        search.setDisabled(0);
        List<ManUser> rList = manUserDao.scan(search);

        ManSysRoleNameResponse response = new ManSysRoleNameResponse();
        if (CollectionUtils.isEmpty(rList)) {
            response.setRepeat(false);
        } else {
            response.setRepeat(true);
        }
        return response;

    }

    /**
     * 重置密码为默认
     * @param request
     * @throws ServiceExceptionSpec
     * @throws BadRequestException
     */
    public void resetPassword(AutoReviewRuleResponse request) throws ServiceExceptionSpec, BadRequestException {

        if (request.getId() == null) {
            throw new ServiceExceptionSpec(ExceptionEnum.MANAGE_EDIT_PASSWORD_ERROR);
        }

        ManUser user = new ManUser();
        user.setId(request.getId());
        user.setPassword(SignUtils.generateMd5(DEFAULT_PASSWORD));
        manUserDao.update(user);
    }

    public List<ReviewerCollectionResponse> getReviewerList() {
        List<SysDicItemModel> allReviewerRoleDicInfo = getAllReviewerRoleDicItems();
        if (CollectionUtils.isEmpty(allReviewerRoleDicInfo)) {
            return null;
        }

        List<ReviewerCollectionResponse> resultList = new ArrayList<>();

        //字典数据保存的时候按照postEnglishName#roleId形式，postEnglishName对应于ReviewerPostEnum
        allReviewerRoleDicInfo.stream().forEach(elem -> {
            String[] roleInfoArr = elem.getDicItemValue().split("#");
            ReviewerCollectionResponse collectionResponse = new ReviewerCollectionResponse();
            collectionResponse.setPostEnglishName(roleInfoArr[0]);
            collectionResponse.setPostName(elem.getDicItemName());
            int roleId = Integer.valueOf(roleInfoArr[1]);
            List<ManUser> userList = reviewerSchedulerDao.getCurrentReviewersByRole(roleId,roleInfoArr[0]);
            if (CollectionUtils.isEmpty(userList)) {
                collectionResponse.setReviewers(new ArrayList<>());
            } else {
                collectionResponse.setReviewers(userList.stream().map(
                        sysUser -> ManSysUserResponse.buildResponseUserProfileFromUserDetail(sysUser))
                        .collect(Collectors.toList()));
            }
            resultList.add(collectionResponse);
        });
        return resultList;
    }

    public List<ManUser> getSysUserByIds(List<Integer> sysUserIds){
        return manUserDao.getSysUserByIds(sysUserIds);
    }


    /***
     * 根据岗位名称查询岗位对应角色id
     * @param postName
     * @return
     */
    public String getReviewerRoleInfoFromDict(ReviewerPostEnum postName) {
        try {
            List<SysDicItemModel> dicItemList = getAllReviewerRoleDicItems();
            if (CollectionUtils.isEmpty(dicItemList)) {
                return null;
            }
            Optional<SysDicItemModel> postRoleRelationInfo = dicItemList.stream()
                    .filter(elem -> elem.getDicItemValue().startsWith(postName.name())).findFirst();
            if (postRoleRelationInfo.isPresent()) {
                return postRoleRelationInfo.get().getDicItemValue();
            }
        } catch (Exception e) {
            log.error("get dicItem error for postName=" + postName, e);
        }
        return null;
    }

    /****
     * 获取所有审核岗的角色信息
     * @return
     */
    public List<SysDicItemModel> getAllReviewerRoleDicItems() {
        try {
            List<SysDicItemModel> dicItemList = sysDicService.
                    sysDicItemsListByDicCode(DictCollectionEnum.REVIEWER_ROLE_RELATION.name());
            return dicItemList;
        } catch (Exception e) {
            log.error("get allReviewerRoleIds error");
        }
        return null;
    }

    public List<ManSysUserResponse> getCurrentReviewersByPostName(ReviewerPostEnum postName){

        List<ReviewerCollectionResponse> responseList = this.getReviewerList();

        if(CollectionUtils.isEmpty(responseList)){
            return new ArrayList<>();
        }

        Optional<ReviewerCollectionResponse> allUser = responseList.stream().filter(elem->elem.getPostEnglishName().equals(postName.name())).findFirst();
        if(allUser.isPresent()){
            return  allUser.get().getReviewers();
        }
        return new ArrayList<>();
    }

    /**
     * 根据用户名，强制修改密码
     * @param userRequest
     */
    public Integer forceChagePassword(ManSysUserRequest userRequest) throws ServiceExceptionSpec, BadRequestException {

        if(StringUtils.isEmpty(userRequest.getUsername()) || StringUtils.isEmpty(userRequest.getOldPassword()) ||
                StringUtils.isEmpty(userRequest.getNewPassword())){
            throw new ServiceExceptionSpec(ExceptionEnum.MANAGE_EDIT_PASSWORD_ERROR);
        }
        ManUser search = new ManUser();
//        search.setUuid(userRequest.getUuid());
        search.setUsername(userRequest.getUsername());
        search.setPassword(SignUtils.generateMd5(userRequest.getOldPassword()));
        List<ManUser> searchResult = this.manUserDao.scan(search);
        if(searchResult.size() <= 0){
            throw new ServiceExceptionSpec(ExceptionEnum.MANAGE_EDIT_PASSWORD_ERROR);
        }
        search = searchResult.get(0);
        search.setPassword(SignUtils.generateMd5(userRequest.getNewPassword()));
        search.setUpdateTime(new Date());
        return this.manUserDao.update(search);
    }

    /**
     * 新增或者删除组员组长
     * @param request
     */
    public Integer addOrDeleteParentId(ManSysUserRequest request) throws ServiceExceptionSpec {

        if (StringUtils.isEmpty(request.getUsername()) || request.getAddOrDelete() == null ||
                request.getAddOrDelete().equals(0)) {
            throw new ServiceExceptionSpec(ExceptionEnum.USER_BASE_PARAMS_ILLEGAL);
        }
        //通过用户名查询用户信息
        ManUser manUser = new ManUser();
        manUser.setDisabled(0);
        manUser.setStatus(0);
        manUser.setUsername(request.getUsername());
        List<ManUser> manUsers = manUserDao.scan(manUser);
        if (CollectionUtils.isEmpty(manUsers)) {
            throw new ServiceExceptionSpec(ExceptionEnum.NO_USER_NAME);
        }
        ManUser user = manUsers.get(0);
        //处理新增情况
        if (request.getAddOrDelete().equals(1)) {
            //新增组长
            if (request.getId() == null || request.getId().equals(0)) {
                user.setParentId(user.getId());
                //新增组员
            } else {
                user.setParentId(request.getId());
            }
            manUserDao.update(user);
            //处理删除情况
        } else if (request.getAddOrDelete().equals(2)){
            //删除组长或者组员必须传Id
            if (request.getId() == null || request.getId().equals(0)) {
                throw new ServiceExceptionSpec(ExceptionEnum.MANAGE_EDIT_ITEM_ERROR);
            }
            //删除组长保证没有组员情况
            if (request.getId().equals(user.getParentId())) {
                ManUser teamLeader = new ManUser();
                teamLeader.setDisabled(0);
                teamLeader.setParentId(request.getId());
                teamLeader.setStatus(0);
                List<ManUser> lists = manUserDao.scan(teamLeader);
                if (!CollectionUtils.isEmpty(lists) && lists.size() > 1) {
                    throw new ServiceExceptionSpec(ExceptionEnum.HAS_TEAM);
                }
            }
            //删除组长或者组员
            user.setParentId(0);
            manUserDao.update(user);
        }
        return 1;
    }

    /**
     * 查询所有组长和组员信息
     * @return
     */
    public List<TeamParentResponse> listParentId() {

        //先查出所有组长
        List<TeamParentResponse> rList = manUserDao.listParent();

        if (CollectionUtils.isEmpty(rList)) {
            logger.info("===============没有查询到组长信息=================");
            return new ArrayList<>();
        }
        //查询组员数据
        rList.stream().forEach(elem ->{
//            List<ManUser> manUsers = new ArrayList<>();
//            ManUser manUser = new ManUser();
//            manUser.setDisabled(0);
//            manUser.setStatus(0);
//            manUser.setId(elem.getId());
//            manUsers.add(manUserDao.scan(manUser).get(0));
            List<ManUser> manUsers = (manUserDao.listTeam(elem.getId()));
            if (!CollectionUtils.isEmpty(manUsers)) {
                elem.setManUserList(manUsers);
            }
        });
        return rList;
    }

    public Optional<ManUser> getManUserByUserName(String userName)  {
        if (StringUtils.isEmpty(userName)) {
            return Optional.empty();
        }
        ManUser search = new ManUser();
        search.setRealname(userName);//他们传的是realName.
        search.setDisabled(0);
       return manUserDao.scan(search).stream().findFirst();
    }

    public Optional<ManUser> getManUserById(Integer id) {
        if (id == null) {
            return Optional.empty();
        }
        ManUser manUser = new ManUser();
        manUser.setDisabled(0);
        manUser.setId(id);
        return manUserDao.scan(manUser).stream().findFirst();
    }

    public Boolean isAllowToSearchOrder(String orderNo, Integer outsourceId){
        Integer allow = manUserDao.isAllowToSearchOrder(orderNo, outsourceId);
        return allow > 0;
    }

}
