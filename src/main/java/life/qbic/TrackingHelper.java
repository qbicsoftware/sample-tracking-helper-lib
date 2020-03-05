package life.qbic;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import life.qbic.services.ConsulServiceFactory;
import life.qbic.ServiceNotFoundException;
import life.qbic.services.Service;
import life.qbic.services.ServiceType;
import life.qbic.services.connectors.ConsulConnector;

public class TrackingHelper {

  private Logger logger = LogManager.getLogger(TrackingHelper.class);

  private List<Service> serviceList;
  private String authHeader;

  public TrackingHelper(String registryURL, String user, String pass) throws Exception {
    serviceList = new ArrayList<>();

    byte[] credentials = Base64.encodeBase64((user + ":" + pass).getBytes(StandardCharsets.UTF_8));
    authHeader = "Basic " + new String(credentials, StandardCharsets.UTF_8);

    URL serviceRegistryUrl = new URL(registryURL);
    try (ConsulConnector connector = new ConsulConnector(serviceRegistryUrl)) {
      ConsulServiceFactory factory = new ConsulServiceFactory(connector);
      serviceList.addAll(factory.getServicesOfType(ServiceType.SAMPLE_TRACKING));
    }
  }

  public void sampleExists(String sampleID) throws Exception {
    if (serviceList.isEmpty()) {
      throw new ServiceNotFoundException();
    } else {
      Service s = serviceList.get(0);
      String baseURL = s.getRootUrl().toString();
      HttpClient client = HttpClientBuilder.create().build();
      String trackingURL = baseURL + "/samples/" + sampleID;
      logger.info("looking for sample using url: " + trackingURL);
      HttpGet getSampleInfo = new HttpGet(trackingURL);
      getSampleInfo.setHeader("Authorization", authHeader);

      HttpResponse response = client.execute(getSampleInfo);

      printResponse(response);
    }
  }

  public void tryUpdateSample(String sampleID, String locationJson) throws Exception {
    if (serviceList.isEmpty()) {
      throw new ServiceNotFoundException();
    } else {
      Service s = serviceList.get(0);
      String baseURL = s.getRootUrl().toString();

      HttpClient client = HttpClientBuilder.create().build();
      String trackingURL = baseURL + "/samples/" + sampleID + "/currentLocation/";
      logger.info("trying to update sample status using url: " + trackingURL);
      HttpPost post = new HttpPost(trackingURL);
      post.setHeader("Authorization", authHeader);

      HttpEntity entity = new StringEntity(locationJson, ContentType.APPLICATION_JSON);
      post.setEntity(entity);
      HttpResponse response = client.execute(post);

      printResponse(response);
    }
  }

  private void printResponse(HttpResponse response) throws Exception {
    StatusLine status = response.getStatusLine();

    if (status.getStatusCode() != 200) {
      logger.error("Request not successful:");
      logger.error(status.toString());
      if (status.getStatusCode() == 404) {
        throw new SampleNotFoundException(status.getReasonPhrase());
      } else {
        throw new Exception(status.getReasonPhrase());
      }
    }
  }

}
