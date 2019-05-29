package it.richkmeli.jframework.util;

import org.junit.Test;

public class LoggerTest {


    @Test
    public void tagTest(){
        Logger.info("info");
        Logger.error("error");
        Logger.error(new Exception("Exception"));

        assert true;
    }
}
