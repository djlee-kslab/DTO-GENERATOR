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

    private static final List<String> annotations = new ArrayList<>();
    private static final List<String> members = new ArrayList<>();
    private static final List<String> fields = new ArrayList<>();
    private static final List<String> columns = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        BufferedReader reader;
        File memberOutput = new File("out/members");
        File fieldOutput = new File("out/fields");
        File columnOutput = new File("out/columns");
        FileOutputStream memberOs = new FileOutputStream(memberOutput);
        FileOutputStream fieldOs = new FileOutputStream(fieldOutput);
        FileOutputStream columnOs = new FileOutputStream(columnOutput);
        BufferedWriter memberWriter = new BufferedWriter(new OutputStreamWriter(memberOs));
        BufferedWriter fieldWriter = new BufferedWriter(new OutputStreamWriter(fieldOs));
        BufferedWriter columnWriter = new BufferedWriter(new OutputStreamWriter(columnOs));

        try {
            if (args.length != 0) {
                reader = new BufferedReader(new FileReader(args[0]));
            } else {
                reader = new BufferedReader(new FileReader("resource"));
            }
            String line = reader.readLine();
            while (line != null) {
                if (line.stripLeading().startsWith("<Column")) {
                    Pattern pattern = Pattern.compile("Column id=\"(.*?)\"");
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.find())
                        line = matcher.group(1);
                }

                buildAnnotations(line);
                buildMembers(line);
                buildFields(line);
                buildColumns(line);
                line = reader.readLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (int i = 0; i < annotations.size(); i++) {
            // write member
            memberWriter.write(annotations.get(i));
            memberWriter.newLine();
            memberWriter.write(members.get(i));
            memberWriter.newLine();
            memberWriter.newLine();

            // write field
            fieldWriter.write(fields.get(i));
            fieldWriter.newLine();
            fieldWriter.newLine();

            // write columns
            columnWriter.write(columns.get(i));
            columnWriter.newLine();
        }

        memberWriter.close();
        fieldWriter.close();
        columnWriter.close();
        System.out.println("SUCCESS");
    }

    private static void buildFields(String line) {
        String line2 = line;
        if (line.startsWith("_")) {
            line2 = line.substring(1);
        }
        fields.add("public static final String " + line2 + " = \"" + line + "\";");
    }

    private static void buildAnnotations(String line) {
        if (line.startsWith("_")) {
            line = line.substring(1);
        }
        annotations.add("@JsonProperty(LongtermXmlFieldName." + line + ")");
    }

    private static void buildMembers(String line) {
        if (line.startsWith("_")) {
            line = line.substring(1);
        }
        if (line.endsWith("_YN")) {
            line = "IS_" + line.substring(0, line.length() - 3);
            members.add("private boolean " + CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, line) + ";");
        } else {
            members.add("private String " + CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, line) + ";");
        }
    }

    private static void buildColumns(String line) {
        if (line.startsWith("_")) {
            line = line.substring(1);
        }
        columns.add("LongtermXmlFieldName." + line + ",");
    }
}

