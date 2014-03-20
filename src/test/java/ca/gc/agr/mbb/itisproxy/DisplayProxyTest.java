package ca.gc.agr.mbb.itisproxy;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.client.WebTarget;
import java.io.IOException;

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
public class DisplayProxyTest 
{
   private final static Logger LOGGER = Logger.getLogger(ca.gc.agr.mbb.itisproxy.TestInfo.TEST_LOG); 

    Proxy proxy = null;

    @Before
    public void init()
    {
	Properties props = new Properties();
	ca.gc.agr.mbb.itisproxy.TestInfo.init();
	props.setProperty(ProxyImpl.NO_CACHING_KEY, "true");
	props.setProperty(CachingProxyImpl.CACHE_LOCATION_KEY, TestUtil.getTestDbDir());
	props.setProperty(ca.gc.agr.mbb.itisproxy.sqlite3.SqliteProxyImpl.SQLITE3_URL_KEY, ca.gc.agr.mbb.itisproxy.sqlite3.SqliteProxyImplTest.SQLITE3_URL);
	props.setProperty(ProxyInfo.PROXY_IMPL_KEY, ProxyImpl.class.getName());
	proxy = (ProxyImpl)ProxyImpl.instance(props);
	proxy = ProxyImpl.displayInstance(proxy);
    }

    @After
    public void done()
    {
	try{
	    proxy.close();
	}catch(IOException e){
	    e.printStackTrace();
	}
	proxy = null;
    }

    @Test
    public void searchByScientificName_ShouldWorkWithGoodArguments()
    {
	String search = ProxyImplTest.searchTermGoodTardigrada;
	int startPage = 0;
	int endPage = 10;

	LOGGER.info("searchByScientificName_ShouldWorkWithGoodArguments name=" + search + "  startPage=" + startPage + " endPage=" + endPage);
	List<ItisRecord> results = search();
	Assert.assertNotNull(results);
    }

    @Test
    public void searchByScientificName_ShouldOnlyHaveDisplayValues()
    {
	String search = ProxyImplTest.searchTermGoodTardigrada;
	int startPage = 0;
	int endPage = 10;

	LOGGER.info("searchByScientificName_ShouldWorkWithGoodArguments name=" + search + "  startPage=" + startPage + " endPage=" + endPage);

	List<ItisRecord> results = search();
	ItisRecord rec = results.get(0);
	Assert.assertTrue(results != null
			  && rec.getTsn() != null
			  && rec.getCombinedName() != null
			  && rec.getVernacularNames() != null
			  && rec.getNameAuthor() == null
			  && rec.getKingdom() == null
			  && rec.getRank() == null
			  );
    }


    List<ItisRecord> search(){
	String search = ProxyImplTest.searchTermGoodTardigrada;
	int startPage = 0;
	int endPage = 10;
	List<ItisRecord> results = null;
	try{
	    results = proxy.searchByScientificName(search, startPage, endPage);
	} catch (Throwable e) {
	    e.printStackTrace();
	    Assert.fail();
	}
	return results;
	
    }



}
