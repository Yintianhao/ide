package com.mine.ide.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisZSetCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author wangjikai
 */
@Repository("redisUtil")
@Component
public class RedisUtil {
    @Autowired
    private RedisTemplate<?, ?> redisTemplate;
    private int dbIndex = 0;


    public RedisTemplate<?, ?> getRedisTemplate() {
        return redisTemplate;
    }

    /**
     * 通过key获取Hash缓存
     *
     * @param key
     * @return
     */
    public Map<String, String> getHashByKey(final String key) {
        return getHashByKey(key, dbIndex);
    }

    public Map<String, String> getHashByKey(final String key, final int db) {
        return redisTemplate.execute(new RedisCallback<Map<String, String>>() {
            @Override
            public Map<String, String> doInRedis(RedisConnection connection)
                    throws DataAccessException {
                if (dbIndex != db) {
                    connection.select(db);
                }
                Map<String, String> result = new HashMap<String, String>();
                RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
                Map<byte[], byte[]> map = connection.hGetAll(serializer.serialize(key));
                Set<Map.Entry<byte[], byte[]>> entrys = map.entrySet();
                for (Map.Entry<byte[], byte[]> entry : entrys) {
                    String key = serializer.deserialize(entry.getKey());
                    String value = serializer.deserialize(entry.getValue());
                    if (value != null && !value.equals("null") && !value.equals("nil")) {
                        result.put(key, value);
                    }
                }
                return result;
            }
        });
    }

    public List<String> hmGet(final String key, final String[] fields) {
        return hmGet(key, fields, dbIndex);
    }

    public List<String> hmGet(final String key, final String[] fields, int db) {
        return redisTemplate.execute(new RedisCallback<List<String>>() {
            @Override
            public List<String> doInRedis(RedisConnection connection)
                    throws DataAccessException {
                if (dbIndex != db) {
                    connection.select(db);
                }
                RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
                byte[][] fieldBytes = new byte[fields.length][];
                for (int i = 0; i < fields.length; i++) {
                    fieldBytes[i] = serializer.serialize(fields[i]);
                }
                List<byte[]> result = connection.hMGet(serializer.serialize(key), fieldBytes);
                List<String> returnList = new ArrayList<String>(result.size());
                for (byte[] entry : result) {
                    String value = serializer.deserialize(entry);
                    if (value != null && !value.equals("null") && !value.equals("nil")) {
                        returnList.add(value);
                    }
                }
                return returnList;
            }
        });
    }

    public void rPush(final String key, final String value) {
        rPush(key, value, dbIndex);
    }

