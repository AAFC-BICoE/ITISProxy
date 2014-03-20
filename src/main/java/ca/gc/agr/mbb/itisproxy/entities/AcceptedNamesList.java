package ca.gc.agr.mbb.itisproxy.entities;

import com.fasterxml.jackson.annotation.JsonProperty; 
import java.util.ArrayList;

public class AcceptedNamesList extends BaseTsn{

    @JsonProperty("acceptedNames")
    public ArrayList<AcceptedName> acceptedNames = null;


    public String toString(){
	StringBuilder sb = new StringBuilder();
	if(acceptedNames != null){
	    for(AcceptedName an: acceptedNames){
		sb.append(an + "; ");
	    }
	}
	return sb.toString();
    }
}
