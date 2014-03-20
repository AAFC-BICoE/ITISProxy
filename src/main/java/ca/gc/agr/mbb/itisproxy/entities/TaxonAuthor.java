package ca.gc.agr.mbb.itisproxy.entities;


import com.fasterxml.jackson.annotation.JsonProperty; 

public class TaxonAuthor extends BaseTsn{

    @JsonProperty("authorship")
    public String authorship;

    @JsonProperty("updateDate")
    public String updateDate;

        public String toString(){
	return 
	    "authorship=" + authorship + "; ";
	}

}
