package life.qbic.sampletracking;

import life.qbic.sampletracking.exceptions.ServiceRequestException;
import life.qbic.services.Service;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.charset.StandardCharsets;

class LocationIndependentSampleTracker implements LocationIndependentSampleTracking {

  private Logger logger = LogManager.getLogger(LocationIndependentSampleTracker.class);

  private Service trackingService;

  private String authHeader;

  public LocationIndependentSampleTracker(Service trackingService, ServiceCredentials credentials) {
    this.trackingService = trackingService;
    setAuthHeader(credentials);
  }

  private void setAuthHeader(ServiceCredentials credentials) {
    byte[] credentialsEncoded = Base64.encodeBase64(
        (credentials.user + ":" + credentials.password).getBytes(StandardCharsets.UTF_8));
    authHeader = "Basic " + new String(credentialsEncoded, StandardCharsets.UTF_8);
  }

  @Override
  public void updateSampleStatus(String sampleCode, String statusChangeRequestJson) {

    String baseURL = trackingService.getRootUrl().toString();

    HttpClient client = HttpClientBuilder.create().build();
    String trackingURL = baseURL + "/v2/samples/" + sampleCode + "/status/";

    logger.info(String.format("Updating sample status using url for sampleCode %s serviceUrl: %s",
            sampleCode, trackingURL));

    HttpEntityEnclosingRequestBase request = new HttpPut(trackingURL);
    request.setHeader("Authorization", authHeader);

    HttpEntity entity = new StringEntity(statusChangeRequestJson, ContentType.APPLICATION_JSON);
    request.setEntity(entity);
    HttpResponse response;
    try {
      response = client.execute(request);
    } catch (Exception e) {
      throw new ServiceRequestException(e);
    }

    if (response.getStatusLine().getStatusCode() != 200) {
      throw new ServiceRequestException(
              String.format("Http response code was %d", response.getStatusLine().getStatusCode()));
    }
  }
}
