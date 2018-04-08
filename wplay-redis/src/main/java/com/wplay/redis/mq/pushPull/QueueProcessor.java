package com.wplay.redis.mq.pushPull;

import com.wplay.redis.client.RedisClient;
import com.wplay.redis.mq.pushPull.constants.RedisMessageQueueConstants;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ��Ϣ�������������߶��߼�
 * @author James
 * @version 1.0
 * @Date 18/01/10
 */
public class QueueProcessor {

    private int timeout = 30;

    /**
     * �ػ��߳�
     */
    private Thread daemonThread;

    private ExecutorService executor = Executors.newFixedThreadPool(30);

    public void setExecutor(ExecutorService executor) {
        this.executor = executor;
    }

    @PostConstruct
    public void init() {
        daemonThread = new Thread(() -> process(executor));
        daemonThread.setDaemon(true);
        daemonThread.setName("Redis Queue Daemon Thread");
        daemonThread.start();
    }

    public void destroy() {
        // TODO
    }

    /**
     * Default constructor for convenient dependency injection via setters.
     */
    public QueueProcessor() {
        init();
    }

    /**
     * �첽ִ�С���Ϣ��������executorִ�С�
     *
     * @param executor
     * @throws Exception
     */
    public void process(ExecutorService executor) {

        while (true) {
            // ȡ��Ҫ���ѵ���Ϣ
            final String messages = RedisClient.domain(jedis -> jedis
                .brpoplpush(RedisMessageQueueConstants.queueName,
                    RedisMessageQueueConstants.consumerQueueName, timeout));
            //            List<String> messages =
            //                RedisClient.domain(jedis ->jedis.brpop(this.timeout, queueName));
            // final String payload = messages.get(1);
            // final String payload = messages;
            submitTask(executor, messages);
        }
    }

    private void submitTask(ExecutorService executor, final String payload) {
        if (StringUtils.isNotBlank(payload)) {
            executor.execute(() -> MessageHandler.onMessage(payload));
            //Future future = executor.submit(() -> MessageHandler.onMessage(payload));
            //future.cancel(true);
        }
    }

    public static void main(String[] a) {
        QueueProcessor queueProcessor = new QueueProcessor();
        for (; ; )
            ;
    }
}
