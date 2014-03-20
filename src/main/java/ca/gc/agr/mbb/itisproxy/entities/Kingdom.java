package ca.gc.agr.mbb.itisproxy.entities;


import com.fasterxml.jackson.annotation.JsonProperty; 

public class Kingdom extends BaseTsn{
    @JsonProperty("kingdomId")
    public String kingdomId;

    @JsonProperty("kingdomName")
    public String kingdomName;

    public String toString(){
	return kingdomId + ":" + kingdomName;
    }
}
