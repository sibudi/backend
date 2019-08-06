package com.yqg.base.data.mapper;

import java.util.List;

import com.yqg.base.data.condition.BaseCondition;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;

import com.yqg.base.data.provider.SqlProvider;

/**
 * @author Jacob
 *
 */
public interface BaseMapper<T extends BaseCondition> {

    /**
     * insert a entity to table
     * e.g.
     * entity(User) fields: id = null, uuid = xyzasdfg, name = Jacob, age = 18
     * sql: insert (uuid, name, age) value ('xyzasdfg', 'Jacob', 18);
     *  
     * note: User's id is autoincrement primary key, so id = null.
     */
    @InsertProvider(type = SqlProvider.class, method = "insert")
    @Options(useGeneratedKeys = true)
    public int insert(T entity);

    /**
     * update a entity to table
     * e.g.
     * entity(User) fields: id = 23, uuid = xyzasdfg, name = nbf, age = 18
     * sql: update user set name = 'nbf', age = 18 where id = 23 and uuid = 'xyzasdfg';
     * 
     * entity(User) fields: id = 23, uuid = null, name = nbf, age = null
     * sql: update user set name = 'nbf' where id = 23 where id = 23 and uuid = 23;
     * 
     *  note: User's id or uuid must be exist.
     */
    @UpdateProvider(type = SqlProvider.class, method = "update")
    public int update(T entity);

    /**
     * select entity's List from table
     * 
     * e.g.
     * entity(User) fields: id = 23, uuid = null, name = null, age = 18
     * sql: select id,uuid,name,age from user where id = 23 and age = 18;
     * 
     */
    @SelectProvider(type = SqlProvider.class, method = "scan")
    public List<T> scan(T entity);

    /**
     * count entity's number from table
     * 
     * e.g.
     * entity(User) fields: id = 23, uuid = null, name = null, age = 18
     * sql: select count(1) from user where id = 23 and age = 18;
     * 
     */
    @SelectProvider(type = SqlProvider.class, method = "count")
    public int count(T entity);
}
