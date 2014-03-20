package ca.gc.agr.mbb.itisproxy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import ca.gc.agr.mbb.itisproxy.sqlite3.SqliteProxyImpl;
import ca.gc.agr.itis.itismodel.ItisRecord;
import ca.gc.agr.itis.itismodel.TaxonComment;
import ca.gc.agr.itis.itismodel.TaxonExpert;
import ca.gc.agr.itis.itismodel.TaxonJurisdictionalOrigin;
import ca.gc.agr.itis.itismodel.TaxonOtherSource;
import ca.gc.agr.itis.itismodel.TaxonPublication;
import ca.gc.agr.itis.itismodel.TaxonomicRank;
import ca.gc.agr.mbb.itisproxy.Proxy;
import ca.gc.agr.mbb.itisproxy.Util;
import ca.gc.agr.mbb.itisproxy.entities.AnyMatchCount;
import ca.gc.agr.mbb.itisproxy.entities.AnyMatchItem;
import ca.gc.agr.mbb.itisproxy.entities.AnyMatchList;
import ca.gc.agr.mbb.itisproxy.entities.Comment;
import ca.gc.agr.mbb.itisproxy.entities.CommonName;
import ca.gc.agr.mbb.itisproxy.entities.CommonNamesList;
import ca.gc.agr.mbb.itisproxy.entities.Expert;
import ca.gc.agr.mbb.itisproxy.entities.FullRecord;
import ca.gc.agr.mbb.itisproxy.entities.GetFullHierarchyFromTSN;
import ca.gc.agr.mbb.itisproxy.entities.GetKingdomNames;
import ca.gc.agr.mbb.itisproxy.entities.HierarchyRecord;
import ca.gc.agr.mbb.itisproxy.entities.JurisdictionalOrigin;
import ca.gc.agr.mbb.itisproxy.entities.Kingdom;
import ca.gc.agr.mbb.itisproxy.entities.OtherSource;
import ca.gc.agr.mbb.itisproxy.entities.Publication;
import ca.gc.agr.mbb.itisproxy.entities.ScientificName;
import ca.gc.agr.mbb.itisproxy.entities.SearchByCommonName;
import ca.gc.agr.mbb.itisproxy.entities.TaxRank;
import ca.gc.agr.mbb.itisproxy.entities.GeoDivision;
import ca.gc.agr.mbb.itisproxy.entities.SearchByScientificName;
import ca.gc.agr.mbb.itisproxy.entities.Synonym;
import ca.gc.agr.mbb.itisproxy.wsclient.WS;
import ca.gc.agr.mbb.itisproxy.wsclient.WSState;

public class ProxyImpl implements Proxy, ProxyInfo{
    private final static Logger LOGGER = Logger.getLogger(ProxyImpl.class.getName()); 

    private volatile boolean inited = false;

    // These are seconds; they define the max time we expect from various ws queries
    private int kingdonResponseLimitSeconds = 5;
    private int getByTsnResponseLimitSeconds = 7;
    private int searchResponseLimitSeconds = 18;

    private static DataConverter dataConverter = new JsonDataConverter();
    private static SearchService searchService = new WS();

    public ProxyImpl(){

    }

    public static final Proxy displayInstance(final Proxy realProxy){
	return new DisplayProxy(realProxy);
    }


    public static final Proxy instance(Properties props){
	Proxy proxy = null;
	if(props != null && props.containsKey(NO_CACHING_KEY) && props.getProperty(NO_CACHING_KEY).toLowerCase().equals("true")){
	    LOGGER.info("Instantiating NON-CACHING proxy");
	    //proxy = new ProxyImpl();
	    proxy = makeProxyImpl(props.getProperty(PROXY_IMPL_KEY));
	}else{
	    LOGGER.info("Instantiating CACHING proxy");
	    proxy = new CachingProxyImpl(new ProxyImpl());
	}
	try{
	    proxy.init(props);
	}catch(Exception e){
	    e.printStackTrace();
	    throw new NullPointerException();
	}
	return proxy;
    }

    private static Proxy makeProxyImpl(String s){
	if(s == null){
	    return  new SqliteProxyImpl();
	}
	return new ProxyImpl();
    }

