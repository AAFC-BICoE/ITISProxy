package ca.gc.agr.mbb.itisproxy.entities;



import com.fasterxml.jackson.annotation.JsonProperty; 


public class GeoDivision extends BaseClass{
    @JsonProperty("geographicValue")
    public String geographicValue;

    @JsonProperty("updateDate")
    protected String updateDate;


    public String toString(){
	return geographicValue;
    }
}
