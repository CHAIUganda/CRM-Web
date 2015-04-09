package com.omnitech.chai.util

import fuzzycsv.FuzzyCSVWriter
import org.springframework.util.Assert

import javax.servlet.http.HttpServletResponse

/**
 * Created with IntelliJ IDEA.
 * User: kay
 * Date: 8/1/13
 * Time: 2:50 PM
 * To change this template use File | Settings | File Templates.
 */
class ServletUtil {
    static FileNameMap fileNameMap
    static {
        fileNameMap = URLConnection.getFileNameMap();
    }

    public static void setAttachment(HttpServletResponse resp, String attachmentName) {
        resp.setHeader("Cache-Control", "no-cache");
        resp.setHeader("Pragma", "no-cache");
        resp.setDateHeader("Expires", -1);
        resp.setHeader("Cache-Control", "no-store");
        resp.setHeader("Content-Type", getMimeType(attachmentName));
        resp.setHeader("Content-Disposition", "attachment; filename=${attachmentName}");
    }

    public static String getMimeType(String fileUrl) {
        String type = fileNameMap.getContentTypeFor(fileUrl);
        if (type)
            return type;
        return "application/binary"
    }


    static void exportCSV(HttpServletResponse response, String fileName, List waterpoints) {
        setAttachment(response, fileName)
        def cSVWriter = new FuzzyCSVWriter(response.writer)
        cSVWriter.writeAll(waterpoints)
    }

    static List<String> extractList(Map params, String name, String msg = null) {
        Assert.notNull params[name], (msg ?: "$name should not be empty")
        return params[name] instanceof String ? [params[name]] : params[name]

    }
}
