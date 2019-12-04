package life.qbic;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import life.qbic.services.Service;

public class TrackingHelper {

  private List<Service> serviceList;

  public TrackingHelper(String registryURL) throws Exception {
    serviceList = new ArrayList<>();
    // URL serviceRegistryUrl = new URL(registryURL);
    // try (ConsulConnector connector = new ConsulConnector(serviceRegistryUrl)) {
    // ConsulServiceFactory factory = new ConsulServiceFactory(connector);
    // serviceList.addAll(factory.getServicesOfType(ServiceType.SAMPLE_TRACKING));
    // }
  }

  public static void main(String[] args) throws Exception {
    TrackingHelper t = new TrackingHelper("");
    t.sampleExists("QTRAK008AX");
  }

  public void sampleExists(String sampleID) throws Exception {
    if (serviceList.isEmpty()) {
      throw new ServiceNotFoundException();
    } else {
      Service s = serviceList.get(0);
      String baseURL = s.getRootUrl().toString();
      HttpClient client = HttpClientBuilder.create().build();
      String trackingURL = baseURL + "/samples/" + sampleID;
      System.out.println("url: " + trackingURL);
      HttpGet getSampleInfo = new HttpGet(trackingURL);

      HttpResponse response = client.execute(getSampleInfo);

      StatusLine status = response.getStatusLine();
      if (status.getStatusCode() != 200) {
        throw new Exception(status.getReasonPhrase());
      }
    }
  }

  public void tryUpdateSample(String sampleID, String locationJson) throws Exception {
    if (serviceList.isEmpty()) {
      throw new ServiceNotFoundException();
    } else {
      Service s = serviceList.get(0);
      String baseURL = s.getRootUrl().toString();

//      Date date = new Date(System.currentTimeMillis());
//
//      loc.setArrivalDate(date);

      HttpClient client = HttpClientBuilder.create().build();
      HttpPost post = new HttpPost(baseURL + "samples/" + sampleID + "/currentLocation/");
//      ObjectMapper mapper = new ObjectMapper();

//      String json = mapper.writeValueAsString(loc);
      HttpEntity entity = new StringEntity(locationJson, ContentType.APPLICATION_JSON);
      post.setEntity(entity);
      HttpResponse response = client.execute(post);

      StatusLine status = response.getStatusLine();
      if (status.getStatusCode() != 200) {
        throw new Exception(status.getReasonPhrase());
      }
    }
  }

}
