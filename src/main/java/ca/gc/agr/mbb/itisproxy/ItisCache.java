package ca.gc.agr.mbb.itisproxy;

import java.util.Map;
import java.util.Properties;
import ca.gc.agr.itis.itismodel.ItisRecord;

public interface ItisCache{

    public final static String LOCATION = "location";
    public final static String OVERWRITE = "overwrite";
    public final static String READ_ONLY = "readonly";

    public void init(Properties p);

    public boolean containsKey(String tsn);

    public ItisRecord get(String tsn);
    public void put(String key, ItisRecord rec);

    public void close();
    public void clear();

    public long size();
}