    public void init(Properties p){
	if(!inited){
	    LOGGER.info("init....");
	    inited = true;
	}
    }
    
    public List<ItisRecord> getKingdoms() throws FailedProxyRequestException{
	/*
	String json = (String)WS.searchService(WSState.SERVICE_GET_KINGDOMS, null, dataConverter, GetKingdomNames.class);
	if(json == null){
	    throw new FailedProxyRequestException();
	}
	GetKingdomNames getKingdomNames = null;
	try{
	    getKingdomNames = (GetKingdomNames)dataConverter.convert(json, GetKingdomNames.class);
	}catch(Exception e){
	    throw new FailedProxyRequestException(e);
	}
	*/
	GetKingdomNames getKingdomNames = (GetKingdomNames)searchService.search(WSState.SERVICE_GET_KINGDOMS, null, dataConverter, GetKingdomNames.class);
	
	List<Kingdom> kingdoms = getKingdomNames.kingdoms;
	int numKingdoms = kingdoms.size();
	List<ItisRecord> itisRecords = new ArrayList<ItisRecord>(numKingdoms);

	ItisRecord itisRecord = null;
	for(Kingdom kingdom: kingdoms){
	    if(kingdom == null){
		break;
	    }
	    itisRecord = new ItisRecord();
	    itisRecord.setTsn(kingdom.tsn);
	    itisRecord.setCombinedName(kingdom.kingdomName);
	    itisRecords.add(itisRecord);
	}
	return itisRecords;
    }


    public final int getAnyMatchCount(String queryString) throws IllegalArgumentException, FailedProxyRequestException{
	AnyMatchCount anyMatchCount = (AnyMatchCount)genericSearch(queryString, WSState.PARAM_SRCH_KEY, 
								   WSState.SERVICE_GET_ANY_MATCH_COUNT, AnyMatchCount.class);
	int count;
	if(anyMatchCount == null){
	    count = 0;
	}else{
	    try{
		count = Integer.parseInt(anyMatchCount.count);
		if(count < 0){
		    throw new FailedProxyRequestException("ITIS-WS returned count < 0: " + anyMatchCount.count);
		}
	
	    }catch(NumberFormatException e){
		throw new FailedProxyRequestException("ITIS-WS returned count not an integer:[" + anyMatchCount.count + "]",
						      e);
	    }
	}
	return count;
    }


    public final List<ItisRecord> searchByAnyMatch(final String s, int start, int end, boolean searchAscending) 
	throws IllegalArgumentException, FailedProxyRequestException{
	Util.checkRange(start, end, "");
	
	//ITIS pages start at 1
	if(start == 0){
	    ++start;
	    ++end;
	}
	AnyMatchList aml = (AnyMatchList)genericSearch(s, searchAscending, 
						       start, end, 
						       WSState.PARAM_SRCH_KEY, 
						       WSState.SERVICE_SEARCH_FOR_ANY_MATCH_PAGED, 
						       AnyMatchList.class);
	List<ItisRecord> results = new ArrayList<ItisRecord>(0);

	List<AnyMatchItem> items = aml.anyMatchItems;

	if(items != null &&  items.size() > 0){
	    int numResults = items.size();

	    results = new ArrayList<ItisRecord>(numResults);
	    for(AnyMatchItem item: items){
		ItisRecord rec = new ItisRecord();
		rec.setTsn(item.tsn);
		rec.setCombinedName(item.sciName);
		rec.setNameAuthor(item.author);
		results.add(rec);
		CommonNamesList names = item.commonNamesList;
		if(names == null){
		    continue;
		}
		for(CommonName name: names.commonNames){
		    rec.addVernacularName(name.commonName, name.language);
		}
	    }
	}
	return results;

    }


    public final List<ItisRecord> searchByCommonName(final String s, final int start, final int end)
	throws IllegalArgumentException, FailedProxyRequestException{
	return searchByCommonName(s, start, end, true);
    }


    public final List<ItisRecord> searchByCommonNameBeginsWith(final String s, final int start, final int end) 
	throws IllegalArgumentException, FailedProxyRequestException{
	return searchByCommonNameBeginsWith(s, start, end, 0l);

    }


