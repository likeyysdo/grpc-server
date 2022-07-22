package com.lncn.rsql;

import com.lncn.rsql.config.RsqlConfig;
import io.quarkus.test.junit.QuarkusTest;
import javax.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @Classname RsqlConfigTest
 * @Description TODO
 * @Date 2022/7/22 13:37
 * @Created by byco
 */
@QuarkusTest
class RsqlConfigTest {

    @Inject
    RsqlConfig config;

    @Test
    public void RsqlConfig_Test(){
        //Arrange

        //Act
        //Assert
        System.out.println(config.defaultFetchSize());
        System.out.println(config.minFetchSize());
        System.out.println(config.maxFetchSize());
        //System.out.println(config.defaultEnableCompress());
        Assertions.assertNotEquals(0,config.defaultFetchSize());
        Assertions.assertNotEquals(0,config.minFetchSize());
        Assertions.assertNotEquals(0,config.maxFetchSize());
    }
}