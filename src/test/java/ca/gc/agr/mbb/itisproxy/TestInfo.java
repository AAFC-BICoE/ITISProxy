package ca.gc.agr.mbb.itisproxy;

import java.util.logging.Level;
import java.util.logging.Logger;

public class TestInfo{
    public static final String TEST_LOG="UnitTesting";
    private final static Logger LOGGER = Logger.getLogger(TEST_LOG); 

    public static void init()
    {
	LOGGER.setLevel(Level.INFO); 
    }

}
