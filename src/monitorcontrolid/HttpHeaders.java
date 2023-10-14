/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package monitorcontrolid;


import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author Emerson
 */
public abstract class HttpHeaders {

    public static Map<String, String> newEmptyHeaders() {
        Map<String, String> headers = new HashMap<>();
        return headers;
    }

    public static void addHeader(Map<String, String> headers, Entry<String, String> header) {
        if (headers == null || header == null) {
            return;
        }

        headers.put(header.getKey(), header.getValue());
    }

    public static Entry<String, String> createContentTypeHeader(String headerValue) {
        Entry<String, String> header = createHeader("Content-Type", headerValue);

        return header;
    }

    @SuppressWarnings("unchecked")
    public static Entry<String, String> createHeader(String headerName, String headerValue) {
        @SuppressWarnings("unchecked")
        SimpleEntry header = new SimpleEntry(headerName, headerValue);

        return header;
    }
}
