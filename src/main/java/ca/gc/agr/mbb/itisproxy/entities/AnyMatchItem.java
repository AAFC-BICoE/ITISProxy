package ca.gc.agr.mbb.itisproxy.entities;

import com.fasterxml.jackson.annotation.JsonProperty; 

import java.util.ArrayList;

public class AnyMatchItem extends BaseTsn{

    @JsonProperty("author")
    public String author;

    @JsonProperty("commonNameList")
    public CommonNameList commonNamesList;
    
    @JsonProperty("sciName")
    public String sciName;
}
