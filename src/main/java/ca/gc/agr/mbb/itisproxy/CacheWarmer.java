package ca.gc.agr.mbb.itisproxy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import ca.gc.agr.mbb.itisproxy.CachingProxyImpl;
import ca.gc.agr.mbb.itisproxy.Proxy;
import ca.gc.agr.mbb.itisproxy.ProxyImpl;
import ca.gc.agr.mbb.itisproxy.ProxyInfo;

public class CacheWarmer extends Thread{
    
    private final static Logger LOGGER = Logger.getLogger(CacheWarmer.class.getName());
    private int numWorkerThreads = 7;
    private int queueSize = 128;
    private int chunkSize = 256;
    
    private ArrayBlockingQueue queue = null;
    private ThreadPoolExecutor tpe = null;
    
    private String[] queries;
    private String tsnFile = null;
    private CachingProxyImpl proxy;

    List<Future<Object>> futures = new ArrayList<Future<Object>>();

    
    public static final void main(final String[] args) {
	//LogManager.getLogManager().reset();
	//Logger globalLogger = Logger.getLogger(java.util.logging.Logger.GLOBAL_LOGGER_NAME);
        //globalLogger.setLevel(java.util.logging.Level.OFF);

	if(args == null || (args.length != 2 && args.length != 3)){
	    usage();
	}
	String cacheDir = args[0];
	boolean overWrite = Boolean.parseBoolean(args[1]);
	String tsnFile = null;

	if(args.length == 3){
	    tsnFile = args[2];
	    warmByTsn(cacheDir, overWrite, tsnFile);

	}else{
	    warm(cacheDir, overWrite);
	}
    }

    public static void warmByTsn(String cacheDir, boolean overWrite, String tsnFile){
	Properties p = new Properties();
	p.setProperty(CachingProxyImpl.CACHE_LOCATION_KEY, cacheDir);
	Proxy proxy = ProxyImpl.instance(p);
	CacheWarmer warmer = new CacheWarmer((CachingProxyImpl)proxy, null, tsnFile);
	warmer.run();
	
	try{
	    proxy.close();
	}catch(IOException e){
	    e.printStackTrace();
	}
    }
	    

    public static void warm(final String cacheDir, final boolean overWrite){
	Properties p = new Properties();
	p.setProperty(CachingProxyImpl.CACHE_LOCATION_KEY, cacheDir);

	Proxy proxy = ProxyImpl.instance(p);
	
	CacheWarmer warmer = new CacheWarmer((CachingProxyImpl)proxy, makeWarmingQueries(), null);
	warmer.run();
	
	try{
	    proxy.close();
	}catch(IOException e){
	    e.printStackTrace();
	}
    }


    public static String[] makeWarmingQueries(){
	//return "a" + CachingProxyImpl.WARM_CACHE_QUERIES_SEPARATOR + "b";
	return Util.makeWholeAlphabetArray();
    }
 

    public CacheWarmer(CachingProxyImpl proxy, final String[] queries, final String tsnFile ) throws IllegalArgumentException{
	if(proxy == null){
	    throw new IllegalArgumentException("Proxy cannot be null");
	}
	this.queries = queries;
	this.proxy = proxy;
	this.tsnFile = tsnFile;
    }

	
    public void run() {
	System.out.println("START run");
	try{
	    Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
	}catch(Throwable t){
	    t.printStackTrace();
	    LOGGER.info("Unable to lower Thread priority");
	}

	queue = new ArrayBlockingQueue(queueSize);
	tpe = initTPE(queue, numWorkerThreads);

	if(tpe == null){
	    return;
	}

	if(tsnFile != null){
	    runTsn(tpe, tsnFile);
	}else{
	    runAlpha(tpe);
	}

	shutdownTpe(tpe);
    }


