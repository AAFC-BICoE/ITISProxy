package ca.gc.agr.mbb.itisproxy.sqlite3;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Properties;
import java.io.IOException;

import ca.gc.agr.mbb.itisproxy.FailedProxyRequestException;
import ca.gc.agr.itis.itismodel.ItisRecord;
import ca.gc.agr.itis.itismodel.TaxonComment;
import ca.gc.agr.itis.itismodel.TaxonExpert;
import ca.gc.agr.itis.itismodel.TaxonJurisdictionalOrigin;
import ca.gc.agr.itis.itismodel.TaxonOtherSource;
import ca.gc.agr.itis.itismodel.TaxonPublication;
import ca.gc.agr.itis.itismodel.TaxonomicRank;
import ca.gc.agr.mbb.itisproxy.Proxy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;

import java.util.logging.Level;
import java.util.logging.Logger;

public class SqliteProxyImpl //implements Proxy
{
    private final static Logger LOGGER = Logger.getLogger(SqliteProxyImpl.class.getName()); 
    public static final String SQLITE3_JDBC_CLASS_NAME = "org.sqlite.JDBC";
    public static final String SQLITE3_URL_KEY = "sqlite3UrlKey";
    private String url = null;
    public SqliteProxyImpl() {

    }

    public void init(Properties p) throws Exception{
	try{
	    Class.forName(SQLITE3_JDBC_CLASS_NAME);
	    LOGGER.info("SQLITE3_JDBC_CLASS_NAME=" + SQLITE3_JDBC_CLASS_NAME);
	}catch(ClassNotFoundException e){
	    e.printStackTrace();
	    LOGGER.severe("Unable to find driver: " + SQLITE3_JDBC_CLASS_NAME);
	    throw new NullPointerException("Unable to find driver: " + SQLITE3_JDBC_CLASS_NAME);
	}	
	if(!p.containsKey(SQLITE3_URL_KEY)){
	    LOGGER.severe("No JDBC URL: need to set SQLITE3_URL_KEY");
	    throw new NullPointerException("No JDBC URL: need to set SQLITE3_URL_KEY");
	}
	url = p.getProperty(SQLITE3_URL_KEY);
	
    }


    private static final String getKingdomsSql = "select tsn, complete_name from taxonomic_units where rank_id = 10 and name_usage in (\"accepted\", \"valid\")";
    public List<ItisRecord> getKingdoms() throws FailedProxyRequestException{
	List<ItisRecord> itisRecords = new ArrayList<ItisRecord>(10);
	Connection conn = null;
	Statement statement = null;
	ResultSet rs = null;
	try{
	    conn = DriverManager.getConnection(url);
	    statement = conn.createStatement();
	    statement.setQueryTimeout(10);
	    rs = statement.executeQuery(getKingdomsSql);
	    while(rs.next()){
		ItisRecord itisRecord = new ItisRecord();
		itisRecord.setTsn(rs.getString("tsn"));

		itisRecords.add(itisRecord);
	    }
	    return itisRecords;
	    
	}catch(Throwable t){
	    t.printStackTrace();
	    throw new FailedProxyRequestException("JDBC problem");
	}finally{
	    closeAll(conn, statement, rs);
	}
    }


    // This is the query I would like to use (works in the sqlite3 command line interface; however, this 3-way join does not return in the Java JDBC implementation
    //
    // select distinct taxonomic_units.tsn, vernacular_name, complete_name, taxon_author, short_author from taxon_authors_lkp, vernaculars, taxonomic_units where taxonomic_units.tsn = vernaculars.tsn and taxon_authors_lkp.taxon_author_id = taxonomic_units.taxon_author_id and (vernacular_name like "%dolphin%" or unit_name1 like "%dolphin%" or unit_name2 like "%dolphin%" or unit_name3 like "%dolphin%" or unit_name4 like "%dolphin%") order by vernacular_name;
    //

    

