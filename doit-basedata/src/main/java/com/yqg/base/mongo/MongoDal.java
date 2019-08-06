package com.yqg.base.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import com.yqg.base.data.condition.BaseMongoEntity;
import com.yqg.base.mongo.anaotations.Column;
import com.yqg.base.mongo.anaotations.Table;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

@Slf4j
public abstract class MongoDal<T> {

    @Autowired
    private MongoTemplate mongoTemplate;

    public String insert(T t) {
        DBObject dBObject = new BasicDBObject();
        Class<?> clazz = t.getClass();
        String tableName = getTableName(clazz);
        Field[] fields = getFields(clazz);
        try {
            for (Field field : fields) {
                if (isStatic(field.getModifiers())) {
                    continue;
                }
                String columnName = getColumnName(field);
                field.setAccessible(true);
                Object object = field.get(t);
                if (object != null) {
                    dBObject.put(columnName, object);
                }
            }
        } catch (Exception e) {
            new RuntimeException("mongo dal exceptoin" + e);
        }
        this.mongoTemplate.insert(dBObject, tableName);

        //???????id
        if (dBObject.get("_id") != null) {
            return dBObject.get("_id").toString();
        }
        return null;
    }
    public String insertObject(T t){
        Class<?> clazz = t.getClass();
        String tableName = getTableName(clazz);
        this.mongoTemplate.insert(t,tableName);
        if(t instanceof BaseMongoEntity){
            return ((BaseMongoEntity) t).getId();
        }
        return null;
    }

    public void delete(T t){
        Query query = new Query();
        Class<?> clazz = t.getClass();
        String tableName = getTableName(clazz);
        Field[] fields = getFields(clazz);
        boolean hasOrderNo = false;
        boolean hasId = false;
        try {
            for (Field field : fields) {
                if (isStatic(field.getModifiers())) {
                    continue;
                }
                String columnName = getColumnName(field);

                field.setAccessible(true);
                Object object = field.get(t);
                if (object != null) {
                    Criteria criteria = new Criteria(columnName);
                    criteria.is(object);
                    query.addCriteria(criteria);
                    if(columnName.equalsIgnoreCase("orderNo")){
                        hasOrderNo = true;
                    }
                    if(columnName.equalsIgnoreCase("id")){
                        hasId = true;
                    }
                    if(columnName.equalsIgnoreCase("_id")){
                        hasId = true;
                    }
                }
            }

        } catch (Exception e) {
            new RuntimeException("mongo dal exceptoin" + e);
        }
        if (!hasId && !hasOrderNo) {
            log.info("now id and orderNo");
            return;
        }
        this.mongoTemplate.remove(query,tableName);
        log.info("the remove orderNo: "+query.toString());
    }




    @SuppressWarnings("unchecked")
    public List<T> find(T t) {
        Query query = new Query();

        Class<?> clazz = t.getClass();
        String tableName = getTableName(clazz);
        Field[] fields = getFields(clazz);
        try {
            for (Field field : fields) {
                if (isStatic(field.getModifiers())) {
                    continue;
                }
                String columnName = getColumnName(field);
                field.setAccessible(true);
                Object object = field.get(t);
                if (object != null) {
                    Criteria criteria = new Criteria(columnName);
                    criteria.is(object);
                    query.addCriteria(criteria);
                }
            }
        } catch (Exception e) {
            new RuntimeException("mongo dal exceptoin" + e);
        }
        List<T> userList = (List<T>) this.mongoTemplate.find(query, clazz,
                tableName);
        return userList;
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
        return ArrayUtils.addAll(beanFields, beanSuperFields);
    }

