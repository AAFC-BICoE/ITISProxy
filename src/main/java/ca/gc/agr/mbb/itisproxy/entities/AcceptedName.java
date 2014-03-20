package ca.gc.agr.mbb.itisproxy.entities;

import com.fasterxml.jackson.annotation.JsonProperty; 

public class AcceptedName extends BaseClass{

    @JsonProperty("acceptedName")
    public String acceptedName;

    @JsonProperty("acceptedTsn")
    public String acceptedTsn;

    @JsonProperty("author")
    public String author;


    public String toString(){
	return  acceptedTsn + ":" + acceptedName + ":" + author;
    }
}
