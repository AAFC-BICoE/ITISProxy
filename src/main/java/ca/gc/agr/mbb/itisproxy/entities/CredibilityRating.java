package ca.gc.agr.mbb.itisproxy.entities;



import com.fasterxml.jackson.annotation.JsonProperty; 
import java.util.ArrayList;

public class CredibilityRating extends BaseTsn{

    @JsonProperty("credRating")
    public String credRating;

    public String toString(){
	return 
	    credRating;
	 
    }    
}
