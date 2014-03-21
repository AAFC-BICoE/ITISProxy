package ca.gc.agr.mbb.itisproxy;



public class TooManyResultsException extends Exception{

    public TooManyResultsException(){
	super();
    }

    public TooManyResultsException(Exception e){
	super(e);
    }

    public TooManyResultsException(String s){
	super(s);
    }

    public TooManyResultsException(String s, Exception e){
	super(s, e);
    }


}
