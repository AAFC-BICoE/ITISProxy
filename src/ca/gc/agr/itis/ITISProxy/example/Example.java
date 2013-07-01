package ca.gc.agr.itis.ITISProxy.example;

import ca.gc.agr.itis.ITISProxy.Proxy;
import ca.gc.agr.itis.ITISProxy.MockUtil;
import ca.gc.agr.itis.ITISProxy.Util;
import ca.gc.agr.itis.ITISModel.*;
import java.util.List;
import java.util.ArrayList;


public final class Example{
   

    public static void main(String [ ] args){
	kingdoms();

	fungi();
    }


    static final void kingdoms(){
	start("Proxy.getKingdoms");
	try{
	    List<ItisRecord>kingdoms = Proxy.getKingdoms();
	    for(ItisRecord kir: kingdoms){
		Util.printRecord(kir);
	    }
	}
	catch(Exception e){
	    e.printStackTrace();
	}
	end("Proxy.getKingdoms");
    }

    
    static final void fungi(){
	try{
	    ItisRecord ir = Proxy.getByTSN(MockUtil.fungiTsn);
	    Util.printRecord(ir);
	}
	catch(Exception e){
	    e.printStackTrace();
	}
    }
    
    static final String br = "-----------------------------------------------------------------";
    static void end(String s){
	//System.out.println(br + " END " + s + br);
	System.out.println(br);
    }

    static void start(String s){
	//System.out.println(br + " START " + s + br + "\n");
    }



}
