package ca.gc.agr.mbb.itisproxy;

import java.util.Properties;
import java.util.List;
import java.io.Closeable;
import java.io.IOException;

import ca.gc.agr.itis.itismodel.ItisRecord;
import ca.gc.agr.itis.itismodel.TaxonomicRank;
import ca.gc.agr.itis.itismodel.TaxonExpert;
import ca.gc.agr.itis.itismodel.TaxonComment;
import ca.gc.agr.itis.itismodel.TaxonOtherSource;
import ca.gc.agr.itis.itismodel.TaxonPublication;
import ca.gc.agr.itis.itismodel.TaxonJurisdictionalOrigin;


/**
 * Interface <em>Proxy</em> provids methods that hide the underlying Web Service calls to ITIS web services: http://www.itis.gov/ws_description.html. 
 * Uses the JSON web services: http://www.itis.gov/web_service.html
 */
public interface Proxy extends Closeable{

/**
 * Returns a list of ItisRecords with the tsn set to the TSN of the kingdom, and the kingdom name in the combinedName field
 * @returns List<ItisRecord>  List of records on each per kingdom
 */
    public List<ItisRecord> getKingdoms() throws FailedProxyRequestException;


/**
 * Returns a list of ItisRecords that match the query string in any ITIS field, with the tsn, the combinedName, the author name, and the commonNames(list) set
 * @param  queryString    The query string; can be anything
 * @param  start          The start record number of interest
 * @param  end            The end record number of interest
 * @param  sortAscending  Whether the results should be sorted in ascending order; default: false
 * @returns List<ItisRecord>  List of matching records. If there are none, returns list of length zero.
 */
    public SearchResults searchByAnyMatch(String queryString, int start, int end, boolean sortAscending) throws IllegalArgumentException, FailedProxyRequestException, TooManyResultsException;


/**
 * Returns a count of the records that match the query string in any ITIS field
 * @returns int   count of matching records
 */
    public int getAnyMatchCount(String queryString) throws IllegalArgumentException, FailedProxyRequestException;

/**
 * Returns a list of ItisRecords that match the query string in ITIS common name field, with the tsn, the combinedName, the author name, and the commonNames(list) set
 * @param  queryString    The query string; should be a common name
 * @param  start          The start record number of interest
 * @param  end            The end record number of interest
 * @returns List<ItisRecord>  List of matching records. If there are none, returns list of length zero.
 */
    public SearchResults searchByCommonName(String queryString, int start, int end) throws IllegalArgumentException, FailedProxyRequestException,TooManyResultsException;

/**
 * Returns a list of ItisRecords that begin with the query string in ITIS common name field, with the tsn, the combinedName, the author name, and the commonNames(list) set
 * @param  queryString    The query string; should be a common name prefix
 * @param  start          The start record number of interest
 * @param  end            The end record number of interest
 * @returns List<ItisRecord>  List of matching records. If there are none, returns list of length zero.
 */
    public SearchResults searchByCommonNameBeginsWith(String queryString, int start, int end) throws IllegalArgumentException, FailedProxyRequestException,TooManyResultsException;

/**
 * Returns a list of ItisRecords that end with the query string in ITIS common name field, with the tsn, the combinedName, the author name, and the commonNames(list) set
 * @param  queryString    The query string; should be a common name suffix
 * @param  start          The start record number of interest
 * @param  end            The end record number of interest
 * @returns List<ItisRecord>  List of matching records. If there are none, returns list of length zero.
 */
    public SearchResults searchByCommonNameEndsWith(String queryString, int start, int end) throws IllegalArgumentException, FailedProxyRequestException,TooManyResultsException;

/**
 * Returns a list of ItisRecords that match the query string in ITIS scientific name field, with the tsn, the combinedName, the author name, and the commonNames(list) set
 * @param  queryString    The query string; should be a common name
 * @param  start          The start record number of interest
 * @param  end            The end record number of interest
 * @returns List<ItisRecord>  List of matching records. If there are none, returns list of length zero.
 */
    public SearchResults searchByScientificName(String queryString, int start, int end) throws IllegalArgumentException, FailedProxyRequestException,TooManyResultsException;

   
/**
 * Returns a single fully populated ItisRecords
 * @param  tsn    The tsn of interest
 * @returns ItisRecord  Matching record. If there is none, returns null.
 */ 
    public ItisRecord getByTSN(String tsn) throws IllegalArgumentException, FailedProxyRequestException;


/**
 * For a given TSN, return the all TSN records that are up the taxonomic hierarch from this taxon to the root of the tree
 * @param  tsn    The tsn of interest
 * @returns List<TaxonomicRank>  List of matching records; each record is one above the previous. If there are none, returns list of length zero.
 */
    public List<TaxonomicRank> getRanksUpFromTsn(String tsn)throws IllegalArgumentException, FailedProxyRequestException;


/**
 * For a given TSN, return the all TSN records that are one level below this taxon (if any)
 * @param  tsn    The tsn of interest
 * @returns List<TaxonomicRank>  List of matching records; each record is one level lower than the TSN of interest. 
 * If there are none, returns list of length zero.
 */
    public List<TaxonomicRank> getRanksOneRankDownFromTsn(String tsn) throws IllegalArgumentException, FailedProxyRequestException;


    public void init(Properties p) throws Exception;

/**
 * Must be called to close resources
 * 
 */
    public void close() throws IOException;




    //public String info();
}
