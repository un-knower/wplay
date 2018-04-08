package com.wplay.redis.mq.pushPull;

import com.alibaba.fastjson.JSON;
import com.wplay.redis.client.RedisClient;
import com.wplay.redis.mq.pushPull.bean.RedisQueueMessage;
import com.wplay.redis.mq.pushPull.bean.User;
import com.wplay.redis.mq.pushPull.constants.RedisMessageQueueConstants;


/**
 * <p>
 *     �����߷�����Ϣģ��
 * </p>
 *
 * @author James
 * @version 1.0
 * @Date 18/01/10
 */
public class RedisQueueProducer {

    private long queueSize = 1000;

    public boolean pushToQueue(RedisQueueMessage registrationInfo) {
        try {
            final String jsonPayload = JSON.toJSONString(registrationInfo);
            RedisClient.doWithOut(jedis -> {
                Long result = jedis.lpush(
                    RedisMessageQueueConstants.queueName, jsonPayload);
                // Ϊ�˱�������ڴ������ֻ����1000����Ϣ
                // jedis.ltrim(QUEUE_MESSAGE_SUBSCRIBE, -queueSize, -1);
            });
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     *
     * @param registrationInfo
     * @return
     */
    public boolean pushToQueueV2(Object registrationInfo) {
        try {
            final String messageBody = JSON.toJSONString(registrationInfo);
            RedisClient.doWithOut(jedis -> {
                Long result = jedis.lpush(RedisMessageQueueConstants.queueName, messageBody);
                // Ϊ�˱�������ڴ������ֻ����1000����Ϣ
                // jedis.ltrim(QUEUE_MESSAGE_SUBSCRIBE, -queueSize, -1);
            });
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    public static void main(String[]a){
        int id =1000;
        RedisQueueProducer redisQueueProducer = new RedisQueueProducer();
        long start = System.currentTimeMillis();
        for(int i =0;i<3;i++){
            User user = new User();
            user.setId(String.valueOf(id + i));
            RedisQueueMessage<User> redisQueueMessage =  new RedisQueueMessage<>(102,user);
            //        if(redisQueueProducer.pushToQueue(redisQueueMessage)){
            //            System.out.println("success");
            //        }
            redisQueueProducer.pushToQueueV2(user);
        }
        System.out.println("�ܹ����ѵ�ʱ����"+(System.currentTimeMillis()-start));
    }
}