    // This method exists to pre-caching (via CacheWarmer) can be done witha delay between requests to reduce impact on ITIS server
    protected final List<ItisRecord> searchByCommonNameBeginsWith(final String s, final int start, final int end, final long delay) 
	throws IllegalArgumentException, FailedProxyRequestException{

	return searchByCommonNameGeneric(WSState.SERVICE_SEARCH_BY_COMMON_NAME_BEGINS_WITH, s, start, end, delay);
    }


	public final List<ItisRecord> searchByCommonNameEndsWith(final String s, final int start, final int end) 
	throws IllegalArgumentException, FailedProxyRequestException{

	return searchByCommonNameGeneric(WSState.SERVICE_SEARCH_BY_COMMON_NAME_ENDS_WITH, s, start, end);
    }	


    protected final static Object genericSearch(final String s, String searchParameterKey, String searchService, Class jsonContentClass)
	throws IllegalArgumentException, FailedProxyRequestException{
	
	return genericSearch(s, false, NO_PAGING, NO_PAGING, searchParameterKey, searchService, jsonContentClass);
    }

    protected final List<ItisRecord> searchByCommonName(final String s, final int start, final int end, final boolean populateScientificName) 
	throws IllegalArgumentException, FailedProxyRequestException{
	
	return searchByCommonNameGeneric(WSState.SERVICE_SEARCH_BY_COMMON_NAME, s, start, end);
    }


    private static final Object genericSearch(final String s, boolean sortAscend, final int start, final int end, String searchParameterKey, String searchServiceName, Class jsonContentClass)
	throws IllegalArgumentException, FailedProxyRequestException{
	if(jsonContentClass == null){
	    throw new IllegalArgumentException("jsonContentClass cannot be null");
	}
	
	String tmp = "START genericSearch s=" + s + " service=" + searchServiceName 
	    + " toClass=" + jsonContentClass.getName();
	LOGGER.info(tmp);
	System.err.println(tmp);

	
	Util.checkString(s,"");
	Util.checkString(searchParameterKey,"");
	Util.checkString(searchServiceName,"");

	Properties getParams = new Properties();

	getParams.setProperty(searchParameterKey, s);
	
	LOGGER.info("genericSearch s=" + s + " service=" + searchServiceName);

	if(start != NO_PAGING){
	    getParams.setProperty(PAGING_START_NUM, Integer.toString(start));
	    getParams.setProperty(PAGING_SIZE, Integer.toString(end-start));
	    if(sortAscend){
		getParams.setProperty(PAGING_SORT_ASCEND, "true");
	    }else{
		getParams.setProperty(PAGING_SORT_ASCEND, "false");
	    }
	}

	System.out.println("Convert to class: " + jsonContentClass);
	return searchService.search(searchServiceName, getParams, dataConverter, jsonContentClass);
    }


    public final List<ItisRecord> searchByScientificName(final String s, final int start, final int end)	
	throws IllegalArgumentException, FailedProxyRequestException{
	return searchByScientificName(s, start, end, 0l);
    }


    // searchByScientificName is used by CachingProxyImpl to pre-warm the cache, and it uses the delay to make sure
    //  we are throttling the requests to the ITIS server. See CachingProxyImpl.WarmCache.run for info
    //
    public final List<ItisRecord> searchByScientificName(final String s, final int start, final int end, long delay)
	throws IllegalArgumentException, FailedProxyRequestException{
	Util.checkRange(start, end, "");
	SearchByScientificName sbsn = 
	    (SearchByScientificName)genericSearch(s, 
						  WSState.PARAM_SRCH_KEY, 
						  WSState.SERVICE_SEARCH_BY_SCIENTIFIC_NAME, 
						  SearchByScientificName.class);
	if(sbsn == null){
	    LOGGER.info("Error: Search:[" + s + "]");
	    throw new FailedProxyRequestException();
	}
	List<ItisRecord> itisRecords = new ArrayList<ItisRecord>(0);

	List<ScientificName> scientificNames = sbsn.scientificNames;
	zeroNullContainingList(scientificNames);
	
	int totalNumHits = scientificNames.size();
	if(scientificNames != null 
	   && totalNumHits > 0
	   && totalNumHits > start){

	    int numHits = min(totalNumHits, end - start);

	    LOGGER.info(" ------------- searchByScientificName total = "
			+ totalNumHits
			+ "  numHits=" + numHits);
	    if(numHits > 0){
		itisRecords = new ArrayList<ItisRecord>(numHits);
		ScientificName scientificName = null;
		for(int i=start; i <= end && i<totalNumHits; i++){
		    scientificName = scientificNames.get(i);
		    if(scientificName == null){
			return itisRecords;
		    }
		    ItisRecord itisRecord = getByTSN(scientificName.tsn);
		    itisRecords.add(itisRecord);
		}
	    }
	}
	LOGGER.info("END searchByScientificNames");
	return itisRecords;
    }

