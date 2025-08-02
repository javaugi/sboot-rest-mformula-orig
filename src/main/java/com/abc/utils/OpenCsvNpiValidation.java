/*
 * Copyright (C) 2021 Center for Information Management, Inc.
 *
 * This program is proprietary.
 * Redistribution without permission is strictly prohibited.
 * For more information, contact <http://www.ciminc.com>
 */
package com.abc.utils;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import javax.net.ssl.SSLContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.apache.hc.core5.ssl.SSLContexts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 *
 * @author david
 * @version $LastChangedRevision $LastChangedDate Last Modified Author:
 * $LastChangedBy
 */
public class OpenCsvNpiValidation {

    private static final Logger log = LoggerFactory.getLogger(OpenCsvNpiValidation.class);
    private static final String ERROR_GET_NPI = "Error finding National Provider ID {}";
    private static final String MSG_INVALID_NPI = "NPI %s was Not found on the NPI Registry";
    private static final String MSG_INVALID_NPI_ERROR = "Invalid NPI %s: %s";
    private static final String MSG_INVALID_NPI_ERROR_UNKNOWN = "Invalid NPI %s: Error unknown";
    private static final String MSG_GET_NPI_STATUS = "status code {} fullUrl {}";
    private static final String MSG_GET_NPI_COUNT = "result_count {} status line {}";

    private static final List<String> VALID_NPI_NOT_IN_REGISTRY = Arrays.asList(new String[]{"1999999984", "1999999992", "1999999976"});
    private static final List<String> DEACTIVATED_NPI_VALUES = Arrays.asList(new String[]{"1063810489", "1073806253", "1225085459", "1457375990",
        "1457383622", "1669463816", "1669577821"});
    //{"Errors": [{"field": "number", "number": "15", "description": "CMS deactivated NPI 1669463816. The provider can no longer use this NPI. Our public registry does not display provider information about NPIs that are not in service."}]}
    private static final String NPI_REGISTRY_URL = "https://npiregistry.cms.hhs.gov/api/?version=2.0";

    public static void main(String[] args) throws IOException, CsvException {

        validateNPIWithRegistry("1316139314");
        String fileName = "/Prod_NPI.csv";
        //new OpenCsvNpiValidation().validateNpiFile(fileName);
    }

