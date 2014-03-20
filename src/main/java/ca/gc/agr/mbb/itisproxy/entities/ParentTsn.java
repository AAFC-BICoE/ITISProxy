package ca.gc.agr.mbb.itisproxy.entities;



import com.fasterxml.jackson.annotation.JsonProperty; 

public class ParentTsn extends BaseTsn{
    @JsonProperty("parentTsn")
    public String parentTsn;

    public String toString(){
	return parentTsn;
    }
}