    public FullRecord getFullRecordByTSN(final String tsn) throws IllegalArgumentException, FailedProxyRequestException{
	Util.checkStringIsPositiveInteger(tsn);
	return (FullRecord)genericSearch(tsn, 
					 WSState.PARAM_TSN, WSState.SERVICE_GET_FULL_RECORD_FROM_TSN, 
					 FullRecord.class);
    }

    public ItisRecord getByTSN(final String tsn) throws IllegalArgumentException, FailedProxyRequestException{
	LOGGER.info("START getByTSN: " + tsn);
	Util.checkStringIsPositiveInteger(tsn);

	ItisRecord rec = null;

	FullRecord fullRecord  = getFullRecordByTSN(tsn);

	if(fullRecord == null || fullRecord.tsn == null){
	    LOGGER.info("END getByTSN: " + tsn);
	    return null;
	}

	cleanFullRecord(fullRecord);
	
	rec = populateFullItisRecord(fullRecord);

	LOGGER.info("END getByTSN: " + tsn);
	return rec;
    }

    public final ScientificName getScientificNameFromTSN(final String tsn) throws IllegalArgumentException, FailedProxyRequestException{
	return (ScientificName)genericSearch(tsn, WSState.PARAM_TSN, WSState.SERVICE_GET_SCIENTIFIC_NAME_FROM_TSN, 
					     ScientificName.class);
    }


    // includes THIS tsn
    public final List<TaxonomicRank> getRanksUpFromTsn(final String tsn)throws IllegalArgumentException, FailedProxyRequestException{
	return getRanksUpFromTsn(tsn, false);
    }

    protected List<TaxonomicRank> getRanksUpFromTsn(final String tsn, final boolean fullRecordPopulate)throws IllegalArgumentException, FailedProxyRequestException{
	return getRank(tsn, WSState.SERVICE_GET_FULL_HIERARCHY_FROM_TSN, true, false, fullRecordPopulate);
    }

    public final List<TaxonomicRank> getRanksOneRankDownFromTsn(final String tsn) throws IllegalArgumentException, FailedProxyRequestException{
	return getRanksOneRankDownFromTsn(tsn, false);
    }

    protected List<TaxonomicRank> getRanksOneRankDownFromTsn(final String tsn, final boolean fullRecordPopulate) throws IllegalArgumentException, FailedProxyRequestException{
	return getRank(tsn, WSState.SERVICE_GET_HIERARCHY_DOWN_FROM_TSN, false, true, fullRecordPopulate);
    }






    ///////////////////////////////////////////////////////////////////////////////////////////////////
    // This deals with issues with both GET_FULL_HIERARCHY_FROM_TSN and GET_HIERARCHY_DOWN_FROM_TSN
    //   - GET_FULL_HIERARCHY_FROM_TSN needs to stop when it reaches itself (otherwise will show children nodes)
    //   - GET_HIERARCHY_DOWN_FROM_TSN needs to ignore self and continue it reaches itself (otherwise will show itself)

