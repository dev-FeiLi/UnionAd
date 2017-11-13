package io.union.wap.common.zoo;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ZooUtils {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public final long TIMEOUT;
    private final String root;
    private CuratorFramework client = null;

    public ZooUtils(@Value("${spring.zookeeper.host-list}") String host, @Value("${spring.zookeeper.lock.root}") String root, @Value("${spring.zookeeper.timeout}") Long timout) {
        this.root = root;
        this.TIMEOUT = timout;
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        client = CuratorFrameworkFactory.newClient(host, retryPolicy);
        client.start();
    }

    public InterProcessMutex buildLock(String lockPath) throws Exception {
        lockPath = root + lockPath;
        if (null == client.checkExists().forPath(root)) {
            client.create().forPath(root);
        }
        if (null == client.checkExists().forPath(lockPath)) {
            client.create().forPath(lockPath);
        }
        return new InterProcessMutex(client, lockPath);
    }

    public void release(InterProcessMutex lock) {
        try {
            if (null != lock) {
                lock.release();
            }
        } catch (Exception e) {
            logger.error("release lock error: ", e);
        }
    }

    public void close() {
        if (null != client) {
            client.close();
            client = null;
        }
    }
}