    private boolean isStatic(int modifiers) {
        return ((modifiers & Modifier.STATIC) != 0);
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

    //??????
//      public void update(T t) {
//        Query query = new Query();
//        Criteria criteria = new Criteria("_id");
//        criteria.is();
//        query.addCriteria(criteria);
//        this.mongoTemplate.updateFirst(query, update, "user");
//    }


//    /**
//     * ??uuid??
//     * @param t
//     * @return ????????????
//     */
//    public int update(T t){
//        Update update=new Update();
//        Query query=new Query();
//        DBObject dBObject = new BasicDBObject();
//        Class<?> clazz = t.getClass();
//        String tableName = getTableName(clazz);
//        Field[] fields = getFields(clazz);
//        boolean flag = false;
//        try {
//            for (Field field : fields) {
//                if (isStatic(field.getModifiers())) {
//                    continue;
//                }
//                String columnName = getColumnName(field);
//                field.setAccessible(true);
//                Object object = field.get(t);
//                if (object != null) {
//                    dBObject.put(columnName, object);
//                    if(columnName.equals("uuid")||columnName.equals("id")){
//                        Criteria criteria = new Criteria(columnName);
//                        criteria.is(object);
//                        query.addCriteria(criteria);
//                        flag = true;
//                    }
//                    else {
//                        update.set(columnName,object);
//                    }
//                }
//            }
//        } catch (Exception e) {
//            new RuntimeException("mongo dal exceptoin" + e);
//        }
//        if(flag){
//            WriteResult writeResult = this.mongoTemplate.updateFirst(query,update,tableName);
//            return writeResult.getN();
//        }else{
//            log.warn("the flag is false, cannot update");
//            return 0;
//        }
//    }
//

    /**
     * ??mongodb_id????
     * @param t
     */
    public int updateById(T t){
        Update update=new Update();
        Query query=new Query();
        DBObject dBObject = new BasicDBObject();
        Class<?> clazz = t.getClass();
        String tableName = getTableName(clazz);
        Field[] fields = getFields(clazz);
        boolean flag = false;
        try {
            for (Field field : fields) {
                if (isStatic(field.getModifiers())) {
                    continue;
                }
                String columnName = getColumnName(field);
                field.setAccessible(true);
                Object object = field.get(t);
                if (object != null) {
                    dBObject.put(columnName, object);
                    if(columnName.equals("id")){
                        Criteria criteria = new Criteria("_id");
                        criteria.is(object);
                        query.addCriteria(criteria);
                        flag = true;
                    }else {
                        update.set(columnName,object);
                    }
                }
            }
        } catch (Exception e) {
            new RuntimeException("mongo dal exceptoin" + e);
        }
        if(flag) {
            WriteResult writeResult = this.mongoTemplate.updateFirst(query, update, tableName);
            return writeResult.getN();
        }else{
            log.warn("the flag is false, cannot updateByOrderNo");
            return 0;
        }
    }

//    /**
//     * ??orderNo??
//     * @param t
//     */
//    public int updateByOrderNo(T t){
//        Update update=new Update();
//        Query query=new Query();
//        DBObject dBObject = new BasicDBObject();
//        Class<?> clazz = t.getClass();
//        String tableName = getTableName(clazz);
//        Field[] fields = getFields(clazz);
//        boolean flag = false;
//        try {
//            for (Field field : fields) {
//                if (isStatic(field.getModifiers())) {
//                    continue;
//                }
//                String columnName = getColumnName(field);
//                field.setAccessible(true);
//                Object object = field.get(t);
//                if (object != null) {
//                    dBObject.put(columnName, object);
//                    if(columnName.equals("orderNo")){
//                        Criteria criteria = new Criteria(columnName);
//                        criteria.is(object);
//                        query.addCriteria(criteria);
//                        flag = true;
//                    }else {
//                        update.set(columnName,object);
//                    }
//                }
//            }
//        } catch (Exception e) {
//            new RuntimeException("mongo dal exceptoin" + e);
//        }
//        if(flag) {
//            WriteResult writeResult = this.mongoTemplate.updateFirst(query, update, tableName);
//            return writeResult.getN();
//        }else{
//            log.warn("the flag is false, cannot updateByOrderNo");
//            return 0;
//        }
//    }

    /**
     * ??userUuid??
     * @param t
     */
    public int updateByUserUuid(T t){
        Update update=new Update();
        Query query=new Query();
        DBObject dBObject = new BasicDBObject();
        Class<?> clazz = t.getClass();
        String tableName = getTableName(clazz);
        Field[] fields = getFields(clazz);
        boolean flag = false;
        try {
            for (Field field : fields) {
                if (isStatic(field.getModifiers())) {
                    continue;
                }
                String columnName = getColumnName(field);
                field.setAccessible(true);
                Object object = field.get(t);
                if (object != null) {
                    dBObject.put(columnName, object);
                    if(columnName.equals("userUuid")){
                        Criteria criteria = new Criteria(columnName);
                        criteria.is(object);
                        query.addCriteria(criteria);
                        flag = true;
                    }else {
                        update.set(columnName,object);
                    }
                }
            }
        } catch (Exception e) {
            new RuntimeException("mongo dal exceptoin" + e);
        }
        if(flag) {
            WriteResult writeResult = this.mongoTemplate.updateFirst(query, update, tableName);
            return writeResult.getN();
        }else{
            log.warn("the flag is false, cannot updateByUserUuid");
            return 0;
        }
    }


    @SuppressWarnings("unchecked")
    public List<T> customFind(T t,List<Criteria> criteriaList) {
        Query query = new Query();

        Class<?> clazz = t.getClass();
        String tableName = getTableName(clazz);

        for(Criteria item:criteriaList){
            query.addCriteria(item);
        }

        List<T> result = (List<T>) this.mongoTemplate.find(query, clazz,tableName);
        return result;
    }

    @SuppressWarnings("unchecked")
    public List<T> customFind2(T t) {
        Query query = new Query();

        Class<?> clazz = t.getClass();
        String tableName = getTableName(clazz);

//        for(Criteria item:criteriaList){
//            query.addCriteria(item);
//        }

        query.with(new Sort(Sort.Direction.DESC,"_id")).limit(20);

        List<T> result = (List<T>) this.mongoTemplate.find(query, clazz,tableName);
        return result;
    }

    @SuppressWarnings("unchecked")
    public List<T> customFindByPage(T t, Sort sort, List<Criteria> criteriaList, Integer pageStart, Integer pageSize) {
        Query query = new Query();

        Class<?> clazz = t.getClass();
        String tableName = getTableName(clazz);

        for(Criteria item:criteriaList){
            query.addCriteria(item).skip(pageStart).limit(pageSize).with(sort);
        }

        List<T> result = (List<T>) this.mongoTemplate.find(query, clazz,tableName);
        return result;
    }

    @SuppressWarnings("unchecked")
    public List<T> customFindByPage(T t,Sort sort,Integer pageStart,Integer pageSize) {
        Query query = new Query();

        Class<?> clazz = t.getClass();
        String tableName = getTableName(clazz);
        Field[] fields = getFields(clazz);
        try {
            for (Field field : fields) {
                if (isStatic(field.getModifiers())) {
                    continue;
                }
                String columnName = getColumnName(field);
                field.setAccessible(true);
                Object object = field.get(t);
                if (object != null) {
                    Criteria criteria = new Criteria(columnName);
                    criteria.is(object);
                    query.addCriteria(criteria);
                }
            }
        } catch (Exception e) {
            new RuntimeException("mongo dal exceptoin" + e);
        }
        query.with(sort).skip(pageStart).limit(pageSize);

        List<T> result = (List<T>) this.mongoTemplate.find(query, clazz,tableName);
        return result;
    }
}
