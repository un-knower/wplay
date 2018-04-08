package com.wplay.redis.lock;

import com.wplay.redis.client.RedisClient;
import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.Transaction;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * <p>
 * �ο����µ�ַ:
 * http://blog.csdn.net/ugg/article/details/41894947
 * http://www.blogjava.net/caojianhua/archive/2013/01/28/394847.html
 * </p>
 *
 * @author James
 * @version 1.0
 * @Date 18/01/10
 */
public class RLock {

    /**
     * �ֲ�ʽ����ǰ׺
     */
    private static final String lockPrefix = "RLOCK:";
    private static final String subPrefix = "-";

    /**
     * ���ȴ�ʱ��,��λΪ��
     */
    private static final int maxLockSecond = 30;

    /**
     * ������Ϊ����һ���汾����ɾ��ʱ,������ӵ�����Ƿ�Ϊ��ǰҪunlock���̵߳ı�־����Ԥ���ֶ�
     */
    String uid = UUID.randomUUID().toString();

    /**
     * �̻߳�ȡ��ʧ�ܵ�ʱ������ʱ��
     */
    private static final long parkNanosTime =
        TimeUnit.NANOSECONDS.convert(3, TimeUnit.MILLISECONDS);

    /**
     * ��������
     *
     * @param key
     * @param time
     * @param unit
     * @return
     */
    public boolean tryLock(String key, long time, TimeUnit unit) throws InterruptedException {
        long nanosTimeout = TimeUnit.NANOSECONDS.convert(time, unit);
        if (time <= 0L)
        {  return false;}
        // ���ڵ�nanoTime
        final long deadline = System.nanoTime() + nanosTimeout;
        for (; ; ) {
            // ���Ի�ȡ��
            if (tryAcquire(key)) {
                return true;
            }
            nanosTimeout = deadline - System.nanoTime();
            if (nanosTimeout <= 0L)
            { return false;}
            // �����߳�
            parkThread(parkNanosTime);
            // ֧���߳��ж�
            if (Thread.interrupted())
            {  throw new InterruptedException();}
        }
    }

    /**
     * ����
     *
     * @param key
     */
    public void lock(String key) {
        // ��һ��������߳�sleep(N��)Ȼ�����³��Ի�ȡ��
        // �ڶ��濼�ǰ�ÿ�����������̷߳ŵ�һ���ȴ�������
        // Ȼ����ݵȴ����е�����(���������ļ������)����̬�滮���Ի�ȡ����ʱ��
        // ��Ϊ����Ļ�ȡ���Ǵ�redis���������Ĳ���,���Ժ���ͨ��lock��ȡ��ͬ
        // ����������Ҫ���Ƕ�redis����ȡ��Ƶ��

        // ѭ����ȡ��
        for (; ; ) {
            // �鿴�Ƿ��ȡ����
            if (tryAcquire(key)) {
                return;
            }
            // �̹߳���һ��ʱ���������Ի�ȡ��
            parkThread(parkNanosTime);
        }
    }

    /**
     * ����ǰ�߳�
     */
    private void parkThread(long nanoTime) {
        LockSupport.parkNanos(nanoTime);
    }

    /**
     * ���Ի�ȡ��</br>
     * ��������Ļ�ȡ��ʽ�Ƿǹ�ƽʽ�Ļ�ȡ
     * û��һ�������ȳ��Ķ�������,�պ�����չ
     *
     * @param key
     * @return
     */
    private boolean tryAcquire(String key) {
        final String keys = lockPrefix + key;
        return RedisClient.domain(redis -> {
            long currentTimeMillis = System.currentTimeMillis();
            // ��ʱ��ʱ��
            long expireTime = System.currentTimeMillis() + maxLockSecond * 1000;
            // һ��ϵͳʱ��+uid�����ֵ
            String lockValue = expireTime + subPrefix + uid;
            // ���Ի�ȡ��,����redis�ṩ��setNx��expire����ԭ���Ե�һ������
            String res = redis.set(keys, lockValue, "NX", "PX", maxLockSecond * 1000);
            // ��ȡ���ɹ������
            if (res != null && "OK".equals(res)) {
                // ���ù���ʱ��,��ֹ�̱߳���������
                redis.expire(keys, maxLockSecond);
                return true;
            } else {
                /**
                 * setnx��ȡ��ʧ��,�����п����ϴλ�������߳��Ѿ�����
                 * ����ָ����setnx�ɹ������ǻ�û���ü����ù���ʱ���ʱ�����
                 * ������Ҫ�����ж����Ƿ���ʧЧ,�����½������ľ���
                 * ������Ҫ�ȿ���watch����,������֤֮���жϲ�����ԭ����
                 */
                redis.watch(keys);
                // ��ȡ���ڵ�ֵ
                String oldValue = redis.get(keys);
                if (StringUtils.isNoneBlank(oldValue)) {
                    String[] valueResult = oldValue.split(subPrefix);
                    // ��������ʱ�����
                    if (Long.parseLong(valueResult[0]) < currentTimeMillis) {
                        /**
                         * ����Ϊʲôû�в�ȡ�ο���������getSet�ķ�ʽ��
                         * ����һ�£�������ǵ�c1����
                         * c2����getSet��ȡʧ��(����c3�Ȼ�ȡ���ɹ�)
                         * ��ʱ���������value����c2��value
                         * ������ʱ��c4���³��Ի�ȡ���Ļ��ͻ������,��Ϊ��������c2��ֵ
                         * ��ô����Ҫc2�ڵ���getSet��ȡʧ��֮���oldֵ��������ȥ
                         * ��ô��ʱ�����c3�������ˣ�Ҫ�ͷ�������ô��ʱ��c2�ְ�c3��ֵ���ȥ��
                         * ���ǿ��ܾ͵���Ϊc3�ǻ�ȡ���ˡ�����ô��ʱ���ֵõȵ�c3��������ʱ��</br>
                         * ���ֲ�����һ�ֲ���Ҫ���˷�,��redis�ṩ����(����cas)��ԭ�Ӳ������㹻��
                         */
                        // ��������
                        Transaction tx = redis.multi();
                        // ִ�в���,�˲�������ԭ����
                        tx.set(keys, lockValue);
                        // �ύ����
                        List<Object> txResult = tx.exec();
                        // ��������ִ�н���ж��Ƿ�ɹ���ȡ����
                        if (txResult == null || txResult.isEmpty()) {
                            redis.unwatch();
                            return false;
                        } else {
                            // ����ִ�гɹ�,����ȡ�ɹ�
                            return true;
                        }
                    } else {
                        return false;
                    }
                } else {
                    // ���ֵλ�պܿ������ͷ����Ĳ����Ѿ����ٽ����
                    // Ϊ�˰�ȫ�����Ŀ�����Ҫ���µ���tryLock�����Ի�ȡ��
                    tryAcquire(keys);
                }
            }
            return false;
        });
    }

    /**
     * �ͷ���
     *
     * @param key
     * @return
     */
    public boolean unlock(String key) {
        return RedisClient.domain(redis -> {
            Long res = redis.del(lockPrefix + key);
            return res.equals(1L);
        });
    }

}