    protected static final List<TaxonomicRank> getRank(final String tsn, final String service, final boolean stopAtSelf, final boolean throwAwaySelf, final boolean fullRecordPopulate)
	throws IllegalArgumentException, FailedProxyRequestException{

	LOGGER.info("GET RANK: " + tsn + " " + service);

	if(service == null){
	    throw new IllegalArgumentException("service cannot be null");
	}

	Util.checkStringIsPositiveInteger(tsn);

	if(! 
	   (service.equals(WSState.SERVICE_GET_HIERARCHY_DOWN_FROM_TSN)
	    || 
	    service.equals(WSState.SERVICE_GET_FULL_HIERARCHY_FROM_TSN))){
	    throw new IllegalArgumentException("Unsupported service:"
					       + service
					       + "  Only supports the services WSState.SERVICE_GET_HIERARCHY_DOWN_FROM_TSN or WSState.SERVICE_GET_FULL_HIERARCHY_FROM_TSN");
	}
	//Only one of these can be true at one time
	if(stopAtSelf && stopAtSelf==throwAwaySelf){
	    throw new IllegalArgumentException("Only one of stopAtSelf and stopAtSelf can be true at once");
	}
	GetFullHierarchyFromTSN gfhft = (GetFullHierarchyFromTSN)genericSearch(tsn, 
							   WSState.PARAM_TSN, service,
							   GetFullHierarchyFromTSN.class);
	List<TaxonomicRank> taxonomicRankRecords = new ArrayList<TaxonomicRank>(20);
	if(gfhft != null && gfhft.hierarchyList != null){
	    List<HierarchyRecord> hierarchyRecords = gfhft.hierarchyList;
	    
	    for(HierarchyRecord hierarchyRecord: hierarchyRecords){
		if(hierarchyRecord == null){
		    return taxonomicRankRecords;
		}
		if(hierarchyRecord.tsn.equals(tsn)){
		    if(stopAtSelf){
			break;
		    }
		    if(throwAwaySelf){
			continue;
		    }
		}
		taxonomicRankRecords.add(populateTaxonomicRank(hierarchyRecord));
	    }
	}

	return taxonomicRankRecords;
    }

    protected final List<ItisRecord> searchByCommonNameGeneric(final String commonSearchService, final String s, final int start, final int end) 
	throws IllegalArgumentException, FailedProxyRequestException{
	return searchByCommonNameGeneric(commonSearchService, s, start, end, 0l);
    }


	protected final List<ItisRecord> searchByCommonNameGeneric(final String commonSearchService, final String s, final int start, final int end, final long delay) 
	throws IllegalArgumentException, FailedProxyRequestException{

	SearchByCommonName sbcn = (SearchByCommonName)genericSearch(s, 
								    WSState.PARAM_SRCH_KEY, commonSearchService,
								    SearchByCommonName.class);
	if(sbcn.commonNames == null){
	    return new ArrayList<ItisRecord>(1);
	}

	zeroNullContainingList(sbcn.commonNames);
	List<CommonName> commonNames = sbcn.commonNames;    

	List<ItisRecord> itisRecords = new ArrayList<ItisRecord>(commonNames.size());

	ItisRecord itisRecord;
	
	int numHits = commonNames.size();

	LOGGER.info(" ------------- Proxy.searchByCommonName* " 
		    + " searchString=[" + s + "]"
		    + "  numHits=" + numHits + "   start=" + start + " end=" + end);

	CommonName commonName = null;
	for(int i=start; i <= end && i<numHits; i++){
	    commonName = commonNames.get(i);
	    if(commonName == null){
		LOGGER.info(" ------------- Proxy.searchByCommonName*:  !!! commonName is null; i=" + i);
		return itisRecords;
	    }

	    LOGGER.info("getByTSN for results: " + commonName.tsn);
	    itisRecord = getByTSN(commonName.tsn);
	    itisRecords.add(itisRecord);
	    if(delay > 0l){
		try{
		    Thread.currentThread().sleep(delay);
		}catch(Throwable t){
		    t.printStackTrace();
		    //OK
		}
	    }
	}

	LOGGER.info(" ------------- Proxy.searchByCommonName: itisRecords.size()=" + itisRecords.size());
	return itisRecords;
    }