    final static String anyCountHeadSql = "select count(distinct taxonomic_units.tsn) as count ";
    final static String anySearchHeadSql = "select taxonomic_units.tsn, vernacular_name, complete_name";
    //final static String anyFromWhereSql = "from vernaculars, taxonomic_units where taxonomic_units.tsn = vernaculars.tsn and (vernacular_name like ? or unit_name1 like ? or unit_name2 like ? or unit_name3 like ? or unit_name4 like ? or vernaculars.tsn like ?)";


    // select "2", complete_name as name, tsn from vernaculars,taxonomic_units where taxonomic_units.tsn=vernaculars.tsn and vernacular_name like "%tardigrad%" or tsn like "%tardigrada%" union all select "1", complete_name as name, tsn from taxonomic_units where complete_name like "%tardigrad%" or tsn like "%tardigrada%" order by name collate nocase;

    final static String anyFromWhereSql = "from vernaculars, taxonomic_units where taxonomic_units.tsn = vernaculars.tsn and (vernacular_name like ? or unit_name1 like ? or unit_name2 like ? or unit_name3 like ? or unit_name4 like ? or vernaculars.tsn like ? or taxonomic_units.tsn = ?)";

    final static String anyCountSql = anyCountHeadSql + " " + anyFromWhereSql;
    final static String anySearchSql = anySearchHeadSql + " " + anyFromWhereSql + " order by complete_name";

    public List<ItisRecord> searchByAnyMatch(String queryString, int start, int end, boolean sortAscending) throws IllegalArgumentException, FailedProxyRequestException{
	System.out.println("=============== " + queryString);
	Connection conn = null;
	PreparedStatement statement = null;
	ResultSet rs = null;
	try{
	    String anySearchSqlComplete = anySearchSql;
	    if(start > 0 && end > 0){
		anySearchSqlComplete += " limit " + start + "," + (end-start);
	    }
	    conn = DriverManager.getConnection(url);
	    statement = conn.prepareStatement(anySearchSqlComplete);
	    statement.setQueryTimeout(10);

	    String query = "%" + queryString + "%";
	    for(int i=1; i<=7; i++){
		statement.setString(i, query);
	    }

	    System.err.println(statement);
	    System.err.println(anySearchSqlComplete);

	    List<ItisRecord> recs = new ArrayList<ItisRecord>(50);

	    rs = statement.executeQuery();
	    while(rs.next()){
		ItisRecord rec = new ItisRecord();
		//return rs.getInt("tsn")
		System.out.println(rs.getInt("tsn") + " " + rs.getString("complete_name") + " " + rs.getString("vernacular_name"));
		rec.setTsn(rs.getString("tsn"));
		//rec.setCombinedName(rec.getString("complete_name"));
		//rec.addVernacularName(rs.getString("vernacular_name"));
	    }
	}catch(Throwable t){
	    t.printStackTrace();
	    throw new FailedProxyRequestException("JDBC problem");
	}finally{
	    closeAll(conn, statement, rs);
	}
	return null;
    }

    public int getAnyMatchCount(String queryString) throws IllegalArgumentException, FailedProxyRequestException{
	Connection conn = null;
	PreparedStatement statement = null;
	ResultSet rs = null;
	try{
	    conn = DriverManager.getConnection(url);
	    statement = conn.prepareStatement(anyCountSql);
	    statement.setQueryTimeout(10);

	    for(int i=1; i<=6; i++){
		statement.setString(i, queryString);
	    }

	    rs = statement.executeQuery();
	    while(rs.next()){
		return rs.getInt("count");
	    }
	}catch(Throwable t){
	    t.printStackTrace();
	    throw new FailedProxyRequestException("JDBC problem");
	}finally{
	    closeAll(conn, statement, rs);
	}
	return 0;
    }



    private static final String searchCommonNameSql = "select tsn, vernacular_name from vernaculars where vernacular_name like ? limit ?, ?";

