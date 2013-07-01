
package ca.gc.agr.itis.ITISProxy;

import ca.gc.agr.itis.ITISModel.*;
import java.util.List;

public class ProxyMock{

    private ProxyMock(){

    }

    static List<ItisRecord> getKingdoms(){
	return MockUtil.getKingdoms();

    }

    
    static List<ItisRecord> searchByCommonOrScientific(String s, int start, int end){
	return null;
    }


    static List<ItisRecord> searchByScientificName(String s, int start, int end){
	return null;
    }

    static List<ItisRecord> searchByEpithet(String s, int start, int end){
	return null;
    }

    static List<ItisRecord> searchByAuthority(String s, int start, int end){
	return null;
    }

    static List<ItisRecord> searchByRecommended(String s, int start, int end){
	return null;
    }

    static ItisRecord getByTSN(String tsn){
	return MockUtil.makeRecordFromTsn(tsn);
    }
	
 
    // includes THIS tsn
    static List<ItisRecord> getRanksUpFromTsn(String tsn){
	return null;
    }

    static List<ItisRecord> getRanksOneRankDownFromTsn(String tsn){
	return null;
    }

	
    static List<ItisRecord> getAllCountDownFromTsn(String tsn){
	return null;
    }

    static List<ItisRecord> getModifiedCountDownFromTsn(String tsn){
	return null;
    }

    static List<ItisRecord> getAcceptedCountDownFromTsn(String tsn){
	return null;
    }




}