    protected static final TaxonomicRank populateTaxonomicRank(final HierarchyRecord hierarchyRecord)throws IllegalArgumentException{ 
	if(hierarchyRecord == null){
	    throw new IllegalArgumentException();
	}

	TaxonomicRank taxonomicRank = null;
	taxonomicRank = new TaxonomicRank();
	taxonomicRank.setTsn(Integer.parseInt(hierarchyRecord.tsn));
	taxonomicRank.setRankName(hierarchyRecord.rankName);
	taxonomicRank.setRankValue(hierarchyRecord.taxonName);

	return taxonomicRank;
    }


    public static final ItisRecord populateFullItisRecord(final FullRecord fr) throws FailedProxyRequestException, IllegalArgumentException{
	return populateFullItisRecord(fr, null, null);
    }

    public static final ItisRecord populateFullItisRecord(final FullRecord fr, final List<TaxRank> aboveRanks, final List<TaxRank> belowRanks) throws FailedProxyRequestException, IllegalArgumentException{
	if(fr == null){
	    throw new IllegalArgumentException("FullRecord is null");
	}
	ItisRecord ir = new ItisRecord();

	ir.setKingdom(fr.kingdom.kingdomName);

	ir.setTsn(fr.tsn);
	if(fr.usage.taxonUsageRating != null){
	    ir.setCurrentTaxonomicStanding(fr.usage.taxonUsageRating);
	}

	if(fr.scientificName != null){
	    ir.setCombinedName(fr.scientificName.combinedName);
	}
	if(fr.taxonAuthor != null){
	    ir.setNameAuthor(fr.taxonAuthor.authorship);
	}

	if(fr.synonymList != null){
	    ir.setRankSynomyms(makeSynonyms(fr.synonymList.synonyms));
	}

	if(fr.commonNameList != null){
	    ir.setVernacularNames(makeVernacularNames(fr.commonNameList.commonNames));
	}

	if(fr.currencyRating != null){
	    ir.setCurrencyRating(fr.currencyRating.taxonCurrency);
	}

	if(fr.completenessRating != null){
	    ir.setCompleteness(fr.completenessRating.completeness);
	    ir.setGlobalSpecies(fr.completenessRating.completeness);
	}


	if(fr.credibilityRating != null){
	    ir.setRecordCredibilityRating(fr.credibilityRating.credRating);
	}

	if(aboveRanks == null){
	    if(fr.tsn != null && ir.getTaxonomicHierarchy().size() == 0){
		//ir.setTaxonomicHierarchy(getRanksUpFromTsn(fr.tsn, true));
		System.err.println("Above ranks");
		ir.setTaxonomicHierarchy(getRank(fr.tsn, WSState.SERVICE_GET_FULL_HIERARCHY_FROM_TSN, true, false, false));
	    }
	}else{
	    populateAboveRanks(ir, aboveRanks);
	}

	if(belowRanks == null){
	    if(fr.tsn != null && ir.getBelowSpeciesRanks().size() == 0){
		//ir.setBelowSpeciesRanks(getRanksOneRankDownFromTsn(fr.tsn, true));
		System.err.println("Below ranks");
		ir.setBelowSpeciesRanks(getRank(fr.tsn, WSState.SERVICE_GET_HIERARCHY_DOWN_FROM_TSN, false, true, false));
	    }
	}else{
	    populateBelowRanks(ir, belowRanks);
	}

	if(fr.expertList != null){
	    ir.setExperts(makeExperts(fr.expertList.experts));
	}

	if(fr.otherSourceList != null){
	    ir.setOtherSources(makeOtherSources(fr.otherSourceList.otherSources));
	}

	ir.setGeographicInfo(makeGeographicDivisions(fr.geographicDivisionList.geoDivisions));

	if(fr.publicationList != null){
	    ir.setReferences(makeReferences(fr.publicationList.publications));
	}

	ir.setTaxonJurisdictionalOrigins(makeJurisdictionalOrigins(fr.jurisdictionalOriginList.jurisdictionalOrigins));
	//LOGGER.info("populate: " + ir.getTaxonJurisdictionalOrigins());

	if(fr.commentList != null){
	    ir.setComments(makeComments(fr.commentList.comments));
	}

	return ir;
    }


