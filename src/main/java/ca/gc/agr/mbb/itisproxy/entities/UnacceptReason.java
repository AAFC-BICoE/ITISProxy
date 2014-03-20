package ca.gc.agr.mbb.itisproxy.entities;


import com.fasterxml.jackson.annotation.JsonProperty; 

public class UnacceptReason extends BaseTsn{

    @JsonProperty("unacceptReason")
    public String unacceptReason;

    public String toString(){
	return 
	    "unacceptReason=" + unacceptReason + "; ";
    }


}
