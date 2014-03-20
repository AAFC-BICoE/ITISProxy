package ca.gc.agr.mbb.itisproxy.sqlite3;

import java.util.List;
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

@RunWith(JUnit4.class)
public class SqliteProxyImplTest{
    public static final String SQLITE3_URL = "jdbc:sqlite:/home/newtong/work/itisproxyloader/data/itisSqlite081613/ITIS.sqlite";
    @Test(timeout=20000)
    public void shouldGetKingdoms()
    {
	Properties p = new Properties();
	p.setProperty(SqliteProxyImpl.SQLITE3_URL_KEY, SQLITE3_URL);
	SqliteProxyImpl spi = new SqliteProxyImpl();
	try{
	    spi.init(p);
	    spi.getKingdoms();
	    spi.close();
	}catch(Exception e){
	    e.printStackTrace();
	}
    }


    @Test(timeout=20000)
    public void shouldSearchByCommonName()
    {
	System.out.println("ComnmonName");
	Properties p = new Properties();
	p.setProperty(SqliteProxyImpl.SQLITE3_URL_KEY, "jdbc:sqlite:/home/newtong/work/itisproxyloader/data/itisSqlite081613/ITIS.sqlite");
	SqliteProxyImpl spi = new SqliteProxyImpl();
	try{
	    spi.init(p);
	    spi.searchByCommonName("badger", 0,100);
	    spi.close();
	}catch(Exception e){
	    e.printStackTrace();
	}
    }

    /*
    @Test(timeout=50000)
    public void shouldSearchByAny()
    {
	System.out.println("Any");
	Properties p = new Properties();
	p.setProperty(SqliteProxyImpl.SQLITE3_URL_KEY, "jdbc:sqlite:/home/newtong/work/itisproxyloader/data/itisSqlite081613/ITIS.sqlite");
	SqliteProxyImpl spi = new SqliteProxyImpl();
	try{
	    spi.init(p);
	    spi.searchByAnyMatch("dolphin", 0,100, false);
	    spi.searchByAnyMatch("milnesium", 0,1000, false);
	    spi.close();
	}catch(Exception e){
	    e.printStackTrace();
	}
    }
    */


    @Test(timeout=20000)
    public void shouldSearchByCommonNameBeginsWith()
    {
	System.out.println("BeginsWith");
	Properties p = new Properties();
	p.setProperty(SqliteProxyImpl.SQLITE3_URL_KEY, "jdbc:sqlite:/home/newtong/work/itisproxyloader/data/itisSqlite081613/ITIS.sqlite");
	SqliteProxyImpl spi = new SqliteProxyImpl();
	try{
	    spi.init(p);
	    spi.searchByCommonNameBeginsWith("badger", 0,100);
	    spi.close();
	}catch(Exception e){
	    e.printStackTrace();
	}
    }

    @Test(timeout=20000)
    public void shouldSearchByCommonNameEndsWith()
    {
	System.out.println("EndsWith");
	Properties p = new Properties();
	p.setProperty(SqliteProxyImpl.SQLITE3_URL_KEY, "jdbc:sqlite:/home/newtong/work/itisproxyloader/data/itisSqlite081613/ITIS.sqlite");
	SqliteProxyImpl spi = new SqliteProxyImpl();
	try{
	    spi.init(p);
	    spi.searchByCommonNameEndsWith("badger", 0,100);
	    spi.close();
	}catch(Exception e){
	    e.printStackTrace();
	}
    }

    @Test(timeout=20000)
    public void shouldSearchByScientificName()
    {
	System.out.println("Scientific");
	Properties p = new Properties();
	p.setProperty(SqliteProxyImpl.SQLITE3_URL_KEY, "jdbc:sqlite:/home/newtong/work/itisproxyloader/data/itisSqlite081613/ITIS.sqlite");
	SqliteProxyImpl spi = new SqliteProxyImpl();
	try{
	    spi.init(p);
	    spi.searchByScientificName("tardigrad", 0,100);
	    spi.close();
	}catch(Exception e){
	    e.printStackTrace();
	}
    }
    String[] tsns = {"183833", "180564", "180539",
		     "621921", "621923", "621923", "333333"};
    @Test(timeout=20000)
    public void shouldGetRanksOneRankDownFromTsn()
    {

	System.out.println("GetRanksOneRankDownFromTsn");
	Properties p = new Properties();
	p.setProperty(SqliteProxyImpl.SQLITE3_URL_KEY, "jdbc:sqlite:/home/newtong/work/itisproxyloader/data/itisSqlite081613/ITIS.sqlite");
	SqliteProxyImpl spi = new SqliteProxyImpl();
	try{
	    spi.init(p);
	    for(String tsn: tsns){
		spi.getRanksOneRankDownFromTsn(tsn);
	    }
	    spi.close();
	}catch(Exception e){
	    e.printStackTrace();
	}
    }


    @Test(timeout=20000)
    public void shouldGetRanksUpFromTsn()
    {
	System.out.println("GetRanksUpFromTsn");
	Properties p = new Properties();
	p.setProperty(SqliteProxyImpl.SQLITE3_URL_KEY, "jdbc:sqlite:/home/newtong/work/itisproxyloader/data/itisSqlite081613/ITIS.sqlite");
	SqliteProxyImpl spi = new SqliteProxyImpl();
	try{
	    for(String tsn: tsns){
		spi.init(p);
		List<TaxonomicRank> rankList = spi.getRanksUpFromTsn(tsn);
		spi.close();
		for(TaxonomicRank rank: rankList){
		    System.out.println("\t\t" + rank.getTsn() + " " + rank.getRankName() 
				       + " " + rank.getRankValue());
		}
	    }

	}catch(Exception e){
	    e.printStackTrace();
	}
    }



    //@Test
    public void shouldGetAllRecords()
    {
	if(true){
	    return;
	}
	System.out.println("All Records");
	Properties p = new Properties();
	p.setProperty(SqliteProxyImpl.SQLITE3_URL_KEY, "jdbc:sqlite:/home/newtong/work/itisproxyloader/data/itisSqlite081613/ITIS.sqlite");
	SqliteProxyImpl spi = new SqliteProxyImpl();
	try{
	    spi.init(p);
	    int count = 0;
	    int limitSize = 100;
	    while(true){
		List<ItisRecord> recs = spi.getAllRecords(count, count + limitSize);
		count += limitSize;
		if(recs == null || recs.size() == 0){
		    break;
		}
		System.err.println(" --");
	    }
	    spi.close();
	}catch(Exception e){
	    e.printStackTrace();
	}
    }
}