    protected static final List<String> makeSynonyms(final List<Synonym>synonyms) throws IllegalArgumentException{
	if(synonyms == null){
	    return new ArrayList<String>(1);
	}
	List<String> rs = new ArrayList<String>(synonyms.size());

	for(Synonym synonym: synonyms){
	    if(synonym == null){
		return rs;
	    }
	    rs.add(synonym.sciName);
	}

	return rs;
    }

    protected static final Map<String, List<String>> makeVernacularNames(final List<CommonName> commonNames)throws IllegalArgumentException{
	if(commonNames == null){
	    return new HashMap<String, List<String>>(1);
	}

	Map<String, List<String>> vns = new HashMap<String, List<String>>();

	for(CommonName name: commonNames){
	    List<String> namesByLanguage = null;
	    String key = name.language;
	    if(! vns.containsKey(key)){
		namesByLanguage = new ArrayList<String>(2);
		vns.put(key, namesByLanguage);
	    }else{
		namesByLanguage = vns.get(key);
	    }
	    namesByLanguage.add(name.commonName);
	}

	return vns;
    }

    protected static final List<TaxonExpert> makeExperts(final List<Expert>experts)throws IllegalArgumentException{
	if(experts == null){
	    return new ArrayList<TaxonExpert>(1);
	}

	List<TaxonExpert> tel = new ArrayList<TaxonExpert>(experts.size());

	for(Expert expert: experts){
	    TaxonExpert te = new TaxonExpert();
	    te.setExpert(expert.expert);
	    te.setComment(expert.comment);
	    tel.add(te);
	}

	return tel;
    }

    protected static final List<TaxonOtherSource>makeOtherSources(final List<OtherSource>otherSources)throws IllegalArgumentException{
	if(otherSources == null){
	    return new ArrayList<TaxonOtherSource>(1);
	}

	List<TaxonOtherSource> tosl = new ArrayList<TaxonOtherSource>(otherSources.size());

	for(OtherSource otherSource: otherSources){
	    TaxonOtherSource tos = new TaxonOtherSource();
	    tos.setSource(otherSource.source);
	    tos.setSourceComment(otherSource.sourceComment);
	    tos.setSourceType(otherSource.sourceType);
	    tosl.add(tos);
	}

	return tosl;
    }

    protected static final List<TaxonPublication>makeReferences(final List<Publication>publications) throws IllegalArgumentException{
	if(publications == null){
	    return new ArrayList<TaxonPublication>(1);
	}

	List<TaxonPublication> tpl = new ArrayList<TaxonPublication>(publications.size());

	for(Publication publication: publications){
	    TaxonPublication tp = new TaxonPublication();
	    tp.setReferenceAuthor(publication.referenceAuthor);
	    tp.setPubYear(publication.listedPubDate);
	    tp.setPubName(publication.pubName);
	    tp.setPublisher(publication.publisher);
	    tp.setPubPlace(publication.pubPlace);
	    tp.setPages(publication.pages);
	    tp.setIsbn(publication.isbn);
	    tp.setPubComment(publication.pubComment);
	    tpl.add(tp);
	}

	return tpl;
    }


    protected static final List<TaxonJurisdictionalOrigin>makeJurisdictionalOrigins(final List<JurisdictionalOrigin>jurisdictions)throws IllegalArgumentException{
	//LOGGER.info("makeJurisdictionalOrigins: jurisdictions: " + jurisdictions);
	if(jurisdictions == null){
	    return new ArrayList<TaxonJurisdictionalOrigin>(1);
	}

	List<TaxonJurisdictionalOrigin> tpol = new ArrayList<TaxonJurisdictionalOrigin>(jurisdictions.size());

	for(JurisdictionalOrigin jurisdiction: jurisdictions){
	    TaxonJurisdictionalOrigin tpo = new TaxonJurisdictionalOrigin();
	    tpo.setOrigin(jurisdiction.origin);
	    tpo.setJurisdiction(jurisdiction.jurisdictionValue);

	    tpol.add(tpo);
	}
	//LOGGER.info("makeJurisdictionalOrigins: tpol: " + tpol);
	return tpol;
    }

