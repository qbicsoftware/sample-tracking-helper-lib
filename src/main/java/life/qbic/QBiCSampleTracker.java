package life.qbic;

import java.nio.charset.StandardCharsets;

import life.qbic.exceptions.ServiceRequestException;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import life.qbic.services.Service;

class QBiCSampleTracker implements SampleTracking {

  private Logger logger = LogManager.getLogger(QBiCSampleTracker.class);

  private Service trackingService;

  private String authHeader;

  private String locationJson;

  QBiCSampleTracker(Service trackingService, ServiceCredentials credentials, String locationJson) {
    this.trackingService = trackingService;
    this.locationJson = locationJson;
    setAuthHeader(credentials);
  }

  private void setAuthHeader(ServiceCredentials credentials) {
    byte[] credentialsEncoded = Base64.encodeBase64((credentials.user + ":" + credentials.password).getBytes(StandardCharsets.UTF_8));
    authHeader = "Basic " + new String(credentialsEncoded, StandardCharsets.UTF_8);
  }

  @Override
  public void updateSampleLocationToCurrentLocation(String sampleId) {
    String baseURL = trackingService.getRootUrl().toString();

    HttpClient client = HttpClientBuilder.create().build();
    String trackingURL = baseURL + "/samples/" + sampleId + "/currentLocation/";

    logger.info(String.format("Updating sample status using url for sampleId %s serviceUrl: %s", sampleId, trackingURL));

    HttpPost post = new HttpPost(trackingURL);
    post.setHeader("Authorization", authHeader);

    HttpEntity entity = new StringEntity(locationJson, ContentType.APPLICATION_JSON);
    post.setEntity(entity);
    HttpResponse response;
    try {
      response = client.execute(post);
    } catch (Exception e) {
      logger.error(e);
      throw new ServiceRequestException(e);
    }

    if (response.getStatusLine().getStatusCode() != 200 ){
      throw new ServiceRequestException(String.format("Http response code was %d", response.getStatusLine().getStatusCode()));
    }

  }
}
