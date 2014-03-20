package ca.gc.agr.mbb.itisproxy.entities;



import com.fasterxml.jackson.annotation.JsonProperty; 

public class CoreMetadata extends BaseTsn{

    @JsonProperty("credRating")
    public String credRating;
    
    @JsonProperty("rankId")
    public String rankId;

    @JsonProperty("taxonCoverage")
    public String taxonCoverage;

    @JsonProperty("taxonCurrency")
    public String taxonCurrency;

    @JsonProperty("taxonUsageRating")
    public String taxonUsageRating;

    @JsonProperty("unacceptReason")
    public String unacceptReason;

    public String toString(){
	return 
	    credRating + ":"
	    + rankId + ":"
	    + taxonCoverage + ":"
	    + taxonCurrency + ":"
	    + taxonUsageRating + ":"
	    + unacceptReason;
    }    

}
