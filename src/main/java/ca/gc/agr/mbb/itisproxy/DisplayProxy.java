package ca.gc.agr.mbb.itisproxy;

import java.util.Properties;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;

import ca.gc.agr.itis.itismodel.ItisRecord;
import ca.gc.agr.itis.itismodel.TaxonomicRank;
import ca.gc.agr.itis.itismodel.TaxonExpert;
import ca.gc.agr.itis.itismodel.TaxonComment;
import ca.gc.agr.itis.itismodel.TaxonOtherSource;
import ca.gc.agr.itis.itismodel.TaxonPublication;
import ca.gc.agr.itis.itismodel.TaxonJurisdictionalOrigin;


// A proxy-of-a-proxy that delegates to a real proxy but creates sparsley populated itisRecords from full ItisRecords, with only 
//  the tsn, combinedName (scientific name) and vernacularNames populated for all 'search' methods

public class DisplayProxy implements Proxy
{
    private Proxy realProxy = null;
    public DisplayProxy(final Proxy realProxy){
	if(realProxy == null){
	    throw new NullPointerException("Real proxy cannot be null");
	}
	this.realProxy = realProxy;
    }

    public List<ItisRecord> getKingdoms() throws FailedProxyRequestException{
	return realProxy.getKingdoms();
    }

    public SearchResults searchByAnyMatch(String queryString, int start, int end, boolean sortAscending) throws IllegalArgumentException, FailedProxyRequestException{
	return makeDisplayList(realProxy.searchByAnyMatch(queryString, start, end, sortAscending));

    }

    public int getAnyMatchCount(String queryString) throws IllegalArgumentException, FailedProxyRequestException{
	return realProxy.getAnyMatchCount(queryString);
    }

    public SearchResults searchByCommonName(String queryString, int start, int end) throws IllegalArgumentException, FailedProxyRequestException{
	return makeDisplayList(realProxy.searchByCommonName(queryString, start, end));
    }

    public SearchResults searchByCommonNameBeginsWith(String queryString, int start, int end) throws IllegalArgumentException, FailedProxyRequestException{
	return makeDisplayList(realProxy.searchByCommonNameBeginsWith(queryString, start, end));
    }

    public SearchResults searchByCommonNameEndsWith(String queryString, int start, int end) throws IllegalArgumentException, FailedProxyRequestException{
	return makeDisplayList(realProxy.searchByCommonNameEndsWith(queryString, start, end));
    }

    public SearchResults searchByScientificName(String queryString, int start, int end) throws IllegalArgumentException, FailedProxyRequestException{
	return makeDisplayList(realProxy.searchByScientificName(queryString, start, end));
    }

    public ItisRecord getByTSN(String tsn) throws IllegalArgumentException, FailedProxyRequestException{
	return shrinkRecord(realProxy.getByTSN(tsn));
    }

    public List<TaxonomicRank> getRanksUpFromTsn(String tsn) throws IllegalArgumentException, FailedProxyRequestException{
	return realProxy.getRanksUpFromTsn(tsn);
    }

    public List<TaxonomicRank> getRanksOneRankDownFromTsn(String tsn) throws IllegalArgumentException, FailedProxyRequestException{
	return realProxy.getRanksOneRankDownFromTsn(tsn);
    }

    public void init(Properties p) throws Exception{

    }

    public void close() throws IOException{
	realProxy.close();
    }


    private static final SearchResults makeDisplayList(final SearchResults actualResults){
	List<ItisRecord> displayList = new ArrayList<ItisRecord>(actualResults.records.size());
	for(ItisRecord rec: actualResults.records){
	    ItisRecord displayRec = shrinkRecord(rec);
	    displayList.add(rec);
	}
	SearchResults displayResults = new SearchResults(actualResults);
	displayResults.records = displayList;
	return displayResults;
    }

    private static final ItisRecord shrinkRecord(final ItisRecord rec){
	ItisRecord displayRec = new ItisRecord();
	displayRec.setTsn(rec.getTsn());
	displayRec.setCombinedName(rec.getCombinedName());
	displayRec.setVernacularNames(rec.getVernacularNames());

	return displayRec;
    }
}
