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
import ca.gc.agr.mbb.itisproxy.entities.Expert;
import ca.gc.agr.mbb.itisproxy.entities.FullRecord;
import ca.gc.agr.mbb.itisproxy.entities.HierarchyRecord;
import ca.gc.agr.mbb.itisproxy.entities.OtherSource;
import ca.gc.agr.mbb.itisproxy.entities.Publication;
import ca.gc.agr.mbb.itisproxy.wsclient.WSState;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class ValidateAllFieldsTest 
{
    private final static Logger LOGGER = Logger.getLogger(ca.gc.agr.mbb.itisproxy.TestInfo.TEST_LOG); 

    private final static String ACCEPTED_NAME_NOT_TSN = "526853";
    private final static String ACCEPTED_NAME_TSN = "183671";
    private final static String COMMENT_TSN = "180543";
    private final static String CURRENCY_RATING_TSN = "28727";
    private final static String EXPERTS_TSN = "180544";
    private final static String GEOGRAPHIC_INFORMATION_TSN = "180543";
    private final static String GLOBAL_SPECIES_COMPLETENESS_TSN = "180541";
    private final static String JURISDICTION_TSN= "180543";
    private final static String OTHER_SOURCES_TSN = "182662";
    private final static String PUBLICATIONS_TSN = "70340";
    private final static String RECORD_CREDIBILITY_RATING_TSN = "526852";
    private final static String TAXON_USAGE_RATING_TSN = "552334";
    private final static String UNACCEPTABILITY_REASON_TSN = "183671";


    ProxyImpl proxy = null;

    @Before
    public void init()
    {
	Properties props = new Properties();
	//props.setProperty(ProxyImpl.NO_CACHING_KEY, "true");
	ca.gc.agr.mbb.itisproxy.TestInfo.init();
	props.setProperty(CachingProxyImpl.CACHE_LOCATION_KEY, TestUtil.getTestDbDir());
	proxy = (ProxyImpl)ProxyImpl.instance(props);
    }

    @After
    public void done()
    {
	proxy.close();
	proxy = null;
    }



    @Test(timeout=20000)
    public void shouldHaveGeographicInformationField(){
	String search = GEOGRAPHIC_INFORMATION_TSN;

	//LOGGER.info("searchByScientificName_ShouldWorkWithGoodArguments name=" + search + "  startPage=" + startPage + " endPage=" + endPage);
	ItisRecord result = null;
	try{
	    result = proxy.getByTSN(search);
	} catch (Throwable e) {
	    e.printStackTrace();
	    Assert.fail();
	}
	Assert.assertNotNull(result);
	Assert.assertNotNull(result.getGeographicInfo());
	Assert.assertTrue(0 < result.getGeographicInfo().size());
    }

    @Test(timeout=20000)
    public void shouldHaveCurrencyRatingField(){
	String search = CURRENCY_RATING_TSN;

	//LOGGER.info("searchByScientificName_ShouldWorkWithGoodArguments name=" + search + "  startPage=" + startPage + " endPage=" + endPage);
	ItisRecord result = null;
	try{
	    result = proxy.getByTSN(search);
	} catch (Throwable e) {
	    e.printStackTrace();
	    Assert.fail();
	}
	Assert.assertNotNull(result);
	Assert.assertNotNull(result.getCurrencyRating());
    }

    @Test(timeout=20000)
    public void shouldHaveGlobalSpeciesCompletenessField(){
	String search = GLOBAL_SPECIES_COMPLETENESS_TSN;

	//LOGGER.info("searchByScientificName_ShouldWorkWithGoodArguments name=" + search + "  startPage=" + startPage + " endPage=" + endPage);
	ItisRecord result = null;
	try{
	    result = proxy.getByTSN(search);
	} catch (Throwable e) {
	    e.printStackTrace();
	    Assert.fail();
	}
	Assert.assertNotNull(result);
	Assert.assertNotNull(result.getGlobalSpecies());
    }

    @Test(timeout=20000)
    public void shouldHaveJurisdictionField(){
	String search = JURISDICTION_TSN;

	//LOGGER.info("searchByScientificName_ShouldWorkWithGoodArguments name=" + search + "  startPage=" + startPage + " endPage=" + endPage);
	ItisRecord result = null;
	try{
	    result = proxy.getByTSN(search);
	} catch (Throwable e) {
	    e.printStackTrace();
	    Assert.fail();
	}
	Assert.assertNotNull(result);
	Assert.assertNotNull(result.getTaxonJurisdictionalOrigins());

	Assert.assertTrue(0 < result.getTaxonJurisdictionalOrigins().size());
    }

    @Test(timeout=20000)
    public void shouldHaveRecordCredibilityRatingField(){
	String search = RECORD_CREDIBILITY_RATING_TSN;

	//LOGGER.info("searchByScientificName_ShouldWorkWithGoodArguments name=" + search + "  startPage=" + startPage + " endPage=" + endPage);
	ItisRecord result = null;
	try{
	    result = proxy.getByTSN(search);
	} catch (Throwable e) {
	    e.printStackTrace();
	    Assert.fail();
	}

	result.setRecordCredibilityRating(result.getRecordCredibilityRating());
	Assert.assertNotNull(result.getRecordCredibilityRating());
    }

    @Test(timeout=20000)
    public void shouldHaveAcceptedNameField(){
	String search = ACCEPTED_NAME_TSN;

	//LOGGER.info("searchByScientificName_ShouldWorkWithGoodArguments name=" + search + "  startPage=" + startPage + " endPage=" + endPage);
	FullRecord result = null;
	try{
	    result = proxy.getFullRecordByTSN(search);
	} catch (Throwable e) {
	    e.printStackTrace();
	    Assert.fail();
	}

	Assert.assertNotNull(result.acceptedNamesList);
	Assert.assertNotNull(result.acceptedNamesList.acceptedNames);
	Assert.assertTrue(0 < result.acceptedNamesList.acceptedNames.size());
    }


    @Test(timeout=20000)
    public void shouldNotHaveAcceptedNameFieldForLackingTsn(){
	String search = ACCEPTED_NAME_NOT_TSN;

	//LOGGER.info("searchByScientificName_ShouldWorkWithGoodArguments name=" + search + "  startPage=" + startPage + " endPage=" + endPage);
	FullRecord result = null;
	try{
	    result = proxy.getFullRecordByTSN(search);
	} catch (Throwable e) {
	    e.printStackTrace();
	    Assert.fail();
	}

	Assert.assertNotNull(result.acceptedNamesList);
	Assert.assertNotNull(result.acceptedNamesList.acceptedNames);
	Assert.assertTrue(1 == result.acceptedNamesList.acceptedNames.size());
	Assert.assertTrue(null == result.acceptedNamesList.acceptedNames.get(0));
    }



    @Test(timeout=20000)
    public void shouldHaveCommentField(){
	String search = COMMENT_TSN;

	//LOGGER.info("searchByScientificName_ShouldWorkWithGoodArguments name=" + search + "  startPage=" + startPage + " endPage=" + endPage);
	FullRecord result = null;
	try{
	    result = proxy.getFullRecordByTSN(search);
	} catch (Throwable e) {
	    e.printStackTrace();
	    Assert.fail();
	}

	Assert.assertNotNull(result.commentList);
	Assert.assertNotNull(result.commentList.comments);
	Assert.assertTrue(0 < result.commentList.comments.size());
    }


    @Test(timeout=20000)
    public void shouldHaveExpertsField(){
	String search = EXPERTS_TSN;

	//LOGGER.info("searchByScientificName_ShouldWorkWithGoodArguments name=" + search + "  startPage=" + startPage + " endPage=" + endPage);
	FullRecord result = null;
	try{
	    result = proxy.getFullRecordByTSN(search);
	} catch (Throwable e) {
	    e.printStackTrace();
	    Assert.fail();
	}

	Assert.assertNotNull(result.expertList);
	Assert.assertNotNull(result.expertList.experts);
	Assert.assertTrue(0 < result.expertList.experts.size());

	Expert ex = result.expertList.experts.get(0);
	Assert.assertNotNull(ex.comment);
	Assert.assertNotNull(ex.expert);
	Assert.assertNotNull(ex.referenceFor);
    }


    @Test(timeout=20000)
    public void shouldHaveOtherSourcesField(){
	String search = OTHER_SOURCES_TSN;

	//LOGGER.info("searchByScientificName_ShouldWorkWithGoodArguments name=" + search + "  startPage=" + startPage + " endPage=" + endPage);
	FullRecord result = null;
	try{
	    result = proxy.getFullRecordByTSN(search);
	} catch (Throwable e) {
	    e.printStackTrace();
	    Assert.fail();
	}

	Assert.assertNotNull(result.otherSourceList);
	Assert.assertNotNull(result.otherSourceList.otherSources);
	Assert.assertTrue(0 < result.otherSourceList.otherSources.size());

	OtherSource os = result.otherSourceList.otherSources.get(0);
	Assert.assertNotNull(os);
    }

    @Test(timeout=20000)
    public void shouldHavePublicationsField(){
	String search = PUBLICATIONS_TSN;

	//LOGGER.info("searchByScientificName_ShouldWorkWithGoodArguments name=" + search + "  startPage=" + startPage + " endPage=" + endPage);
	FullRecord result = null;
	try{
	    result = proxy.getFullRecordByTSN(search);
	} catch (Throwable e) {
	    e.printStackTrace();
	    Assert.fail();
	}

	Assert.assertNotNull(result.publicationList);
	Assert.assertNotNull(result.publicationList.publications);
	Assert.assertTrue(0 < result.publicationList.publications.size()); 

	Publication pub = result.publicationList.publications.get(0);
    }


    @Test(timeout=20000)
    public void shouldHaveUnacceptabilityReasonField(){
	String search = UNACCEPTABILITY_REASON_TSN;

	//LOGGER.info("searchByScientificName_ShouldWorkWithGoodArguments name=" + search + "  startPage=" + startPage + " endPage=" + endPage);
	FullRecord result = null;
	try{
	    result = proxy.getFullRecordByTSN(search);
	} catch (Throwable e) {
	    e.printStackTrace();
	    Assert.fail();
	}

	Assert.assertNotNull(result.unacceptReason);
	Assert.assertNotNull(result.unacceptReason.unacceptReason);
    }

    @Test(timeout=20000)
    public void shouldHaveTaxonUsageRatingField(){
	String search = TAXON_USAGE_RATING_TSN;
	FullRecord result = null;
	try{
	    result = proxy.getFullRecordByTSN(search);
	} catch (Throwable e) {
	    e.printStackTrace();
	    Assert.fail();
	}

	Assert.assertNotNull(result.usage);
	Assert.assertNotNull(result.usage.taxonUsageRating);
    }




}
