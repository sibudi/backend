package com.yqg.base.data.provider;

import com.yqg.base.data.annotations.Column;
import com.yqg.base.data.annotations.Common;
import com.yqg.base.data.annotations.Condition;
import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * @author Jacob
 */
public class SqlProvider {
    private Logger logger = LoggerFactory.getLogger(SqlProvider.class);

    private static List<String> UNIQUE_KEY = new ArrayList<>();

    static {
        UNIQUE_KEY.add("uuid");
        UNIQUE_KEY.add("id");
    }

    private static final String START = "_start";
    private static final String PAGE_SIZE = "_pageSize";
    private static final String ORDER_BY = "_orderBy";
    private static final String IN_FIELD = "_inField";
    private static final String IN_VALUES = "_inValues";
    private static final String LIKE_FIELD = "_likeField";
    private static final String LIKE_KEYWORD = "_likeKeyword";

    public String insert(Object bean) {
        Class<?> clazz = bean.getClass();
        String tableName = getTableName(clazz);
        Field[] fields = getFields(clazz);
        StringBuilder insertSql = new StringBuilder();
        StringBuilder valueSql = new StringBuilder();
        insertSql.append("INSERT INTO ").append(tableName).append("(");
        this.addCommonField(bean, SqlType.INSERT);
        try {
            for (Field field : fields) {
                if (isStatic(field.getModifiers()) || field.getAnnotation(Common.class) != null) {
                    continue;
                }
                String columnName = getColumnName(field);
                field.setAccessible(true);
                Object object = field.get(bean);
                if (object != null) {
                    insertSql.append(columnName).append(",");
                    valueSql.append("#{").append(field.getName())
                            .append("}").append(",");
                }
            }
        } catch (Exception e) {
            new RuntimeException("sql exceptoin" + e);
        }
        String result = StringUtils.removeEnd(insertSql.toString(), ",") + ") VALUES("
                + StringUtils.removeEnd(valueSql.toString(), ",") + ")";
        logger.debug(result);
        return result;
    }

    public String update(Object bean) {
        Class<?> clazz = bean.getClass();
        String tableName = getTableName(clazz);
        Field[] fields = getFields(clazz);
        StringBuilder updateSql = new StringBuilder();
        updateSql.append("update ").append(tableName).append(" set ");
        StringBuilder conditionBuilder = new StringBuilder();
        this.addCommonField(bean, SqlType.UPDATE);
        try {
            for (Field field : fields) {
                if (isStatic(field.getModifiers()) || field.getAnnotation(Common.class) != null) {
                    continue;
                }
                String columnName = getColumnName(field);
                field.setAccessible(true);
                Object beanValue = field.get(bean);
                if (beanValue != null) {
                    if (UNIQUE_KEY.contains(field.getName())) {
                        conditionBuilder.append(columnName).append("=#{").append(field.getName())
                                .append("}").append(" and ");
                    } else {
                        updateSql.append(columnName).append("=#{").append(field.getName())
                                .append("}").append(",");
                    }
                }
            }

        } catch (Exception e) {
            new RuntimeException("sql exceptoin" + e);
        }
        String result = StringUtils.removeEnd(updateSql.toString(), ",") + " where "
                + StringUtils.removeEnd(conditionBuilder.toString(), " and ");
        logger.debug(result);
        return result;
    }

    public String scan(Object bean) {
        Class<?> clazz = bean.getClass();
        String tableName = getTableName(clazz);
        Field[] fields = getFields(clazz);
        StringBuilder selectSql = new StringBuilder();
        StringBuilder conditionSql = new StringBuilder();
        Map<String, Object> baseCondition = new HashMap<>();
        Map<String, Object> condition = new HashMap<>();
        selectSql.append("select ");
        this.addCommonField(bean, SqlType.SELECT);
        try {
            for (Field field : fields) {
                if (isStatic(field.getModifiers())) {
                    continue;
                }
                field.setAccessible(true);
                Object object = field.get(bean);
                if (field.getAnnotation(Common.class) != null) {
                    baseCondition.put(field.getName(), object);
                } else if (field.getAnnotation(Condition.class) != null) {
                    condition.put(field.getName(), object);
                } else {

                    selectSql.append(field.getName()).append(",");
                    if (object != null) {
                        conditionSql.append(field.getName()).append("=").append("#{")
                                .append(field.getName()).append("}")
                                .append(" and ");
                    }
                }
            }
            conditionSql = this.generateCondition(condition, conditionSql);
        } catch (Exception e) {
            new RuntimeException("sql exceptoin" + e);
        }
        String result = StringUtils.removeEnd(selectSql.toString(), ",") + " from " + tableName + " where "
                + StringUtils.removeEnd(conditionSql.toString(), "and ")
                + generateBaseCondition(baseCondition);
        logger.debug(result);
        return result;
    }

