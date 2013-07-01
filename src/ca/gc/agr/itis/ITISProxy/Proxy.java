
package ca.gc.agr.itis.ITISProxy;

import ca.gc.agr.itis.ITISModel.*;
import java.util.List;


public class Proxy{
    private Proxy(){

    }

    public static List<ItisRecord> getKingdoms() throws Exception{
	return ProxyMock.getKingdoms();
    }

    
    public static List<ItisRecord> searchByCommonOrScientific(String s, int start, int end) throws Exception{
	checkRange(start, end, "");
	checkString(s,"");
	return ProxyMock.searchByCommonOrScientific(s, start, end);
    }


    public static List<ItisRecord> searchByScientificName(String s, int start, int end) throws Exception {
	checkRange(start, end, "");
	checkString(s,"");
	return ProxyMock.searchByScientificName(s, start, end);
    }

    public static List<ItisRecord> searchByEpithet(String s, int start, int end) throws Exception {
	checkRange(start, end, "");
	checkString(s, "");
	return ProxyMock.searchByEpithet(s, start, end);
    }

    public static List<ItisRecord> searchByAuthority(String s, int start, int end) throws Exception{
	checkRange(start, end, "");
	checkString(s, "");
	return ProxyMock.searchByAuthority(s, start, end);
    }

    public static List<ItisRecord> searchByRecommended(String s, int start, int end) throws Exception{
	checkRange(start, end, "");
	checkString(s, "");
	return ProxyMock.searchByRecommended(s, start, end);
    }

    public static ItisRecord getByTSN(String tsn) throws Exception{
	checkString(tsn, "");
	return ProxyMock.getByTSN(tsn);
    }

 
    // includes THIS tsn
    public static List<ItisRecord> getRanksUpFromTsn(String tsn)throws Exception{
	checkString(tsn, "");
	return ProxyMock.getRanksUpFromTsn(tsn);
    }

    public static List<ItisRecord> getRanksOneRankDownFromTsn(String tsn)throws Exception{
	checkString(tsn, "");
	return ProxyMock.getRanksOneRankDownFromTsn(tsn);
    }

	
    public static List<ItisRecord> getAllCountDownFromTsn(String tsn)throws Exception{
	checkString(tsn, "");
	return ProxyMock.getAllCountDownFromTsn(tsn);
    }

    public static List<ItisRecord> getModifiedCountDownFromTsn(String tsn)throws Exception{
	checkString(tsn, "");
	return ProxyMock.getModifiedCountDownFromTsn(tsn);
    }

    public static List<ItisRecord> getAcceptedCountDownFromTsn(String tsn)throws Exception{
	checkString(tsn, "");
	return ProxyMock.getAcceptedCountDownFromTsn(tsn);
    } 

    
    static void checkString(String s, String mes) throws Exception{
	if(s==null){
	    throw new Exception("String cannot be null " + mes);
	}

	if(s.length() <1){
	    throw new Exception("String cannot be empty " + mes);
	}
	
    }

    static void checkRange(int start, int end, String mes) throws Exception{
	if(mes == null){
	    mes = "";
	}

	if(start < 0){
	    throw new Exception("Start cannot be < 0. " + mes);
	}

	if(end <= 0){
	    throw new Exception("End cannot be <= Start. " + mes);
	}
    }

}
