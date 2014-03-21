package ca.gc.agr.mbb.itisproxy;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.client.WebTarget;

import ca.gc.agr.mbb.itisproxy.TooManyResultsException;
import ca.gc.agr.itis.itismodel.ItisRecord;
import ca.gc.agr.itis.itismodel.TaxonomicRank;
import ca.gc.agr.mbb.itisproxy.Util;
import ca.gc.agr.mbb.itisproxy.entities.HierarchyRecord;
import ca.gc.agr.mbb.itisproxy.wsclient.WSState;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class MaxResultsTest 
{
    private final static Logger LOGGER = Logger.getLogger(ca.gc.agr.mbb.itisproxy.TestInfo.TEST_LOG); 

    ProxyImpl proxy = null;

    @Before
    public void init()
    {
	Properties props = new Properties();
	ca.gc.agr.mbb.itisproxy.TestInfo.init();
	props.setProperty(ProxyImpl.NO_CACHING_KEY, "true");
	props.setProperty(CachingProxyImpl.CACHE_LOCATION_KEY, TestUtil.getTestDbDir());
	props.setProperty(ProxyInfo.PROXY_IMPL_KEY, ProxyImpl.class.getName());
	props.setProperty(ProxyInfo.WEB_SERVICE_MAX_RESULTS_KBYTES_KEY, "12");
	proxy = (ProxyImpl)ProxyImpl.instance(props);
    }

    @After
    public void done()
    {
	proxy.close();
	proxy = null;
    }

    @Test
    public void searchByScientificName_ShouldFailWithTooManyResults()
    {
	String search = "tard";
	int startPage = 0;
	int endPage = 100;

	LOGGER.info("searchByScientificName_ShouldWorkWithGoodArguments name=" + search + "  startPage=" + startPage + " endPage=" + endPage);
	SearchResults results = null;
	try{
	    results = proxy.searchByScientificName(search, startPage, endPage);
	} catch (TooManyResultsException e) {
	    Assert.assertTrue(true);
	    return;
	} catch (Exception e) {
	    e.printStackTrace();
	}
	Assert.assertTrue(false);
    }


    @Test
    public void searchByCommonName_ShouldFailWithTooManyResults()
    {
	String search = "american";
	int startPage = 0;
	int endPage = 100;

	LOGGER.info("searchByCommonName_ShouldFailWithTooManyResults  name=" + search + "  startPage=" + startPage + " endPage=" + endPage);
	SearchResults results = null;
	try{
	    results = proxy.searchByCommonName(search, startPage, endPage);
	} catch (TooManyResultsException e) {
	    Assert.assertTrue(true);
	    return;
	} catch (Exception e) {
	    e.printStackTrace();
	    Assert.assertTrue(false);
	}
	Assert.assertTrue(false);
    }

}
