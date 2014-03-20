package ca.gc.agr.mbb.itisproxy;

import java.util.Map;
import java.util.Properties;
import ca.gc.agr.itis.itismodel.ItisRecord;
import ca.gnewton.tuapait.TCache;
import ca.gnewton.tuapait.TCacheManager;
import java.io.Serializable;

public class ItisCacheImpl implements ItisCache{
    private TCache cache = null;
    private Properties properties = null;

    public void init(final Properties p){
	cache = TCacheManager.instance(p);
	properties = p;
    }

    public boolean containsKey(String tsn){
	return cache.containsKey(tsn);
    }

    public ItisRecord get(String tsn){
	return (ItisRecord)cache.get(tsn);
    }
    public void put(String key, ItisRecord rec){
	cache.put(key, (Serializable)rec);
    }

    public void close(){
	cache.close();
    }

    public void clear(){
	cache.clear();
    }

    public long size(){
	return cache.size();
    }

    public String toString(){
	return cache.toString();
    }


}
