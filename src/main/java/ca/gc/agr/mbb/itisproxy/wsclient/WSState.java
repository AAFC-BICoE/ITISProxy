package ca.gc.agr.mbb.itisproxy.wsclient;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;


public class WSState {
    public static final String URL_HOST = "https://www.itis.gov";

    public static String USER_AGENT = "ITISProxy: AAFC-Agriculture-Canada-ITIS client: glen.newton@agr.gc.ca";

    public static final String PATH_XML = "ITISWebService/services/ITISService";
    public static String PATH_JSON = "ITISWebService/jsonservice";

    // seconds
    public static int TIME_LIMIT_SHORT = 20;
    public static int TIME_LIMIT_MEDIUM = 25;
    public static int TIME_LIMIT_LONG = 35;

    public static final String SERVICE_GET_ANY_MATCH_COUNT = "getAnyMatchCount";
    public static final String SERVICE_GET_DATE_DATA_FROM_TSN= "getDateDataFromTSN";
    public static final String SERVICE_GET_FULL_HIERARCHY_FROM_TSN = "getFullHierarchyFromTSN";
    public static final String SERVICE_GET_HIERARCHY_DOWN_FROM_TSN = "getHierarchyDownFromTSN";
    public static final String SERVICE_GET_FULL_RECORD_FROM_TSN = "getFullRecordFromTSN";
    public static final String SERVICE_GET_KINGDOMS = "getKingdomNames";
    public static final String SERVICE_GET_SCIENTIFIC_NAME_FROM_TSN = "getScientificNameFromTSN";
    public static final String SERVICE_SEARCH_BY_COMMON_NAME = "searchByCommonName";
    public static final String SERVICE_SEARCH_BY_COMMON_NAME_BEGINS_WITH = "searchByCommonNameBeginsWith";
    public static final String SERVICE_SEARCH_BY_COMMON_NAME_ENDS_WITH = "searchByCommonNameEndsWith";
    public static String SERVICE_SEARCH_BY_SCIENTIFIC_NAME = "searchByScientificName";
    public static final String SERVICE_SEARCH_FOR_ANY_MATCH_PAGED = "searchForAnyMatchPaged";
    // For junit tests
    public static final String SERVICE_NON_EXISTANT_SERVICE = "bad-service-for-junit-test";;


    public static final String PARAM_SRCH_KEY= "srchKey";
    public static final String PARAM_PAGE_SIZE= "pageSize";
    public static final String PARAM_PAGE_NUM= "pageNum";
    public static final String PARAM_ASCEND= "ascend";
    public static final String PARAM_TSN= "tsn";
    

    private static final Map<String,WebTarget> targets = new HashMap<String,WebTarget>(20);
    private static final Map<String,Integer> targetTimeLimits = new HashMap<String,Integer>(20);

    private static Client client = null;
    private static WebTarget target = null;

    static{
	init();
    }

    private WSState(){
    }

    public static final WebTarget getTarget(final String serviceKey){
	return targets.get(serviceKey);
    }

    public static final float getTargetTimeLimit(final String serviceKey){
	return targetTimeLimits.get(serviceKey).floatValue();
    }

    // Used only for testing
    public static final void reInit(){
	init();
    }

    private static final void init(){
	System.setProperty("http.agent", "[ " + USER_AGENT + " ]"); 

	client = ClientBuilder.newClient();

	client.register(UserAgentFilter.class);
	
	target = client.target(URL_HOST).path(PATH_JSON);

	registerService(SERVICE_GET_ANY_MATCH_COUNT);
	targetTimeLimits.put(SERVICE_GET_ANY_MATCH_COUNT, TIME_LIMIT_MEDIUM);

	registerService(SERVICE_GET_DATE_DATA_FROM_TSN);
	targetTimeLimits.put(SERVICE_GET_DATE_DATA_FROM_TSN, TIME_LIMIT_SHORT);

	registerService(SERVICE_GET_FULL_HIERARCHY_FROM_TSN);
	targetTimeLimits.put(SERVICE_GET_FULL_HIERARCHY_FROM_TSN, TIME_LIMIT_SHORT);

	registerService(SERVICE_GET_FULL_RECORD_FROM_TSN);
	targetTimeLimits.put(SERVICE_GET_FULL_RECORD_FROM_TSN, TIME_LIMIT_SHORT);

	registerService(SERVICE_GET_HIERARCHY_DOWN_FROM_TSN);
	targetTimeLimits.put(SERVICE_GET_HIERARCHY_DOWN_FROM_TSN, TIME_LIMIT_SHORT);

	registerService(SERVICE_GET_KINGDOMS);
	targetTimeLimits.put(SERVICE_GET_KINGDOMS, TIME_LIMIT_SHORT);

	registerService(SERVICE_GET_SCIENTIFIC_NAME_FROM_TSN);
	targetTimeLimits.put(SERVICE_GET_SCIENTIFIC_NAME_FROM_TSN, TIME_LIMIT_SHORT);

	registerService(SERVICE_SEARCH_BY_COMMON_NAME);
	targetTimeLimits.put(SERVICE_SEARCH_BY_COMMON_NAME, TIME_LIMIT_LONG);

	registerService(SERVICE_SEARCH_BY_COMMON_NAME_BEGINS_WITH);
	targetTimeLimits.put(SERVICE_SEARCH_BY_COMMON_NAME_BEGINS_WITH, TIME_LIMIT_LONG);

	registerService(SERVICE_SEARCH_BY_COMMON_NAME_ENDS_WITH);
	targetTimeLimits.put(SERVICE_SEARCH_BY_COMMON_NAME_ENDS_WITH, TIME_LIMIT_LONG);

	registerService(SERVICE_SEARCH_BY_SCIENTIFIC_NAME);
	targetTimeLimits.put(SERVICE_SEARCH_BY_SCIENTIFIC_NAME, TIME_LIMIT_LONG);

	registerService(SERVICE_SEARCH_FOR_ANY_MATCH_PAGED);
	targetTimeLimits.put(SERVICE_SEARCH_FOR_ANY_MATCH_PAGED, TIME_LIMIT_LONG);
    }



    private static final void registerService(final String service){
	targets.put(service, target.path(service));
    }


    protected static final Iterator<String> getTargetsKeys(){
	return targets.keySet().iterator();
    }

}
