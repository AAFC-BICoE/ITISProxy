
package ca.gc.agr.mbb.itisproxy;

public interface ProxyInfo{
    static final int NO_PAGING = -1;
    static final String PAGING_SIZE = "pageSize";
    static final String PAGING_START_NUM = "pageNum";
    static final String PAGING_SORT_ASCEND = "ascend";


    public static final String WARM_CACHE_QUERIES_KEY = "warmCacheQueries";
    public static final String NO_CACHING_KEY = "noCaching";
    public static final String PROXY_IMPL_KEY = "proxyImpl";
    public static final String PROXY_FILE_SIZE_KEY=ca.gnewton.tuapait.TCache.BDB_LOG_FILE_SIZE_MB_KEY;
}