    protected static final List<String> makeGeographicDivisions(List<GeoDivision> geoDivisions){
	List<String> gd = new ArrayList<String>(geoDivisions.size());
	for(GeoDivision geog: geoDivisions){
	    gd.add(geog.geographicValue);
	}
	return gd;
    }

    protected static final List<TaxonComment> makeComments(final List<Comment> comments) throws IllegalArgumentException{
	if(comments == null){
	    return new ArrayList<TaxonComment>(1);
	}

	List<TaxonComment> tcl = new ArrayList<TaxonComment>(comments.size());

	for(Comment comment: comments){
	    TaxonComment tc = new TaxonComment();
	    tc.setCommentator(comment.commentator);
	    tc.setCommentDetail(comment.commentDetail);
	    tcl.add(tc);
	}
	return tcl;
    }

    class GetScientificNameFromTSNThread extends Thread {
	private String tsn;
	private ItisRecord itisRecord;
	GetScientificNameFromTSNThread(final String tsn, ItisRecord itisRecord){
	    this.tsn = tsn; 
	    this.itisRecord = itisRecord;
	}
	
	public void run() {
	    ScientificName scientificName;
	    try{
		scientificName = getScientificNameFromTSN(tsn);
	    }
	    catch(FailedProxyRequestException e){
		throw new NullPointerException();
	    }
	    itisRecord.setCombinedName(scientificName.combinedName);
	    itisRecord.setNameAuthor(scientificName.author);
	}
    }



    // The json --"acceptedNames":[null]-- is converted into a list of length=1, with a null as first entry
    // This is to fix this: returns an list of length 0 by clearing the list
    protected static final FullRecord cleanFullRecord(final FullRecord rec){
	if(rec != null){
	    if(rec.acceptedNamesList != null){
		zeroNullContainingList(rec.acceptedNamesList.acceptedNames);
	    }
	    if(rec.commentList != null){
		zeroNullContainingList(rec.commentList.comments);
	    }
	    if(rec.commonNameList != null){
		zeroNullContainingList(rec.commonNameList.commonNames);
	    }
	    if(rec.expertList != null){
		zeroNullContainingList(rec.expertList.experts);
	    }
	    if(rec.geographicDivisionList != null){
		zeroNullContainingList(rec.geographicDivisionList.geoDivisions);
	    }
	    if(rec.jurisdictionalOriginList != null){
		zeroNullContainingList(rec.jurisdictionalOriginList.jurisdictionalOrigins);
	    }
	    if(rec.otherSourceList != null){
		zeroNullContainingList(rec.otherSourceList.otherSources);
	    }
	    if(rec.publicationList != null){
		zeroNullContainingList(rec.publicationList.publications);
	    }
	    if(rec.synonymList != null){
		zeroNullContainingList(rec.synonymList.synonyms);
	    }
	}
	return rec;
    }

    protected static final void zeroNullContainingList(final List<?> l){
	if(l != null && l.size() == 1 && l.get(0) == null){
	    l.clear();
	}
    }

    private static int min(final int a, final int b){
	if(a<b){
	    return a;
	}
	return b;
    }

    public void close(){

    }


    public final void info(){

    }


    static final void populateAboveRanks(final ItisRecord ir, final List<TaxRank> aboveRanks){
	if(aboveRanks == null){
	    return;
	}
	for(TaxRank tr: aboveRanks){
	    TaxonomicRank rank = new TaxonomicRank();
	    rank.setTsn(Integer.parseInt(tr.tsn));
	    rank.setRankName(tr.rankName);
	    rank.setRankValue(tr.rankValue);
	    rank.setCommonNames(tr.commonNames);
	    ir.addTaxonomicHierarchy(rank);
	    }
    }



    static final void populateBelowRanks(final ItisRecord ir, final List<TaxRank> belowRanks){
	if(belowRanks == null){
	    return;
	}
	for(TaxRank tr: belowRanks){
	    TaxonomicRank rank = new TaxonomicRank();
	    rank.setTsn(Integer.parseInt(tr.tsn));
	    rank.setRankName(tr.rankName);
	    rank.setRankValue(tr.rankValue);
	    rank.setCommonNames(tr.commonNames);
	    ir.addBelowSpeciesRank(rank);
	}
    }

}