package com.lncn.rsql.statement;

import com.lncn.rsql.Session;
import com.lncn.rsql.config.RsqlConfig;
import io.quarkus.test.junit.QuarkusTest;
import javax.inject.Inject;
import org.junit.jupiter.api.Test;

/**
 * @Classname SessionTest
 * @Description TODO
 * @Date 2022/7/22 13:45
 * @Created by byco
 */
@QuarkusTest
class SessionTest {

    @Inject
    RsqlConfig defaultConfig;

    @Test
    public void Inject_Test(){
        //Arrange
        Session session = new Session(null, defaultConfig, null);
        //Act
        //Assert
        System.out.println(session.getConfig().maxFetchSize());
    }

}