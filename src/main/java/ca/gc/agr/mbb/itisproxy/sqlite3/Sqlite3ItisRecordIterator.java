package ca.gc.agr.mbb.itisproxy.sqlite3;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import ca.gc.agr.itis.itismodel.ItisRecord;
import ca.gc.agr.itis.itismodel.TaxonComment;
import ca.gc.agr.itis.itismodel.TaxonExpert;
import ca.gc.agr.itis.itismodel.TaxonJurisdictionalOrigin;
import ca.gc.agr.itis.itismodel.TaxonOtherSource;
import ca.gc.agr.itis.itismodel.TaxonPublication;
import ca.gc.agr.itis.itismodel.TaxonomicRank;

public class Sqlite3ItisRecordIterator implements ItisRecordIterator{
    private static final List<PreparedStatement> hierStatements = new ArrayList<PreparedStatement>(12);
    private final static Logger LOGGER = Logger.getLogger(Sqlite3ItisRecordIterator.class.getName()); 
    public static final String SQLITE3_JDBC_CLASS_NAME = "org.sqlite.JDBC";
    public static final String SQLITE3_URL_KEY = "sqlite3UrlKey";

    private Connection conn = null;
    private Statement statement = null;
    private ResultSet rs = null;
    private String url = null;
    private Properties properties = null;
    private boolean hasNext = false;


 
    public void init(final Properties properties) throws IOException{
	this.properties = properties;
	System.out.println("------- kkkk ");
	for(int i=0; i< 12; i++){
	    System.out.println("------- kkkk " + i);
	    hierStatements.add(i, (PreparedStatement)null);
	}
	try{
	    Class.forName(SQLITE3_JDBC_CLASS_NAME);
	    LOGGER.info("SQLITE3_JDBC_CLASS_NAME=" + SQLITE3_JDBC_CLASS_NAME);
	}catch(ClassNotFoundException e){
	    e.printStackTrace();
	    LOGGER.severe("Unable to find driver: " + SQLITE3_JDBC_CLASS_NAME);
	    throw new NullPointerException("Unable to find driver: " + SQLITE3_JDBC_CLASS_NAME);
	}	
	if(!properties.containsKey(SQLITE3_URL_KEY)){
	    LOGGER.severe("No JDBC URL: need to set SQLITE3_URL_KEY");
	    throw new NullPointerException("No JDBC URL: need to set SQLITE3_URL_KEY");
	}
	url = properties.getProperty(SQLITE3_URL_KEY);

	try{
	    conn = DriverManager.getConnection(url);
	}catch(Exception e){
	    e.printStackTrace();
	    LOGGER.severe("Problem opening JDBC connection");
	    throw new IOException();
	}

	try{
	    statement = conn.createStatement();
	}
	catch(Exception e){
	    e.printStackTrace();
	    LOGGER.severe("Problem creating statement");
	    throw new IOException();
	}

	String sql = makeSql(properties);
	try{
	    rs = statement.executeQuery(sql);

	    // JDBC driver does not implement ResultSet.isLast, so this is how we need to emulate this functionality...
	    hasNext = rs.next();
	}
	catch(Exception e){
	    e.printStackTrace();
	    LOGGER.severe("Problem executing query for sql: " + sql);
	    throw new IOException();
	}

	//memoizeKingdoms(url);

    }

    public boolean hasNext(){
	return hasNext;
    }

    public ItisRecord next(){
	ItisRecord rec = null;
	try{
	    rec = populateRecord(rs);
	}catch(SQLException e){
	    e.printStackTrace();
	    return null;
	}
	try{
	    hasNext = rs.next();
	}catch(SQLException se){
	    se.printStackTrace();
	    return null;
	}
	return rec;
    }

    public void remove(){
	//
    }