    public List<ItisRecord> searchByCommonName(String queryString, int start, int end) throws IllegalArgumentException, FailedProxyRequestException{
	return searchByCommonNameGeneric("%" + queryString + "%", start, end);
    }

    public List<ItisRecord> searchByCommonNameBeginsWith(String queryString, int start, int end) throws IllegalArgumentException, FailedProxyRequestException{
	return searchByCommonNameGeneric(queryString + "%", start, end);
    }


    public List<ItisRecord> searchByCommonNameEndsWith(String queryString, int start, int end) throws IllegalArgumentException, FailedProxyRequestException{
	return searchByCommonNameGeneric("%" + queryString, start, end);
    }


    private List<ItisRecord> searchByCommonNameGeneric(String queryString, int start, int end) throws IllegalArgumentException, FailedProxyRequestException{
	List<ItisRecord> itisRecords = new ArrayList<ItisRecord>(10);
	Connection conn = null;
	PreparedStatement statement = null;
	ResultSet rs = null;
	try{
	    conn = DriverManager.getConnection(url);
	    statement = conn.prepareStatement(searchCommonNameSql);
	    statement.setString(1, queryString);
	    statement.setInt(2, start);
	    statement.setInt(3, end-start);
	    rs = statement.executeQuery();
	    while(rs.next()){
		ItisRecord itisRecord = new ItisRecord();
		itisRecord.setTsn(rs.getString("tsn"));
		itisRecord.setCombinedName(rs.getString("vernacular_name"));
		itisRecords.add(itisRecord);
		System.err.println(rs.getString("tsn") + " " + rs.getString("vernacular_name"));
	    }
	    return itisRecords;
	    //return null;
	    
	}catch(Throwable t){
	    t.printStackTrace();
	    throw new FailedProxyRequestException("JDBC problem");
	}finally{
	    closeAll(conn, statement, rs);
	    }
    }

    public List<ItisRecord> getAllRecords(int start, int end) throws IllegalArgumentException, FailedProxyRequestException{
	List<ItisRecord> itisRecords = new ArrayList<ItisRecord>(end-start);
	Connection conn = null;
	Statement statement = null;
	ResultSet rs = null;
	try{
	    conn = DriverManager.getConnection(url);
	    statement = conn.createStatement();
	    rs = statement.executeQuery("select * from taxonomic_units limit " + start + "," + (end-start));
	    while(rs.next()){
		ItisRecord itisRecord = new ItisRecord();
		itisRecord.setTsn(rs.getString("tsn"));
		itisRecords.add(itisRecord);
		System.err.println(rs.getString("tsn") + " " + rs.getString("complete_name"));
	    }
	    return itisRecords;
	    //return null;
	    
	}catch(Throwable t){
	    t.printStackTrace();
	    throw new FailedProxyRequestException("JDBC problem");
	}finally{
	    closeAll(conn, statement, rs);
	    }
    }


    private static final String searchScientificNameSql = "select tsn, complete_name from taxonomic_units where complete_name like ? limit ?,?";
    public List<ItisRecord> searchByScientificName(String queryString, int start, int end) throws IllegalArgumentException, FailedProxyRequestException{
	List<ItisRecord> itisRecords = new ArrayList<ItisRecord>(10);
	Connection conn = null;
	PreparedStatement statement = null;
	ResultSet rs = null;
	try{
	    System.out.println(queryString);
	    conn = DriverManager.getConnection(url);
	    statement = conn.prepareStatement(searchScientificNameSql);
	    statement.setString(1, "%" + queryString + "%");
	    statement.setInt(2, start);
	    statement.setInt(3, end-start);
	    rs = statement.executeQuery();
	    while(rs.next()){
		ItisRecord itisRecord = new ItisRecord();
		String tsn = rs.getString("tsn");
		itisRecord.setTsn(tsn);
		System.out.println(tsn + " " + rs.getString("complete_name"));
		//itisRecord.setCombinedName(rs.getString("vernacular_name"));
		itisRecord.setVernacularNames(getVernacularNames(tsn));
		itisRecords.add(itisRecord);
	    }
	    return itisRecords;
	    
	}catch(Throwable t){
	    t.printStackTrace();
	    throw new FailedProxyRequestException("JDBC problem");
	}finally{
	    closeAll(conn, statement, rs);
	}
    }