    public void rPush(final String key, final String value, int db) {
        redisTemplate.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection connection)
                    throws DataAccessException {
                if (dbIndex != db) {
                    connection.select(db);
                }
                RedisSerializer<String> serializer = redisTemplate
                        .getStringSerializer();
                return connection.rPush(serializer.serialize(key), serializer.serialize(value));
            }
        });
    }

    public List<String> lRange(final String key, final long begin, final long end) {
        return lRange(key, begin, end, dbIndex);
    }

    public List<String> lRange(final String key, final long begin, final long end, int db) {
        return redisTemplate.execute(new RedisCallback<List<String>>() {
            @Override
            public List<String> doInRedis(RedisConnection connection)
                    throws DataAccessException {
                if (dbIndex != db) {
                    connection.select(db);
                }
                RedisSerializer<String> serializer = redisTemplate
                        .getStringSerializer();
                List<byte[]> bValues = connection.lRange(serializer.serialize(key), begin, end);
                List<String> sValues = new ArrayList();
                for (byte[] bytes : bValues) {
                    sValues.add(serializer.deserialize(bytes));
                }
                return sValues;
            }
        });
    }

    public void zadd(final String key, final String value, final long core) {
        zadd(key, value, core, dbIndex);
    }

    public void zadd(final String key, final String value, final long core, int db) {
        redisTemplate.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection connection)
                    throws DataAccessException {
                if (dbIndex != db) {
                    connection.select(db);
                }
                RedisSerializer<String> serializer = redisTemplate
                        .getStringSerializer();
                return connection.zAdd(serializer.serialize(key), core, serializer.serialize(value));
            }
        });
    }

    public Long zRem(final String key, final String value) {
        return zRem(key, value, dbIndex);
    }

    public Long zRem(final String key, final String value, int db) {
        return redisTemplate.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection connection)
                    throws DataAccessException {
                if (dbIndex != db) {
                    connection.select(db);
                }
                RedisSerializer<String> serializer = redisTemplate
                        .getStringSerializer();
                System.out.println("zSetName:" + key + "del:" + value);
                return connection.zRem(serializer.serialize(key), serializer.serialize(value));
            }
        });
    }

    public Set<String> zrange(final String key, final long start, final long end) {
        return zrange(key, start, end, dbIndex);
    }

    public Set<String> zrange(final String key, final long start, final long end, int db) {
        return redisTemplate.execute(new RedisCallback<Set<String>>() {
            @Override
            public Set<String> doInRedis(RedisConnection connection)
                    throws DataAccessException {
                if (dbIndex != db) {
                    connection.select(db);
                }
                RedisSerializer<String> serializer = redisTemplate
                        .getStringSerializer();
                Set<byte[]> keyByteSet = connection.zRange(serializer.serialize(key), start, end);
                Set<String> strings = new HashSet<String>();
                for (byte[] keyByte : keyByteSet) {
                    strings.add(redisTemplate.getStringSerializer()
                            .deserialize(keyByte));
                }
                return strings;
            }
        });
    }

    public Set<String> zRangeByScore(final String key, final long start, final long end) {
        return zRangeByScore(key, start, end, dbIndex);
    }

    public Set<String> zRangeByScore(final String key, final long start, final long end, int db) {
        return redisTemplate.execute(new RedisCallback<Set<String>>() {
            @Override
            public Set<String> doInRedis(RedisConnection connection)
                    throws DataAccessException {
                if (dbIndex != db)
                    connection.select(db);
                RedisSerializer<String> serializer = redisTemplate
                        .getStringSerializer();
                Set<byte[]> tuples = connection.zRangeByScore(serializer.serialize(key), start, end);
                Set<String> strings = new HashSet<String>();
                for (byte[] tuple : tuples) {
                    strings.add(redisTemplate.getStringSerializer()
                            .deserialize(tuple));
                }
                return strings;
            }
        });
    }

    public Set<RedisZSetCommands.Tuple> zRangeWithScores(final String key, final long start, final long end) {
        return zRangeWithScores(key, start, end, dbIndex);
    }

    public Set<RedisZSetCommands.Tuple> zRangeWithScores(final String key, final long start, final long end, int db) {
        return redisTemplate.execute(new RedisCallback<Set<RedisZSetCommands.Tuple>>() {
            @Override
            public Set<RedisZSetCommands.Tuple> doInRedis(RedisConnection connection)
                    throws DataAccessException {
                if (dbIndex != db)
                    connection.select(db);
                RedisSerializer<String> serializer = redisTemplate
                        .getStringSerializer();
                return connection.zRevRangeWithScores(serializer.serialize(key), start, end);
            }
        });
    }

    public Set<RedisZSetCommands.Tuple> zRangeByScoreWithScores(final String key, final long start, final long end) {
        return zRangeByScoreWithScores(key, start, end, dbIndex);
    }

    public Set<RedisZSetCommands.Tuple> zRangeByScoreWithScores(final String key, final long start, final long end, int db) {
        return redisTemplate.execute(new RedisCallback<Set<RedisZSetCommands.Tuple>>() {
            @Override
            public Set<RedisZSetCommands.Tuple> doInRedis(RedisConnection connection)
                    throws DataAccessException {
                if (dbIndex != db)
                    connection.select(db);
                RedisSerializer<String> serializer = redisTemplate
                        .getStringSerializer();
                return connection.zRangeByScoreWithScores(serializer.serialize(key), start, end);
            }
        });
    }

    public void lPush(final String key, final String value) {
        lPush(key, value, dbIndex);
    }

    public void lPush(final String key, final String value, int db) {
        redisTemplate.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection connection)
                    throws DataAccessException {
                if (dbIndex != db)
                    connection.select(db);
                RedisSerializer<String> serializer = redisTemplate
                        .getStringSerializer();
                return connection.lPush(serializer.serialize(key), serializer.serialize(value));
            }
        });
    }

    public Long getListLength(final String key) {
        return getListLength(key, dbIndex);
    }

    public Long getListLength(final String key, int db) {
        return redisTemplate.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection connection)
                    throws DataAccessException {
                if (dbIndex != db)
                    connection.select(db);
                RedisSerializer<String> serializer = redisTemplate
                        .getStringSerializer();
                return connection.lLen(serializer
                        .serialize(key));
            }
        });
    }

    public String get(final String key) {
        return get(key, dbIndex);
    }

    public String get(final String key, int db) {
        return redisTemplate.execute(new RedisCallback<String>() {
            @Override
            public String doInRedis(RedisConnection connection)
                    throws DataAccessException {
                if (dbIndex != db)
                    connection.select(db);
                RedisSerializer<String> serializer = redisTemplate
                        .getStringSerializer();
                byte[] resultByte = connection.get(serializer
                        .serialize(key));
                if (resultByte != null) {
                    String result = redisTemplate.getStringSerializer()
                            .deserialize(resultByte);
                    if (!result.equals("nil"))
                        return result;
                }
                return null;
            }
        });
    }

    public String rPop(final String key) {
        return rPop(key, dbIndex);
    }

    public String rPop(final String key, int db) {
        return getRedisTemplate().execute(new RedisCallback<String>() {
            @Override
            public String doInRedis(RedisConnection connection)
                    throws DataAccessException {
                if (dbIndex != db)
                    connection.select(db);
                RedisSerializer<String> serializer = redisTemplate
                        .getStringSerializer();
                byte[] keyByte = connection.rPop(serializer
                        .serialize(key));
                if (keyByte != null) {
                    String key = serializer.deserialize(keyByte);
                    if (!key.equals("nil")) {
                        return key;
                    }
                }
                return null;
            }
        });
    }

    public void expire(final String key, final long seconds) {
        expire(key, seconds, dbIndex);
    }

    public void expire(final String key, final long seconds, int db) {
        redisTemplate.execute(new RedisCallback<Integer>() {
            @Override
            public Integer doInRedis(RedisConnection connection)
                    throws DataAccessException {
                if (dbIndex != db)
                    connection.select(db);
                RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
                connection.expire(serializer.serialize(key), seconds);
                return null;
            }
        });
    }

    public void hmSet(final String key, final Map<byte[], byte[]> map, final long seconds) {
        hmSet(key, map);
        expire(key, seconds);
    }

    public void hmSet(final String key, final Map<byte[], byte[]> map, final long seconds, int db) {
        hmSet(key, map, db);
        expire(key, seconds, db);
    }

    public void hmSet(final String key, final Map<byte[], byte[]> map) {
        hmSet(key, map, dbIndex);
    }

    public void hmSet(final String key, final Map<byte[], byte[]> map, int db) {
        redisTemplate.execute(new RedisCallback<Integer>() {
            @Override
            public Integer doInRedis(RedisConnection connection)
                    throws DataAccessException {
                if (dbIndex != db)
                    connection.select(db);
                RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
                connection.hMSet(serializer.serialize(key), map);
                return null;
            }
        });
    }

    public void hmSetWithStrKey(final String key, final Map<String, String> map) {
        hmSetWithStrKey(key, map, dbIndex);
    }

    public void hmSetWithStrKey(final String key, final Map<String, String> map, int db) {
        final Map<byte[], byte[]> bmap = new HashMap<byte[], byte[]>();
        Set<String> keys = map.keySet();
        for (String ky : keys) {
            if (ky == null || map.get(ky) == null) {
                continue;
            }
            try {
                bmap.put(ky.getBytes("UTF-8"), map.get(ky).getBytes("UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        redisTemplate.execute(new RedisCallback<Integer>() {
            @Override
            public Integer doInRedis(RedisConnection connection)
                    throws DataAccessException {
                if (dbIndex != db)
                    connection.select(db);
                RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
                connection.hMSet(serializer.serialize(key), bmap);
                return null;
            }
        });
    }

    public Boolean hSet(final String key, final String field, final String value) {
        return hSet(key, field, value, dbIndex);
    }

    public Boolean hSet(final String key, final String field, final String value, int db) {
        if (field == null || value == null) {
            return false;
        }
        return redisTemplate.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection connection)
                    throws DataAccessException {
                if (dbIndex != db)
                    connection.select(db);
                RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
                return connection.hSet(serializer.serialize(key), serializer.serialize(field), serializer.serialize(value));
            }
        });
    }

    public List<String> getKey(final String key) {
        return getKey(key, dbIndex);
    }

    public List<String> getKey(final String key, int db) {
        return redisTemplate.execute(new RedisCallback<List<String>>() {
            @Override
            public List<String> doInRedis(RedisConnection connection)
                    throws DataAccessException {
                if (dbIndex != db)
                    connection.select(db);
                RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
                Set<byte[]> result = connection.keys(serializer.serialize(key));
                List<String> returnList = new ArrayList<String>(result.size());
                for (byte[] entry : result) {
                    String value = serializer.deserialize(entry);
                    if (value != null && !value.equals("null") && !value.equals("nil"))
                        returnList.add(value);
                }
                return returnList;
            }
        });
    }

    public void set(final String key, String value) {
        set(key, value, dbIndex);
    }

    public void set(final String key, String value, int db) {
//        SimpleDateFormat sdf = new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss.S]");
        redisTemplate.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection connection)
                    throws DataAccessException {
                if (dbIndex != db)
                    connection.select(db);
                RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
                connection.set(serializer.serialize(key), serializer.serialize(value));
                return true;
            }
        });
    }

    public void log(final String logKey, String info) {
        log(logKey, info, dbIndex);
    }

    public void log(final String logKey, String info, int db) {
        SimpleDateFormat sdf = new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss.S]");
        final String logInfo = sdf.format(new Date()) + info;
        redisTemplate.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection connection)
                    throws DataAccessException {
                if (dbIndex != db)
                    connection.select(db);
                RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
                connection.set(serializer.serialize(logKey), serializer.serialize(logInfo));
                connection.expire(serializer.serialize(logKey), 3600L);
                return true;
            }
        });
    }

    public void del(final String key) {
        del(key, dbIndex);
    }

    public void del(final String key, int db) {
        redisTemplate.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection connection)
                    throws DataAccessException {
                if (dbIndex != db)
                    connection.select(db);
                return connection.del(redisTemplate.getStringSerializer().serialize(key));
            }
        });
    }

    public Boolean exists(final String key) {
        return exists(key, dbIndex);
    }

    public Boolean exists(final String key, int db) {
        return redisTemplate.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection connection)
                    throws DataAccessException {
                if (dbIndex != db) {
                    connection.select(db);
                }
                return connection.exists(redisTemplate.getStringSerializer().serialize(key));
            }
        });
    }
}