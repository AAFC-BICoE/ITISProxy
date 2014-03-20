package ca.gc.agr.mbb.itisproxy.entities;



import com.fasterxml.jackson.annotation.JsonProperty; 

public class CommonName extends BaseTsn{
    @JsonProperty("commonName")
    public String commonName;
    
    @JsonProperty("language")
    public String language;

    public String toString(){
	return language + ":" + commonName;
    }    
}
