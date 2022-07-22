package org.acme.statement;

import io.quarkus.test.junit.QuarkusTest;
import javax.inject.Inject;
import org.acme.RsqlConfig;
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
        Session session = new Session(null, defaultConfig);
        //Act
        //Assert
        System.out.println(session.getConfig().maxFetchSize());
    }

}