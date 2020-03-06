package life.qbic;

import life.qbic.exceptions.ServiceNotFoundException;
import life.qbic.services.Service;

import java.net.URL;
import java.util.List;

public class SampleTracker {

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
