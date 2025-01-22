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
 * to update the R Number in both the DNX and DC metadata of a given IE within GDA
 *
 * Given a csv input list of the format IE,Previous R Number,New R number,
 * the plugin will for each IE in the list:
 * - Update the DNX Object Identifier with the type 'ArchwayUniqueID' - removing the matching previous
 * R number, and replace it with the new R Number
 * - Update the DC element of the type 'dc:identifier xsi:type="ArchwayUniqueID"' with the new R number.
 *
 */
public class GDAMetaDataUpdatePlugin implements RepositoryTaskPlugin {

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

        logger.info("Executing " + getClass().getSimpleName() + " for IE:[" +  IE_PID
                + "] with Input file: [" + inputFileNameAndPath
                + "]");

        // Retrieving all init parameter values within this method to avoid declaration
        // of all the properties as class properties or variables
        if (inputFileNameAndPath == null) {
            logger.error("Input file name and location is missing! Unable to update the DNX or DC metadata for IE:[" + IE_PID + "]");
            taskResults.addResult(IE_PID, null, false, "Input file name and location is missing! Unable to update the DNX or DC metadata for IE");

        } else {

            // Check if file provided exists
            File inputFile = new File(inputFileNameAndPath);
            if (inputFile.exists()) {

                try {

                    FileInputStream fis = new FileInputStream(inputFile);
                    BufferedReader br = new BufferedReader(new InputStreamReader(fis));
                    String strLine;

                    String rNumber = "";
                    String updatedRNumber = "";

                    DnxDocumentHelper ieDNX = ieEditor.getDnxHelperForIE();
                    List<ObjectIdentifier> objectIdentifiers = ieDNX.getObjectIdentifiers();
                    List<ObjectIdentifier> objIdentifierListNew = new ArrayList<>();

                    boolean matchFound = false;

                    // Find the matching RNumber for the IE
                    while ((strLine = br.readLine()) != null && !matchFound) {
                        String[] lineVals = strLine.split(",");

                        for (ObjectIdentifier o : objectIdentifiers) {
                            if (Objects.equals(o.getObjectIdentifierType(), "ArchwayUniqueID") &&
                                    Objects.equals(o.getObjectIdentifierValue(), lineVals[1])) {
                                updatedRNumber = lineVals[2];
                                logger.info("Found the rNumber to update :[" + rNumber + "] for IE " + IE_PID + " new rNumber :[" + updatedRNumber + "]");
                                matchFound = true;
                            }
                        }
                    }
                    br.close();

                    // Update the IE DNX metadata with the new R Number, if one has been found
                    if (!updatedRNumber.isEmpty()) {
                        // DNX MetaData update section
                        for (ObjectIdentifier identifier : objectIdentifiers) {
                            if (Objects.equals(identifier.getObjectIdentifierType(), "ArchwayUniqueID")) {
                                identifier.setObjectIdentifierValue(updatedRNumber);
                                objIdentifierListNew.add(identifier);
                                logger.info("Successfully updated IE DNX record with new rNumber:[" + updatedRNumber
                                        + "] for IE PID:[" + IE_PID + "] using the " + getClass().getSimpleName());
                            } else {
                                objIdentifierListNew.add(identifier);
                            }
                        }

                        ieDNX.setObjectIdentifiers(objIdentifierListNew);

                        // DC MetaData update section
                        DublinCore ieDublinCore = ieEditor.getDcForIE();
                        if (ieDublinCore.getValue("dc:identifier@archwayUniqueId") == null ||
                                ieDublinCore.getValue("dc:identifier@archwayUniqueId").isEmpty()) {
                            ieDublinCore.removeElemet("dc", "identifier", "dcterms:archwayUniqueId");
                            ieDublinCore.addElement("dc:identifier@dcterms:archwayUniqueId", updatedRNumber);
                        } else {
                            ieDublinCore.removeElemet("dc", "identifier", "archwayUniqueId");
                            ieDublinCore.addElement("dc:identifier@archwayUniqueId", updatedRNumber);
                        }

                        // Save new DNX and DC to IE
                        ieEditor.setDnxForIE(ieDNX);
                        ieEditor.setDC(ieDublinCore, ieDublinCore.getMid());

                        logger.info("Successfully updated IE DC record with new rNumber:[" + updatedRNumber
                                + "] for IE PID:[" + IE_PID + "] using the " + getClass().getSimpleName());

                    } else {
                        logger.error("Unable to find the corresponding updated rNumber for IE PID:[" + IE_PID + "]");
                        taskResults.addResult(IE_PID, null, false, "Unable to find the corresponding updated rNumber for IE PID:[" + IE_PID + "]");
                    }

                } catch (IOException ex) {
                    logger.error("IO Exception occurred while executing " + getClass().getSimpleName() + ". " + ex.getMessage());
                    taskResults.addResult(IE_PID, null, false, "IO Exception occurred! " + ex.getMessage());

                } catch (Exception e) {
                    logger.error("Exception occurred while executing " + getClass().getSimpleName() + ". " + e.getMessage());
                    taskResults.addResult(IE_PID, null, false, "Exception occurred! " + e.getMessage());
                }

            } else {
                logger.error("Input file does not exist in the specified location! Unable to update the DNX or DC metadata for IE:[" + IE_PID +"]");
                taskResults.addResult(IE_PID, null, false, "Input file does not exist in the specified location! Unable to update the DNX or DC metadata for IE");
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
