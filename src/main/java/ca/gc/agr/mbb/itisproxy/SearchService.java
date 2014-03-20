package ca.gc.agr.mbb.itisproxy;

import java.util.Properties;


public interface SearchService {
    public Object search(final String service, final Properties getParameters, final DataConverter dataConverter, final Object dataConverterObject);
}
