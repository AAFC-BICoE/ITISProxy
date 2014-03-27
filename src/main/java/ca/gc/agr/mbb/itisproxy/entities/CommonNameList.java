package ca.gc.agr.mbb.itisproxy.entities;

import com.fasterxml.jackson.annotation.JsonProperty; 

import java.util.ArrayList;

public class CommonNameList extends BaseTsn{

    @JsonProperty("commonNames")
    public ArrayList<CommonName> commonNames;

    public String toString(){
	StringBuilder sb = new StringBuilder();
	if(commonNames != null){
	    for(CommonName comm: commonNames){
		sb.append(comm + "; ");
	    }
	}
	return sb.toString();
    }
}
