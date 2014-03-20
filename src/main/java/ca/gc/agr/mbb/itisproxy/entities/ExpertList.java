package ca.gc.agr.mbb.itisproxy.entities;



import com.fasterxml.jackson.annotation.JsonProperty; 
import java.util.ArrayList;

public class ExpertList extends BaseTsn{

    @JsonProperty("experts")
    public ArrayList<Expert> experts;

    public String toString(){
	StringBuilder sb = new StringBuilder();
	if(experts != null){
	    for(Expert exp: experts){
		sb.append(exp + "; ");
	    }
	}
	return sb.toString();
    }

}
