package ca.gc.agr.mbb.itisproxy.wsclient;

import java.util.Iterator;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.client.WebTarget;

import ca.gc.agr.itis.itismodel.ItisRecord;
import ca.gc.agr.mbb.itisproxy.DataConverter;
import ca.gc.agr.mbb.itisproxy.JsonDataConverter;
import ca.gc.agr.mbb.itisproxy.SearchService;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class WSTest 
{
    private final static Logger LOGGER = Logger.getLogger(ca.gc.agr.mbb.itisproxy.TestInfo.TEST_LOG); 

    private static DataConverter dataConverter = new JsonDataConverter();

    @BeforeClass
    public static void init()
    {
	ca.gc.agr.mbb.itisproxy.TestInfo.init();
	WSState.USER_AGENT = WSState.USER_AGENT + ":: junit tests";
    }

    @Test(timeout=10000)
    public void serviceSearchByScientificName(){
	Properties getParams = new Properties();
	getParams.setProperty("srchKey","Tardigrada");
	
	Object results = ws(WSState.SERVICE_SEARCH_BY_SCIENTIFIC_NAME, getParams);

	Assert.assertTrue(results != null);
    }

    @Test(timeout=10000)
    public void serviceSearchForAnyMatchPaged_ShouldWorkWithGoodParameters(){
	Properties getParams = new Properties();
	getParams.setProperty("srchKey", "Tardigrada");
	getParams.setProperty("pageSize", "5");
	getParams.setProperty("pageNum", "1");
	getParams.setProperty("ascend", "false");
	
	Object results = ws(WSState.SERVICE_SEARCH_FOR_ANY_MATCH_PAGED, getParams);
	Assert.assertTrue(results != null);
    }

    @Test(timeout=20000)
    public void serviceSearchByScientific_ShouldWorkWithNameUnknown(){
	Properties getParams = new Properties();
	getParams.setProperty("srchKey","Tardigrazzzzzzzda");
	
	Object results = ws(WSState.SERVICE_SEARCH_BY_SCIENTIFIC_NAME, getParams);
	Assert.assertTrue(results != null);
    }


    @Ignore
    private Object ws(String targetKey, Properties getParams){
	SearchService ws = new WS();
	LOGGER.info("Target: " + targetKey + "   GET Properties: " + getParams);
	return ws.search(targetKey, getParams, dataConverter, ItisRecord.class);
    }


    @Test(timeout=20000)
    public void getKingdoms(){
	Properties getParams = new Properties();
	getParams.setProperty("srchKey","Tardigrada");
	
	Object results = ws(WSState.SERVICE_SEARCH_BY_SCIENTIFIC_NAME, getParams);

	Assert.assertTrue(results != null);
    }

}
