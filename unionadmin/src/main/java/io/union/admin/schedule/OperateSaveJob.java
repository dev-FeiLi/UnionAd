package io.union.admin.schedule;

import io.union.admin.entity.SysManage;
import io.union.admin.entity.SysOperateLog;
import io.union.admin.service.SysManageService;
import io.union.admin.service.SysOperateLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class OperateSaveJob {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final BlockingQueue<SysOperateLog> logQueue = new LinkedBlockingQueue<>();
    private final BlockingQueue<SysManage> manQueue = new LinkedBlockingQueue<>();


    public OperateSaveJob(@Autowired SysOperateLogService sysOperateLogService, @Autowired SysManageService sysManageService) {
        AtomicBoolean hasClose = new AtomicBoolean(false);
        ExecutorService executor = Executors.newCachedThreadPool();

        executor.submit(() -> {
            while (!hasClose.get()) {
                try {
                    SysOperateLog item = logQueue.poll(1, TimeUnit.SECONDS);
                    if (null != item) {
                        sysOperateLogService.save(item);
                    }
                } catch (Exception e) {
                    logger.error("save operate error: ", e);
                }
            }
        });

        executor.submit(() -> {
            while (!hasClose.get()) {
                try {
                    SysManage item = manQueue.poll(1, TimeUnit.SECONDS);
                    if (null != item) {
                        sysManageService.save(item);
                    }
                } catch (Exception e) {
                    logger.error("save manager error: ", e);
                }
            }
        });
    }

    public void addOperate(SysOperateLog operateLog) {
        logQueue.add(operateLog);
    }

    public void addManage(SysManage manage) {
        manQueue.add(manage);
    }
}
