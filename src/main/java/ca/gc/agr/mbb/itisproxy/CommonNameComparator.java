package ca.gc.agr.mbb.itisproxy;

import java.util.Comparator;
import ca.gc.agr.mbb.itisproxy.entities.CommonName;



class CommonNameComparator implements Comparator<CommonName>{
    final static String LANG_EN = "English";
    final static String LANG_FR = "French";

    private String language = "donotcare";

    public CommonNameComparator(){
	
    }

    public CommonNameComparator(final String language){
	this.language = language;
    }
    
    public int compare(CommonName o1, CommonName o2){
	if(o1 == null || o2 == null){
	    return 0;
	}

	if(o1.language == language && o2.language == language 
	   || o1.language != language && o2.language != language){
	    return o1.commonName.toLowerCase().compareTo(o2.commonName.toLowerCase());
	}else{
	    if(o1.language == language && o2.language != language){
		return 1;
	    }else{
		if(o1.language != language && o2.language == language){
		    return -1;
		}
	    }
	}
	return 0;
    }
    public boolean equals(Object obj){
		return false;
    }

} //