    private static final String getByTSNSql = "select * from taxonomic_units where tsn=?";

    public ItisRecord getByTSN(String tsn) throws IllegalArgumentException, FailedProxyRequestException{
	ItisRecord rec = new ItisRecord();
	Connection conn = null;
	PreparedStatement statement = null;
	ResultSet rs = null;
	try{
	    System.out.println(tsn);
	    conn = DriverManager.getConnection(url);
	    statement = conn.prepareStatement(getByTSNSql);
	    statement.setString(1, tsn);
	    rs = statement.executeQuery();
	    while(rs.next()){
		
		break;
	    }
	}catch(Throwable t){
	    t.printStackTrace();
	    throw new FailedProxyRequestException("JDBC problem");
	}finally{
	    closeAll(conn, statement, rs);
	}
	return rec;
    }


    private static final String ranksUpSql = "select complete_name, tsn, parent_tsn, rank_id from taxonomic_units where tsn=?";

    public List<TaxonomicRank> getRanksUpFromTsn(String tsn)throws IllegalArgumentException, FailedProxyRequestException{
	List<TaxonomicRank> upRanks = new ArrayList<TaxonomicRank>();
	Connection conn = null;
	PreparedStatement statement = null;
	ResultSet rs = null;
	try{
	    System.out.println(tsn);
	    conn = DriverManager.getConnection(url);
	    statement = conn.prepareStatement(ranksUpSql);
	    statement.setString(1, tsn);
	    rs = statement.executeQuery();
	    while(rs.next()){
		System.out.println("\t\t---- " + rs.getString("complete_name"));
		String parentTsn = rs.getString("parent_tsn");
		upRanks.addAll(getRanksUpFromTsn(parentTsn));
		TaxonomicRank rank = new TaxonomicRank();
		rank.setRankName(getTaxonType(rs.getString("rank_id")));
		rank.setRankValue(rs.getString("complete_name"));
		int childTsn = rs.getInt("tsn");
		rank.setTsn(childTsn);
		upRanks.add(rank);
		break;
	    }
	}catch(Throwable t){
	    t.printStackTrace();
	    throw new FailedProxyRequestException("JDBC problem");
	}finally{
	    if(statement != null){
		try{
		    statement.close();
		}catch(Throwable t){
		    // OK
		}
	    }
	    if(conn != null){
		try{
		    conn.close();
		}catch(Throwable t){
		    // OK
		}
	    }
	    if(rs != null){
		try{
		    rs.close();
		}catch(Throwable t){
		    // OK
		}
		
	    }
	}
	return upRanks;
    }


    private static final String childrenSql = "select rank_id, tsn, complete_name, parent_tsn from taxonomic_units where parent_tsn =?";
    public List<TaxonomicRank> getRanksOneRankDownFromTsn(String tsn) throws IllegalArgumentException, FailedProxyRequestException{

	List<TaxonomicRank> downRank = new ArrayList<TaxonomicRank>();
	Connection conn = null;
	PreparedStatement statement = null;
	ResultSet rs = null;
	try{
	    System.out.println(tsn);
	    conn = DriverManager.getConnection(url);
	    statement = conn.prepareStatement(childrenSql);
	    //statement.setInt(1, Integer.parseInt(tsn));
	    statement.setString(1, tsn);
	    rs = statement.executeQuery();
	    while(rs.next()){
		TaxonomicRank rank = new TaxonomicRank();
		int childTsn = rs.getInt("tsn");
		rank.setTsn(childTsn);
		System.out.println(rs.getString("tsn") + " " + rs.getString("complete_name") + " " + rs.getString("parent_tsn") + " " + rs.getString("rank_id"));
		System.out.println("\t" + getTaxonType(rs.getString("rank_id")));
	    }
	}catch(Throwable t){
	    t.printStackTrace();
	    throw new FailedProxyRequestException("JDBC problem");
	}finally{
	    if(statement != null){
		try{
		    statement.close();
		}catch(Throwable t){
		    // OK
		}
	    }
	    if(conn != null){
		try{
		    conn.close();
		}catch(Throwable t){
		    // OK
		}
	    }
	    if(rs != null){
		try{
		    rs.close();
		}catch(Throwable t){
		    // OK
		}
		
	    }
	}
	return null;
    }


