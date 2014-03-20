package ca.gc.agr.mbb.itisproxy.sqlite3;

import ca.gc.agr.itis.itismodel.ItisRecord;
import java.io.Closeable;
import java.util.Iterator;
import java.util.Properties;
import java.io.IOException;

public interface ItisRecordIterator<ItisRecord> extends Iterator<ItisRecord>, Closeable {
    public void init(Properties p) throws IOException;

}
