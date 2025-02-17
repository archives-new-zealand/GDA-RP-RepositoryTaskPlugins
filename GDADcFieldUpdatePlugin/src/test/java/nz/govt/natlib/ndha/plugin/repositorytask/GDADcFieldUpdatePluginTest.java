package nz.govt.natlib.ndha.plugin.repositorytask;

import com.exlibris.core.sdk.formatting.DublinCore;
import com.exlibris.core.sdk.formatting.DublinCoreFactory;
import com.exlibris.core.sdk.repository.TaskResults;
import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import static org.junit.Assert.*;

public class GDADcFieldUpdatePluginTest {

    private static final Logger log = Logger.getLogger(GDADcFieldUpdatePluginTest.class);
    private Map<String, String> initParams;

    @Before
    public void init() {
        // Perform any initialization tasks for the tests here
    }
    @After
    public void destroy() {
        // Perform any cleaning up tasks after the tests here
    }
    @Test
    public void executeTest() throws FileNotFoundException, DocumentException, Exception  {
        // Test updating the dcterms:provenance field
        testGDADcFUPlugin("IE12345678",
                "Transferred by agency AABK",
                "AABK",
                "dcterms:provenance",
                "./src/test/resources/provenance.csv",
                true);

        // Test updating the dcterms:isPartOf field
        testGDADcFUPlugin("IE12345678",
                "",
                "67890",
                "dcterms:isPartOf",
                "./src/test/resources/isPartOf.csv",
                true);

        // Test no update happens when the old value in the csv doesn't match what's in the IE
        testGDADcFUPlugin("IE12345678",
                "12345",
                "67890",
                "dcterms:isPartOf",
                "./src/test/resources/isPartOf_wrong_value.csv",
                false);
    }

    public void testGDADcFUPlugin(String IE_PID, String oldValue, String newValue, String dcField, String csvFile, boolean shouldUpdate) throws Exception {
        GDADcFieldUpdatePlugin GDADcFUPlugin = new GDADcFieldUpdatePlugin();
        mockIEEditorImpl ieEditor = new mockIEEditorImpl(IE_PID);

        String dc_str = new Scanner(new File("./src/test/resources/" + IE_PID + "_dc.xml")).useDelimiter("\\A").next();
        DublinCore dcObj = DublinCoreFactory.getInstance().createDocument(dc_str);

        ieEditor.setDCForIE(dcObj);

        initParams = new HashMap<String, String>();
        initParams.put("infileNameAndPath", csvFile);
        initParams.put("dcField", dcField);

        // Test initial values match
        String dcValue = ieEditor.getDcForIE().getXMLPathValue(dcField);

        if (shouldUpdate) {
            assertEquals("DC initial value", oldValue, dcValue);
        } else {
            assertNotEquals("Dc values don't match", oldValue, dcValue);
        }

        // Run the plugin
        TaskResults taskResult = GDADcFUPlugin.execute(ieEditor, initParams, new TaskResults());
        System.out.println(taskResult);
        // Check DC value has been updated
        dcValue = ieEditor.getDcForIE().getXMLPathValue(dcField);

        if (shouldUpdate) {
            assertEquals("DC updated " + dcField + " field", newValue, dcValue);
        } else {
            assertNotEquals("DC should not update " + dcField + " field", newValue, dcValue);
        }
    }

    // Not part of the JUNIT tests - Local manual testing purposes only
    public static void main(String[] args) {

    }
}
