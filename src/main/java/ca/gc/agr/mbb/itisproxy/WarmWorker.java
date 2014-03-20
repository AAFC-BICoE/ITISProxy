package ca.gc.agr.mbb.itisproxy;

import java.util.logging.Level;
import java.util.logging.Logger;

public class WarmWorker implements Runnable{
    private final static Logger LOGGER = Logger.getLogger(WarmWorker.class.getName()); 

    String query = null;
    CachingProxyImpl proxy = null;

    public WarmWorker(){

    }

    public WarmWorker(final String query, final CachingProxyImpl proxy){
	this.query = query;
	this.proxy = proxy;
    }

    public void run() {
	LOGGER.info("START WarmWorker: query=" + query);
	System.err.println("WarmWorker: query=" + query);
	try{
	    Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
	}catch(Throwable t){
	    t.printStackTrace();
	    LOGGER.info("Unable to lower Thread priority");
	}
	try{
	    runQuery(query);
	}catch(FailedProxyRequestException e){
	    e.printStackTrace();
	}
	LOGGER.info("END WarmWorker: query=" + query);
    }

    protected void runQuery(String query) throws FailedProxyRequestException{
	//proxy.searchByCommonNameBeginsWith(query, 0, proxy.warmCacheQueriesMaxNumHits, proxy.warmCacheQueriesDelayMillis);
	proxy.searchByCommonNameBeginsWith(query, 0, 999999, 1);
    }

}
