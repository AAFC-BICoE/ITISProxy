package ca.gc.agr.mbb.itisproxy.entities;



import com.fasterxml.jackson.annotation.JsonProperty; 
import java.util.ArrayList;

public class PublicationList extends BaseTsn{
    @JsonProperty("publications")
    public ArrayList<Publication> publications;

    public String toString(){
	StringBuilder sb = new StringBuilder();
	if(publications != null){
	    for(Publication pub: publications){
		sb.append(pub + "; ");
	    }
	}
	return sb.toString();
    }    
}
