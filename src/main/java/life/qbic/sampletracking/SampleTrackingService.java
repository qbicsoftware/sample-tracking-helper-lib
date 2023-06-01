package life.qbic.sampletracking;

import life.qbic.sampletracking.exceptions.ServiceRequestException;
import life.qbic.services.ConsulServiceFactory;
import life.qbic.services.Service;
import life.qbic.services.ServiceType;
import life.qbic.services.connectors.ConsulConnector;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

class SampleTrackingService {

    static List<Service> requestServices(URL serviceRegistryUrl) {
        List<Service> serviceList;
        try (ConsulConnector connector = new ConsulConnector(serviceRegistryUrl)) {
            ConsulServiceFactory factory = new ConsulServiceFactory(connector);
            serviceList = new ArrayList<>(factory.getServicesOfType(ServiceType.SAMPLE_TRACKING));
        } catch (Exception e) {
            throw new ServiceRequestException(e);
        }
        return serviceList;

    }

}
