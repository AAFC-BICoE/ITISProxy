package ca.gc.agr.mbb.itisproxy.entities;

import com.fasterxml.jackson.annotation.JsonProperty; 


public class BaseTsn extends BaseClass{
    @JsonProperty("tsn")
    public String tsn = null;

    //public String toString(){
    //return pairOut("TSN", tsn);
    //}


}