    public void close() throws IOException{
	closeAll(conn, statement, rs);
	closeAll(authorConn, authorStatement, null);
	closeAll(vernConn, vernStatement, null);
	closeAll(hierConn, null, null);
	for(PreparedStatement ps: hierStatements){
	    closeAll(null, ps, null);
	}
	closeAll(belowConn, belowStatement, null);
	closeAll(ttConn, ttStatement, null);
	closeAll(synConn, synStatement, null);
	closeAll(tsnConn, tsnStatement, null);
	closeAll(geographicConn, geographicStatement, null);
	closeAll(commConn, commStatement, null);
	closeAll(expConn, expStatement, null);
	closeAll(expIdConn, expIdStatement, null);    
	closeAll(otherConn, otherStatement, null);
	closeAll(sourceConn, sourceStatement, null);
	closeAll(docConn, docStatement, null);
	closeAll(refConn, refStatement, null);
	closeAll(jurConn, jurStatement, null);
    }

    /////////////////////
    private String makeSql(final Properties p){
	//return "select * from taxonomic_units where completeness_rtng not null limit 10000";
	return "select * from taxonomic_units";
    }

    private static final void closeAll(final Closeable... closeables){
	for(Closeable c: closeables){
	    if(c != null){
		try{
		    c.close();
		}catch(Throwable t){
		    // OK
		}
	    }
	}
    }

    private static final void closeAll(final Connection connection, final Statement statement, final ResultSet resultSet){
	    if(connection != null){
		try{
		    connection.close();
		}catch(Throwable t){
		    // OK
		}
	    }
	    if(statement != null){
		try{
		    statement.close();
		}catch(Throwable t){
		    // OK
		}
	    }
	    if(resultSet != null){
		try{
		    resultSet.close();
		}catch(Throwable t){
		    // OK
		}
	    }
    }

    private ItisRecord populateRecord(final ResultSet rs) throws SQLException{
	ItisRecord rec = new ItisRecord();
	String tsn = rs.getString("tsn");
	rec.setTsn(tsn);
	rec.setCombinedName(rs.getString("complete_name"));
	tsnToNameMap.put(rec.getTsn(), rec.getCombinedName());
	
	rec.setNameAuthor(makeNameAuthor(rs.getString("taxon_author_id"), url));

	rec.setRankSynomyms(makeRankSynonyms(tsn, url));

	rec.setVernacularNames(makeVernaculars(tsn, url));

	rec.setGlobalSpecies(rs.getString("completeness_rtng"));
	rec.setCompleteness(rs.getString("completeness_rtng"));
	rec.setCurrentTaxonomicStanding(rs.getString("name_usage"));
	//rec.setCurrentRating(rs.getString("currency_rating"));

	rec.setRecordCredibilityRating(rs.getString("credibility_rtng"));

	List<TaxonomicRank> taxonomicHierarchy = makeTaxonomicHierarchy(tsn, url, 0);
	if(taxonomicHierarchy == null || taxonomicHierarchy.size() ==0){
	    // Some TSNs have no up hierarchy when they should; see Euastrum coralloides var. subintegrum TSN: 8561
	    rec.setTaxonomicHierarchy(makeKingdomFromTsn(tsn, url));
	}else{
	    rec.setTaxonomicHierarchy(taxonomicHierarchy);
	}
	rec.setBelowSpeciesRanks(makeBelowSpeciesRanks(tsn, url));

	//rec.setExperts(makeExperts(tsn, url));
	rec.setExperts(makeExperts2(tsn, url));
	rec.setOtherSources(makeOtherSources2(tsn, url));
	rec.setReferences(makeReferences2(tsn, url));


	rec.setGeographicInfo(makeGeographicInfo(tsn, url));
	rec.setComments(makeTaxonComments(tsn, url));

	rec.setTaxonJurisdictionalOrigins(makeJurisdictionalOrigins(tsn, url));

	return rec;
    }
    


