package com.lncn.rsql;

import com.lncn.rsql.cache.RCacheManager;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Classname LogTest
 * @Description TODO
 * @Date 2022/7/29 12:35
 * @Created by byco
 */
public class LogTest {

    private static final Logger log = LoggerFactory.getLogger(LogTest.class);

    @Test
    public void Log_Test(){
        //Arrange
        log.info("asdasd","123213123");
        //Act
        //Assert
    }
}
