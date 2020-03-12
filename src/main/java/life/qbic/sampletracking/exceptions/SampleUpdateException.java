package life.qbic.sampletracking.exceptions;

public class SampleUpdateException extends RuntimeException {

    public SampleUpdateException(){
        super();
    }

    public SampleUpdateException(String msg){
        super(msg);
    }

    public SampleUpdateException(Throwable t){
        super(t);
    }

}
