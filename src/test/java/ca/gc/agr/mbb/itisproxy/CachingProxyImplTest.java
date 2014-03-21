package ca.gc.agr.mbb.itisproxy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import ca.gc.agr.itis.itismodel.ItisRecord;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class CachingProxyImplTest 
{

    private final static Logger LOGGER = Logger.getLogger(ca.gc.agr.mbb.itisproxy.TestInfo.TEST_LOG); 

    String[] searchTerms = {"tard",
			    //"tarz", "abaz", "acaz", "ursi"
    };



    @Test
    public void loadRecordTest(){
	System.err.println("Start loadRecordTest");
	ca.gc.agr.mbb.itisproxy.TestInfo.init();

	Properties props = new Properties();
	//String queries = Util.makeWholeAlphabet();
	//String queries = "abn";
	    //String queries = "an" 
	    //+ CachingProxyImpl. WARM_CACHE_QUERIES_SEPARATOR + "ba";

	//props.setProperty(CachingProxyImpl.WARM_CACHE_QUERIES_KEY, queries);
	
	props.setProperty(CachingProxyImpl.CACHE_LOCATION_KEY, TestUtil.getTestDbDir());

	props.setProperty(CachingProxyImpl.TTL_MINUTES_KEY, "1000000");
	props.setProperty(CachingProxyImpl.BDB_DEFERRED_WRITE_SIZE_KEY, "16");
	props.setProperty(ca.gc.agr.mbb.itisproxy.ProxyInfo.WEB_SERVICE_MAX_RESULTS_KBYTES_KEY, "99999999");
	
	Proxy proxy;
	proxy = ProxyImpl.instance(props);
	//searchTerms = Util.makeWholeAlphabetArray();

	for(String term: searchTerms){
	    searchByScientificName(term, proxy);
	}
	try{
	    proxy.close();
	}catch(IOException e){
	    e.printStackTrace();
	    Assert.fail();
	}
    }

    //@Test
    public void multithreaded1(){
	System.err.println("Start multithreaded1");
	List<Thread> threads = new ArrayList<Thread>();

	String queries = "hel" + CachingProxyImpl.WARM_CACHE_QUERIES_SEPARATOR + "ant";

	Properties props = new Properties();
	//props.setProperty(CachingProxyImpl.WARM_CACHE_QUERIES_KEY, queries);
	props.setProperty(CachingProxyImpl.CACHE_LOCATION_KEY, TestUtil.getTestDbDir());

	Proxy proxy;
	proxy = ProxyImpl.instance(props);

	for(String term: searchTerms){
	    threads.add(searchByScientificNameThreaded(term, proxy));
	}
	
	for(Thread t: threads){
	    try{
		t.join();
	    }
	    catch(Throwable e){
		e.printStackTrace();
		Assert.fail();
	    }
	}
	
	try{
	    proxy.close();
	}catch(IOException e){
	    e.printStackTrace();
	    Assert.fail();
	}
    }

    /*
    @Test
    public void multithreadedTest2(){
	List<Thread> threads = new ArrayList<Thread>();

	for(String term: searchTerms){
	    Proxy p = new ProxyImpl();
	    p.init(null);
	    threads.add(searchByScientificNameThreaded(term, p));
	    p.close();
	}
	
	for(Thread t: threads){
	    try{
		t.join();
	    }
	    catch(Throwable e){
		e.printStackTrace();
		Assert.fail();
	    }
	}
    }
    */


    class B extends Thread{
	String sciName;
	Proxy p;
	public B(String sciName, Proxy p){
	    this.sciName = sciName;
	    this.p = p;
	}

	public void run(){
	    searchByScientificName(sciName, p);
	}
    }

    public Thread searchByScientificNameThreaded(String sciName, Proxy p){
	B b = new B(sciName, p);
	b.start();
	return b;
    }


    public void searchByScientificName(String sciName, Proxy p){
	for(int i=0; i<1; i++){
	    SearchResults searchResults = null;

	    try{
		searchResults = p.searchByScientificName(sciName, 0, 2000);
	    }catch(Throwable t){
		t.printStackTrace();
		Assert.fail();
	    }
	    List<ItisRecord> results = searchResults.records;
	    ItisRecord byTsn = null;
	    for(ItisRecord rec: results){
		try{
		    byTsn = p.getByTSN(rec.getTsn());
		}catch(Throwable t){
		    t.printStackTrace();
		    Assert.fail();
		}
	    }
	}

    }


}
