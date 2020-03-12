package life.qbic.sampletracking.exceptions;

public class ServiceRequestException extends RuntimeException {

    public ServiceRequestException(){
        super();
    }

    public ServiceRequestException(String msg){
        super(msg);
    }

    public ServiceRequestException(Throwable t){
        super(t);
    }

}
