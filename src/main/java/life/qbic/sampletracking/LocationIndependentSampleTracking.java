package life.qbic.sampletracking;

/**
 * <b>Changes the status of samples using a predefined status and timepoint; does not use location information as opposed to the old API</b>
 *
 */
public interface LocationIndependentSampleTracking {

    /**
     * This method changes the status of a specific sample with the provided code. Status and time point information is taken from the
     * provided json String
     * @param sampleCode the sample code of the sample being changed
     * @param statusChangeRequestJson json containing the status name and timepoint of the change
     */
    void updateSampleStatus(String sampleCode, String statusChangeRequestJson);
}