package life.qbic.sampletracking;

/**
 * <b>Changes the status of samples using a predefined status and timepoint; does not use location information as opposed to the old API</b>
 *
 */
public interface LocationIndependentSampleTracking {

    /**
     * This method changes the status of a specific sample with the provided code. Status and time point information is taken from the class
     * implementing this functionality, or the script calling it.
     * @param sampleCode the sample code of the sample being changed
     */
    void updateSampleStatus(String sampleCode);
}