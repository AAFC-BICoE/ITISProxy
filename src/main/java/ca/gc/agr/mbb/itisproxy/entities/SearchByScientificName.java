package ca.gc.agr.mbb.itisproxy.entities;


import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;


public class SearchByScientificName extends BaseTsn{
    @JsonProperty("scientificNames")
    public ArrayList<ScientificName> scientificNames;    
}