    Connection authorConn = null;
    PreparedStatement authorStatement = null;    
    Map<String, String> authorIdMemo = new HashMap<String, String>(1000);
    String authorSql = "select taxon_author from taxon_authors_lkp where taxon_author_id = ?";
    private String makeNameAuthor(final String authorId, final String url) throws SQLException{
	if(authorId == null){
	    return "";
	}

	if(authorIdMemo.containsKey(authorId)){
	    return authorIdMemo.get(authorId);
	}


	ResultSet lrs = null;

	try{
	    if(authorConn == null){
		authorConn = DriverManager.getConnection(url);
		authorStatement = authorConn.prepareStatement(authorSql);
	    }
	    authorStatement.clearParameters();
	    authorStatement.setString(1, authorId);
	    lrs = authorStatement.executeQuery();
	    if(!lrs.next()){
		return "";
	    }
	    String taxonAuthor = lrs.getString("taxon_author");
	    authorIdMemo.put(authorId, taxonAuthor);
	    return taxonAuthor;
	}finally{
	    //closeAll(authorConn, authorStatement, lrs);
	    closeAll(null, null, lrs);
	}
    }


    //preparedStatement.clearParameters();
    Connection vernConn = null;
    PreparedStatement vernStatement = null;
    private Map<String, Map<String, List<String>>> tsnToVernacularMemoize = new HashMap<String, Map<String, List<String>>>();
    String LANGUAGE = "language";
    String VERNACULAR_NAME = "vernacular_name";
    String vernacularSql = "select " + LANGUAGE + "," + VERNACULAR_NAME + " from vernaculars where tsn = ?";
    private Map<String, List<String>> makeVernaculars(final String tsn, final String url) throws SQLException{
	Map<String, List<String>> verns = new HashMap<String, List<String>>(); 
	if(tsn == null){
	    return verns;
	}

	if(tsnToVernacularMemoize.containsKey(tsn)){
	    return tsnToVernacularMemoize.get(tsn);
	}

	ResultSet lrs = null;

	try{
	    if(vernConn == null){
		vernConn = DriverManager.getConnection(url);
		vernStatement = vernConn.prepareStatement(vernacularSql);
	    }
	    vernStatement.clearParameters();
	    vernStatement.setString(1, tsn);
	    lrs = vernStatement.executeQuery();
	    while(lrs.next()){
		String language = lrs.getString(LANGUAGE);
		String vernacular = lrs.getString(VERNACULAR_NAME);
		List<String> list = null;
		if(verns.containsKey(language)){
		    list = verns.get(language);
		}else{
		    list = new ArrayList<String>();
		    verns.put(language, list);
		}
		list.add(vernacular);
	    }
	    tsnToVernacularMemoize.put(tsn, verns);
	    return verns;
	}finally{
	    closeAll(null, null, lrs);
	}
    }



    Connection hierConn = null;

    private static final Map<String, List<TaxonomicRank>> tsnToHierarchy = new HashMap<String, List<TaxonomicRank>>(1000);
    private static final String ranksUpSql = "select complete_name, tsn, parent_tsn, rank_id from taxonomic_units where tsn=?";
    public List<TaxonomicRank> makeTaxonomicHierarchy(final String tsn, final String url, final int depth){
	if(tsnToHierarchy.containsKey(tsn)){
	    return tsnToHierarchy.get(tsn);
	}
	List<TaxonomicRank> upRanks = new ArrayList<TaxonomicRank>();


	ResultSet rs = null;
	try{
	    if(hierConn == null){
		hierConn = DriverManager.getConnection(url);
	    }
	    if(hierStatements.get(depth) == null){
		hierStatements.set(depth, hierConn.prepareStatement(ranksUpSql));
	    }
	    PreparedStatement hierStatement = hierStatements.get(depth);
	    hierStatement.clearParameters();
	    hierStatement.setString(1, tsn);

	    rs = hierStatement.executeQuery();
	    while(rs.next()){
		String parentTsn = rs.getString("parent_tsn");
		// recursion
		upRanks.addAll(makeTaxonomicHierarchy(parentTsn, url, depth+1));
		TaxonomicRank rank = new TaxonomicRank();
		rank.setRankName(getTaxonType(rs.getString("rank_id"), url));
		rank.setRankValue(rs.getString("complete_name"));
		int childTsn = rs.getInt("tsn");
		rank.setTsn(childTsn);
		rank.setCommonNames(makeVernaculars(tsn, url));
		upRanks.add(rank);
		tsnToHierarchy.put(tsn, upRanks);
		break;
	    }
	}catch(Throwable t){
	    t.printStackTrace();
	    return null;
	}finally{
	    closeAll(null, null, rs);
	}
	return upRanks;
    }

