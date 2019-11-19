package life.qbic;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import life.qbic.services.ConsulServiceFactory;
import life.qbic.services.Service;
import life.qbic.services.ServiceType;
import life.qbic.services.connectors.ConsulConnector;
import life.qbic.utils.TimeUtils;

public class TrackingHelper {

  private List<Service> serviceList;


  public TrackingHelper(String registryURL) throws Exception {
    serviceList = new ArrayList<>();
    URL serviceRegistryUrl = new URL(registryURL);
    try (ConsulConnector connector = new ConsulConnector(serviceRegistryUrl)) {
      ConsulServiceFactory factory = new ConsulServiceFactory(connector);
      serviceList.addAll(factory.getServicesOfType(ServiceType.SAMPLE_TRACKING));
    }
    TimeUtils.getCurrentTimestampString();
  }

  public static void main(String[] args) throws Exception {
    TrackingHelper t = new TrackingHelper("http://service-registry.local:8500/v1");
    System.out.println(t.sampleExists("QABCD"));
    
    System.out.println(t.sampleExists("QABCD004AO"));
  }

  public boolean sampleExists(String sampleID) throws ClientProtocolException, IOException, ServiceNotFoundException {
    if (serviceList.isEmpty()) {
      throw new ServiceNotFoundException();
    } else {
      Service s = serviceList.get(0);
      String baseURL = s.getRootUrl().toString();
      
      System.out.println(baseURL);

      HttpClient client = HttpClientBuilder.create().build();

      HttpGet getSampleInfo = new HttpGet(baseURL + "samples/" + sampleID);
      HttpResponse response = client.execute(getSampleInfo);
      System.out.println(response.getStatusLine());

      // ObjectMapper mapper = new ObjectMapper();
      // Sample sample = mapper.readValue(response.getEntity().getContent(), Sample.class);
    }
    return false;
  }


  public void tryUpdateSample(String sampleID) throws ServiceNotFoundException {
    if (serviceList.isEmpty()) {
      throw new ServiceNotFoundException();
    } else {
      Service s = serviceList.get(0);
      s.getRootUrl();
    }
  }

}
