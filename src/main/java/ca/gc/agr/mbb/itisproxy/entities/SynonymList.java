package ca.gc.agr.mbb.itisproxy.entities;

import com.fasterxml.jackson.annotation.JsonProperty; 
import java.util.ArrayList;

public class SynonymList extends BaseTsn{
    @JsonProperty("synonyms")
    public ArrayList<Synonym> synonyms;

    public String toString(){
	StringBuilder sb = new StringBuilder();
	if(synonyms != null){
	    for(Synonym syn: synonyms){
		sb.append(syn + "; ");
	    }
	}
	return sb.toString();
    }
}
