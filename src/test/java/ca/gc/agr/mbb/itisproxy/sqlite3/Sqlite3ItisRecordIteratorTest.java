
package ca.gc.agr.mbb.itisproxy.sqlite3;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import ca.gc.agr.itis.itismodel.TaxonomicRank;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import ca.gc.agr.itis.itismodel.ItisRecord;
import ca.gc.agr.itis.itismodel.TaxonomicRank;
import ca.gc.agr.mbb.itisproxy.Util;

@RunWith(JUnit4.class)
public class Sqlite3ItisRecordIteratorTest{

    @Test
    public void iteratorShouldWork()
    {
	Properties p = new Properties();
	p.setProperty(Sqlite3ItisRecordIterator.SQLITE3_URL_KEY, "jdbc:sqlite:/home/newtong/work/itisproxyloader/data/itisSqlite081613/ITIS.sqlite");

	Sqlite3ItisRecordIterator  it = new Sqlite3ItisRecordIterator();

	try{
	    int n=0;
	    int max=500;
	    it.init(p);
	    while(it.hasNext() && n<max){
		System.out.println(n + "=======================================================================");
		ItisRecord rec = it.next();
		System.out.println(Util.toString(rec));
		++n;
	    }
	    it.close();
	}catch(Exception e){
	    e.printStackTrace();
	}
    }



}
