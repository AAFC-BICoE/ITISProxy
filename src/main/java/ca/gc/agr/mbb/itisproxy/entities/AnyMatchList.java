package ca.gc.agr.mbb.itisproxy.entities;

import com.fasterxml.jackson.annotation.JsonProperty; 

import java.util.ArrayList;

public class AnyMatchList extends BaseTsn{

    @JsonProperty("anyMatchList")
    public ArrayList<AnyMatchItem> anyMatchItems;

}
