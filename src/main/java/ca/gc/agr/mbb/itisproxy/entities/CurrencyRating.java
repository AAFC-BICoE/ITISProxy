package ca.gc.agr.mbb.itisproxy.entities;


import com.fasterxml.jackson.annotation.JsonProperty; 

public final class CurrencyRating extends BaseTsn{

    @JsonProperty("rankId")
    public String rankId;

    @JsonProperty("taxonCurrency")
    public String taxonCurrency;

    public String toString(){
	return 
	    taxonCurrency + ":"
	    + rankId;
    }
}