    public static boolean validateNPIWithRegistry(String npiEntry, BufferedWriter writer) {

        CloseableHttpResponse response = null;
        try {
            CloseableHttpClient httpclient = HttpClients.createDefault();
            String fullUrl = NPI_REGISTRY_URL + "&number=" + npiEntry;
            HttpGet httpGet = new HttpGet(fullUrl);
            response = httpclient.execute(httpGet);
            log.info(MSG_GET_NPI_STATUS, response.getStatusLine().getStatusCode(), fullUrl);
            if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
                HttpEntity entity = response.getEntity();
                String returnValue = EntityUtils.toString(entity);
                log.info("return value {}", returnValue);
                if (returnValue.contains("result_count")) {
                    JSONObject jsonValue = new JSONObject(returnValue);
                    int count = jsonValue.getInt("result_count");
                    log.info(MSG_GET_NPI_COUNT, count, response.getStatusLine());
                    if (count > 0) {
                        return true;
                    } else {
                        writeMessage(writer, String.format(MSG_INVALID_NPI, npiEntry));
                        return false;
                    }
                } else {
                    if (returnValue.contains("Errors")) {
                        JSONObject jsonValue = new JSONObject(returnValue);
                        log.error(MSG_INVALID_NPI_ERROR, npiEntry, jsonValue.get("Errors"));
                        writeMessage(writer, String.format(MSG_INVALID_NPI_ERROR, npiEntry, jsonValue.get("Errors")));
                    } else {
                        writeMessage(writer, String.format(MSG_INVALID_NPI_ERROR_UNKNOWN, npiEntry));
                    }

                    return false;
                }
            }
        } catch (IOException | ParseException | JSONException ex) {
            log.error(ERROR_GET_NPI, npiEntry, ex);
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException ex) {
                }
            }
            if (writer != null) {
                try {
                    writer.flush();
                    writer.close();
                    writer = null;
                } catch (IOException ex) {
                }
            }
        }

        return false;
    }

    public static boolean validateNPI(String npiEntry, BufferedWriter writer) {

        CloseableHttpResponse response = null;
        try {
            String fullUrl = NPI_REGISTRY_URL + "&number=" + npiEntry;
            response = callNPI(fullUrl);
            StatusLine statusLine = response.getStatusLine();
            log.info(MSG_GET_NPI_STATUS, statusLine.getStatusCode(), fullUrl);
            if (HttpStatus.SC_OK == statusLine.getStatusCode()) {
                HttpEntity entity = response.getEntity();
                String returnValue = EntityUtils.toString(entity);
                log.info("return value {}", returnValue);
                JSONObject jsonValue = new JSONObject(returnValue);

                if (jsonValue.get("result_count") != null) {
                    int count = jsonValue.getInt("result_count");
                    log.info(MSG_GET_NPI_COUNT, count, statusLine);
                    if (count > 0) {
                        return true;
                    } else {
                        writeMessage(writer, String.format(MSG_INVALID_NPI, npiEntry));
                        return false;
                    }
                } else {
                    if (jsonValue.get("Errors") != null) {
                        log.error(MSG_INVALID_NPI_ERROR, npiEntry, jsonValue.get("Errors"));
                        writeMessage(writer, String.format(MSG_INVALID_NPI_ERROR, npiEntry, jsonValue.get("Errors")));
                    } else {
                        writeMessage(writer, String.format(MSG_INVALID_NPI_ERROR_UNKNOWN, npiEntry));
                    }

                    return false;
                }
            }
        } catch (IOException | ParseException | JSONException ex) {
            log.error(ERROR_GET_NPI, npiEntry, ex);
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException ex) {
                }
            }
            if (writer != null) {
                try {
                    writer.flush();
                    writer.close();
                    writer = null;
                } catch (IOException ex) {
                }
            }
        }

        return false;
    }

    public static CloseableHttpResponse callNPI(String fullUrl) {

        try {
            //Step 1 - Create SSLContextBuilder object
            SSLContextBuilder sSLBuilder = SSLContexts.custom();

            //Step 2 - Load the Keystore
            File file = new File("mykeystore.jks");
            sSLBuilder = sSLBuilder.loadTrustMaterial(file, "changeit".toCharArray());

            //Step 3 - build an SSLContext object
            SSLContext sslContext = sSLBuilder.build();

            //Step 4 - Creating SSLConnectionSocketFactory object
            ////Creating SSLConnectionSocketFactory SSLConnectionSocketFactory object
            SSLConnectionSocketFactory sslConSocFactory = new SSLConnectionSocketFactory(sslContext,
                    new String[]{"TLSv1"}, null, SSLConnectionSocketFactory.getDefaultHostnameVerifier());

            //Step 5 - Create an HttpClientBuilder object
            HttpClientBuilder clientbuilder = HttpClients.custom();

            //Step 6 - Set the SSLConnectionSocketFactory object
            clientbuilder = clientbuilder.setSSLSocketFactory(sslConSocFactory);

            //Step 7 - Build the CloseableHttpClient object
            CloseableHttpClient httpClient = clientbuilder.build();

            //Step 8 - Create an HttpGet object
            HttpGet httpGet = new HttpGet(fullUrl);

            //Step 9 - Execute the request
            httpClient.execute(httpGet, response -> {
                return response;
            });
        } catch (Exception ex) {
        }

        return null;
    }

    private static void writeMessage(BufferedWriter writer, String message) {
        try {
            if (writer == null) {
                return;
            }
            writer.write(message);
            writer.newLine();
        } catch (IOException e) {
        }
    }

    public static boolean validateNPIWithRegistry(String npiEntry) {
        return validateNPIWithRegistry(npiEntry, null);
    }

    private void validateNpiFile(String filename) {
        BufferedWriter writer = null;
        try {
            File file = ProjectResourceAccess.getResourceFile(filename);
            File outputFile = ProjectResourceAccess.createResourceFile("output.txt");
            writer = new BufferedWriter(new FileWriter(outputFile));

            try (CSVReader reader = new CSVReader(new FileReader(file))) {
                List<String[]> r = reader.readAll();
                //r.forEach(x -> System.out.println(Arrays.toString(x)));
                for (String[] arr : r) {
                    for (String npi : arr) {
                        log.info("{}", npi);
                        if (StringUtils.isBlank(npi) || npi.trim().length() != 10
                                //|| DEACTIVATED_NPI_VALUES.contains(npi.trim())
                                || VALID_NPI_NOT_IN_REGISTRY.contains(npi.trim())) {
                            continue;
                        }
                        validateNPIWithRegistry(npi.trim(), writer);
                        /*
                        boolean valid = validateNPIWithRegistry(npi.trim(), writer);
                        if (!valid) {
                            log.info("{}", String.format(MSG_INVALID_NPI, npi));
                        }
                        // */
                    }
                }
            }
        } catch (CsvException | IOException e) {
            log.error("Error validateNpiFile {}", filename, e);
        } finally {
            try {
                if (writer != null) {
                    writer.flush();
                    writer.close();
                    writer = null;
                }
            } catch (Exception e) {

            }
        }
    }

}

