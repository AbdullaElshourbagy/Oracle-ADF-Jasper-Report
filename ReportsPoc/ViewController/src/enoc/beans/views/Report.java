package enoc.beans.views;

import net.sf.jasperreports.engine.*;


import javax.faces.context.FacesContext;

import javax.servlet.http.HttpServletResponse;

import java.io.InputStream;
import java.io.OutputStream;

import java.sql.Connection;
import java.sql.DriverManager;

import java.util.HashMap;
import java.util.Map;

import enoc.beans.supers.ReportGenerator;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.HashMap;

import javax.faces.component.UIViewRoot;
import javax.faces.event.ActionEvent;

import oracle.adf.view.rich.component.rich.RichPopup;
import oracle.adf.view.rich.component.rich.input.RichInputText;
import oracle.adf.view.rich.component.rich.nav.RichCommandButton;


public class Report extends ReportGenerator {
    private RichPopup reportPopup;

    public Report() {
        super("test", "");
    }

    public String getCurrentDate() {
        return new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss").format(new Date());

    }

    @Override
    public void generateParamsMap(HashMap parametersMap) {
        parametersMap.put("dept_id", 100);
    }


    public void generateReportPdf(ActionEvent actionEvent) {
        reportName = "test";
        try {
            constructReport();
            printReport(actionEvent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void generateReportAsPopupPdf(ActionEvent actionEvent) {
        reportName = "test";
        try {
            constructReport();
            printReportAsPopup(actionEvent);
            showReportPopup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void downloadReportPdf(FacesContext facesContext, OutputStream outputStream) {
        reportName = "test";
        try {
            constructReport();
            downloadReport(outputStream, "cb2pdf");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void downloadReportXlsx(FacesContext facesContext, OutputStream outputStream) {
        reportName = "testXlsx";
        try {
            constructReport();
            downloadReport(outputStream, "cb1xlsx");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void downloadReportDocx(FacesContext facesContext, OutputStream outputStream) {
        reportName = "testDocx";
        try {
            constructReport();
            downloadReport(outputStream, "cb4docx");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    public void showReportPopup() {
        if (reportPopup != null) {
            RichPopup.PopupHints hints = new RichPopup.PopupHints();
            reportPopup.show(hints);
        }
    }

    public void setReportPopup(RichPopup reportPopup) {
        this.reportPopup = reportPopup;
    }

    public RichPopup getReportPopup() {
        return reportPopup;
    }


}
