package ca.gc.agr.mbb.itisproxy.entities;

import com.fasterxml.jackson.annotation.JsonProperty; 

import java.lang.reflect.Field;

public class BaseClass{
    int printDepth = 0;
    @JsonProperty("class")
    public String _class;
 
    /*
    public String toString(){
	System.out.println(this.getClass().getName() + " Fields: ");
	String s = new String();
	Field[] fields = this.getClass().getFields();
	for(Field f: fields){
	    System.out.println("Field: " + f.getName());
	    s += "    Field: " + f.getName() + "=" + getValue(f);
	}
	return s;
    }
    */

    public String pairOut(String k, String v){
	return spaces(printDepth) + k + ": [" + v + "]\n";
    }

    String spaces(int n){
	StringBuilder sb = new StringBuilder(n);
	for(int i=0; i<n; i++){
	    sb.append(" ");
	}
	return sb.toString();
    }

    Object getValue(Field f){
	Class t = f.getType();
	System.out.println("class=" + t.getName());

	if(t.getName().equals("java.lang.String")){
	    try{
		return (String) f.get(this);
	    }catch(Exception e){
		e.printStackTrace();
		return "++";
	    }
	}
	return "--";
    }
}
