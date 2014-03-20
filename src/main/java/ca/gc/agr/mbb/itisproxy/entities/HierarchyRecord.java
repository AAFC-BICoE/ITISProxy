package ca.gc.agr.mbb.itisproxy.entities;


import com.fasterxml.jackson.annotation.JsonProperty; 

public class HierarchyRecord extends BaseTsn{
    @JsonProperty("parentName")
    public String parentName;

    @JsonProperty("parentTsn")
    public String parentTsn;

    @JsonProperty("rankName")
    public String rankName;

    @JsonProperty("taxonName")
    public String taxonName;
}
