package io.union.admin.common.zoo;

import io.union.admin.common.redis.CacheLockPach;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class ZooUtils {
    final Logger logger = LoggerFactory.getLogger(getClass());

    public final long TIMEOUT;
    private final String root;
    private CuratorFramework client = null;
    private CacheLockPach cacheLockPach;

    public ZooUtils(@Value("${spring.zookeeper.host-list}") String host, @Value("${spring.zookeeper.lock.root}") String root,
                    @Value("${spring.zookeeper.timeout}") Long timout, @Autowired CacheLockPach cacheLockPach) {
        this.root = root;
        this.TIMEOUT = timout;
        this.cacheLockPach = cacheLockPach;
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        client = CuratorFrameworkFactory.newClient(host, retryPolicy);
        client.start();
    }

    public InterProcessMutex buildLock(String lockPath) throws Exception {
        try {
            lockPath = root + lockPath;
            try {
                if (null == client.checkExists().forPath(root)) {
                    client.create().forPath(root);
                }
            } catch (KeeperException.NodeExistsException e) {
                logger.error("create don't worry, node: " + lockPath);
            }
            try {
                if (null == client.checkExists().forPath(lockPath)) {
                    client.create().forPath(lockPath);
                }
            } catch (KeeperException.NodeExistsException e) {
                logger.error("create don't worry, node: " + lockPath);
            }
            return new InterProcessMutex(client, lockPath);
        } catch (Exception e) {
            logger.error("build path error: ", e);
        }
        return null;
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

    /**
     * 删除分布式锁产生的节点<br/>
     * 由于产生的node太多，导致了在获取children的时候就失去了connection<br/>
     * 所以要根据日期匹配具体的路径，匹配到了就删掉，时间间隔是30天
     *
     * @param path
     */
    public void deleteChild(String path) {
        LocalDate today = LocalDate.now(), yestoday = today.minusDays(1);
        LocalDate firstOfSep = today.minusDays(30);
        String todayDate = today.format(DateTimeFormatter.ISO_DATE);
        String yestoayDate = yestoday.format(DateTimeFormatter.ISO_DATE);
        while (firstOfSep.isBefore(today)) {
            try {
                String targetDate = firstOfSep.format(DateTimeFormatter.ISO_DATE);
                firstOfSep = firstOfSep.plusDays(1);
                String toPath = path.replace(todayDate, targetDate);
                toPath = toPath.replace(yestoayDate, targetDate);
                if (toPath.contains(targetDate)) {
                    cacheLockPach.delete(toPath);
                    String fullPath = root + toPath;
                    if (null != client.checkExists().forPath(fullPath)) {
                        client.delete().forPath(fullPath);
                    }
                }
            } catch (Exception e) {
                logger.error("delete error: ", e);
            }
        }
    }
}
