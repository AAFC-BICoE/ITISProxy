package ca.gc.agr.mbb.itisproxy.entities;


import com.fasterxml.jackson.annotation.JsonProperty; 

public class ReferenceFor{

    @JsonProperty("class")
    protected String _class;

    @JsonProperty("name")
    protected String name;

    @JsonProperty("refLanguage")
    protected String refLanguage;

    @JsonProperty("referredTsn")
    protected String referredTsn;


}