    public void close() throws IOException{
    }



    private static final String vernacularSql = "select vernacular_name, language from vernaculars where tsn = ?";

    private Map<String, List<String>> getVernacularNames(final String tsn) throws FailedProxyRequestException{
	Map<String, List<String>> vMap = new HashMap<String, List<String>>();

	Connection conn = null;
	PreparedStatement statement = null;
	ResultSet rs = null;
	try{
	    conn = DriverManager.getConnection(url);
	    statement = conn.prepareStatement(vernacularSql);
	    statement.setString(1, tsn);
	    rs = statement.executeQuery();
	    while(rs.next()){
		String lang = rs.getString("language");
		String vern = rs.getString("vernacular_name");
		System.out.println("\t" + lang + " " + vern);
		List<String> langVer = null;
		if(vMap.containsKey(lang)){
		    langVer = vMap.get(lang);
		}else{
		    langVer = new ArrayList<String>();
		    vMap.put(lang, langVer);
		}
		langVer.add(vern);
	    }
	    return vMap;
	}catch(Throwable t){
	    t.printStackTrace();
	    throw new FailedProxyRequestException("JDBC problem");
	}finally{
	    if(statement != null){
		try{
		    statement.close();
		}catch(Throwable t){
		    // OK
		}
	    }
	    if(conn != null){
		try{
		    conn.close();
		}catch(Throwable t){
		    // OK
		}
	    }
	    if(rs != null){
		try{
		    rs.close();
		}catch(Throwable t){
		    // OK
		}
	    
	    }
	}
    }


    private static final String taxonTypeSql = "select rank_name from taxon_unit_types where rank_id=? limit 1";
    String getTaxonType(String rankId) throws FailedProxyRequestException{
	Connection conn = null;
	PreparedStatement statement = null;
	ResultSet rs = null;
	try{
	    conn = DriverManager.getConnection(url);
	    statement = conn.prepareStatement(taxonTypeSql);
	    statement.setString(1, rankId);
	    rs = statement.executeQuery();
	    while(rs.next()){
		return rs.getString("rank_name");
	    }
	}catch(Throwable t){
	    t.printStackTrace();
	    throw new FailedProxyRequestException("JDBC problem");
	}finally{
	    if(statement != null){
		try{
		    statement.close();
		}catch(Throwable t){
		    // OK
		}
	    }
	    if(conn != null){
		try{
		    conn.close();
		}catch(Throwable t){
		    // OK
		}
	    }
	    if(rs != null){
		try{
		    rs.close();
		}catch(Throwable t){
		    // OK
		}
	    
	    }
	}
	return null;
    }


    private static final void closeAll(final Connection conn, final Statement statement, final ResultSet rs){
	if(statement != null){
	    try{
		statement.close();
	    }catch(Throwable t){
		// OK
	    }
	}
	if(conn != null){
	    try{
		conn.close();
	    }catch(Throwable t){
		// OK
	    }
	}
	if(rs != null){
	    try{
		rs.close();
	    }catch(Throwable t){
		// OK
	    }
	}
    }
}
