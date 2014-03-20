package ca.gc.agr.mbb.itisproxy.entities;

import com.fasterxml.jackson.annotation.JsonProperty; 

import java.util.ArrayList;

public class JurisdictionalOrigin extends BaseClass{

    @JsonProperty("jurisdictionValue")
    public String jurisdictionValue;

    @JsonProperty("origin")
    public String origin;

    @JsonProperty("updateDate")
    public String updateDate;

    public String toString(){
	return jurisdictionValue + ":" + origin;
    }

}
