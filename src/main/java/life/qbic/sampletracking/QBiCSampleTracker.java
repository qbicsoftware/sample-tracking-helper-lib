package life.qbic.sampletracking;

import java.nio.charset.StandardCharsets;
import com.fasterxml.jackson.databind.ObjectMapper;
import life.qbic.datamodel.samples.Sample;
import life.qbic.sampletracking.exceptions.SampleUpdateException;
import life.qbic.sampletracking.exceptions.ServiceRequestException;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import life.qbic.services.Service;

class QBiCSampleTracker implements SampleTracking {

  private final Logger logger = LogManager.getLogger(QBiCSampleTracker.class);

  private final Service trackingService;

  private String authHeader;

  private final String locationJson;

  QBiCSampleTracker(Service trackingService, ServiceCredentials credentials, String locationJson) {
    this.trackingService = trackingService;
    this.locationJson = locationJson;
    setAuthHeader(credentials);
  }

  private void setAuthHeader(ServiceCredentials credentials) {
    byte[] credentialsEncoded = Base64.encodeBase64(
        (credentials.user + ":" + credentials.password).getBytes(StandardCharsets.UTF_8));
    authHeader = "Basic " + new String(credentialsEncoded, StandardCharsets.UTF_8);
  }

  @Override
  public void updateSampleLocationToCurrentLocation(String sampleId) {

    String baseURL = trackingService.getRootUrl().toString();

    HttpClient client = HttpClientBuilder.create().build();
    String trackingURL = baseURL + "/samples/" + sampleId + "/currentLocation/";

    logger.info(String.format("Updating sample status using url for sampleId %s serviceUrl: %s",
        sampleId, trackingURL));

    HttpEntityEnclosingRequestBase request =
        (isSampleAtQBiC(sampleId)) ? new HttpPut(trackingURL) : new HttpPost(trackingURL);
    request.setHeader("Authorization", authHeader);

    HttpEntity entity = new StringEntity(locationJson, ContentType.APPLICATION_JSON);
    request.setEntity(entity);
    HttpResponse response;
    try {
      response = client.execute(request);
    } catch (Exception e) {
      logger.error(e);
      throw new ServiceRequestException(e);
    }

    if (response.getStatusLine().getStatusCode() != 200) {
      throw new ServiceRequestException(
          String.format("Http response code was %d", response.getStatusLine().getStatusCode()));
    }
  }

  private boolean isSampleAtQBiC(String sampleId) {
    String baseURL = trackingService.getRootUrl().toString();

    HttpClient client = HttpClientBuilder.create().build();
    String trackingURL = baseURL + "/samples/" + sampleId;

    logger.info(String.format("Querying sample tracking information for sample: %s.", sampleId));

    HttpUriRequest request = new HttpGet(trackingURL);
    request.setHeader("Authorization", authHeader);
    HttpResponse response;
    try {
      response = client.execute(request);
    } catch (Exception e) {
      logger.error("Query sample location failed.", e);
      throw new ServiceRequestException("Could not query sample location information.");
    }

    int statusCode = response.getStatusLine().getStatusCode();

    if (statusCode == 404) {
      logger.info("History of sample with code " + sampleId
          + " is not known to the tracking database. Sample code will be added.");
      return false;
    }

    if (response.getStatusLine().getStatusCode() != 200) {
      throw new ServiceRequestException(
          String.format("Http response code was %d", response.getStatusLine().getStatusCode()));
    }

    if (response.getEntity() == null) {
      logger.info("No current location information available for sample " + sampleId);
      return false;
    }

    ObjectMapper mapper = new ObjectMapper();
    Sample sample;
    try {
      sample = mapper.readValue(EntityUtils.toString(response.getEntity(), "UTF-8"), Sample.class);
    } catch (Exception e) {
      logger.error("Could not parse sample object for sample with id " + sampleId, e);
      throw new SampleUpdateException("The request for the current sample location failed.");
    }

    return sample.getCurrentLocation().getName().equalsIgnoreCase("QBiC");
  }
}