    Connection belowConn = null;
    PreparedStatement belowStatement = null;
    private static final String childrenSql = "select rank_id, tsn, complete_name, parent_tsn from taxonomic_units where parent_tsn =?";
    public List<TaxonomicRank> makeBelowSpeciesRanks(final String tsn, final String url){
	List<TaxonomicRank> downRank = new ArrayList<TaxonomicRank>();

	ResultSet rs = null;
	try{
	    if(belowConn == null){
		belowConn = DriverManager.getConnection(url);
		belowStatement = belowConn.prepareStatement(childrenSql);
	    }
	    belowStatement.clearParameters();
	    belowStatement.setString(1, tsn);
	    rs = belowStatement.executeQuery();
	    while(rs.next()){
		TaxonomicRank rank = new TaxonomicRank();
		downRank.add(rank);
		int childTsn = rs.getInt("tsn");
		rank.setTsn(childTsn);
		rank.setRankName(getTaxonType(rs.getString("rank_id"), url));
		rank.setRankValue(rs.getString("complete_name"));
		rank.setCommonNames(makeVernaculars(tsn, url));
	    }
	}catch(Throwable t){
	    t.printStackTrace();
	    return null;
	}finally{
	    //closeAll(belowConn, belowStatement, rs);
	    closeAll(null, null, rs);
	}
	return downRank;
    }



    Connection ttConn = null;
    PreparedStatement ttStatement = null;
    Map<String, String> taxonTypeMemo = new HashMap<String, String>(10000);
    private static final String taxonTypeSql = "select rank_name from taxon_unit_types where rank_id=? limit 1";
    private String getTaxonType(final String rankId, final String url){
	if(rankId == null || rankId.length() ==0){
	    return "";
	}
	if(taxonTypeMemo.containsKey(rankId)){
	    return taxonTypeMemo.get(rankId);
	}
				     
	ResultSet rs = null;
	try{
	    if(ttConn == null){
		ttConn = DriverManager.getConnection(url);
		ttStatement = conn.prepareStatement(taxonTypeSql);
	    }
	    ttStatement.clearParameters();
	    ttStatement.setString(1, rankId);
	    rs = ttStatement.executeQuery();
	    while(rs.next()){
		String rank = rs.getString("rank_name");
		taxonTypeMemo.put(rankId, rank);
		return rank;
	    }
	}catch(Throwable t){
	    t.printStackTrace();
	}finally{
	    closeAll(null, null, rs);
	}
	return null;
    }


    Connection synConn = null;
    PreparedStatement synStatement = null;
    final static String TSN_ACCEPTED = "tsn_accepted";
    private String rankSynonymSql = "select " + TSN_ACCEPTED + " from synonym_links where tsn = ?";

    private List<String> makeRankSynonyms(final String tsn, final String url) throws SQLException{
	List<String> syns = new ArrayList<String>(); 
	if(tsn == null){
	    return syns;
	}
	ResultSet lrs = null;

	try{
	    if(synConn == null){
		synConn = DriverManager.getConnection(url);
		synStatement = synConn.prepareStatement(rankSynonymSql);
	    }
	    synStatement.clearParameters();
	    synStatement.setString(1, tsn);
	    lrs = synStatement.executeQuery();
	    while(lrs.next()){
		String synTsn = lrs.getString(TSN_ACCEPTED);
		syns.add(getNameFromTsn(synTsn, url));
	    }
	}finally{
	    closeAll(null, null, lrs);
	}
	return syns;
    }


