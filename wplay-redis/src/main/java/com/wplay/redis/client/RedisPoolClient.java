package com.wplay.redis.client;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * <p>
 * redis���ӳؿͻ��˹�����
 * </p>
 *
 * @author James
 * @version 1.0
 * @Date 18/01/10
 */
public class RedisPoolClient {

    private RedisPoolClient() {
    }

    public static RedisPoolClient getInstance() {
        return LazyHolder.redisPoolClient;
    }

    private static class LazyHolder {
        private static final RedisPoolClient redisPoolClient = new RedisPoolClient();
    }

    // ���ӳ�
    private static JedisPool pool;

    private static final ResourceBundle bundle = ResourceBundle.getBundle("redis");

    public void initPool() {
        if (bundle == null) {
            throw new IllegalArgumentException("[redis.properties] is not found!");
        }
        // redis������Ϣ
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(Integer.valueOf(bundle.getString("redis.pool.maxTotal")));
        config.setMaxIdle(Integer.valueOf(bundle.getString("redis.pool.maxIdle")));
        config.setMaxWaitMillis(Long.valueOf(bundle.getString("redis.pool.maxWait")));
        config.setTestOnBorrow(Boolean.valueOf(bundle.getString("redis.pool.testOnBorrow")));
        config.setTestOnReturn(Boolean.valueOf(bundle.getString("redis.pool.testOnReturn")));

        // redis������Ϣ
        pool = new JedisPool(config, bundle.getString("redis.ip"),
            Integer.valueOf(bundle.getString("redis.port")),
            Integer.valueOf(bundle.getString("redis.connectionOutTime")),
            bundle.getString("redis.auth"));
    }


    public static void main(String[] args) {
        RedisPoolClient.getInstance().initPool();
        Jedis jedis = RedisPoolClient.getInstance().getJedis();




        jedis.zremrangeByLex("James","[09987:123","[��");
        Set<String> s = jedis.zrevrange("James", 0, 10);





        Iterator<String> it = s.iterator();
        while(it.hasNext()){
            System.out.println(it.next());
        }


    }

    /**
     * ��ȡJedisʵ��
     *
     * @return
     */
    public synchronized Jedis getJedis() {
        try {
            if (pool != null) {
                Jedis resource = pool.getResource();
                return resource;
            } else {
                // ��Ѷ�Ƶ�redis,3��Сʱû�����ݴ���ͻ�Ͽ�������,������Ϊ�����½���������
                initPool();
                return pool != null ? pool.getResource() : null;
            }
        } catch (Exception e) {
            //TODO: connect error log�ļ�¼....
        }
        return null;
    }

    /**
     * �ͷ�jedis��Դ
     *
     * @param jedis
     */
    public void returnResource(final Jedis jedis) {
        if (jedis != null && pool != null) {
            pool.returnResourceObject(jedis);

        }
    }

    /**
     * �ͷŶ����
     */
    public void destroy() {
        synchronized (pool) {
            if (pool != null) {
                pool.destroy();
            }
        }
    }
}
