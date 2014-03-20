package ca.gc.agr.mbb.itisproxy.entities;

import com.fasterxml.jackson.annotation.JsonProperty; 

public class Synonym extends BaseTsn{

    @JsonProperty("author")
    public String author;

    @JsonProperty("sciName")
    public String sciName;


    public String toString(){
	return 
	    "author=" + author + "; "
	    + "sciName=" + sciName + "; ";
    }
}