    Connection tsnConn = null;
    PreparedStatement tsnStatement = null;
    private Map<String, String>tsnToNameMap = new HashMap<String, String>(2000);
    private static final String nameFromTsnSql = "select complete_name from taxonomic_units where tsn =?";    
    private String getNameFromTsn(final String tsn, final String url) throws SQLException{
	if(tsnToNameMap.containsKey(tsn)){
	    return tsnToNameMap.get(tsn);
	}

	ResultSet rs = null;
	try{
	    if(tsnConn == null){
		tsnConn = DriverManager.getConnection(url);
		tsnStatement = tsnConn.prepareStatement(nameFromTsnSql);
	    }
	    tsnStatement.clearParameters();
	    tsnStatement.setString(1, tsn);
	    rs = tsnStatement.executeQuery();
	    while(rs.next()){
		String name = rs.getString("complete_name");
		tsnToNameMap.put(tsn, name);
		return name;
	    }
	}catch(Throwable t){
	    t.printStackTrace();
	}finally{
	    closeAll(null, null, rs);
	}
	return null;
    }



    Connection geographicConn = null;
    PreparedStatement geographicStatement = null;
    private static final String geographicInfoSql = "select geographic_value from geographic_div where tsn =?";    
    private List<String> makeGeographicInfo(final String tsn, final String url) throws SQLException{
	List<String> geoInfo = new ArrayList<String>();
	//Connection conn = null;
	//PreparedStatement statement = null;
	ResultSet rs = null;
	try{
	    if(geographicConn == null){
		geographicConn = DriverManager.getConnection(url);
		geographicStatement = geographicConn.prepareStatement(geographicInfoSql);
	    }
	    //preparedStatement.
	    geographicStatement.clearParameters();
	    geographicStatement.setString(1, tsn);
	    rs = geographicStatement.executeQuery();
	    while(rs.next()){
		geoInfo.add(rs.getString("geographic_value"));
	    }
	}catch(Throwable t){
	    t.printStackTrace();
	}finally{
	    closeAll(null, null, rs);
	}
	return geoInfo;
    }




    Connection commConn = null;
    PreparedStatement commStatement = null;
    private static final String taxonCommentsSql = "select tsn, commentator,comment_detail from tu_comments_links,comments where tsn = ? and tu_comments_links.comment_id = comments.comment_id";
    private List<TaxonComment> makeTaxonComments(final String tsn, final String url) throws SQLException{
	List<TaxonComment> taxonComments = new ArrayList<TaxonComment>();

	ResultSet rs = null;
	try{
	    if(commConn == null){
		commConn = DriverManager.getConnection(url);
		commStatement = commConn.prepareStatement(taxonCommentsSql);
	    }
	    commStatement.clearParameters();
	    commStatement.setString(1, tsn);
	    rs = commStatement.executeQuery();
	    while(rs.next()){
		TaxonComment tc = new TaxonComment();
		tc.setCommentator(rs.getString("commentator"));
		tc.setCommentDetail(rs.getString("comment_detail"));
		taxonComments.add(tc);
	    }
	}catch(Throwable t){
	    t.printStackTrace();
	}finally{
	    closeAll(null, null,rs);
	}
	return taxonComments;
    }


    Connection expConn = null;
    PreparedStatement expStatement = null;
    private static final String expertsSql2 = "select documentation_id from reference_links where reference_links.tsn = ? and doc_id_prefix = \"EXP\"";
    private List<TaxonExpert> makeExperts2(final String tsn, final String url) throws SQLException{
	List<TaxonExpert> experts = new ArrayList<TaxonExpert>();

	ResultSet rs = null;
	try{
	    if(expConn == null){
		expConn = DriverManager.getConnection(url);
		expStatement = expConn.prepareStatement(expertsSql2);
	    }
	    expStatement.clearParameters();
	    expStatement.setString(1, tsn);
	    rs = expStatement.executeQuery();
	    while(rs.next()){
		String expertId = rs.getString("documentation_id");
		TaxonExpert te = expertIdToTaxonExpert(expertId, url);
		if(te != null){
		    experts.add(te);
		}
	    }
	}catch(Throwable t){
	    t.printStackTrace();
	}finally{
	    closeAll(null, null, rs);
	}
	return experts;
    }


