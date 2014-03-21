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
import java.io.InputStream;

import ca.gc.agr.mbb.itisproxy.Util;
import ca.gc.agr.mbb.itisproxy.TooManyResultsException;
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
    public Object search(final String service, final Properties getParameters, final DataConverter dataConverter, final Object dataConverterObject) throws IllegalArgumentException, ServiceUnavailableException, TooManyResultsException{
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

	if(getParameters!= null && getParameters.containsKey(SearchService.MAX_RESULTS_KBYTES_KEY)){
	    LOGGER.info("MAX_RESULTS_KBYTES_KEY: " + 	getParameters.getProperty(SearchService.MAX_RESULTS_KBYTES_KEY));
	}

	if(dataConverterObject == null){
	    LOGGER.severe("dataConverterObject cannot be null");
	    throw new IllegalArgumentException("dataConverterObject cannot be null");
	}

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
		if(key != SearchService.MAX_RESULTS_KBYTES_KEY){
		    thisTarget = thisTarget.queryParam(key, getParameters.getProperty(key));
		}
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

	    long maxResultsKBytes = getMaxResultsKBytes(getParameters);

	    LOGGER.info("Response length kb: "  
			+ response.getLength() / 1024
			+ "   maxResultsKBytes=" + maxResultsKBytes);

	    checkResponseLength(response.getLength(), maxResultsKBytes);

	    if (response.getStatus() != 200) {
		LOGGER.info("####### Bad HTTP Response status:  [" + response.getStatus() + "]");
		return null;
	    }
	    
	    LOGGER.info("Reading entity");
	    String content = response.readEntity(String.class);

	    // We have to do this twice because some queries return -1 response.length
	    checkResponseLength(content.length(), maxResultsKBytes);
	    //InputStream input = (InputStream)response.getEntity();

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

    private long getMaxResultsKBytes(Properties p){
	if(p != null && p.containsKey(MAX_RESULTS_KBYTES_KEY)){
	    try{
		return Long.decode(p.getProperty(MAX_RESULTS_KBYTES_KEY));
	    }catch(NumberFormatException e){
		LOGGER.severe("Not a number: MAX_RESULTS_KBYTES_KEY value=" + p.getProperty(MAX_RESULTS_KBYTES_KEY));
		e.printStackTrace();
	    }
	}
	return Long.MAX_VALUE;
    }


    private void checkResponseLength(long responseLength, long maxKb) throws TooManyResultsException{
	if(responseLength / 1024 > maxKb){
	    String msg = "Response length too big: " 
		+ responseLength / 1024
		+ " > maxResultsKBytes=" + maxKb;
	    LOGGER.severe(msg);
	    throw new TooManyResultsException(msg);
	}
    }
}//
