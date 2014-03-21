package ca.gc.agr.mbb.itisproxy.example;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import ca.gc.agr.itis.itismodel.ItisRecord;
import ca.gc.agr.itis.itismodel.TaxonomicRank;
import ca.gc.agr.mbb.itisproxy.FailedProxyRequestException;
import ca.gc.agr.mbb.itisproxy.ProxyImpl;
import ca.gc.agr.mbb.itisproxy.Proxy;
import ca.gc.agr.mbb.itisproxy.SearchResults;

public class Example{
    private final static Logger LOGGER = Logger.getLogger(Example.class.getName());

    public final static String SEARCH_STRING = "yellow";
    public final static String[] TSNS = {"58274", "65562", "155166", "155358", "563959",
					 // the Kindom TSNs
					 "202243", "630578", "555705", "202420", "202422", "630577"};
    

    public static final void main(final String[] args) {
	Example example = new Example();
	example.run();
    }

    public final void run(){
	// This creates a caching proxy, with default location of persistent cache: /tmp/bdbcache (actually: System.getProperty("java.io.tmpdir") + / + bdbcache)
	// If you don't want it persistent, make a properties object and setProperty(ProxyInfo.NO_CACHING_KEY, "true")
       
	// If you want the location elswhere, make a properties object and setProperty(ProxyInfo.CACHE_LOCATION_KEY, "/home/foo/cache")
	//
	Properties p = null;
	Proxy proxy = ProxyImpl.instance(p);
	getKingdoms(proxy);

	searchByAnyMatch(proxy);
	getAnyMatchCount(proxy);

	searchByCommonName(proxy);
	searchByCommonNameBeginsWith(proxy);
	searchByCommonNameEndsWith(proxy);

	searchByScientificName(proxy);

	getByTSN(proxy);

	getRanksUpFromTsn(proxy);
	getRanksOneRankDownFromTsn(proxy);
	try{
	    proxy.close();
	}catch(IOException e){
	    e.printStackTrace();
	    // log
	}
    }

    //////////////////////////////////////
    // gtKingdoms
    //////////////////////////////////////
    public static void getKingdoms(final Proxy proxy){
	List<ItisRecord> results;
	try{
	    results = proxy.getKingdoms();
	}catch(FailedProxyRequestException e){
	    e.printStackTrace();
	    throw new NullPointerException();
	}
	for(ItisRecord rec: results){
	    LOGGER.info("Kingdom=" + rec.getCombinedName() + "  TSN=" + rec.getTsn());
	}
    }

    //////////////////////////////////////
    // searchyByAnyMatch
    //////////////////////////////////////
    public static void searchByAnyMatch(final Proxy proxy){
	SearchResults results;
	try{
	    results = proxy.searchByAnyMatch(SEARCH_STRING, 0, 20, false);
	}catch(FailedProxyRequestException e){
	    e.printStackTrace();
	    throw new NullPointerException();
	}
	catch(IllegalArgumentException e){
	    e.printStackTrace();
	    throw new NullPointerException();
	}

	printSearchResults(results);
    }

    //////////////////////////////////////
    // getAnyMatchCount
    //////////////////////////////////////
    public static void getAnyMatchCount(final Proxy proxy){
	int numResults = 0;
	try{
	    numResults = proxy.getAnyMatchCount(SEARCH_STRING);
	}catch(FailedProxyRequestException e){
	    e.printStackTrace();
	    throw new NullPointerException();
	}
	catch(IllegalArgumentException e){
	    e.printStackTrace();
	    throw new NullPointerException();
	}
	
	LOGGER.info("Search=" + SEARCH_STRING + "   numResults=" + numResults);
    }


    //////////////////////////////////////
    // searchByCommonName
    //////////////////////////////////////
    private static void searchByCommonName(final Proxy proxy){
	SearchResults results;
	try{
	    results = proxy.searchByCommonName(SEARCH_STRING, 0, 20);
	}catch(FailedProxyRequestException e){
	    e.printStackTrace();
	    throw new NullPointerException();
	}
	catch(IllegalArgumentException e){
	    e.printStackTrace();
	    throw new NullPointerException();
	}
	printSearchResults(results);
    }


    //////////////////////////////////////
    // searchByCommonNameBeginsWith
    //////////////////////////////////////
    public static void searchByCommonNameBeginsWith(final Proxy proxy){
	SearchResults results;
	try{
	    results = proxy.searchByCommonNameBeginsWith(SEARCH_STRING, 0, 20);
	}catch(FailedProxyRequestException e){
	    e.printStackTrace();
	    throw new NullPointerException();
	}
	catch(IllegalArgumentException e){
	    e.printStackTrace();
	    throw new NullPointerException();
	}
	printSearchResults(results);
    }