    private void runTsn(final ThreadPoolExecutor tpe, String tsnFile){
	System.err.println("START runTsn");

	File f = new File(tsnFile);
	if(!f.exists() & !f.canRead()){
	    System.err.println("File does not exist or unable to read: [" + tsnFile + "]");
	    usage();
	    return;
	}

	BufferedReader br = null;
	try {
	    br = new BufferedReader(new FileReader(f));
	    StringBuilder sb = new StringBuilder();
	    String line = br.readLine();
	    
	    int nn = 0;
	    int limit = 10;
	    List<String>queries = new ArrayList<String>(chunkSize);
	    while (line != null) {

		System.out.println("# " + line);
		queries.add(line.trim());
		if(queries.size() >= chunkSize){
		    ++nn;
		    WarmWorker worker = new TSNWarmWorker(queries, proxy);
		    //tpe.execute(worker);
		    futures.add(tpe.submit(worker, (Object)null));
		    if(nn == limit){
			nn = 0;
			for (Future<Object> future : futures) {
			    System.out.println("---------------------Waiting on thread to finish");
			    try{
				future.get();
			    }catch(Throwable t){
				t.printStackTrace();
				Thread.sleep(8000);
			    }
			}
			futures.clear();
		    }
		    queries = new ArrayList<String>(chunkSize);
		}
		line = br.readLine();

	    }

	    // any left over
	    if(queries.size() > 0){
		WarmWorker worker = new TSNWarmWorker(queries, proxy);
		Future<Object> future = tpe.submit(worker, (Object)null);
		future.get();
	    }


	    for (Future<Object> future : futures) {
		// this joins with the submitted job
		try{
		    future.get();
		}catch(Throwable t){
		    t.printStackTrace();
		    Thread.sleep(8000);
		}
		System.out.println("---------------------Waiting on thread to finish");
	    }
	}catch(Throwable t){
	    t.printStackTrace();
	    return;
	} finally {
	    if(br!= null){
		try{
		    br.close();
		}catch(Throwable t){
		    t.printStackTrace();
		    return;
		}
	    }
	}
    }


    static ThreadPoolExecutor initTPE(final BlockingQueue queue, final int numWorkerThreads){
	try{
	    return new ThreadPoolExecutor(1, 
					 numWorkerThreads,
					 16l,
					 TimeUnit.SECONDS,
					 queue,
					 new ThreadPoolExecutor.CallerRunsPolicy());
	    
	}catch(IllegalArgumentException e){
	    e.printStackTrace();
	    return null;
	}catch(NullPointerException e){
	    e.printStackTrace();
	    return null;
	}
    }


    private void runAlpha(final ThreadPoolExecutor tpe){
	if(queries == null){
	    return;
	}
	runQueries(tpe);
    }


    public void runQueries(final ThreadPoolExecutor tpe){
	for(String query: queries){
	    LOGGER.info("Warming cache with query: [" + query + "]");
	    try{
		WarmWorker worker = new WarmWorker(query, proxy);
		tpe.execute(worker);
	    }catch(Throwable t){
		t.printStackTrace();
	    }
	}
    }


    void shutdownTpe(final ThreadPoolExecutor tpe){
	LOGGER.info("SHUTTING DOWN ThreadPoolExecutor");
	tpe.shutdown();
	try{
	    LOGGER.info("START awaitTermination ThreadPoolExecutor");
	    int count = 0;
	    while(!tpe.awaitTermination(15l, TimeUnit.SECONDS)){
		    LOGGER.info("Waiting for Executor to terminate: " + count);
		    count++;
		}
	    LOGGER.info("END awaitTermination ThreadPoolExecutor");
	}catch(InterruptedException e){
	    e.printStackTrace();
	}
    }


    public static final void usage(){
	System.err.println("Usage: CacheWarmer cacheDirectory overwrite[true|false] <tsn_input_file");
	System.err.println("\t\t If only 2 args, populates from alphabetical query of CommonNameBeginsWith; if 3 args, takes tsns from this text file & does getByTSN");
	System.exit(42);
    }


    String printArray(String[] a){
	StringBuilder sb = new StringBuilder();
	for(String s: a){
	    sb.append(s + "  ");
	}
	return sb.toString();
    }
}

