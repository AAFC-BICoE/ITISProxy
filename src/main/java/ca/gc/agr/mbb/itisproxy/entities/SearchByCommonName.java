package ca.gc.agr.mbb.itisproxy.entities;


import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;

public class SearchByCommonName extends BaseTsn{
    
    @JsonProperty("commonNames")
    public ArrayList<CommonName> commonNames;    
}
