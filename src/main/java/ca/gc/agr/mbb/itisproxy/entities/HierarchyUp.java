package ca.gc.agr.mbb.itisproxy.entities;



import com.fasterxml.jackson.annotation.JsonProperty; 

public class HierarchyUp extends BaseTsn{

    @JsonProperty("author")
    public String author;

     @JsonProperty("parentName")
     public String parentName;

    @JsonProperty("parentTsn")
    public String parentTsn;

    @JsonProperty("rankName")
    public String rankName;

    @JsonProperty("taxonName")
    public String taxonName;

    
    public String toString(){
	return 
	    "author=" + author + "; "
	    + "parentName=" + parentName + "; "
	    + "parentTsn=" + parentTsn + "; "
	    + "rankName=" + rankName + "; "
	    + "taxonName=" + taxonName + "; ";
    }
}