    public String count(Object bean) {
        Class<?> clazz = bean.getClass();
        String tableName = getTableName(clazz);
        Field[] fields = getFields(clazz);
        StringBuilder selectSql = new StringBuilder();
        StringBuilder conditionSql = new StringBuilder();
        Map<String, Object> condition = new HashMap<>();
        selectSql.append("select count(1) ");
        try {
            for (Field field : fields) {
                if (isStatic(field.getModifiers()) || field.getAnnotation(Common.class) != null) {
                    continue;
                }
                field.setAccessible(true);
                Object object = field.get(bean);
                if (object != null) {
                    if (field.getAnnotation(Condition.class) != null) {
                        condition.put(field.getName(), object);
                    } else {
                        conditionSql.append(field.getName()).append("=").append("#{")
                                .append(field.getName()).append("}")
                                .append(" and ");
                    }
                }
            }
            conditionSql = this.generateCondition(condition, conditionSql);
        } catch (Exception e) {
            new RuntimeException("sql exceptoin:" + e);
        }
        selectSql.append(" from ").append(tableName).append(" where ")
                .append(StringUtils.removeEnd(conditionSql.toString(), "and "));
        logger.debug(selectSql.toString());
        return selectSql.toString();
    }

    public String scanAccountRecords(String uuid, String type, String caseoutAccount, String caseoutAccountName, String beginTime, String endTime, String userUuid, Integer pageSize, Integer pageStart, String channel) {
        StringBuilder selectSql = new StringBuilder();
        selectSql.append("select id,uuid,createUser,createTime,updateUser,updateTime,remark,userUuid,balance,amount,type,channel,caseoutAccount,goPayUserName from activityAccountRecord where disabled = 0");
        if (StringUtils.isNoneEmpty(uuid))
            selectSql.append(" and uuid='").append(uuid).append("' ");
        if (StringUtils.isNoneEmpty(channel))
            selectSql.append(" and channel='").append(channel).append("' ");
        if (StringUtils.isNoneEmpty(userUuid))
            selectSql.append(" and userUuid='").append(userUuid).append("' ");
        if (StringUtils.isNoneEmpty(type))
            selectSql.append(" and type='").append(type).append("' ");
        if (StringUtils.isNoneEmpty(caseoutAccount))
            selectSql.append(" and caseoutAccount='").append(caseoutAccount).append("' ");
        if (StringUtils.isNoneEmpty(caseoutAccountName))
            selectSql.append(" and goPayUserName='").append(caseoutAccountName).append("' ");
        if (StringUtils.isNoneEmpty(beginTime) && StringUtils.isNoneEmpty(endTime))
            selectSql.append(" and createTime between '").append(beginTime).append("' and '").append(endTime).append("'");
        selectSql.append(" order by createTime desc limit ").append(pageStart).append(",").append(pageSize);
//        logger.info(selectSql.toString());
        return selectSql.toString();
    }

