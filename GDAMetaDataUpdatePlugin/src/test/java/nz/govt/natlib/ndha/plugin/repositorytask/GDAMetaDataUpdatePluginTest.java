package nz.govt.natlib.ndha.plugin.repositorytask;

import com.exlibris.core.sdk.formatting.DublinCore;
import com.exlibris.core.sdk.formatting.DublinCoreFactory;
import com.exlibris.core.sdk.repository.TaskResults;

import static org.junit.Assert.assertEquals;

import com.exlibris.digitool.common.dnx.DnxDocumentFactory;
import com.exlibris.digitool.common.dnx.DnxDocumentHelper;
import com.exlibris.digitool.common.dnx.DnxDocumentHelper.ObjectIdentifier;
import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import org.junit.*;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class GDAMetaDataUpdatePluginTest {

    private static final Logger log = Logger.getLogger(GDAMetaDataUpdatePluginTest.class);
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
    public void executeTest() throws FileNotFoundException, DocumentException, Exception {
        testGDAMUPlugin("IE12345678", "R123456789", "R12345678", "dc:identifier xsi:type=\"archwayUniqueId\"");
        testGDAMUPlugin("IE23456789", "R100456789", "R456789", "dc:identifier xsi:type=\"dcterms:archwayUniqueId\"");
    }

    public void testGDAMUPlugin(String IE_PID, String oldRNumber, String newRNumber, String dcField) throws Exception {
        GDAMetaDataUpdatePlugin GDAMUPlugin = new GDAMetaDataUpdatePlugin();
        mockIEEditorImpl ieEditor = new mockIEEditorImpl(IE_PID);

        // Use delimiter \A=The beginning of the input
        // https://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html
        String dc_str = new Scanner(new File("./src/test/resources/" + IE_PID + "_dc.xml")).useDelimiter("\\A").next();
        DublinCore dcObj = DublinCoreFactory.getInstance().createDocument(dc_str);

        String dnx_str = new Scanner(new File("./src/test/resources/" + IE_PID + "_dnx.xml")).useDelimiter("\\A").next();
        DnxDocumentHelper ieDNX = new DnxDocumentHelper(DnxDocumentFactory.getInstance().parse(dnx_str));

        ieEditor.setDCForIE(dcObj);
        ieEditor.setDnxForIE(ieDNX);

        // TEST DNX UPDATE
        initParams = new HashMap<String, String>();
        initParams.put("infileNameAndPath","./src/test/resources/RNumbers.csv");

        // Check original RNumber matches
        List<ObjectIdentifier> objIdentifierList1 = ieDNX.getObjectIdentifiers();
        for (ObjectIdentifier objIdentifier : objIdentifierList1) {
            if (objIdentifier.getObjectIdentifierType().equalsIgnoreCase("ArchwayUniqueID")) {
                assertEquals("DNX initial ArchwayUniqueID", oldRNumber, objIdentifier.getObjectIdentifierValue());
            }
        }

        String dcValue = ieEditor.getDcForIE().getXMLPathValue(dcField);
        assertEquals("DC initial ArchwayUniqueID", oldRNumber, dcValue);
        TaskResults taskResult = GDAMUPlugin.execute(ieEditor, initParams, new TaskResults());

        System.out.println(taskResult);

        // Check RNumber has been updated
        for (ObjectIdentifier objIdentifier : objIdentifierList1) {
            if (objIdentifier.getObjectIdentifierType().equalsIgnoreCase("ArchwayUniqueID")) {
                assertEquals("DNX updated ArchwayUniqueID", newRNumber, objIdentifier.getObjectIdentifierValue());
            }
        }

        // Check DC value has been updated
        dcValue = ieEditor.getDcForIE().getXMLPathValue(dcField);
        assertEquals("DC updated ArchwayUniqueID", newRNumber, dcValue);
    }

    // Not part of the JUNIT tests - Local manual testing purposes only
    public static void main(String[] args) {

    }
}
