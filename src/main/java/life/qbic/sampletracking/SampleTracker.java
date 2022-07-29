package life.qbic.sampletracking;

import life.qbic.sampletracking.exceptions.ServiceNotFoundException;
import life.qbic.services.Service;

import java.net.URL;
import java.util.List;

public class SampleTracker {

    public static SampleTracking createQBiCSampleTracker(URL serviceRegistryUrl,
                                                  ServiceCredentials credentials,
                                                  String location) {
        List<Service> trackingService = SampleTrackingService.requestServices(serviceRegistryUrl);
        if (trackingService.isEmpty()) {
            throw new ServiceNotFoundException();
        }
        return new QBiCSampleTracker(trackingService.get(0), credentials, location);
    }

    public static LocationIndependentSampleTracking createLocationIndependentSampleTracker(URL serviceRegistryUrl,
                                                         ServiceCredentials credentials, String statusChangeRequestJson) {
        List<Service> trackingService = SampleTrackingService.requestServices(serviceRegistryUrl);
        if (trackingService.isEmpty()) {
            throw new ServiceNotFoundException();
        }
        return new LocationIndependentSampleTracker(trackingService.get(0), credentials, statusChangeRequestJson);
    }
}
