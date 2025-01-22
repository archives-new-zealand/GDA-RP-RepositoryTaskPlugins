package nz.govt.natlib.ndha.plugin.repositorytask;

import com.exlibris.core.sdk.formatting.DublinCore;
import com.exlibris.digitool.common.dnx.DnxDocumentHelper;
import com.exlibris.digitool.exceptions.DigitoolException;
import com.exlibris.digitool.repository.api.IEEditor;

import java.util.List;
import java.util.Map;

/**
 * Mock implementation for IEEditor interface
 *
 * @author Mathachan.Kulathinal
 * @since 24.Mar.2016
 */

public class mockIEEditorImpl implements IEEditor {

    private String IEPid;
    private DublinCore dcObject;
    private DnxDocumentHelper dnxObject;

    public mockIEEditorImpl(String iepid) {
        this.IEPid = iepid;
    }

    public DublinCore getDc(String s) throws Exception {
        return null;
    }

    public DublinCore getDcForIE() throws Exception {
        return dcObject;
    }

    public DnxDocumentHelper getDnxHelper(String s) throws DigitoolException {
        return null;
    }

    public DnxDocumentHelper getDnxHelperForIE() throws DigitoolException {
        return dnxObject;
    }

    public List<String> getFilesForRep(String s) throws DigitoolException {
        return null;
    }

    public List<String> getReps() throws DigitoolException {
        return null;
    }

    public void setDC(DublinCore dublinCore, String s) {
    }

    public void setDnx(DnxDocumentHelper dnxDocumentHelper, String s) {
    }

    public void setDCForIE(DublinCore dublinCore) {
        dcObject = dublinCore;
    }

    public void setDnxForIE(DnxDocumentHelper dnxDocumentHelper) {
        dnxObject = dnxDocumentHelper;
    }

    public Map<String, DnxDocumentHelper> getChangedDnxs() {
        return null;
    }

    public Map<String, DublinCore> getChangedDcs() {
        return null;
    }

    public String getIEPid() {
        return IEPid;
    }

}