    public String scanAccountRecordsCount(String uuid, String type, String caseoutAccount, String caseoutAccountName, String beginTime, String endTime, String userUuid, String channel) {
        StringBuilder selectSql = new StringBuilder();
        selectSql.append("select count(1) from activityAccountRecord where disabled = 0");
        if (StringUtils.isNoneEmpty(uuid))
            selectSql.append(" and uuid='").append(uuid).append("' ");
        if (StringUtils.isNoneEmpty(userUuid))
            selectSql.append(" and userUuid='").append(userUuid).append("' ");
        if (StringUtils.isNoneEmpty(channel))
            selectSql.append(" and channel='").append(channel).append("' ");
        if (StringUtils.isNoneEmpty(type))
            selectSql.append(" and type='").append(type).append("' ");
        if (StringUtils.isNoneEmpty(caseoutAccount))
            selectSql.append(" and caseoutAccount='").append(caseoutAccount).append("' ");
        if (StringUtils.isNoneEmpty(caseoutAccountName))
            selectSql.append(" and goPayUserName='").append(caseoutAccountName).append("' ");
        if (StringUtils.isNoneEmpty(beginTime) && StringUtils.isNoneEmpty(endTime))
            selectSql.append(" and createTime between '").append(beginTime).append("' and '").append(endTime).append("'");
//        logger.info(selectSql.toString());
        return selectSql.toString();
    }

    public String withdrawRecord(String type, String caseoutAccount, String caseoutAccountName, String beginTime, String endTime, String userUuid, String uuid, Integer pageSize, Integer pageStart) {
        StringBuilder selectSql = new StringBuilder();
        selectSql.append("select id,uuid,createUser,createTime,updateUser,updateTime,remark,userUuid,balance,amount,type,channel from activityAccountRecord where disabled = 0");
        if (StringUtils.isNoneEmpty(uuid))
            selectSql.append(" and uuid='").append(uuid).append("' ");
        if (StringUtils.isNoneEmpty(userUuid))
            selectSql.append(" and userUuid='").append(userUuid).append("' ");
        if (StringUtils.isNoneEmpty(type))
            selectSql.append(" and type='").append(type).append("' ");
        else
            selectSql.append(" and type in (3,4,5,6) ");
        if (StringUtils.isNoneEmpty(caseoutAccount))
            selectSql.append(" and caseoutAccount='").append(caseoutAccount).append("' ");
        if (StringUtils.isNoneEmpty(caseoutAccountName))
            selectSql.append(" and goPayUserName='").append(caseoutAccountName).append("' ");
        if (StringUtils.isNoneEmpty(beginTime) && StringUtils.isNoneEmpty(endTime))
            selectSql.append(" and createTime between '").append(beginTime).append("' and '").append(endTime).append("'");
        selectSql.append(" order by createTime desc limit ").append(pageStart).append(",").append(pageSize);
//        logger.info(selectSql.toString());
        return selectSql.toString();
    }

    public String withdrawRecordCount(String uuid, String type, String caseoutAccount, String caseoutAccountName, String beginTime, String endTime, String userUuid) {
        StringBuilder selectSql = new StringBuilder();
        selectSql.append("select count(1) from activityAccountRecord where disabled = 0");
        if (StringUtils.isNoneEmpty(uuid))
            selectSql.append(" and uuid='").append(uuid).append("' ");
        if (StringUtils.isNoneEmpty(userUuid)) {
            selectSql.append(" and userUuid='").append(userUuid).append("' ");
        }
        if (StringUtils.isNoneEmpty(caseoutAccount))
            selectSql.append(" and caseoutAccount='").append(caseoutAccount).append("' ");
        if (StringUtils.isNoneEmpty(caseoutAccountName)) {
            selectSql.append(" and goPayUserName='").append(caseoutAccountName).append("' ");
        }
        if (StringUtils.isNoneEmpty(beginTime) && StringUtils.isNoneEmpty(endTime)) {
            selectSql.append(" and createTime between '").append(beginTime).append("' and '").append(endTime).append("'");
        }
        if (StringUtils.isNoneEmpty(type))
            selectSql.append(" and type='").append(type).append("' ");
        else
            selectSql.append(" and type in (3,4,5,6) ");
//        logger.info(selectSql.toString());
        return selectSql.toString();
    }

    private String generateBaseCondition(Map<String, Object> baseCondition) {
        StringBuilder stringBuilder = new StringBuilder();
        if (baseCondition.get(ORDER_BY) != null) {
            stringBuilder.append(" order by ").append(baseCondition.get(ORDER_BY).toString());
        }
        if (baseCondition.get(START) != null && baseCondition.get(PAGE_SIZE) != null) {
            stringBuilder.append(" limit ").append(baseCondition.get(START).toString()).append(",")
                    .append(baseCondition.get(PAGE_SIZE).toString());
        }
        return stringBuilder.toString();
    }

