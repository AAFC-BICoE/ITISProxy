package ca.gc.agr.mbb.itisproxy;

import java.util.Comparator;
import ca.gc.agr.mbb.itisproxy.entities.ScientificName;

class ScientificNameComparator implements Comparator<ScientificName>{
    public int compare(ScientificName o1, ScientificName o2){
	if(o1 == null || o2 == null){
	    return 0;
	}
	return o1.combinedName.toLowerCase().compareTo(o2.combinedName.toLowerCase());
    }
    
    public boolean equals(Object obj){
	return false;
    }
}
