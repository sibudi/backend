package com.yqg.common.redis;

import com.yqg.common.utils.JsonUtils;
import com.yqg.common.utils.MD5Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yqg.common.constants.RedisContants;

import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;
import java.util.*;

/**
 * @author Jacob
 */
@Component
@Slf4j
public class RedisClient {

    public static final String SET_IF_NOT_EXISTS = "NX";
    public static final String SET_EXPIRE_TIME_IN_SECONDS = "EX";
    @Autowired
    private JedisSentinelPool jedisPool;

    public void set(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = this.jedisPool.getResource();
            jedis.set(key, value);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public void set(String key, String value, int seconds) {
        Jedis jedis = null;
        try {
            jedis = this.jedisPool.getResource();
            jedis.setex(key, seconds, value);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public String get(String key) {

        Jedis jedis = null;
        try {
            jedis = this.jedisPool.getResource();
            return jedis.get(key);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public Long del(String key) {
        Jedis jedis = null;
        try {
            jedis = this.jedisPool.getResource();
            return jedis.del(key);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }
    public void delAll(String key) {
        Jedis jedis = null;
        try {
            jedis = this.jedisPool.getResource();
            Set<String> set=jedis.keys(key+"*");
            Iterator<String> it=set.iterator();
            while (it.hasNext()){
                String keyStr=it.next();
                jedis.del(keyStr);
            }
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public Set keys(String key){
        Jedis jedis = null;
        try {
            jedis = this.jedisPool.getResource();
            return jedis.keys(key+"*");
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public boolean lock(String key, String value, int seconds) {
        return lockDistributed(key,value,seconds);
//        Jedis jedis = null;
//        try {
//            jedis = this.jedisPool.getResource();
//            String oldValue = jedis.get(key);
//            if (oldValue != null && oldValue.equals(value)) {
//                return false;
//            }
//            jedis.setex(key, seconds, value);
//            return true;
//        } finally {
//            if (jedis != null) {
//                jedis.close();
//            }
//        }
    }

    /***
     * 通过单实例redis支持分布式锁【不考虑独占释放，所有方法使用的时候都是先加锁再释放锁，不能先释放锁，再加锁】
     * @param key
     * @param value
     * @param seconds
     * @return
     */
    public boolean lockDistributed(String key, String value, int seconds) {
        Jedis jedis = null;
        try {
            jedis = this.jedisPool.getResource();
            //以秒为单位
            String result = jedis.set(key, value, SET_IF_NOT_EXISTS, SET_EXPIRE_TIME_IN_SECONDS,seconds);
            if("OK".equals(result)){
                return true;
            }
            return false;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public boolean lock(String key) {
        return this.lock(key, "1", RedisContants.GOBAL_FIVE_MIN_SECONDS);
    }


    public boolean unLock(String key) {
        log.info("解锁------"+key);
        Jedis jedis = null;
        try {
            jedis = this.jedisPool.getResource();
            Long result = jedis.del(key);
            if (result > 0) {
                return true;
            }
            return false;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * ????????
     * Jacob 20170809
     *
     * @param key
     * @param value
     */
    public void set(String key, Object value) {
        this.set(key, JsonUtils.serialize(value));
    }

    /**
     * ?????????????
     * Jacob 20170809
     *
     * @param key
     * @param value
     * @param seconds
     */
    public void set(String key, Object value, int seconds) {
        this.set(key, JsonUtils.serialize(value), seconds);
    }

    /**
     * ??key?????????
     * Jacob 20170809
     *
     * @param key
     * @param clazz
     * @param <T>
     * @return ?????????null
     */
    public <T> T get(String key, Class<T> clazz) {
        String value = this.get(key);

        return StringUtils.isEmpty(value) ? null : JsonUtils.deserialize(value, clazz);
    }

    /**
     * ???????key
     * Jacob 20170809
     *
     * @param key
     * @return
     */
    public boolean exists(String key) {
        Jedis jedis = null;
        try {
            jedis = this.jedisPool.getResource();
            return jedis.exists(key);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * ?????
     * <p>
     * Jacob 20170809
     *
     * @param key     ??key
     * @param timeout ????,?
     * @return
     */
    public boolean tryGetLock(String key, int timeout) {
        log.info("锁定---"+key);
        for (int i = 0; i < timeout; i++) {
            Jedis jedis = null;
            try {
                jedis = this.jedisPool.getResource();
                Long result = jedis.setnx(key, key);
                if(result==1){
                    return true;
                }else {
                    //??
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } finally {
                if (jedis != null) {
                    jedis.close();
                }
            }
        }
        return false;
    }

    /**
     * ??????
     * <p>
     * Jacob 20170809
     *
     * @param object
     * @return
     */
    public boolean lockRepeat(Object object) {
        String key = MD5Util.md5UpCase(JsonUtils.serialize(object));
        return this.lock(RedisContants.REPEAT_LOCK + key);
    }

    public boolean lockRepeatWithSeconds(Object object, int seconds) {
        String key = MD5Util.md5UpCase(JsonUtils.serialize(object));
        return this.lock(RedisContants.REPEAT_LOCK + key, "1", seconds);
    }

    public static void main(String[] args) {
        String object ="toOrder" + "9153AFFBAB274FA090E813089A83B3B2" ;
        String key = MD5Util.md5UpCase(JsonUtils.serialize(object));
        System.err.println(RedisContants.REPEAT_LOCK + key);

        String key1 = MD5Util.md5UpCase(JsonUtils.serialize(object));
        System.err.println(RedisContants.REPEAT_LOCK + key1);
    }



    public boolean lockRepeat120(Object object) {
        String key = MD5Util.md5UpCase(JsonUtils.serialize(object));
        return this.lock(RedisContants.REPEAT_LOCK + key,"1",RedisContants.GOBAL_120_SECONDS);
    }
    /**
     * ???????
     * Jacob 20170809
     *
     * @param object
     */
    public void unlockRepeat(Object object) {
        String key = MD5Util.md5UpCase(JsonUtils.serialize(object));
        this.unLock(RedisContants.REPEAT_LOCK + key);
    }


    /**
     * ??list
     * Jacob 20170810
     *
     * @param key
     * @param object
     */
    public void listAdd(String key, Object object) {
        Jedis jedis = null;
        try {
            jedis = this.jedisPool.getResource();
            jedis.lpush(key, JsonUtils.serialize(object));
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * ??list
     * Jacob 20170810
     *
     * @param key
     * @param objectList
     */
    public void listAddAll(String key, List objectList) {
        for (Object object : objectList) {
            listAdd(key, object);
        }
    }

    /**
     * ??list?????
     * Jacob 20170810
     *
     * @param key
     * @return
     */
    public <T> T listGetHead(String key, Class<T> T) {
        Jedis jedis = null;
        try {
            jedis = this.jedisPool.getResource();
            String value= jedis.lpop(key);
            return StringUtils.isEmpty(value) ? null : JsonUtils.deserialize(value, T);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * ??list?????
     * Jacob 20170810
     *
     * @param key
     * @return
     */
    public <T> T listGetTail(String key, Class<T> T) {
        Jedis jedis = null;
        try {
            jedis = this.jedisPool.getResource();
            String value= jedis.rpop(key);
            return StringUtils.isEmpty(value) ? null : JsonUtils.deserialize(value, T);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * ??list??????
     * Jacob 20170810
     *
     * @param key
     * @return
     */
    public <T> List<T> getList(String key, Class<T> T) {
        Jedis jedis = null;
        try {
            jedis = this.jedisPool.getResource();
            List<String> list = jedis.lrange(key, 0, -1);
            List<T> resultList = new ArrayList<>();
            for (String value : list) {
                resultList.add(JsonUtils.deserialize(value, T));
            }
            return resultList;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * ?map???????????
     * Jacob 20170811
     *
     * @param key
     * @param itemKey
     * @param itemValue
     */
    public void mapSetValue(String key, String itemKey, String itemValue) {
        Jedis jedis = null;
        try {
            jedis = this.jedisPool.getResource();
            jedis.hset(key, itemKey, itemValue);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * ?map??????????
     *
     * @param key
     * @param itemKey
     * @param itemValue
     */
    public void mapSetValue(String key, String itemKey, Object itemValue) {
        Jedis jedis = null;
        try {
            jedis = this.jedisPool.getResource();
            jedis.hset(key, itemKey, JsonUtils.serialize(itemValue));
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * ?map?????key???
     * Jacob 20170811
     *
     * @param key
     * @param itemKey
     * @return
     */
    public String mapGetValue(String key, String itemKey) {
        Jedis jedis = null;
        try {
            jedis = this.jedisPool.getResource();
            return jedis.hget(key, itemKey);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * ?map?????key???,??????????????
     * Jacob 20170811
     *
     * @param key
     * @param itemKey
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T mapGetValue(String key, String itemKey, Class<T> clazz) {
        Jedis jedis = null;
        try {
            jedis = this.jedisPool.getResource();
            String value = jedis.hget(key, itemKey);
            return StringUtils.isEmpty(value) ? null : JsonUtils.deserialize(value, clazz);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * ?map???????
     * Jacob 20170811
     *
     * @param key
     * @return
     */
    public Map<String, String> mapGetAll(String key) {
        Jedis jedis = null;
        try {
            jedis = this.jedisPool.getResource();
            return jedis.hgetAll(key);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * ?map?????
     * Jacob 20170811
     *
     * @param key
     * @return
     */
    public Set<String> mapGetItemKeys(String key) {
        Jedis jedis = null;
        try {
            jedis = this.jedisPool.getResource();
            return jedis.hkeys(key);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * ????key?????????
     * Jacob 20170811
     *
     * @param key
     * @param itemKey
     */
    public void mapDelItems(String key, String... itemKey) {
        Jedis jedis = null;
        try {
            jedis = this.jedisPool.getResource();
            jedis.hdel(key, itemKey);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public void setAdd(String key,List<String> members){
        Jedis jedis = null;
        try {
            if (members == null || members.size() == 0) {
                log.info("the members is empty");
                return;
            }
            jedis = this.jedisPool.getResource();
            jedis.sadd(key,members.toArray(new String[0]));
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public void setRemove(String key,List<String> members){
        Jedis jedis = null;
        try {
            if (members == null || members.size() == 0) {
                log.info("the members is empty");
                return;
            }
            jedis = this.jedisPool.getResource();
            jedis.srem(key,members.toArray(new String[0]));
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public Boolean sisMember(String key ,String memberValue){
        Jedis jedis = null;
        try {
            jedis = this.jedisPool.getResource();
            return jedis.sismember(key,memberValue);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }


    /***
     * 获取对应的实例的过期时间
     * @return
     */
    public long getExpireTime(String key) {
        Jedis jedis = null;
        try {
            jedis = this.jedisPool.getResource();
            //以秒为单位
            return jedis.ttl(key);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }
}

