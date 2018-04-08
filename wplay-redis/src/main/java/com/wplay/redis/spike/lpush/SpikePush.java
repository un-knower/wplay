package com.wplay.redis.spike.lpush;


import com.wplay.redis.client.RedisClient;
import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.Transaction;

import java.util.List;

/**
 * <p>
 * ����redis�����������
 * </p>
 *
 * @author James
 * @version 1.0
 * @Date 18/01/10
 */
public class SpikePush {
    /**
     * ���������
     */
    int spikeMaxGoodNumber = 2;

    String purchaseKey = "PURCHASE:KEY";
    String spikeMaxGoodNumberKey = "SPIKE:MAX:GOOD:NUMBER:KEY";

    /**
     * ��������
     * <description>
     * 1. �����жϳɹ��������˵�����
     * 2. �ж��Ƿ��Ѿ����
     * 3. ����cas������
     * 4. �ɹ���������+1
     * 5. �������������û�
     * 6. �ж�ִ�н��
     * </description>
     *
     * @param userId
     */
    public boolean spike(String userId) {
        return RedisClient.domain(redis -> {
            String watchResult = redis.watch(spikeMaxGoodNumberKey);
            // ����cas�Ƿ�ɹ�
            if (!"OK".equals(watchResult))
                return false;
            // �ж��Ƿ��Ѿ����,������Ƽ��ķ�ʽ���������ε������������
            // ��nginxÿ��������ͬһ��userId���øýӿڵĴ���
            Boolean isBuy = redis.sismember(purchaseKey, userId);
            // ��ֹռ�ù�������
            if (isBuy) {
                return false;
            }
            // ��ȡ�Ѿ�������û���
            String spikeNum = redis.get(spikeMaxGoodNumberKey);
            int spikeNumber = StringUtils.isBlank(spikeNum) ? 0 : Integer.parseInt(spikeNum);
            // �ж��Ƿ񳬹������������޶�
            if (spikeNumber >= spikeMaxGoodNumber) {
                return false;
            }
            // ��������
            Transaction tx = redis.multi();
            // ����������Ա������
            tx.set(spikeMaxGoodNumberKey, spikeNumber + 1 + "");
            // �ύ����
            List<Object> result = tx.exec();
            // ��������ִ�н���ж��Ƿ������ɹ�
            if (result == null || result.isEmpty()) {
                redis.unwatch();
                return false;
            } else {
                // ���뵽�����ɹ����û�����
                tx.sadd(purchaseKey, userId);
                return true;
            }
        });
    }

    /**
     * �÷���������������������(�ӳٲ鿴��������)
     * ����:��Ҫ�������������
     * ���ַ�ʽ�������ķ�ʽ��ֻ��Ҫ������ʱ��ȡ����������Ķ���ֵ�����ݼ���
     * @param userId
     * @param purchaseKey
     * @return
     */
    public boolean spikeUseLrem(String userId, String purchaseKey) {
        return RedisClient.domain(redis -> {
            long spikeNum = redis.llen(purchaseKey);
            try {
                // ģ��4ms��redis���ݻ�ȡ�������ӳٵ����
                Thread.sleep(4);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (spikeNum >= spikeMaxGoodNumber) {
                return false;
            }
            // ���뵽�����ɹ����û�����
            Long purchaseResult = redis.sadd(purchaseKey, userId);
            return purchaseResult == 1;
        });
    }
}
