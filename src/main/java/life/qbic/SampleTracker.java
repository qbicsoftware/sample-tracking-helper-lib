package life.qbic;

import life.qbic.exceptions.ServiceNotFoundException;
import life.qbic.services.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.List;

public class SampleTracker {

    final static Logger LOGGER = LogManager.getLogger(SampleTracker.class);

    static SampleTracking createQBiCSampleTracker(URL serviceRegistryUrl,
                                                  ServiceCredentials credentials,
                                                  String location) {
        List<Service> trackingService = SampleTrackingService.requestServices(serviceRegistryUrl);
        if (trackingService.isEmpty()) {
            throw new ServiceNotFoundException();
        }
        return new QBiCSampleTracker(trackingService.get(0), credentials, location);
    }


}
