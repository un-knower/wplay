
## redisses-lock  

redisses-lock��һ������redisʵ�ֵķֲ�ʽ��

### ʹ�÷�ʽ  

```
    RLock rLock = new RLock();
    // lockKeyΪҪ��ס���ַ���
    rLock.lock(lockKey);
        try {
            //TODO doSomeThing
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            rLock.unlock(lockKey);
        }
```

### ʵ�ַ�ʽ  

redis�ṩ��һ��SetNX����,���ǵ�key�����ڵ�ʱ���������key��value  

���ǿ��Ի������������չ��ʵ�����ǵķֲ�ʽ��,ͬʱ����expire��������key��ʱʱ��,  

����������Ľ��̹ҵ�֮���ͷ�������ɵ�����.  

redis���ṩ����SetNX��expire����ԭ����һ������������,ʹ�ֲ�ʽ�����ӿɿ���

### �ŵ�  

��ǿ�������ļ�⣬һ�����������Ĭ�ϵ������ʱ��30s    

���30�����ͶԴ洢 valueֵ���м��  

����multi���Ƽ�ǿ�˶�����֮���ٴ����¾�������ͬʱ��ȡ��������  

### ��Ҫ�Ż��ĵط�

Ŀǰ�����������ڳ��Ի�ȡ��ʧ��֮��ʹ��locksupport�����ི�̹߳���3s  

׼���ο�redisson��lock����(pub/sub)�������ȴ����ͷ���֪ͨ  
