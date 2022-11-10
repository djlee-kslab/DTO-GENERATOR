package com.example.demo;

import com.google.common.base.CaseFormat;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringBootApplication
public class DtoAndDictionaryGenerator {

    //request
    private static final List<String> requestAnnotations = new ArrayList<>();
    private static final List<String> requestMembers = new ArrayList<>();
    private static final List<String> requestFields = new ArrayList<>();
    private static final List<String> requestColumns = new ArrayList<>();

    //response
    private static final List<String> responseAnnotations = new ArrayList<>();
    private static final List<String> responseMembers = new ArrayList<>();
    private static final List<String> responseFields = new ArrayList<>();
    private static final List<String> responseColumns = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        BufferedReader requestReader;
        File requestMemberOutput = new File("out/request/members");
        File requestFieldOutput = new File("out/request/fields");
        File requestColumnOutput = new File("out/request/columns");
        FileOutputStream requestMemberOs = new FileOutputStream(requestMemberOutput);
        FileOutputStream requestFieldOs = new FileOutputStream(requestFieldOutput);
        FileOutputStream requestColumnOs = new FileOutputStream(requestColumnOutput);
        BufferedWriter requestMemberWriter = new BufferedWriter(new OutputStreamWriter(requestMemberOs));
        BufferedWriter requestFieldWriter = new BufferedWriter(new OutputStreamWriter(requestFieldOs));
        BufferedWriter requestColumnWriter = new BufferedWriter(new OutputStreamWriter(requestColumnOs));

        BufferedReader responseReader;
        File responseMemberOutput = new File("out/response/members");
        File responseFieldOutput = new File("out/response/fields");
        File responseColumnOutput = new File("out/response/columns");
        FileOutputStream responseMemberOs = new FileOutputStream(responseMemberOutput);
        FileOutputStream responseFieldOs = new FileOutputStream(responseFieldOutput);
        FileOutputStream responseColumnOs = new FileOutputStream(responseColumnOutput);
        BufferedWriter responseMemberWriter = new BufferedWriter(new OutputStreamWriter(responseMemberOs));
        BufferedWriter responseFieldWriter = new BufferedWriter(new OutputStreamWriter(responseFieldOs));
        BufferedWriter responseColumnWriter = new BufferedWriter(new OutputStreamWriter(responseColumnOs));

        try {
            if (args.length != 0) {
                requestReader = new BufferedReader(new FileReader(args[0]));
                responseReader = new BufferedReader(new FileReader(args[1]));
            } else {
                requestReader = new BufferedReader(new FileReader("in/request"));
                responseReader = new BufferedReader(new FileReader("in/response"));
            }

            // request
            String requestByLine = requestReader.readLine();
            while (requestByLine != null) {
                if (requestByLine.stripLeading().startsWith("<Column")) {
                    Pattern pattern = Pattern.compile("Column id=\"(.*?)\"");
                    Matcher matcher = pattern.matcher(requestByLine);
                    if (matcher.find())
                        requestByLine = matcher.group(1);
                }

                buildRequestAnnotations(requestByLine);
                buildRequestMembers(requestByLine);
                buildRequestFields(requestByLine);
                buildRequestColumns(requestByLine);
                requestByLine = requestReader.readLine();
            }

            // response
            String responseByLine = responseReader.readLine();
            while (responseByLine != null) {
                if (responseByLine.stripLeading().startsWith("<Column")) {
                    Pattern pattern = Pattern.compile("Column id=\"(.*?)\"");
                    Matcher matcher = pattern.matcher(responseByLine);
                    if (matcher.find())
                        responseByLine = matcher.group(1);
                }

                buildResponseAnnotations(responseByLine);
                buildResponseMembers(responseByLine);
                buildResponseFields(responseByLine);
                buildResponseColumns(responseByLine);
                responseByLine = responseReader.readLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (int i = 0; i < requestAnnotations.size(); i++) {
            // write member
            requestMemberWriter.write(requestAnnotations.get(i));
            requestMemberWriter.newLine();
            requestMemberWriter.write(requestMembers.get(i));
            requestMemberWriter.newLine();
            requestMemberWriter.newLine();

            // write field
            requestFieldWriter.write(requestFields.get(i));
            requestFieldWriter.newLine();
            requestFieldWriter.newLine();

            // write columns
            requestColumnWriter.write(requestColumns.get(i));
            requestColumnWriter.newLine();
        }

        for (int i = 0; i < responseAnnotations.size(); i++) {
            // write member
            responseMemberWriter.write(responseAnnotations.get(i));
            responseMemberWriter.newLine();
            responseMemberWriter.write(responseMembers.get(i));
            responseMemberWriter.newLine();
            responseMemberWriter.newLine();

            // write field
            responseFieldWriter.write(responseFields.get(i));
            responseFieldWriter.newLine();
            responseFieldWriter.newLine();

            // write columns
            responseColumnWriter.write(responseColumns.get(i));
            responseColumnWriter.newLine();
        }

        requestMemberWriter.close();
        requestFieldWriter.close();
        requestColumnWriter.close();

        responseMemberWriter.close();
        responseFieldWriter.close();
        responseColumnWriter.close();

        System.out.println("SUCCESS");
    }

    private static void buildRequestAnnotations(String line) {
        if (line.startsWith("_")) {
            line = line.substring(1);
        }
        requestAnnotations.add("@JsonProperty(LongtermXmlFieldName." + line + ")");
    }

    private static void buildRequestFields(String line) {
        String line2 = line;
        if (line.startsWith("_")) {
            line2 = line.substring(1);
        }
        requestFields.add("public static final String " + line2 + " = \"" + line + "\";");
    }

    private static void buildRequestMembers(String line) {
        if (line.startsWith("_")) {
            line = line.substring(1);
        }
        if (line.endsWith("_YN")) {
            line = "IS_" + line.substring(0, line.length() - 3);
            requestMembers.add("private boolean " + CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, line) + ";");
        } else {
            requestMembers.add("private String " + CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, line) + ";");
        }
    }

    private static void buildRequestColumns(String line) {
        if (line.startsWith("_")) {
            line = line.substring(1);
        }
        requestColumns.add("LongtermXmlFieldName." + line + ",");
    }


    private static void buildResponseAnnotations(String line) {
        if (line.startsWith("_")) {
            line = line.substring(1);
        }
        responseAnnotations.add("@JsonProperty(LongtermXmlFieldName." + line + ")");
    }

    private static void buildResponseFields(String line) {
        String line2 = line;
        if (line.startsWith("_")) {
            line2 = line.substring(1);
        }
        responseFields.add("public static final String " + line2 + " = \"" + line + "\";");
    }

    private static void buildResponseMembers(String line) {
        if (line.startsWith("_")) {
            line = line.substring(1);
        }
        if (line.endsWith("_YN")) {
            line = "IS_" + line.substring(0, line.length() - 3);
            responseMembers.add("private Boolean " + CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, line) + ";");
        } else {
            responseMembers.add("private String " + CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, line) + ";");
        }
    }

    private static void buildResponseColumns(String line) {
        if (line.startsWith("_")) {
            line = line.substring(1);
        }
        responseColumns.add("LongtermXmlFieldName." + line + ",");
    }

}