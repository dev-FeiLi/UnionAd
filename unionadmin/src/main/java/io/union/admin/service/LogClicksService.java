package io.union.admin.service;

import io.union.admin.dao.LogClicksRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Administrator on 2017/7/31.
 */
@Service
public class LogClicksService {
    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private LogClicksRepository logClicksRepository;


}
