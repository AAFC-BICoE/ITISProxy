package ca.gc.agr.mbb.itisproxy.entities;



import com.fasterxml.jackson.annotation.JsonProperty; 

public class Usage extends BaseClass{

    @JsonProperty("taxonUsageRating")
    public String taxonUsageRating;

    public String toString(){
	return 
	    "taxonUsageRating=" + taxonUsageRating + "; ";

    }

}
