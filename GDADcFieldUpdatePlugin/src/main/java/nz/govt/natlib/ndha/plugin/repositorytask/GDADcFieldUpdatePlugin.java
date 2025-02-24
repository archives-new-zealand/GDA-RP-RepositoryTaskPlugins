package nz.govt.natlib.ndha.plugin.repositorytask;

import com.exlibris.core.infra.common.exceptions.logging.ExLogger;
import com.exlibris.core.sdk.formatting.DublinCore;
import com.exlibris.core.sdk.repository.TaskResults;
import com.exlibris.digitool.common.dnx.DnxDocumentHelper;
import com.exlibris.digitool.common.dnx.DnxDocumentHelper.ObjectIdentifier;
import com.exlibris.digitool.repository.api.IEEditor;
import com.exlibris.digitool.repository.api.RepositoryTaskPlugin;

import java.io.*;
import java.util.*;

/*
 * GDAMetaDataUpdatePlugin is a Rosetta RepositoryTask Java Plugin
 * to update a DC field in the metadata of a given IE within GDA
 * The field is one of the params selected when run from Rosetta
 * Currently this can be either dcterms:isPartOf or dcterms:provenance
 *
 * Given a csv input list of the format IE,Current data,New DC Value
 * - Update the DC element of the type either dcterms:isPartOf or dcterms:provenance
 *   in the specified IE
 */
public class GDADcFieldUpdatePlugin implements RepositoryTaskPlugin {

    // Define the logger - no need to include any log4j config
    // as this logger will attach itself with the Rosetta Server
    // logging and print messages to the server log
    private ExLogger logger = ExLogger.getExLogger(getClass());

    /*
     * Method implementation defined in the RepositoryTaskPlugin interface
     * The execute method is the method that gets executed by
     * Rosetta's Process Automation(PA) framework tasks
     */
    @Override
    public TaskResults execute(IEEditor ieEditor, Map<String, String> initParams, TaskResults taskResults) {

        String IE_PID  = ieEditor.getIEPid();
        String inputFileNameAndPath = initParams.get("infileNameAndPath"); // Retrieve the csv filename and location
        String dcField = initParams.get("dcField"); // Retrieve the Meta Data Type: DC or DNX

        logger.info("Executing " + getClass().getSimpleName() + " for IE:[" +  IE_PID
                + "] with Input file: [" + inputFileNameAndPath
                + "]");

        // Retrieving all init parameter values within this method to avoid declaration
        // of all the properties as class properties or variables
        if (inputFileNameAndPath == null) {
            logger.error("Input file name and location is missing! Unable to update the DC metadata for IE:[" + IE_PID + "]");
            taskResults.addResult(IE_PID, null, false, "Input file name and location is missing! Unable to update the DC metadata for IE");

        } else if (dcField == null) {
            logger.error("DC Field Type not specified! Unable to update the DC metadata for IE:[" + IE_PID + "]");
            taskResults.addResult(IE_PID, null, false, "DC Field Type not specified! Unable to update the DC metadata for IE");
        } else {
            // Check if file provided exists
            File inputFile = new File(inputFileNameAndPath);
            if (inputFile.exists()) {

                try {

                    FileInputStream fis = new FileInputStream(inputFile);
                    BufferedReader br = new BufferedReader(new InputStreamReader(fis));
                    String strLine;

                    boolean matchFound = false;

                    // Retrieve the corresponding TITLE from the input file for the given IE PID
                    String exisitingDcValue = "";
                    String newDcValue = "";

                    while ((strLine = br.readLine()) != null && !matchFound) {
                        String[] lineVals = strLine.split(","); // Use the limit parameter for the split function
                        // Split the items on each line: IE PID,Original Value,Value to update (DC)
                        if (IE_PID.equals(lineVals[0])) {
                            logger.info("Found the new DC Value for the given IE PID:[" + IE_PID + "] at [" + strLine + "]");
                            exisitingDcValue = lineVals[1].replace("\"", "").trim();
                            newDcValue = lineVals[2].replace("\"", "").trim();
                            // Exit the while loop as we have found the matching IE PID and Title
                            break;
                        }
                    }
                    br.close();

                    // Update the IE DC metadata with new value from the csv file if available for the given IE PID
                    if (!newDcValue.equals("")) {

                        DublinCore ieDublinCore = ieEditor.getDcForIE();

                        if (Objects.equals(ieDublinCore.getValue(dcField), exisitingDcValue)) {
                            ieDublinCore.removeElemet(dcField);
                            ieDublinCore.addElement(dcField, newDcValue);
                            ieEditor.setDC(ieDublinCore, ieDublinCore.getMid());
                            logger.info("Successfully updated IE dc:isPartOf for IE PID:[" + IE_PID + "] using the " + getClass().getSimpleName());
                            taskResults.addResult(IE_PID, null, true, dcField + " was successfully updated");

                        } else {
                            logger.error("The current data in the csv does not match the IE " + IE_PID);
                            taskResults.addResult(IE_PID, null, false, "The current data in the csv does not match the data in IE :[" + IE_PID + "]");
                        }

                    } else {
                        logger.error("Unable to find the corresponding dc value for IE PID:[" + IE_PID + "]");
                        taskResults.addResult(IE_PID, null, false, "Unable to find the corresponding updated dc value for IE PID:[" + IE_PID + "]");
                    }

                } catch (IOException ex) {
                    logger.error("IO Exception occurred while executing " + getClass().getSimpleName() + ". " + ex.getMessage());
                    taskResults.addResult(IE_PID, null, false, "IO Exception occurred! " + ex.getMessage());

                } catch (Exception e) {
                    logger.error("Exception occurred while executing " + getClass().getSimpleName() + ". " + e.getMessage());
                    taskResults.addResult(IE_PID, null, false, "Exception occurred! " + e.getMessage());
                }

            } else {
                logger.error("Input file does not exist in the specified location! Unable to update the DC metadata for IE:[" + IE_PID +"]");
                taskResults.addResult(IE_PID, null, false, "Input file does not exist in the specified location! Unable to update the DC metadata for IE");
            }
        }

        return taskResults;
    }

    // Method implementation defined in the interface - leave default
    @Override
    public boolean isReadOnly() {
        return false;
    }
}
