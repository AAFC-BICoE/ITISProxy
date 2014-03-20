package ca.gc.agr.mbb.itisproxy;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class JsonDataConverter implements DataConverter{
    private ObjectMapper mapper;

    public JsonDataConverter(){
	init();
    }

    private final void init(){
	mapper = new ObjectMapper(); 
	mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public Object convert(Object...args) throws IllegalArgumentException{
	String json = (String)args[0];
	Class toClass = (Class)args[1];
	if(json == null){
	    throw new IllegalArgumentException("JSON string is null");
	}
	if(toClass == null){
	    throw new IllegalArgumentException("toClass is null");
	}
	try{
	    return mapper.readValue(json, toClass);
	}catch(IOException e){
	    e.printStackTrace();
	    throw new NullPointerException();
	}
    }

}