    Connection expIdConn = null;
    PreparedStatement expIdStatement = null;    
    private Map<String, TaxonExpert> expertIdToTaxonExpertMemo = new HashMap<String, TaxonExpert>(200);
    private static final String expertIdToExpertsSql = "select expert, exp_comment from experts where expert_id = ?";
    private TaxonExpert expertIdToTaxonExpert(final String expertId, final String url) throws SQLException{
	if(expertIdToTaxonExpertMemo.containsKey(expertId)){
	    System.out.println("$$");
	    return expertIdToTaxonExpertMemo.get(expertId);
	}

	ResultSet rs = null;
	try{
	    if(expIdConn == null){
		expIdConn  = DriverManager.getConnection(url);
		expIdStatement = expIdConn.prepareStatement(expertIdToExpertsSql);
	    }
	    expIdStatement.clearParameters();
	    expIdStatement.setString(1, expertId);
	    rs = expIdStatement.executeQuery();
	    while(rs.next()){
		TaxonExpert te = new TaxonExpert();
		te.setExpert(rs.getString("expert"));
		te.setComment(rs.getString("exp_comment"));
		expertIdToTaxonExpertMemo.put(expertId, te);;
		return te;
	    }
	}catch(Throwable t){
	    t.printStackTrace();
	}finally{
	    //closeAll(conn, statement, rs);
	    closeAll(null, null, rs);
	}
	return null;
    }	




    Connection otherConn = null;
    PreparedStatement otherStatement = null;
    //private static final String otherSourcesSql2 = "select source, source_type, source_comment from other_sources, reference_links where reference_links.tsn = ? and doc_id_prefix = \"SRC\" and reference_links.documentation_id=other_sources.source_id";
private static final String otherSourcesSql2 = "select documentation_id from reference_links where reference_links.tsn = ? and doc_id_prefix = \"SRC\"";
    private List<TaxonOtherSource> makeOtherSources2(final String tsn, final String url) throws SQLException{
	List<TaxonOtherSource> otherSources = new ArrayList<TaxonOtherSource>();

	ResultSet rs = null;
	try{
	    if(otherConn == null){
		otherConn = DriverManager.getConnection(url);
		otherStatement = otherConn.prepareStatement(otherSourcesSql2);
	    }
	    otherStatement.clearParameters();
	    otherStatement.setString(1, tsn);
	    rs = otherStatement.executeQuery();
	    while(rs.next()){
		String otherSourceId = rs.getString("documentation_id");
		TaxonOtherSource tos = sourceIdToOtherSource(otherSourceId, url);
		if(tos != null){
		    otherSources.add(tos);
		}
	    }
	}catch(Throwable t){
	    t.printStackTrace();
	}finally{
	    closeAll(null, null, rs);
	}
	return otherSources;
    }



    Connection sourceConn = null;
    PreparedStatement sourceStatement = null;
    private Map<String, TaxonOtherSource> otherSourcesIdToTaxonOtherSourceMemo = new HashMap<String, TaxonOtherSource>(200);
    private static final String sourceIdToOtherSource2Sql = "select source, source_type, source_comment from other_sources where source_id = ?";
     private TaxonOtherSource sourceIdToOtherSource(String otherSourceId, String url){
	 if(otherSourceId == null){
	     return null;
	 }

	 if(otherSourcesIdToTaxonOtherSourceMemo.containsKey(otherSourceId)){
	     return otherSourcesIdToTaxonOtherSourceMemo.get(otherSourceId);
	 }

	ResultSet rs = null;
	try{
	    if(sourceConn == null){
		sourceConn = DriverManager.getConnection(url);
		sourceStatement = sourceConn.prepareStatement(sourceIdToOtherSource2Sql);
	    }
	    sourceStatement.clearParameters();
	    sourceStatement.setString(1, otherSourceId);
	    rs = sourceStatement.executeQuery();
	    while(rs.next()){
		TaxonOtherSource tos = new TaxonOtherSource();
		tos.setSource(rs.getString("source"));
		tos.setSourceComment(rs.getString("source_comment"));
		tos.setSourceType(rs.getString("source_type"));
		otherSourcesIdToTaxonOtherSourceMemo.put(otherSourceId, tos);
		return tos;
	    }
	}catch(Throwable t){
	    t.printStackTrace();
	}finally{
	    closeAll(null, null, rs);
	}
	return null;
     }



