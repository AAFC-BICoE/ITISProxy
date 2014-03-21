package ca.gc.agr.mbb.itisproxy;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.client.WebTarget;

import ca.gc.agr.itis.itismodel.ItisRecord;
import ca.gc.agr.itis.itismodel.TaxonomicRank;
import ca.gc.agr.mbb.itisproxy.Util;
import ca.gc.agr.mbb.itisproxy.entities.HierarchyRecord;
import ca.gc.agr.mbb.itisproxy.wsclient.WSState;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class ProxyImplTest 
{
    private final static Logger LOGGER = Logger.getLogger(ca.gc.agr.mbb.itisproxy.TestInfo.TEST_LOG); 

    public final String tsnMastigocoleusTestarum1378 = "1378";
    public final String tsnBullfrog_MultipleCommonNames_173441 = "Rana catesbeiana";
    public final String tsnMonera202420 = "202420";
    public static final String searchTermGoodTardigrada = "Tardigrada";
    public final String searchTermBadTardigradaZzzz = searchTermGoodTardigrada + "zzzzzzzzzzzzz";

    protected static final String[] VALID_COMMON_NAMES = {
	"ferret-badger", "ferret", "badger", "american bullfrog", "grizzly bear",
	"Falcated Duck", "Fendler's penstemon", "frosted buckwheat", "fringed loosestrife", "fringed thistle",
	"flax-leaf fleabane", "fangtooth smoothhead", "Fresno kangaroo rat", "filose mangelia", "fluvial shiner",
	"false babystars", "fringed linanthus", "false goldenweed"
    };

    protected static final String[] INVALID_COMMON_NAMES = {
	"jjj mmm", 
	"-1-1-1-1 mm", 
	"00000000000 bbb",
	"this should not match anything"
    };

    ProxyImpl proxy = null;

    @Before
    public void init()
    {
	Properties props = new Properties();
	ca.gc.agr.mbb.itisproxy.TestInfo.init();
	props.setProperty(ProxyImpl.NO_CACHING_KEY, "true");
	props.setProperty(CachingProxyImpl.CACHE_LOCATION_KEY, TestUtil.getTestDbDir());
	props.setProperty(ca.gc.agr.mbb.itisproxy.sqlite3.SqliteProxyImpl.SQLITE3_URL_KEY, ca.gc.agr.mbb.itisproxy.sqlite3.SqliteProxyImplTest.SQLITE3_URL);
	props.setProperty(ProxyInfo.PROXY_IMPL_KEY, ProxyImpl.class.getName());
	proxy = (ProxyImpl)ProxyImpl.instance(props);
    }

    @After
    public void done()
    {
	proxy.close();
	proxy = null;
    }



    @Test
    public void searchByScientificName_ShouldWorkWithGoodArguments()
    {
	String search = searchTermGoodTardigrada;
	int startPage = 0;
	int endPage = 10;

	LOGGER.info("searchByScientificName_ShouldWorkWithGoodArguments name=" + search + "  startPage=" + startPage + " endPage=" + endPage);
	SearchResults results = null;
	try{
	    results = proxy.searchByScientificName(search, startPage, endPage);
	} catch (Throwable e) {
	    e.printStackTrace();
	    Assert.fail();
	}
	Assert.assertNotNull(results);
    }

    @Test
    public void searchByScientificName_ShouldHaveCommonNamesForBullfrog()
    {
	LOGGER.info("searchByScientificName_ShouldHaveCommonNamesForBullfrog");
	String search = tsnBullfrog_MultipleCommonNames_173441;
	int startPage = 0;
	int endPage = 10;
	LOGGER.info("searchByScientificName name=" + search + "  startPage=" + startPage + " endPage=" + endPage);
	SearchResults results = null;
	try{
	    results = proxy.searchByScientificName(search, startPage, endPage);
	} catch (Throwable e) {
	    e.printStackTrace();
	    Assert.fail();
	}
	Assert.assertNotNull(results);

	for(ItisRecord rec: results.records){
	    Map<String, List<String>> commonNames = rec.getVernacularNames();
	    Assert.assertNotNull(commonNames);	    
	    Assert.assertTrue(commonNames.size() > 0);	    
	}
    }

    @Test
    public void searchByScientificName_TardigradaShouldGiveNineResults()
    {
	LOGGER.info("searchByScientificName_TardigradaShouldGiveNineResults");
	String search = searchTermGoodTardigrada;
	int startPage = 0;
	int endPage = 10;
	LOGGER.info("searchByScientificName name=" + search + "  startPage=" + startPage + " endPage=" + endPage);
	SearchResults results = null;
	try{
	    results = proxy.searchByScientificName(search, startPage, endPage);
	} catch (Throwable e) {
	    e.printStackTrace();

	}
	if(results == null || results.records == null){
	    Assert.fail();
	}

	LOGGER.info("-------------TardigradaShouldGiveZeroResultsPaged: " + results.records.size());
	Assert.assertTrue(results.records.size() == 9);
    }

    @Test
    public void searchByScientificName_TardigradaShouldGiveFiveResultsIfPagingFive()
    {
	LOGGER.info("searchByScientificName_TardigradaShouldGiveFiveResultsIfPagingFive");
	String search = searchTermGoodTardigrada;
	int startPage = 0;
	int endPage = 4;
	LOGGER.info("searchByScientificName name=" + search + "  startPage=" + startPage + " endPage=" + endPage);
	SearchResults results = null;
	try{
	    results = proxy.searchByScientificName(search, startPage, endPage);
	} catch (Throwable e) {
	    e.printStackTrace();
	    Assert.fail();
	}
	if(results == null || results.records == null){
	    Assert.fail();
	}
	LOGGER.info("-------------: TardigradaShouldGiveFiveResultsIfPagingFive" + results.records.size());
	Assert.assertEquals(5, results.records.size());
    }

    @Test
    public void searchByScientificName_TardigradaShouldGiveZeroResultsPaged()
    {
	LOGGER.info("searchByScientificName_TardigradaShouldGiveZeroResultsPaged");
	String search = searchTermGoodTardigrada;
	int startPage = 90;
	int endPage = 100;
	LOGGER.info("searchByScientificName name=" + search + "  startPage=" + startPage + " endPage=" + endPage);
	SearchResults results = null;
	try{
	    results = proxy.searchByScientificName(search, startPage, endPage);
	    Assert.assertNotNull(results);
	    Assert.assertTrue(results.records.size() == 0);
	} catch (Throwable e) {
	    e.printStackTrace();
	    Assert.assertTrue(false);
	}
    }

    @Test(expected=FailedProxyRequestException.class)
    public void searchByScientificName_ShouldFailWithBadURLPath() throws FailedProxyRequestException, TooManyResultsException
    {
	LOGGER.info("searchByScientificName_ShouldFailWithBadURLPath");
	String goodPath = WSState.PATH_JSON;
	WSState.PATH_JSON = "bad-path-for-junit-test";
	WSState.reInit();
	String search = searchTermGoodTardigrada;
	int startPage = 0;
	int endPage = 100;
	LOGGER.info("searchByScientificName name=" + search + "  startPage=" + startPage + " endPage=" + endPage);

	try{
	    SearchResults results = proxy.searchByScientificName(search, startPage, endPage);
	    Assert.assertTrue(results != null);
	}
	finally{
	    WSState.PATH_JSON = goodPath;
	    WSState.reInit();
	}
    }

    @Test(expected=FailedProxyRequestException.class)
    public void searchByScientificName_ShouldFailWithBadServicePath() throws FailedProxyRequestException, TooManyResultsException
    {
	LOGGER.info("searchByScientificName_ShouldFailWithBadServicePath");
	String goodService = WSState.SERVICE_SEARCH_BY_SCIENTIFIC_NAME;
	WSState.SERVICE_SEARCH_BY_SCIENTIFIC_NAME = WSState.SERVICE_NON_EXISTANT_SERVICE;
	WSState.reInit();
	String search = searchTermGoodTardigrada;
	int startPage = 0;
	int endPage = 10;
	LOGGER.info("searchByScientificName name=" + search + "  startPage=" + startPage + " endPage=" + endPage);

	try{
	    SearchResults results = proxy.searchByScientificName(search, startPage, endPage);
	}
	finally{
	    WSState.SERVICE_SEARCH_BY_SCIENTIFIC_NAME = goodService;
	    WSState.reInit();
	}
    }

    @Test(timeout=20000)
    public void searchByScientificName_ShouldReturnZeroHitsWithUnknownName()
    {
	LOGGER.info("searchByScientificName_ShouldReturnZeroHitsWithUnknownName");
	String search = searchTermBadTardigradaZzzz;
	int startPage = 0;
	int endPage = 10;
	LOGGER.info("searchByScientificName name=" + search + "  startPage=" + startPage + " endPage=" + endPage);
	try{
	    SearchResults results = proxy.searchByScientificName(search, startPage, endPage);
	    Assert.assertTrue(results != null);
	    Assert.assertTrue(results.records.size() == 0);
	} catch (Throwable e) {
	    e.printStackTrace();
	    Assert.assertTrue(false);
	}
    }

    @Test(expected=IllegalArgumentException.class)
    public void searchByScientificName_StartPageMustBeZeroOrGreater()
    {
	String search = searchTermGoodTardigrada;
	int startPage = -1;
	int endPage = 10;
	
	LOGGER.info("searchByScientificName name=" + search + "  startPage=" + startPage + " endPage=" + endPage);
	try{
	    SearchResults results = proxy.searchByScientificName(search, startPage, endPage);
	}catch(IllegalArgumentException e){
	    throw e;
	}catch(Exception e){
	    Assert.assertTrue(false);
	}
    }

    @Test(expected=IllegalArgumentException.class)
    public void searchByScientificName_StartPageMustBeLessThanEndPage()
    {
	String search = searchTermGoodTardigrada;
	int startPage = 4;
	int endPage = 1;
	
	LOGGER.info("searchByScientificName name=" + search + "  startPage=" + startPage + " endPage=" + endPage);
	try{
	    SearchResults results = proxy.searchByScientificName(search, startPage, endPage);
	}catch(IllegalArgumentException e){
	    throw e;
	}catch(Exception e){
	    e.printStackTrace();
	    Assert.assertTrue(false);
	}	

    }

    @Test(expected=IllegalArgumentException.class)
    public void searchByScientificName_SearchStringMustNotBeNull()
    {
	String search = null;
	int startPage = 0;
	int endPage = 10;
	
	LOGGER.info("searchByScientificName name=" + search + "  startPage=" + startPage + " endPage=" + endPage);
	try{
	    SearchResults results = proxy.searchByScientificName(search, startPage, endPage);
	}catch(IllegalArgumentException e){
	    throw e;
	}catch(Exception e){
	    Assert.assertTrue(false);
	}
    }

    @Test(expected=IllegalArgumentException.class)
    public void searchByScientificName_SearchStringMustHaveNonZeroLength()
    {
	String search = "";
	int startPage = 0;
	int endPage = 10;
	
	LOGGER.info("searchByScientificName name=" + search + "  startPage=" + startPage + " endPage=" + endPage);
	try{
	    SearchResults results = proxy.searchByScientificName(search, startPage, endPage);
	}catch(IllegalArgumentException e){
	    throw e;
	}catch(Exception e){
	    Assert.assertTrue(false);
	}
    }


    @Test(timeout=20000)
    public void getKingdoms()
    {
	LOGGER.info("getKingdoms");
	try{
	    List<ItisRecord> results = proxy.getKingdoms();
	    Assert.assertTrue(results != null);
	    for(ItisRecord ir: results){
		LOGGER.info(" Kingdom=" + ir.getCombinedName() + "  tsn=" + ir.getTsn());
	    }
	} catch (Throwable e) {
	    e.printStackTrace();
	    Assert.assertTrue(false);
	}
    }


    @Test(timeout=20000)
    public void getRanksUpFromTsn_ShouldBeOKWithGoodTSN_Taxon()
    {
	String tsn=tsnMastigocoleusTestarum1378;
	getRanksUpFromTsn(tsn);
    }

    @Test(timeout=20000)
    public void getRanksUpFromTsn_ShouldBeOKWithGoodTSN_KingdomMonera()
    {
	String tsn=tsnMonera202420;
	getRanksUpFromTsn(tsn);
    }
    
    @Ignore
    public void getRanksUpFromTsn(String tsn){
	LOGGER.info("getRanksUpFromTsn  tsn=" + tsn);
	try{
	    List<TaxonomicRank> results = proxy.getRanksUpFromTsn(tsn);

	    Assert.assertTrue(results != null);
	    for(TaxonomicRank tr: results){
		LOGGER.info(" Rank: " + tr.getRankName() + ":" + tr.getRankValue() + "    tsn=" + tr.getTsn());
	    }
	} catch (Throwable e) {
	    e.printStackTrace();
	    Assert.assertTrue(false);
	}
    }

    @Test(timeout=20000)
    public void getRanksOneDownFromTsn_ShouldBeOKWithGoodTSN_Taxon()
    {
	String tsn=tsnMastigocoleusTestarum1378;
	try{
	    List<TaxonomicRank> results = proxy.getRanksOneRankDownFromTsn(tsn);
	    Assert.assertTrue(results != null);
	} catch (Throwable e) {
	    e.printStackTrace();
	    Assert.assertTrue(false);
	}
    }

    @Test(timeout=20000)
    public void getRanksOneDownFromTsn_ShouldBeOKWithGoodTSN_KingdomMonera()
    {
	String tsn=tsnMonera202420;
	try{
	    List<TaxonomicRank> results = proxy.getRanksOneRankDownFromTsn(tsn);
	    Assert.assertTrue(results != null);
	} catch (Throwable e) {
	    e.printStackTrace();
	    Assert.assertTrue(false);
	}

    }
    
    @Test(timeout=20000)
    public void getRanksOneDownFromTsn_ShouldBeSizeZeroWithTSN_1378()
    {
	String tsn=tsnMastigocoleusTestarum1378;
	try{
	    List<TaxonomicRank> results = proxy.getRanksOneRankDownFromTsn(tsn);
	    Assert.assertTrue(results != null);
	    Assert.assertTrue(results.size() == 0);
	} catch (Throwable e) {
	    e.printStackTrace();
	    Assert.assertTrue(false);
	}
    }

    @Test(timeout=20000)
    public void getRanksOneDownFromTsn_ShouldBeSizeTwoWithTSN_202420()
    {
	String tsn=tsnMonera202420;

	try{
	    List<TaxonomicRank> results = proxy.getRanksOneRankDownFromTsn(tsn);
	    LOGGER.info("*** #################### size=" + results.size());
	    Assert.assertTrue(results != null);
	    Assert.assertTrue(results.size() == 2);
	} catch (Throwable e) {
	    e.printStackTrace();
	    Assert.assertTrue(false);
	}
    }

    @Test(expected=IllegalArgumentException.class)
    public void shouldFailWithNullArgument(){
	proxy.populateTaxonomicRank(null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void populateTaxonomicRank_ShouldFailWithNullArgument(){
	proxy.populateTaxonomicRank(null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void populateTaxonomicRank_ShouldFailWithEmptyObjectArgument(){
	HierarchyRecord hierarchyRecord = new HierarchyRecord();
	proxy.populateTaxonomicRank(hierarchyRecord);
    }


    @Test
    public void initShouldStartOK(){
	proxy.init(null);
	Assert.assertTrue(true);
    }

    @Test(expected=IllegalArgumentException.class)
    public void getRankShouldFailWithNullTSN() throws FailedProxyRequestException{
	List<TaxonomicRank> tmp = proxy.getRank(null, WSState.SERVICE_GET_FULL_HIERARCHY_FROM_TSN, false, false, false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void getRankShouldFailWithNullService() throws FailedProxyRequestException{
	List<TaxonomicRank> tmp = proxy.getRank("3432", null, false, false, false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void getRankShouldFailWithZeroLengthTSN() throws FailedProxyRequestException{
	List<TaxonomicRank> tmp = proxy.getRank("", WSState.SERVICE_GET_FULL_HIERARCHY_FROM_TSN, false, false, false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void getRankShouldFailWithZeroLengthService() throws FailedProxyRequestException{
	List<TaxonomicRank> tmp = proxy.getRank("3432", "", false, false, false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void getRankShouldFailWithBothTrue() throws FailedProxyRequestException{
	List<TaxonomicRank> tmp = proxy.getRank("3432", WSState.SERVICE_GET_FULL_HIERARCHY_FROM_TSN, true, true, false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void getRankShouldFailWithUnsupportedService() throws FailedProxyRequestException{
	List<TaxonomicRank> tmp = proxy.getRank("3432", WSState.SERVICE_GET_ANY_MATCH_COUNT, true, true, false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void getRankShouldFailWithUnknownService() throws FailedProxyRequestException{
	List<TaxonomicRank> tmp = proxy.getRank("3432", "foobarService", true, true, false);
    }


    @Test(expected=IllegalArgumentException.class)
    public void getByTSN_ShouldFailWithNull() throws FailedProxyRequestException{
	proxy.getByTSN(null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void getByTSN_ShouldFailWithEmptyString() throws FailedProxyRequestException{
	proxy.getByTSN("");
    }

    @Test(expected=IllegalArgumentException.class)
    public void getByTSN_ShouldFailWithNonPositiveIntegerString() throws FailedProxyRequestException{
	proxy.getByTSN("-300");
    }

    @Test(expected=IllegalArgumentException.class)
    public void getByTSN_ShouldFailWithNonIntegerString() throws FailedProxyRequestException{
	proxy.getByTSN("foobar");
    }




}