    /**
     * in??,like????
     *
     * @param condition
     * @param conditionSql
     */
    private StringBuilder generateCondition(Map<String, Object> condition, StringBuilder conditionSql) {
        StringBuilder stringBuilder = new StringBuilder();
        if (condition.get(IN_FIELD) != null && condition.get(IN_VALUES) != null) {
            stringBuilder.append(condition.get(IN_FIELD))
                    .append(" in (");
            for (Object object : (Object[]) condition.get(IN_VALUES)) {
                if (object instanceof String) {
                    stringBuilder.append("'");
                    stringBuilder.append(object);
                    stringBuilder.append("',");
                } else if (object instanceof Integer) {
                    stringBuilder.append(object);
                    stringBuilder.append(",");
                }
            }
            stringBuilder.append(") and ");
            stringBuilder.deleteCharAt(stringBuilder.lastIndexOf(","));
        }
        if (condition.get(LIKE_FIELD) != null && condition.get(LIKE_KEYWORD) != null) {
            stringBuilder
                    .append(condition.get(LIKE_FIELD))
                    .append(" like '%")
                    .append(condition.get(LIKE_KEYWORD))
                    .append("%' and ");
        }
        return stringBuilder.append(conditionSql);
    }

    private static String getColumnName(Field field) {
        Column column = field.getAnnotation(Column.class);
        String columnName = "";
        if (column != null) {
            columnName = column.value();
        }
        if (StringUtils.isEmpty(columnName)) {
            return field.getName();
        }
        return columnName;
    }

    private String getTableName(Class<?> clazz) {
        String tableName = "";
        Table table = clazz.getAnnotation(Table.class);
        if (table != null) {
            tableName = table.value();
        } else {
            tableName = clazz.getSimpleName();
        }
        return tableName;
    }

    private Field[] getFields(Class<?> clazz) {
        Field[] beanFields = clazz.getDeclaredFields();
        Class<?> beanSuperClass = clazz.getSuperclass();
        Field[] beanSuperFields = beanSuperClass.getDeclaredFields();
        Field[] beanSuper2Fields = beanSuperClass.getSuperclass().getDeclaredFields();
        beanFields = ArrayUtils.addAll(beanFields, beanSuper2Fields);
        return ArrayUtils.addAll(beanFields, beanSuperFields);
    }

    private boolean isStatic(int modifiers) {
        return ((modifiers & Modifier.STATIC) != 0);
    }

    //    private String getParamObjectName(Class<?> clazz) {
    //        char[] chars = clazz.getSimpleName().toCharArray();
    //        char temp = Character.toLowerCase(chars[0]);
    //        chars[0] = temp;
    //        return new String(chars);
    //    }

    /**
     * ?????? BaseEntity ??????,???????????????
     *
     * @param object
     */
    private void addCommonField(Object object, SqlType sqlTypet) {
        if (object instanceof BaseEntity) {
            BaseEntity baseEntity = (BaseEntity) object;
            switch (sqlTypet) {
                case INSERT:
                    if (null == baseEntity.getUuid()) {
                        baseEntity.setUuid(UUID.randomUUID().toString().replace("-", "").toUpperCase());
                    }
                    if (null == baseEntity.getCreateTime()) {
                        baseEntity.setCreateTime(new Date());
                    }
                    if (null == baseEntity.getUpdateTime()) {
                        baseEntity.setUpdateTime(new Date());
                    }
                    break;
                case UPDATE:
                    if (null != baseEntity.getUpdateTime()) {
                        baseEntity.setUpdateTime(new Date());
                    }
                    break;
                case SELECT://????????????
                    if (null != baseEntity.getDisabled()) {
                        baseEntity.setDisabled(0);
                    }
                    break;
            }

        }
    }

    /**
     * sql??
     */
    enum SqlType {
        INSERT, UPDATE, SELECT
    }
}