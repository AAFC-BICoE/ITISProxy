package ca.gc.agr.mbb.itisproxy;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TSNWarmWorker extends WarmWorker{
    private final static Logger LOGGER = Logger.getLogger(TSNWarmWorker.class.getName()); 
    private List<String> queries = null;
    public TSNWarmWorker(final List<String> queries, final CachingProxyImpl proxy){
	super(null, proxy);
	this.queries = queries;
    }

    @Override
    protected void runQuery(String query) throws FailedProxyRequestException{
	proxy.getByTSN(query);
    }

    @Override
    public void run() {
	LOGGER.info("START WarmWorker");
	try{
	    Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
	}catch(Throwable t){
	    t.printStackTrace();
	    LOGGER.info("Unable to lower Thread priority");
	}
	try{
	    for(String query: queries){
		LOGGER.info("WarmWorker: query=" + query);
		runQuery(query);
		LOGGER.info("done WarmWorker: query=" + query);
	    }
	}catch(FailedProxyRequestException e){
	    e.printStackTrace();
	}
	LOGGER.info("END WarmWorker");

    }

}
