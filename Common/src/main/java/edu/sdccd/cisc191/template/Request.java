package edu.sdccd.cisc191.template;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Represents a customer request within the system. Each request includes
 * a request type, an associated ID, and optional attributes for modification.
 *
 *  This class supports serialization to and deserialization from JSON format
 * to enable easy data exchange.
 *
 * @author Andy Ly, Andrew Huang
 */
public class Request implements Serializable {

    private Map<String, Object> attributesToModify;
    private Integer id;
    private String requestType;

    @JsonIgnore
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Serializes a  CustomerRequest  object into a JSON string.
     *
     * @param customer The  CustomerRequest  object to serialize.
     * @return A JSON string representation of the  CustomerRequest .
     * @throws Exception If serialization fails.
     */
    public static String toJSON(Request customer) throws Exception {
        return objectMapper.writeValueAsString(customer);
    }

    /**
     * Deserializes a JSON string into a  CustomerRequest  object.
     *
     * @param input The JSON string to deserialize.
     * @return A  CustomerRequest  object created from the JSON string.
     * @throws Exception If deserialization fails.
     */
    public static Request fromJSON(String input) throws Exception {
        return objectMapper.readValue(input, Request.class);
    }

    /**
     * Default constructor for  CustomerRequest .
     * Required for JSON serialization/deserialization.
     */
    protected Request() {
        // Default constructor for deserialization purposes
    }

    /**
     * Creates a  CustomerRequest  with a specified request type and ID.
     *
     * @param requestType The type of the request.
     * @param id The ID associated with the request.
     */
    public Request(String requestType, Integer id) {
        this.requestType = requestType;
        this.id = id;
        this.attributesToModify = new HashMap<>();
    }

    /**
     * Creates a CustomerRequest with a specified request type, ID, and
     * attributes to modify.
     *
     * @param requestType The type of the request.
     * @param id The ID associated with the request.
     * @param attributesToModify A map of attributes to be modified.
     */
    public Request(String requestType, int id, Map<String, Object> attributesToModify) {
        this.requestType = requestType;
        this.id = id;
        this.attributesToModify = attributesToModify;
    }

    /**
     * Retrieves the attributes to be modified for this request.
     *
     * @return A map containing the attributes to modify.
     */
    public Map<String, Object> getAttributesToModify() {
        return attributesToModify;
    }

    /**
     * Converts the  CustomerRequest  object into a string representation.
     *
     * @return A string containing the request type and ID.
     */
    @Override
    public String toString() {
        return String.format(
                """
                Request Type: [type=%s]
                RequestID: [id=%d]
                """, requestType, id);
    }

    /**
     * Retrieves the ID associated with this request.
     *
     * @return The request ID.
     */
    public Integer getId() {
        return id;
    }

    /**
     * Retrieves the type of this request.
     *
     * @return The request type.
     */
    public String getRequestType() {
        return requestType;
    }

    /**
     * Sets the ID associated with this request.
     *
     * @param id The new request ID.
     */
    public void setId(Integer id) {
        this.id = id;
    }
}
