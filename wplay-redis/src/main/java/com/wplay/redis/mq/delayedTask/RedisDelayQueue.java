package com.wplay.redis.mq.delayedTask;

import com.wplay.redis.client.RedisClient;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.Tuple;

import java.util.Set;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * <p>
 * ����redis���ӳٶ���
 * </p>
 * <p>
 * TODO:�����ڿ����Ƿ���ֱ�Ӵ�delayQueue����������뵽�����list�У�����ֱ�Ӹ�ʹ���߷���Ԫ��ֵ
 *
 * @author James
 * @version 1.0
 * @Date 18/01/10
 * @see DelayQueue
 * @see ReentrantLock
 * @see Condition
 */
public class RedisDelayQueue {

    /**
     * �õ����Ĳ�������
     */
    private RedisDelayQueue() {
    }

    private static class LazyHolder {
        private static RedisDelayQueue instance = new RedisDelayQueue();
    }

    public static RedisDelayQueue getInstance() {
        return LazyHolder.instance;
    }

    /**
     * ���delayQueue��������
     */
    private final transient ReentrantLock lock = new ReentrantLock();

    /**
     * Condition signalled when a newer element becomes available at the head of
     * the queue or a new thread may need to become leader.
     */
    private final Condition available = lock.newCondition();

    /**
     * <p>
     * �ο���delayQueue���ڲ��ṹ
     * </p>
     * </br>
     * Thread designated to wait for the element at the head of
     * the queue.  This variant of the Leader-Follower pattern
     * (http://www.cs.wustl.edu/~schmidt/POSA/POSA2/) serves to
     * minimize unnecessary timed waiting.  When a thread becomes
     * the leader, it waits only for the next delay to elapse, but
     * other threads await indefinitely.  The leader thread must
     * signal some other thread before returning from take() or
     * poll(...), unless some other thread becomes leader in the
     * interim.  Whenever the head of the queue is replaced with
     * an element with an earlier expiration time, the leader
     * field is invalidated by being reset to null, and some
     * waiting thread, but not necessarily the current leader, is
     * signalled.  So waiting threads must be prepared to acquire
     * and lose leadership while waiting.
     */
    private Thread leader = null;

    /**
     * �洢�ӳ�����Ķ���
     */
    private static final String delayedQueueKeyName = "DelayedQueueSet";

    /**
     * ������Ҫִ�еĶ���
     */
    private static final String executeQueueKeyName = "DelayedQueueExecuteSet";

    /**
     * ���waitʱ��
     */
    private static final long MAX_WAIT_TIME = 1000 * 60 * 60;

    /**
     * ���
     * Inserts the specified element into this delay queue.
     *
     * @param businessObjectString
     * @param delay
     */
    public void offer(String businessObjectString, final long delay) {
        // �����ӳٶ���ʱ��
        long currentTimeMillis = System.currentTimeMillis();
        final long key = currentTimeMillis + delay + 1;

        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            RedisClient.doWithOut(redis -> {
                if (delay > 0) {
                    redis.zadd(delayedQueueKeyName, key, businessObjectString);
                } else {
                    redis.rpush(executeQueueKeyName, businessObjectString);
                }
            });
            // �жϼ����Ԫ���Ƿ���ڶ���
            if (key < peekScore()) {
                /** DelayQueue�������ж��Ƿ��Ƕ��� */
                leader = null;
                available.signal();
                /** end */
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * ���Ӳ���
     * <description>
     * �����take���ӷ����ںܴ�ĳ̶��ϲ�����DelayQueue�е�take��ʵ�ַ�ʽ
     * </description>
     *
     * @throws InterruptedException
     * @see #peek()
     * @see DelayQueue#take()
     */
    public Set<String> take() throws InterruptedException {
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        try {
            // ��for(;;)����while(true)
            // ��Ϊfor(;;)�����ָ���٣���ռ�üĴ���������û���ж���ת
            for (;;) {
                // ��ȡ���ڵ�ҵ�������Ϣ
                Set<String> elementValue = peek();
                // �ж��Ƿ��й��ڵ�Ԫ��,����еĻ�ֱ�ӳ���
                if (!elementValue.isEmpty()) {
                    return elementValue;
                }

                if (leader != null) {
                    // ������ǰ�߳�
                    available.await();
                } else {
                    Thread thisThread = Thread.currentThread();
                    leader = thisThread;
                    try {
                        // ��ȡ��Ԫ�ص�score
                        long firstElementDelayScore = peekScore();
                        // �������Ҫ�ȴ���ʱ��
                        long waitTime = firstElementDelayScore == MAX_WAIT_TIME ?
                            firstElementDelayScore :
                            firstElementDelayScore - System.currentTimeMillis();
                        // ������������ܻ��leaderΪ�յ�ʱ�����,��һ������Ч��������ʽ
                        available.await(waitTime, TimeUnit.MILLISECONDS);
                    } finally {
                        if (leader == thisThread)
                            leader = null;
                    }
                }
            }
        } finally {
            if (leader == null && !peek().isEmpty())
                available.signal();
            lock.unlock();
        }
    }

    /**
     * ��ѯ��key���ڵ�ǰʱ���
     * <p>
     * �˷�����
     * </p>
     *
     * @return
     */
    private Set<String> peek() {
        Long currentTimeMillis = System.currentTimeMillis();
        return RedisClient.domain(redis -> {
            // �������񱣳�ԭ����
            Transaction tx = redis.multi();
            // ��õ�����Ҫִ�еĶ���
            Response<Set<String>> getResult =
                tx.zrangeByScore(delayedQueueKeyName, 0, currentTimeMillis);
            Set<String> result = getResult.get();
            // ִ��ɾ��
            if (!result.isEmpty()) {
                tx.zremrangeByScore(delayedQueueKeyName, 0, currentTimeMillis);
            }
            // �ύ����
            tx.exec();
            return result;

        });

    }

    /**
     * ��ѯ����С��score
     *
     * @return
     */
    private long peekScore() {
        final Long currentTimeMillis = System.currentTimeMillis();
        return RedisClient.domain(redis -> {

            /**
             * ��ȡ��Score�ڵ�ǰʱ��~֮���#MAX_WAIT_TIME#ʱ���ڵĵ�����
             * ��Ȼ�����MAX_WAIT_TIME��ø���ʵ�ʵ�ʹ����������������߸���һ���������㷨��ƥ��һ�����ʵ�ֵ
             */
            Set<Tuple> tupleSet = redis
                .zrangeByScoreWithScores(delayedQueueKeyName, currentTimeMillis,
                    currentTimeMillis + MAX_WAIT_TIME);
            // ���MAX_WAIT_TIME��û����ͷ������ȴ�ʱ��MAX_WAIT_TIME
            if (tupleSet.isEmpty()) {
                return MAX_WAIT_TIME;
            } else {
                // ���ض��׵�scoreֵ
                Tuple tuple = tupleSet.iterator().next();
                return (long) tuple.getScore();
            }
        });
    }

    public static void main(String[] a) throws Exception {
        RedisDelayQueue redisDelayQueueApi = RedisDelayQueue.getInstance();
        redisDelayQueueApi.offer("sss", 60l * 1000l * 60l * 24l * 30l * 3l);
        redisDelayQueueApi.take();
    }
}
