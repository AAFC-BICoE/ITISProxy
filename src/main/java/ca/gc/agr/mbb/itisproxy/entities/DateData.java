package ca.gc.agr.mbb.itisproxy.entities;



import com.fasterxml.jackson.annotation.JsonProperty; 

public class DateData extends BaseTsn{
    @JsonProperty("initialTimeStamp")
    protected String initialTimeStamp;

    @JsonProperty("updateDate")
    protected String updateDate;

    public String toString(){
	return 
	    initialTimeStamp + ":"
	    + updateDate;
    }

}
