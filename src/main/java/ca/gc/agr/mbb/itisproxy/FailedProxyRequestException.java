package ca.gc.agr.mbb.itisproxy;



public class FailedProxyRequestException extends Exception{

    public FailedProxyRequestException(){
	super();
    }

    public FailedProxyRequestException(Exception e){
	super(e);
    }

    public FailedProxyRequestException(String s){
	super(s);
    }

    public FailedProxyRequestException(String s, Exception e){
	super(s, e);
    }


}
