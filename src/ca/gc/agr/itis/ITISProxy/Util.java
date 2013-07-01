
package ca.gc.agr.itis.ITISProxy;

import ca.gc.agr.itis.ITISModel.*;
import java.util.List;
import java.util.Map;

public class Util{

    private Util(){

    }

    public static void printRecord(ItisRecord ir){
	System.out.println("Name=" + ir.getCombinedName() + "\t\t\t\t [ItisRecord.getCombinedName()]");
	//System.out.println("Rank=" + ir.getRank() + "   [ItisRecord.getRank().getRankName()]");
	System.out.println("\ttsn=" + ir.getTsn()  + "\t\t\t [ItisRecord.getTsn()]");
	Map<String, List<String>> vernacularNames = ir.getVernacularNames();
	if(vernacularNames != null && vernacularNames.size() > 0){
	    System.out.println("\tVernacularNames:");
	    for(String langKey: vernacularNames.keySet()){
		List<String> langNames = vernacularNames.get(langKey);
		for(String name: langNames){
		    System.out.println("\t\t" + langKey + ":" + name);
		}
	    }
	}
	System.out.println();
    }

}
