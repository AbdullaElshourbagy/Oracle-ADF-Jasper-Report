package enoc.beans.common;


import enoc.beans.helpers.Constants;
import enoc.beans.utils.JSFUtils;

import java.io.File;

import javax.servlet.ServletContext;


public class ApplicationBean {

    private final String REAL_PATH;
    private static String OS = System.getProperty(Constants.OS).toLowerCase();

    public ApplicationBean() {
        super();
        ServletContext ext = (ServletContext)JSFUtils.getFacesContext().getExternalContext().getContext();
        String tempPath = ext.getRealPath("/");
        String realPath = null;
        if (isWindows())
            realPath = tempPath;
        if (isUnix())
            realPath = tempPath + File.separator;
        REAL_PATH = realPath;

    }


    public String getREAL_PATH() {
        return REAL_PATH;
    }

    public static boolean isWindows() {
        return (OS.indexOf(Constants.WINDOWS) >= 0);
    }

    public static boolean isUnix() {
        return (OS.indexOf(Constants.UNIX_NIX) >= 0 || OS.indexOf(Constants.UNIX_NUX) >= 0 || OS.indexOf(Constants.UNIX_AIX) > 0);
    }


}
