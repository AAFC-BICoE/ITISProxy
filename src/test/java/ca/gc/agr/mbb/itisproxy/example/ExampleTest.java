package ca.gc.agr.mbb.itisproxy.example;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.Assert;

import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)

public class ExampleTest{

    @Test
    public final void runTest(){
	Example example = new Example();
	example.run();
    }

}
