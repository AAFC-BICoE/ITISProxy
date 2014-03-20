package ca.gc.agr.mbb.itisproxy.entities;

import com.fasterxml.jackson.annotation.JsonProperty; 

import java.util.ArrayList;

public class AnyMatchCount extends BaseTsn{

    @JsonProperty("return")
    public String count;
}
