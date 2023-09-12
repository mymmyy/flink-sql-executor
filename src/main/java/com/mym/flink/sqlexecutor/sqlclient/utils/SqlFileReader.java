package com.mym.flink.sqlexecutor.sqlclient.utils;

import com.mym.flink.sqlexecutor.sqlclient.bean.SqlGraphNode;
import com.mym.flink.sqlexecutor.sqlclient.bean.SqlStatement;
import com.mym.flink.sqlexecutor.sqlclient.exception.FlinkClientException;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.java.tuple.Tuple2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SqlFileReader {

    final static Logger LOGGER = LoggerFactory.getLogger(SqlFileReader.class);
    private final static String SQL_DELIMITER = ";";
    private final static String SQL_ANNOTATION_START = "--#";
    private final static String SQL_INNER_ANNOTATION_START = "--";
    private final static String SQL_ANNOTATION_DELIMITER = ",";
    private final static String SQL_ANNOTATION_KV_DELIMITER = "=";
    private final static String TABLE_NAME = "tablename";
    private final static String NODE_NAME = "nodename";
    private final static String ALIAS = "alias";
    private final static String DEPENDENT_Table = "dependenttable";
    private final static String systemFileSeparator = System.getProperty("file.separator");
    private final static String resourceSeparator = "/";

    private final static Pattern dependentTablePattern = Pattern.compile("<(.*?)>");

    public static void main(String[] args) throws Exception {
        Tuple2<LinkedList<SqlGraphNode>, LinkedList<SqlGraphNode>> ddldmlTuple2 = loadSql();
    }

    public static Tuple2<LinkedList<SqlGraphNode>, LinkedList<SqlGraphNode>> loadSql() throws Exception {
        return loadSql("sql", true);
    }

    public static synchronized Tuple2<LinkedList<SqlGraphNode>, LinkedList<SqlGraphNode>> loadSql(String baseDirName, boolean isResourcePath) throws Exception {
        LOGGER.info("start to load sql, scan base dir then foreach read sql file!");
        Tuple2<LinkedList<SqlGraphNode>, LinkedList<SqlGraphNode>> ddldmlTuple2 = new Tuple2<>();
        // f0 from ddl; f1 from dml
        ddldmlTuple2.f0 = new LinkedList<>();
        ddldmlTuple2.f1 = new LinkedList<>();
        String systemBasePath = systemFileSeparator + baseDirName;
        List<String> fileNameList = isResourcePath ? getResourceFiles(systemBasePath, false) : getPathFiles(systemBasePath, false);
        LOGGER.info("read file from baseDirName:{}, systemBasePath:{}, get fileNameList:{}", baseDirName, systemBasePath, fileNameList);

        LOGGER.info("read sql over, there is all read sql graph node info print start ---------------------");
        boolean isDDL = false;
        for (String fileName : fileNameList) {
            if(!fileName.endsWith(".sql")){
                LOGGER.warn("foreach read sql, fileName is not a .sql file, will skip it!");
                continue;
            }
            String path = isResourcePath ? resourceSeparator + baseDirName + resourceSeparator + fileName
                    : systemBasePath + systemFileSeparator + fileName;
            LOGGER.info("foreach read sql, file name:{}", path);
            LinkedList<SqlGraphNode> sqlGraphNodes = parseSqlFile(path, isResourcePath);
            if(fileName.toLowerCase(Locale.ROOT).startsWith("ddl_")){
                ddldmlTuple2.f0.addAll(sqlGraphNodes);
                isDDL = true;
            } else {
                ddldmlTuple2.f1.addAll(sqlGraphNodes);
                isDDL = false;
            }
            for (SqlGraphNode sqlGraphNode : sqlGraphNodes) {
                LOGGER.info("read sql over, sql graph node print, sql type:{}, sql content:{}", isDDL ? "ddl" : "dml", sqlGraphNode);
            }
        }
        LOGGER.info("read sql over, there is all read sql graph node info print end -----------------------");

        return ddldmlTuple2;
    }
    public static LinkedList<SqlGraphNode> parseSqlFile(String fileName, boolean isResourcePath) throws IOException {
//        fileName = "/sql/ddl_test.sql";
        LinkedHashMap<String, String> readResultMap = readSQLFile(fileName, isResourcePath);
        LinkedList<SqlGraphNode> resultList = new LinkedList<>();
        Set<String> tableNameDistinct = new HashSet<>();
        Set<String> nodeNameDistinct = new HashSet<>();
        RandomTokenGenerator randomTokenGenerator = new RandomTokenGenerator(3);

        readResultMap.forEach((sql, annotation) -> {
            SqlStatement sqlStatement = new SqlStatement(sql);
            String tableName = "";
            String nodeName = "";
            String alias = "";
            String dependentTableStr = "";

            // 解析注释
            String[] annotationArray = annotation.split(SQL_ANNOTATION_START);
            for (String annoStr : annotationArray) {
                String[] annoItemArray = annoStr.split(SQL_ANNOTATION_DELIMITER);
                for (String annoItem : annoItemArray) {
                    String annoItemTrim = annoItem.trim();
                    if(annoItemTrim.toLowerCase(Locale.ROOT).startsWith(TABLE_NAME)){
                        String[] split = annoItemTrim.split(SQL_ANNOTATION_KV_DELIMITER);
                        tableName = split.length > 1 ? split[1].trim() : tableName;
                    }
                    if(annoItemTrim.toLowerCase(Locale.ROOT).startsWith(NODE_NAME)){
                        String[] split = annoItemTrim.split(SQL_ANNOTATION_KV_DELIMITER);
                        nodeName = split.length > 1 ? split[1].trim() : nodeName;
                    }
                    if(annoItemTrim.toLowerCase(Locale.ROOT).startsWith(ALIAS)){
                        String[] split = annoItemTrim.split(SQL_ANNOTATION_KV_DELIMITER);
                        alias = split.length > 1 ? split[1].trim() : alias;
                    }
                    if(annoItemTrim.toLowerCase(Locale.ROOT).startsWith(DEPENDENT_Table)){
                        String[] split = annoItemTrim.split(SQL_ANNOTATION_KV_DELIMITER);
                        dependentTableStr = split.length > 1 ? split[1].trim() : dependentTableStr;
                    }
                }
            }
            sqlStatement.setAlias(alias);
            if(tableNameDistinct.contains(tableName)){
                throw new FlinkClientException("tableName重名！");
            }
            if(nodeNameDistinct.contains(nodeName)){
                throw new FlinkClientException("nodeName重名！");
            }

            nodeName = StringUtils.isBlank(nodeName) ? "node-" + randomTokenGenerator.generateToken() : nodeName;
            tableName = StringUtils.isBlank(tableName) ? "table-" + randomTokenGenerator.generateToken() : tableName;
            SqlGraphNode sqlGraphNode = new SqlGraphNode(nodeName, annotation, tableName, sqlStatement);
            if(StringUtils.isNotBlank(dependentTableStr)){
                sqlGraphNode.getDependentTable().addAll(extractDependentTable(dependentTableStr));
            }
            resultList.add(sqlGraphNode);
        });

        return resultList;
    }
    private static LinkedHashMap<String, String> readSQLFile(String fileName, boolean isResourcePath) throws IOException {
        String filePath = null;
        if(isResourcePath){
            URL resource = SqlFileReader.class.getResource(fileName);
            filePath = resource.getFile();
        } else {
            filePath = fileName;
        }

        LinkedHashMap<String, String> sqlStatements = new LinkedHashMap();
        StringBuilder annotationSB = new StringBuilder();
        StringBuilder sqlSB = new StringBuilder();

        BufferedReader br = null;
        FileReader fr = null;
        try {
            fr = new FileReader(filePath);
            br = new BufferedReader(fr);
            String line;
            int sqlAnnoStartIndex;
            while ((line = br.readLine()) != null) {
                if(line.startsWith(SQL_ANNOTATION_START)){
                    annotationSB.append(line);
                } else {
                    // 去除sql内以--开头的注释
                    sqlAnnoStartIndex = line.indexOf(SQL_INNER_ANNOTATION_START);
                    if(sqlAnnoStartIndex >= 0){
                        sqlSB.append(line.substring(0, sqlAnnoStartIndex));
                    } else {
                        sqlSB.append(line);
                    }
                    sqlSB.append(" ");
                }

                // 当我们遇到一个分号，我们知道 SQL 语句结束了
                if (line.endsWith(SQL_DELIMITER) && !line.contains(SQL_INNER_ANNOTATION_START)) {
                    sqlStatements.put(sqlSB.toString(), annotationSB.toString());
                    sqlSB = new StringBuilder();
                    annotationSB = new StringBuilder();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(br != null){br.close();}
            if(fr != null){fr.close();}
        }

        return sqlStatements;
    }

    private static List<String> extractAnnotation(String input) {
        List<String> comments = new ArrayList<>();

        // 使用非贪婪的正则表达式来匹配 /* ... */
        Pattern pattern = Pattern.compile("/\\*(.*?)\\*/", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(input);

        while (matcher.find()) {
            comments.add(matcher.group(1).trim());
        }

        return comments;
    }

    private static List<String> getResourceFiles(String path, boolean getAbsolutePath) throws URISyntaxException, IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        List<String> filenames = new ArrayList<>();

        Enumeration<URL> resources = classLoader.getResources(path);

        while (resources.hasMoreElements()) {
            URL resourceUrl = resources.nextElement();
            Path resourcePath = Paths.get(resourceUrl.toURI());
            Files.walk(resourcePath).filter(Files::isRegularFile).forEach(file -> filenames.add(getAbsolutePath ? file.toAbsolutePath().toString() : file.getFileName().toString()));
        }

        return filenames;
    }

    private static List<String> getPathFiles(String path, boolean getAbsolutePath) throws URISyntaxException, IOException {
        // 创建File对象
        File directory = new File(path);
        if(!directory.exists()){
            return new ArrayList<>();
        }
        // 列出目录中的所有文件和子目录
        File[] filesList = directory.listFiles();
        List<String> fileName = new ArrayList<>();
        if (filesList != null) {
            for (File file : filesList) {
                if (file.isFile()) {
                    if(getAbsolutePath){
                        fileName.add(file.getAbsolutePath());
                    } else {
                        fileName.add(file.getName());
                    }
                }
            }
        } else {
            System.out.println("The specified path is not a valid directory or an I/O error occurred. path: " + path);
        }
        return fileName;
    }

    private static List<String> extractDependentTable(String input) {
        // 使用正则表达式匹配 <...> 形式的内容
        Pattern pattern = Pattern.compile("<(.*?)>");
        Matcher matcher = pattern.matcher(input);

        List<String> results = new ArrayList<>();
        while (matcher.find()) {
            results.add(matcher.group(1));
        }
        return results;
    }

}
