package com.wplay.redis.mq.pushPull;



/**
 * <p>
 * ��ʱ���ӿڣ���ʱ����ʱɨ����Ҫ��doing�з�����task�������ᵽpending������
 * </p>
 *
 * @author James
 * @version 1.0
 * @Date 18/01/10
 */
public class TimerScan {

    public static void init() {
        scan();
    }

    private static void scan() {
        MessageRePending.rePending();
        //        RedisClient.doWithOut(jedis -> jedis.());
    }
}