/* HttpStatus
    public static final int SC_CONTINUE = 100;
    public static final int SC_SWITCHING_PROTOCOLS = 101;
    public static final int SC_PROCESSING = 102;
    public static final int SC_OK = 200;
    public static final int SC_CREATED = 201;
    public static final int SC_ACCEPTED = 202;
    public static final int SC_NON_AUTHORITATIVE_INFORMATION = 203;
    public static final int SC_NO_CONTENT = 204;
    public static final int SC_RESET_CONTENT = 205;
    public static final int SC_PARTIAL_CONTENT = 206;
    public static final int SC_MULTI_STATUS = 207;
    public static final int SC_MULTIPLE_CHOICES = 300;
    public static final int SC_MOVED_PERMANENTLY = 301;
    public static final int SC_MOVED_TEMPORARILY = 302;
    public static final int SC_SEE_OTHER = 303;
    public static final int SC_NOT_MODIFIED = 304;
    public static final int SC_USE_PROXY = 305;
    public static final int SC_TEMPORARY_REDIRECT = 307;
    public static final int SC_BAD_REQUEST = 400;
    public static final int SC_UNAUTHORIZED = 401;
    public static final int SC_PAYMENT_REQUIRED = 402;
    public static final int SC_FORBIDDEN = 403;
    public static final int SC_NOT_FOUND = 404;
    public static final int SC_METHOD_NOT_ALLOWED = 405;
    public static final int SC_NOT_ACCEPTABLE = 406;
    public static final int SC_PROXY_AUTHENTICATION_REQUIRED = 407;
    public static final int SC_REQUEST_TIMEOUT = 408;
    public static final int SC_CONFLICT = 409;
    public static final int SC_GONE = 410;
    public static final int SC_LENGTH_REQUIRED = 411;
    public static final int SC_PRECONDITION_FAILED = 412;
    public static final int SC_REQUEST_TOO_LONG = 413;
    public static final int SC_REQUEST_URI_TOO_LONG = 414;
    public static final int SC_UNSUPPORTED_MEDIA_TYPE = 415;
    public static final int SC_REQUESTED_RANGE_NOT_SATISFIABLE = 416;
    public static final int SC_EXPECTATION_FAILED = 417;
    public static final int SC_INSUFFICIENT_SPACE_ON_RESOURCE = 419;
    public static final int SC_METHOD_FAILURE = 420;
    public static final int SC_UNPROCESSABLE_ENTITY = 422;
    public static final int SC_LOCKED = 423;
    public static final int SC_FAILED_DEPENDENCY = 424;
    public static final int SC_TOO_MANY_REQUESTS = 429;
    public static final int SC_INTERNAL_SERVER_ERROR = 500;
    public static final int SC_NOT_IMPLEMENTED = 501;
    public static final int SC_BAD_GATEWAY = 502;
    public static final int SC_SERVICE_UNAVAILABLE = 503;
    public static final int SC_GATEWAY_TIMEOUT = 504;
    public static final int SC_HTTP_VERSION_NOT_SUPPORTED = 505;
    public static final int SC_INSUFFICIENT_STORAGE = 507;

 */
