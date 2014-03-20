package ca.gc.agr.mbb.itisproxy;

import java.util.logging.Level;
import java.util.logging.Logger;
import ca.gc.agr.itis.itismodel.ItisRecord;
import java.util.List;
import ca.gc.agr.itis.itismodel.TaxonomicRank;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import ca.gnewton.tuapait.TCache;

import java.util.ArrayList;

public class CachingProxyImpl extends ProxyImpl{
    private final static Logger LOGGER = Logger.getLogger(CachingProxyImpl.class.getName()); 

    public static final String OVERWRITE_KEY = TCache.OVERWRITE_KEY;
    public static final String CACHE_LOCATION_KEY = TCache.DB_DIR_KEY;
    public static final String TTL_MINUTES_KEY = TCache.TTL_MINUTES_KEY;
    public static final String BDB_DEFERRED_WRITE_SIZE_KEY = TCache.BDB_DEFERRED_WRITE_SIZE_KEY;

    public static final String WARM_CACHE_QUERIES_KEY = "warmCacheQueries.CachingProxyImpl";
    public static final String WARM_CACHE_QUERIES_SEPARATOR = "|";
    public static final String WARM_CACHE_QUERIES_DELAY_MILLIS_KEY = "warmCacheQueriesDelayMillis.CachingProxyImpl.CachingProxyImpl";

    private volatile List<ItisRecord> kingdomsCache = null;
    private volatile ItisCache cache = null;
    private volatile boolean inited = false;

    private CacheWarmer cacheWarmer = null;

    protected int warmCacheQueriesDelayMillis = 200;
    protected int warmCacheQueriesMaxNumHits = 100000;

    private ProxyImpl realProxy = null;

    public CachingProxyImpl(){
	super();
    }

    public CachingProxyImpl(final ProxyImpl realProxy){
	this();
	this.realProxy = realProxy;
    }

    public void init(final Properties p){
	LOGGER.info("Start init");
	super.init(p);
	realProxy.init(p);
	if(!inited){
	    if(p != null){
		prepareProperties(p);
	    try{
		cache = new ItisCacheImpl();
		cache.init(p);
		warmCacheQueries(p);

	    }catch(Throwable t){
		t.printStackTrace();
		LOGGER.info("Unable to instantiate cache");
	    }
	    kingdomsCache = new ArrayList<ItisRecord>(10); 
	    }
	}
    }

    @Override
    public List<ItisRecord> getKingdoms() throws FailedProxyRequestException{
	if(kingdomsCache == null || kingdomsCache.size() == 0){
	    kingdomsCache = realProxy.getKingdoms();	    
	}
	return  kingdomsCache;
    }


    @Override
    protected List<TaxonomicRank> getRanksUpFromTsn(final String tsn, final boolean fullRecordPopulate)throws IllegalArgumentException, FailedProxyRequestException{
	if(cache != null && !fullRecordPopulate && cache.containsKey(tsn)){
	    LOGGER.info("\t--@--\t RANK HIT: " + tsn + "    cache: " + cache + "   fullRecordPopulate:" + fullRecordPopulate );
	    return ((ItisRecord)cache.get(tsn)).getTaxonomicHierarchy();
	}
	LOGGER.info("\t--@--\t RANK MISS: " + tsn + "    cache: " + cache + "   fullRecordPopulate:" + fullRecordPopulate );
	return realProxy.getRanksUpFromTsn(tsn, false);
    }


    protected final List<TaxonomicRank> getRanksOneRankDownFromTsn(final String tsn, final boolean fullRecordPopulate) throws IllegalArgumentException, FailedProxyRequestException{
	if(cache != null && !fullRecordPopulate && cache.containsKey(tsn)){
	    LOGGER.info("\t--@--\t RANK HIT: " + tsn + "    cache: " + cache + "   fullRecordPopulate:" + fullRecordPopulate );
	    return ((ItisRecord)cache.get(tsn)).getBelowSpeciesRanks();
	}
	LOGGER.info("\t--@--\t RANK MISS: " + tsn + "    cache: " + cache + "   fullRecordPopulate:" + fullRecordPopulate );
	return realProxy.getRanksOneRankDownFromTsn(tsn, false);
    }


    @Override
    public ItisRecord getByTSN(String tsn) throws IllegalArgumentException, FailedProxyRequestException{
	LOGGER.info("START getByTSN: " + tsn + " cache=" + cache);
	System.err.println("START getByTSN: " + tsn + " cache=" + cache);
	try{
	    if(cache == null){
		return realProxy.getByTSN(tsn);
	    }else{
		if(cache.containsKey(tsn)){
		    LOGGER.info("\t--@--\t hit get: " + tsn);
		    return cache.get(tsn);
		}
		ItisRecord rec = realProxy.getByTSN(tsn);
		add(rec);
		return rec;
	    }
	}
	finally{
	    LOGGER.info("END getByTSN: " + tsn);
	}
    }

    public void add(ItisRecord rec){
	try{
	    if(cache != null){
		cache.put(rec.getTsn(), rec);
	    }
	}catch(Throwable t){
	    t.printStackTrace();
	}
    }

    @Override
    public void close(){
	LOGGER.info("START close");

	if(cacheWarmer != null){
	    LOGGER.info("Waiting on the cache warmer thread to finish");
	    try{
		cacheWarmer.join();
	    }catch(Throwable t){
		t.printStackTrace();
	    }
	    LOGGER.info("Cache warmer thread done");
	}
	if(cache != null){
	    cache.close();
	}
	cache = null;
	LOGGER.info("END close");
    }


    private void warmCacheQueries(Properties p){
	if(p == null || !p.containsKey(WARM_CACHE_QUERIES_KEY)){
	    return;
	}
	String s = p.getProperty(WARM_CACHE_QUERIES_KEY).trim();
	if(s == null || s.length() == 0){
	    return;
	}

	String[] queries = s.split("\\" + WARM_CACHE_QUERIES_SEPARATOR);
	if(queries == null){
	    return;
	}

	cacheWarmer = new CacheWarmer(this, queries, null); 
	cacheWarmer.start();
    }



    private void prepareProperties(Properties p){
	if(p == null){
	    return;
	}

	if(p.containsKey(WARM_CACHE_QUERIES_DELAY_MILLIS_KEY)){
	    try{
		warmCacheQueriesDelayMillis = Integer.parseInt(p.getProperty(WARM_CACHE_QUERIES_DELAY_MILLIS_KEY));
	    }catch(NumberFormatException n){
		LOGGER.info("WARM_CACHE_QUERIES_DELAY_MILLIS_KEY (" + WARM_CACHE_QUERIES_DELAY_MILLIS_KEY + ")  cannot be converted to an integer: ["
			    + p.getProperty(WARM_CACHE_QUERIES_DELAY_MILLIS_KEY) + "]");
	    }
	}
    }



}
