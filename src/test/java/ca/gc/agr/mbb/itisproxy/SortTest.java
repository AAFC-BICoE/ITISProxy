package ca.gc.agr.mbb.itisproxy;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Properties;
import java.util.List;
import java.util.Collections;

import ca.gc.agr.mbb.itisproxy.CommonNameComparator;
import ca.gc.agr.mbb.itisproxy.Proxy;
import ca.gc.agr.mbb.itisproxy.ProxyImpl;
import ca.gc.agr.mbb.itisproxy.ScientificNameComparator;
import ca.gc.agr.mbb.itisproxy.entities.CommonName;
import ca.gc.agr.mbb.itisproxy.entities.ScientificName;
import ca.gc.agr.mbb.itisproxy.entities.SearchByCommonName;
import ca.gc.agr.mbb.itisproxy.entities.SearchByScientificName;
import ca.gc.agr.mbb.itisproxy.wsclient.WS;
import ca.gc.agr.mbb.itisproxy.wsclient.WSState;

@RunWith(JUnit4.class)
public class SortTest
{
    private final static Logger LOGGER = Logger.getLogger(ca.gc.agr.mbb.itisproxy.TestInfo.TEST_LOG); 

    ProxyImpl proxy = null;
    private SearchService searchService = new WS();

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
    }

    @After
    public void done()
    {
	proxy.close();
	proxy = null;
    }


    @Test
    public void searchByCommonName_ShouldBeSorted()
    {
	String search = ProxyImplTest.VALID_COMMON_NAMES[2];
	int startPage = 0;
	int endPage = 10;

	LOGGER.info("searchByScientificName_ShouldWorkWithGoodArguments name=" + search + "  startPage=" + startPage + " endPage=" + endPage);


	//SearchByCommonName sbcn = (SearchByCommonName)(ProxyImpl.genericSearch(search, 
	try{
	    CommonNameComparator commonNameComparator = new CommonNameComparator();
	    SearchByCommonName sbcn = (SearchByCommonName)ProxyImpl.genericSearch(search, false,
										  startPage, endPage,
										  WSState.PARAM_SRCH_KEY, WSState.SERVICE_SEARCH_BY_COMMON_NAME,
										  SearchByCommonName.class);

	    Collections.sort(sbcn.commonNames, commonNameComparator);
	    Assert.assertTrue(sortedByCommonName(sbcn.commonNames));
	}catch(Throwable t){
	    t.printStackTrace();
	    Assert.fail();
	}
    }

    @Test
    public void searchByScientific_ShouldBeSorted()
    {
	String search = ProxyImplTest.searchTermGoodTardigrada;
	int startPage = 0;
	int endPage = 10;

	try{
	    ScientificNameComparator scientificNameComparator = new ScientificNameComparator();
	    SearchByScientificName sbsn = (SearchByScientificName)ProxyImpl.genericSearch(search, false,
										  startPage, endPage,
										  WSState.PARAM_SRCH_KEY, WSState.SERVICE_SEARCH_BY_SCIENTIFIC_NAME,
										  SearchByScientificName.class);

	    Collections.sort(sbsn.scientificNames, scientificNameComparator);
	    Assert.assertTrue(sortedByScientificName(sbsn.scientificNames));
	}catch(Throwable t){
	    t.printStackTrace();
	    Assert.fail();
	}
    }

    private boolean sortedByCommonName(List<CommonName> commonNames){
	if(commonNames == null || commonNames.size() < 2){
	    return true;
	}
	for(int i=0; i<commonNames.size()-1; i++){

	    String thisOne = commonNames.get(i).commonName.toLowerCase();
	    String nextOne = commonNames.get(i+1).commonName.toLowerCase();
	    if(thisOne.compareTo(nextOne) > 0)
		return false;
	}
	return true;
    }

    private boolean sortedByScientificName(List<ScientificName> scientificNames){
	if(scientificNames == null || scientificNames.size() < 2){
	    return true;
	}
	for(int i=0; i<scientificNames.size()-1; i++){

	    String thisOne = scientificNames.get(i).combinedName.toLowerCase();
	    String nextOne = scientificNames.get(i+1).combinedName.toLowerCase();
	    if(thisOne.compareTo(nextOne) > 0)
		return false;
	}
	return true;
    }
}

