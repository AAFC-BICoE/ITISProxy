package ca.gc.agr.mbb.itisproxy.entities;


import com.fasterxml.jackson.annotation.JsonProperty; 

public class CompletenessRating extends BaseTsn{

    @JsonProperty("completeness")
    public String completeness;

    @JsonProperty("rankId")
    public String rankId;


    public String toString(){
	return completeness + ":" + rankId;
    }    
}

