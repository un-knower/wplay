package com.wplay.redis.mq.pushPull.bean;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author James
 * @version 1.0
 * @Date 18/01/10
 */
public class BaseBean implements Serializable {
    // ������е�ʱ���
    private long timeMillis;
    // Ϊ���Ժ�֧�ּ�Ⱥ����չ,Ѱ�Ҷ�Ӧ��server�ڴ����id
    private String serverId;
    // ���¶������ϢId,�����Ϊ���������������ݵ���
    private String messageId;
    // ��Ϣ���ѵ�ʱ��(���뵽doing���е�ʱ��)
    private long execTime;
}
