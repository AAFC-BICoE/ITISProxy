package ca.gc.agr.mbb.itisproxy.entities;


import com.fasterxml.jackson.annotation.JsonProperty; 
import java.util.ArrayList;

public class OtherSourceList extends BaseTsn{
    @JsonProperty("otherSources")
    public ArrayList<OtherSource>otherSources ;

    public String toString(){
	StringBuilder sb = new StringBuilder();
	if(otherSources != null){
	    for(OtherSource oso: otherSources){
		sb.append(oso + "; ");
	    }
	}
	return sb.toString();
    }
}
