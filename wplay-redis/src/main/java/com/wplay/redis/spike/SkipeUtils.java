package com.wplay.redis.spike;

import java.util.Random;

/**
 * <p>
 * һ�������,�������������,���ﵽһ�������������˷��Ž�������
 * </p>
 *
 * @author James
 * @version 1.0
 * @Date 18/01/10
 */
public class SkipeUtils {

    /**
     * ���͸���������취
     *
     * @param userId int���͵��û�id
     * @param weight Ȩ��ֵ1-9֮��,��������Ϊ8,�Ǿ���2�ɵ�ͨ������,8�ɵģ�
     * @return
     */
    public boolean randomAccess(int userId, int weight) {
        Random random = new Random(userId);
        int r = random.nextInt();
        return r > userId * 10 / weight;
    }
}
