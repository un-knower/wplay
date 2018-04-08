package com.wplay.redis.mq.pushPull;

import com.alibaba.fastjson.JSON;
import com.wplay.redis.client.RedisClient;
import com.wplay.redis.mq.pushPull.bean.RedisQueueMessage;
import com.wplay.redis.mq.pushPull.bean.User;
import com.wplay.redis.mq.pushPull.constants.RedisMessageQueueConstants;


/**
 * ��Ϣ����ӿڡ����ﲻ����MessageConverter�ĸ��ֻ����textMessage��һ����˵��JSON��
 * @author James
 * @version 1.0
 * @Date 18/01/10
 */
public class MessageHandler {

    /**
     * <p>
     * ����ִ�еĵط�,��������Լ��Խ�����ӿ�,��Ҫ������ִ��֮��
     * <br>����fastJson���л�֮���classKey���ж�
     * </p>
     *
     * @param message
     */
    public static void onMessage(String message) {
        try {
            String clazz = JSON.parseObject(message).getString("classKey");
            User user = null;
            if (clazz.toLowerCase().equals("user")) {
                user = JSON.parseObject(message, User.class);
            }
            // ģ����������
            System.out.println(user.getId() + " is doing......");
            Thread.sleep(3000);
            System.out.println(user.getId() + " done......");
            // ��redis��ɾ����Ϣ
            completedTaskDelMessageFromRedis(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * ��Э�����ʽ����
     *
     * @param message
     */
    public static void onMessage2(String message) {
        try {

            RedisQueueMessage redisQueueMessage =
                JSON.parseObject(message, RedisQueueMessage.class);
            int bodyCode = redisQueueMessage.getBodyCode();
            User user = null;
            if (bodyCode == 102) {
                user = JSON.parseObject(message).getObject("body", User.class);
            }
            // ģ����������
            System.out.println(user.getId() + " is doing......");
            Thread.sleep(3000);
            System.out.println(user.getId() + " done......");
            // ��redis��ɾ����Ϣ
            completedTaskDelMessageFromRedis(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * �������ɾ��redis�����е�message��Ϣ
     */
    public static void completedTaskDelMessageFromRedis(String message){
        // ɾ��doing�е����񣬱�ʾ�������
        long result = RedisClient.domain(
            jedis -> jedis.lrem(RedisMessageQueueConstants.consumerQueueName, 1, message));
        // ˵���������Ѿ�����doing������
        if (result != 1) {
            long resultPending = RedisClient
                .domain(jedis -> jedis.lrem(RedisMessageQueueConstants.queueName, 1, message));
            // ˵�����ٴ�������
            if(resultPending!=1){
                //TODO:�����ظ����ѵ�����Ĵ���
                //messageQueueDoingError(message);
            }
        }
    }


    // �������û���¼�ظ�����
    // abstract void messageQueueDoingError(String message);
}
