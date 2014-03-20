package ca.gc.agr.mbb.itisproxy.wsclient;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ServiceUnavailableException;
import java.util.Properties;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import ca.gc.agr.mbb.itisproxy.Util;
import ca.gc.agr.mbb.itisproxy.SearchService;
import ca.gc.agr.mbb.itisproxy.DataConverter;
import ca.gc.agr.mbb.itisproxy.entities.DateData;
import ca.gc.agr.mbb.itisproxy.entities.FullRecord;

public class WS implements SearchService{
    private final static Logger LOGGER = Logger.getLogger(WS.class.getName()); 

    private final static int RESPONSE_CUTOFF = 300;
    
    static long kbytesPulled = 0l;

    public WS(){

    }

    // method search(String service, Properties p, dataConverter dc, returnClass rc
    public Object search(final String service, final Properties getParameters, final DataConverter dataConverter, final Object dataConverterObject) throws IllegalArgumentException, ServiceUnavailableException{
	if(service == null){
	    LOGGER.severe("Service cannot be null");
	    throw new IllegalArgumentException("Service cannot be null");
	}
	if(service.length() == 0){
	    LOGGER.severe("Service cannot zero length");
	    throw new IllegalArgumentException("Service cannot be zero length string");
	}
	
	if(dataConverter == null){
	    LOGGER.severe("dataConverter cannot be null");
	    throw new IllegalArgumentException("dataConverter cannot be null");
	}

	if(dataConverterObject == null){
	    LOGGER.severe("dataConverterObject cannot be null");
	    throw new IllegalArgumentException("dataConverterObject cannot be null");
	}

	// used for testing
	if(false)
	    return dataConverter.convert("{   \"acceptedNameList\":{      \"acceptedNames\":[         {            \"acceptedName\":\"Acer rubrum var. drummondii\",            \"acceptedTsn\":\"526853\",            \"author\":null,            \"class\":\"gov.usgs.itis.itis_service.data.SvcAcceptedName\"         }      ],      \"class\":\"gov.usgs.itis.itis_service.data.SvcAcceptedNameList\",      \"tsn\":\"183671\"   },   \"class\":\"gov.usgs.itis.itis_service.data.SvcFullRecord\",   \"commentList\":{      \"class\":\"gov.usgs.itis.itis_service.data.SvcTaxonCommentList\",      \"comments\":[         null      ],      \"tsn\":\"183671\"   },   \"commonNameList\":{      \"class\":\"gov.usgs.itis.itis_service.data.SvcCommonNameList\",      \"commonNames\":[         null      ],      \"tsn\":\"183671\"   },   \"completenessRating\":{      \"class\":\"gov.usgs.itis.itis_service.data.SvcGlobalSpeciesCompleteness\",      \"completeness\":\"\",      \"rankId\":220,      \"tsn\":\"183671\"   },   \"coreMetadata\":{      \"class\":\"gov.usgs.itis.itis_service.data.SvcCoreMetadata\",      \"credRating\":\"TWG standards met\",      \"rankId\":220,      \"taxonCoverage\":\"\",      \"taxonCurrency\":\"\",      \"taxonUsageRating\":\"not accepted\",      \"tsn\":\"183671\",      \"unacceptReason\":\"synonym\"   },   \"credibilityRating\":{      \"class\":\"gov.usgs.itis.itis_service.data.SvcCredibilityData\",      \"credRating\":\"TWG standards met\",      \"tsn\":\"183671\"   },   \"currencyRating\":{      \"class\":\"gov.usgs.itis.itis_service.data.SvcCurrencyData\",      \"rankId\":220,      \"taxonCurrency\":\"\",      \"tsn\":\"183671\"   },   \"dateData\":{      \"class\":\"gov.usgs.itis.itis_service.data.SvcTaxonDateData\",      \"initialTimeStamp\":\"1996-06-13 14:51:08.0\",      \"tsn\":\"183671\",      \"updateDate\":\"2011-12-08\"   },   \"expertList\":{      \"class\":\"gov.usgs.itis.itis_service.data.SvcTaxonExpertList\",      \"experts\":[         null      ],      \"tsn\":\"183671\"   },   \"geographicDivisionList\":{      \"class\":\"gov.usgs.itis.itis_service.data.SvcTaxonGeoDivisionList\",      \"geoDivisions\":[         null      ],      \"tsn\":\"183671\"   },   \"hierarchyUp\":{      \"author\":null,      \"class\":\"gov.usgs.itis.itis_service.data.SvcHierarchyRecord\",      \"parentName\":null,      \"parentTsn\":null,      \"rankName\":null,      \"taxonName\":null,      \"tsn\":\"183671\"   },   \"jurisdictionalOriginList\":{      \"class\":\"gov.usgs.itis.itis_service.data.SvcTaxonJurisdictionalOriginList\",      \"jurisdictionalOrigins\":[         null      ],      \"tsn\":\"183671\"   },   \"kingdom\":{      \"class\":\"gov.usgs.itis.itis_service.data.SvcKingdomInfo\",      \"kingdomId\":\"3\",      \"kingdomName\":\"Plantae   \",      \"tsn\":\"183671\"   },   \"otherSourceList\":{      \"class\":\"gov.usgs.itis.itis_service.data.SvcTaxonOtherSourceList\",      \"otherSources\":[         {            \"acquisitionDate\":\"2011-07-11\",            \"class\":\"gov.usgs.itis.itis_service.data.SvcTaxonOtherSource\",            \"referenceFor\":[               {                  \"class\":\"gov.usgs.itis.itis_service.data.SvcReferenceForElement\",                  \"name\":\"Acer drummondii\",                  \"refLanguage\":null,                  \"referredTsn\":\"183671\"               }            ],            \"source\":\"Sapindaceae of North America Update\",            \"sourceComment\":\"Updated for ITIS by the Flora of North America Expertise Network, in connection with an update for USDA PLANTS (2007-2010)\",            \"sourceType\":\"database\",            \"updateDate\":\"2011-12-08\",            \"version\":\"2011\"         },         {            \"acquisitionDate\":\"1996-07-29\",            \"class\":\"gov.usgs.itis.itis_service.data.SvcTaxonOtherSource\",            \"referenceFor\":[               {                  \"class\":\"gov.usgs.itis.itis_service.data.SvcReferenceForElement\",                  \"name\":\"Acer drummondii\",                  \"refLanguage\":null,                  \"referredTsn\":\"183671\"               }            ],            \"source\":\"NODC Taxonomic Code\",            \"sourceComment\":\"\",            \"sourceType\":\"database\",            \"updateDate\":\"2010-01-14\",            \"version\":\"8.0\"         },         {            \"acquisitionDate\":\"2000-01-21\",            \"class\":\"gov.usgs.itis.itis_service.data.SvcTaxonOtherSource\",            \"referenceFor\":[               {                  \"class\":\"gov.usgs.itis.itis_service.data.SvcReferenceForElement\",                  \"name\":\"Acer drummondii\",                  \"refLanguage\":null,                  \"referredTsn\":\"183671\"               }            ],            \"source\":\"The PLANTS Database\",            \"sourceComment\":\"National Plant Data Center, NRCS, USDA. Baton Rouge, LA 70874-4490 USA. http://plants.usda.gov\",            \"sourceType\":\"database\",            \"updateDate\":\"2004-02-23\",            \"version\":\"5.1.1\"         },         {            \"acquisitionDate\":\"1996-07-26\",            \"class\":\"gov.usgs.itis.itis_service.data.SvcTaxonOtherSource\",            \"referenceFor\":[               {                  \"class\":\"gov.usgs.itis.itis_service.data.SvcReferenceForElement\",                  \"name\":\"Acer drummondii\",                  \"refLanguage\":null,                  \"referredTsn\":\"183671\"               }            ],            \"source\":\"The PLANTS Database\",            \"sourceComment\":\"National Plant Data Center, NRCS, USDA. Baton Rouge, LA 70874-4490 USA. http://plants.usda.gov\",            \"sourceType\":\"database\",            \"updateDate\":\"2004-02-23\",            \"version\":\"4.0.4\"         }      ],      \"tsn\":\"183671\"   },   \"parentTSN\":{      \"class\":\"gov.usgs.itis.itis_service.data.SvcParentTsn\",      \"parentTsn\":\"0\",      \"tsn\":\"183671\"   },   \"publicationList\":{      \"class\":\"gov.usgs.itis.itis_service.data.SvcTaxonPublicationList\",      \"publications\":[         null      ],      \"tsn\":\"183671\"   },   \"scientificName\":{      \"author\":\"Hook. & Arn. ex Nutt.\",      \"class\":\"gov.usgs.itis.itis_service.data.SvcScientificName\",      \"combinedName\":\"Acer drummondii\",      \"kingdom\":null,      \"tsn\":\"183671\",      \"unitInd1\":null,      \"unitInd2\":null,      \"unitInd3\":null,      \"unitInd4\":null,      \"unitName1\":\"Acer                               \",      \"unitName2\":\"drummondii\",      \"unitName3\":null,      \"unitName4\":null   },   \"synonymList\":{      \"class\":\"gov.usgs.itis.itis_service.data.SvcSynonymNameList\",      \"synonyms\":[         null      ],      \"tsn\":\"183671\"   },   \"taxRank\":{      \"class\":\"gov.usgs.itis.itis_service.data.SvcTaxonRankInfo\",      \"kingdomId\":\"3\",      \"kingdomName\":\"Plantae   \",      \"rankId\":\"220\",      \"rankName\":\"Species        \",      \"tsn\":\"183671\"   },   \"taxonAuthor\":{      \"authorship\":\"Hook. & Arn. ex Nutt.\",      \"class\":\"gov.usgs.itis.itis_service.data.SvcTaxonAuthorship\",      \"tsn\":\"183671\",      \"updateDate\":\"2000-03-15\"   },   \"tsn\":\"183671\",   \"unacceptReason\":{      \"class\":\"gov.usgs.itis.itis_service.data.SvcUnacceptData\",      \"tsn\":\"183671\",      \"unacceptReason\":\"synonym\"   },   \"usage\":{      \"class\":\"gov.usgs.itis.itis_service.data.SvcTaxonUsageData\",      \"taxonUsageRating\":\"not accepted\",      \"tsn\":\"183671\"   }}", dataConverterObject);
	
	WebTarget thisTarget = WSState.getTarget(service);
	LOGGER.info("URL: " + WSState.URL_HOST + "/" + WSState.PATH_JSON + "/" + service);

	if(thisTarget == null){
	    LOGGER.severe("Unknown or unregistered service: " + service);
	    throw new IllegalArgumentException("Unknown or unregistered service: " + service);
	}

	float queryTimeLimit = WSState.getTargetTimeLimit(service);
	
	if (getParameters != null) {
	    Iterator<String> it = getParameters.stringPropertyNames().iterator();
	    while(it.hasNext()){
		String key = it.next();
		thisTarget = thisTarget.queryParam(key, getParameters.getProperty(key));
	    }
	}

	LOGGER.info("\t\t WebTarget URI: " + thisTarget.getUri());
	
	Invocation.Builder invocationBuilder =
	    thisTarget.request(MediaType.TEXT_PLAIN_TYPE);
	
	Response response = null;
	long requestStartTime = System.currentTimeMillis();
	try{
	    try {
		response = invocationBuilder.get(); 
	    } catch (javax.ws.rs.ProcessingException e) {
		LOGGER.severe("FAIL: invocationBuilder.get: [" + e.getMessage() + "]");
		return null;
	    }
	    finally{
		double requestTimeSeconds = ((double)(System.currentTimeMillis() - requestStartTime)/1000.0);

		if(requestTimeSeconds > queryTimeLimit){
		    LOGGER.warning("!!!!!!!! Web service response time: " + (int)requestTimeSeconds + "s     >limit for service: " + queryTimeLimit);
		    throw new ServiceUnavailableException("Web service response time over limit: " + (int)requestTimeSeconds + "s  over limit for service: " + queryTimeLimit);
		}else{
		    LOGGER.info("Web service response time: " + requestTimeSeconds + "s");
		}
		if(response != null){
		    LOGGER.info("Response status: " + response.getStatus());		    
		    LOGGER.info("Response status info: " + response.getStatusInfo().getFamily() 
				+ ": " 
				+  response.getStatusInfo().getReasonPhrase());
		}
	    }

	    LOGGER.info("Response mediaType: " + response.getMediaType());
	    LOGGER.info("Response date: " + response.getDate());
	    LOGGER.info("Response last-Modified: " + response.getLastModified());
	    LOGGER.info("Response length: " + response.getLength());

	    if (response.getStatus() != 200) {
		LOGGER.info("####### Bad HTTP Response status:  [" + response.getStatus() + "]");
		return null;
	    }
	    
	    LOGGER.info("Reading entity");
	    String content = response.readEntity(String.class);
	    if(content != null && content.length() < 4000){
		LOGGER.info("Response content.substring(0," + RESPONSE_CUTOFF 
			    + "): [" + content.substring(0,(content.length()>RESPONSE_CUTOFF? RESPONSE_CUTOFF :content.length()))
			    + (content.length()>RESPONSE_CUTOFF? " ... " : "")
			    + "]");
	    }

	    if(content == null || content.length() == 0){
		return null;
	    }

	    LOGGER.info("Response length (actual): " + content.length());
	    kbytesPulled += (1 + (content.length()/1000));
	    return dataConverter.convert(content, dataConverterObject);
	    //return content;
	    }
	finally{
	    if(response != null){
		response.close();
	    }
	    LOGGER.info("WS: total kbytes: " + kbytesPulled);
	}
    }


}//
