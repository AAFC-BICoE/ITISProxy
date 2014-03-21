package ca.gc.agr.mbb.itisproxy;

import java.util.Properties;
import javax.ws.rs.ServiceUnavailableException;


public interface SearchService {
    public Object search(final String service, final Properties getParameters, final DataConverter dataConverter, final Object dataConverterObject)throws IllegalArgumentException, ServiceUnavailableException, TooManyResultsException;
    public static final String MAX_RESULTS_KBYTES_KEY = "MAX_RESULTS_KBYTES_KEY";
}