    //////////////////////////////////////
    // searchByCommonNameEndsWith
    //////////////////////////////////////
    public static void searchByCommonNameEndsWith(final Proxy proxy){
	SearchResults results;
	try{
	    results = proxy.searchByCommonNameEndsWith(SEARCH_STRING, 0, 20);
	}catch(FailedProxyRequestException e){
	    e.printStackTrace();
	    throw new NullPointerException();
	}
	catch(IllegalArgumentException e){
	    e.printStackTrace();
	    throw new NullPointerException();
	}
	printSearchResults(results);
    }


    //////////////////////////////////////
    // searchByScientificName
    //////////////////////////////////////
    public static void searchByScientificName(final Proxy proxy){
	SearchResults results;
	try{
	    results = proxy.searchByScientificName(SEARCH_STRING, 0, 20);
	}catch(FailedProxyRequestException e){
	    e.printStackTrace();
	    throw new NullPointerException();
	}
	catch(IllegalArgumentException e){
	    e.printStackTrace();
	    throw new NullPointerException();
	}
	printSearchResults(results);
    }



    //////////////////////////////////////
    // getByTSN
    //////////////////////////////////////
    public static void getByTSN(final Proxy proxy){
	ItisRecord result;

	for(String tsn: TSNS){
	    try{
		result = proxy.getByTSN(tsn);
	    }catch(FailedProxyRequestException e){
		e.printStackTrace();
		throw new NullPointerException();
	    }
	    catch(IllegalArgumentException e){
		e.printStackTrace();
		throw new NullPointerException();
	    }
	    printSearchResult(result);
	}
    }

    //////////////////////////////////////
    // getRanksUpFromTsn
    //////////////////////////////////////
    public static void getRanksUpFromTsn(final Proxy proxy){
	List<TaxonomicRank> results;

	for(String tsn: TSNS){
	    try{
		results = proxy.getRanksUpFromTsn(tsn);
	    }catch(FailedProxyRequestException e){
		e.printStackTrace();
		throw new NullPointerException();
	    }
	    catch(IllegalArgumentException e){
		e.printStackTrace();
		throw new NullPointerException();
	    }

	    LOGGER.info("Ranks above tsn: " + tsn 
			+ " [" + getNameByTSN(tsn, proxy) + "]"
			+ " =" + concatRanks(results));
	}
    }

    //////////////////////////////////////
    // getRanksDownFromTsn
    //////////////////////////////////////
    public static void getRanksOneRankDownFromTsn(final Proxy proxy){
	List<TaxonomicRank> results;

	for(String tsn: TSNS){
	    try{
		results = proxy.getRanksOneRankDownFromTsn(tsn);
	    }catch(FailedProxyRequestException e){
		e.printStackTrace();
		throw new NullPointerException();
	    }
	    catch(IllegalArgumentException e){
		e.printStackTrace();
		throw new NullPointerException();
	    }

	    LOGGER.info("Ranks one rank below tsn: " + tsn 
			+ " [" + getNameByTSN(tsn, proxy) + "]"
			+ " =" + concatRanks(results));
	}
    }


    private static final String concatRanks(List<TaxonomicRank> ranks){
	StringBuilder sb = new StringBuilder();
	for(TaxonomicRank rank: ranks){
	    sb.append("| RankName: ");
	    sb.append(rank.getRankName());
	    
	    sb.append("  RankValue: ");
	    sb.append(rank.getRankValue());
	    sb.append("| ");
	}
	return sb.toString();
    }

	


    /////////////////////////////////////////////
    private static final String getNameByTSN(String tsn, Proxy proxy){
	ItisRecord result;
	try{
	    result = proxy.getByTSN(tsn);
	}catch(FailedProxyRequestException e){
	    e.printStackTrace();
	    throw new NullPointerException();
	}
	catch(IllegalArgumentException e){
	    e.printStackTrace();
	    throw new NullPointerException();
	}
	return result.getCombinedName();
    }

    private final static void printSearchResults(SearchResults searchResults){
	List<ItisRecord> records = searchResults.records;
	for(ItisRecord rec: records){
	    printSearchResult(rec);
	}
    }

    private final static void printSearchResult(ItisRecord rec){
	LOGGER.info("Name=" + rec.getCombinedName() + "  TSN=" + rec.getTsn() + "  Author=" + rec.getNameAuthor());
	Map<String, List<String>> vns = rec.getVernacularNames();
	Set<String> langs = vns.keySet();
	LOGGER.info("\tCommon Names START for TSN: " + rec.getTsn());
	for(String lang: langs){

	    List<String> names = vns.get(lang);
	    for(String name: names){
		LOGGER.info("\t Lang=" + lang + "  Name=" + name);
	    }
	}
	LOGGER.info("\tCommon Names END for TSN: " + rec.getTsn());
    }


}
