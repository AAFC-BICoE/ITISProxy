package ca.gc.agr.mbb.itisproxy.entities;


import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;

public class GetKingdomNames extends BaseTsn{
    
    @JsonProperty("kingdomNames")
    public ArrayList<Kingdom> kingdoms;    
}