    Connection docConn = null;
    PreparedStatement docStatement = null;
    private static final Map<String, TaxonPublication> docIdToPublicationMemoize = new HashMap<String, TaxonPublication>();
    private static final String publicationsMemoSql = "select reference_author, title, publication_name, actual_pub_date, publisher, pub_place, isbn,issn,pages, pub_comment from publications where publication_id = ?";
    private final TaxonPublication docIdToPublication(final String docId, final String url){
	if(docIdToPublicationMemoize.containsKey(docId)){
	    docIdToPublicationMemoize.get(docId);
	}

	ResultSet rs = null;
	try{
	    if(docConn == null){
		docConn = DriverManager.getConnection(url);
		docStatement = docConn.prepareStatement(publicationsMemoSql);
	    }
	    docStatement.clearParameters();
	    docStatement.setString(1, docId);
	    rs = docStatement.executeQuery();
	    while(rs.next()){
		TaxonPublication tp = new TaxonPublication();
		tp.setReferenceAuthor(rs.getString("reference_author"));
		tp.setPubYear(rs.getString("actual_pub_date"));
		tp.setPubName(rs.getString("title"));
		tp.setPublisher(rs.getString("publisher"));
		tp.setPubPlace(rs.getString("pub_place"));
		tp.setPages(rs.getString("pages"));
		tp.setIsbn(rs.getString("isbn"));
		tp.setPubComment(rs.getString("pub_comment"));
		docIdToPublicationMemoize.put(docId, tp);
		return tp;
	    }
	}catch(Throwable t){
	    t.printStackTrace();
	}finally{
	    closeAll(null, null, rs);
	}
	return null;
    }


    Connection refConn = null;
    PreparedStatement refStatement = null;
    private static final String publicationsSql2 = "select documentation_id from reference_links where tsn = ? and doc_id_prefix = \"PUB\"";
    private List<TaxonPublication> makeReferences2(final String tsn, final String url) throws SQLException{
	List<TaxonPublication> publications = new ArrayList<TaxonPublication>();

	ResultSet rs = null;
	try{
	    if(refConn == null){
		refConn = DriverManager.getConnection(url);
		refStatement = refConn.prepareStatement(publicationsSql2);
	    }
	    refStatement.clearParameters();
	    refStatement.setString(1, tsn);

	    rs = refStatement.executeQuery();
	    while(rs.next()){
		String docId = rs.getString("documentation_id");
		publications.add(docIdToPublication(docId, url));
	    }
	}catch(Throwable t){
	    t.printStackTrace();
	}finally{
	    closeAll(null, null, rs);
	}
	return publications;
    }




    PreparedStatement jurStatement = null;
    Connection jurConn = null;
    private static final String jurisdictionalOriginSql = "select jurisdiction_value, origin from jurisdiction where tsn = ?";
    private List<TaxonJurisdictionalOrigin> makeJurisdictionalOrigins(final String tsn, final String url) throws SQLException{
	List<TaxonJurisdictionalOrigin> jurisdictionalOrigins = new ArrayList<TaxonJurisdictionalOrigin>();
	ResultSet rs = null;
	try{
	    if(jurConn == null){
		jurConn = DriverManager.getConnection(url);
		jurStatement = jurConn.prepareStatement(jurisdictionalOriginSql);
	    }
	    jurStatement.clearParameters();
	    jurStatement.setString(1, tsn);
	    rs = jurStatement.executeQuery();
	    while(rs.next()){
		TaxonJurisdictionalOrigin tjo = new TaxonJurisdictionalOrigin();
		tjo.setJurisdiction(rs.getString("jurisdiction_value"));
		tjo.setOrigin(rs.getString("origin"));
		jurisdictionalOrigins.add(tjo);
	    }
	}catch(Throwable t){
	    t.printStackTrace();
	}finally{
	    //closeAll(jurConn, jurStatement, rs);
	    closeAll(null, null, rs);
	}
	return jurisdictionalOrigins;
    }

    private static final List<TaxonomicRank> makeKingdomFromTsn(final String tsn, final String url){
	return null;
    }
}

