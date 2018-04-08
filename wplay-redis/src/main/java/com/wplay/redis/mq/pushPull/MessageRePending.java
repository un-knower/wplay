package com.wplay.redis.mq.pushPull;

import com.alibaba.fastjson.JSON;
import com.wplay.redis.client.RedisClient;
import com.wplay.redis.mq.pushPull.constants.RedisMessageQueueConstants;

/**
 * <p>
 *     ��Ϣdoing���ڻ���δ���ѳɹ�ʵ��
 * </p>
 *
 * @author James
 * @version 1.0
 * @Date 18/01/10
 */
public class MessageRePending {
    // Ĭ�����������3s
    private static final int maxTime= 1000 * 3;
    /**
     * ��������ṩ�����°�doing���ᵽpending��
     * <br>���￼��������delayedQueueʵ��
     */
    public static void rePending() {
        // TODO:ɨ��doing�й��ڵ�����
        RedisClient.doWithOut(jedis -> jedis.lrange(RedisMessageQueueConstants.consumerQueueName, 0, -1));
        // ��ȡ���Ԫ�صĿ�ʼִ��ʱ��
        long execTime = JSON.parseObject("a").getLongValue("execTime");
        // �ж��Ƿ�ʱ
        if (execTime + maxTime > System.currentTimeMillis()) {
            // TODO:�������ᵽpending������
        }
    }
}
