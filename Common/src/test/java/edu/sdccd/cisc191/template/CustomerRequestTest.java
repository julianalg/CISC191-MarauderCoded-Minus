package edu.sdccd.cisc191.template;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the CustomerRequest class.
 * This test class ensures that the functionality of the methods in
 * CustomerRequest performs as expected, including JSON
 * serialization and deserialization, as well as attribute handling.
 *
 * @author Andy Ly
 */
class CustomerRequestTest {

    private Request request;
    private Map<String, Object> attributes;

    /**
     * Sets up test data before each test case.
     */
    @BeforeEach
    void setUp() {
        attributes = new HashMap<>();
        attributes.put("Name", "John");
        attributes.put("Age", 30);

        request = new Request("ModifyUser", 1, attributes);
    }

    /**
     * Tests the getter for getRequestType .
     * Ensures the request type is correctly returned.
     */
    @Test
    void testGetRequestType() {
        assertEquals("ModifyUser", request.getRequestType(), "The request type should be 'ModifyUser'.");
    }

    /**
     * Tests the getter for getId .
     * Ensures the request ID is correctly returned.
     */
    @Test
    void testGetId() {
        assertEquals(1, request.getId(), "The request ID should be 1.");
    }

    /**
     * Tests the getter for getAttributesToModify .
     * Ensures that the attributes are returned correctly.
     */
    @Test
    void testGetAttributesToModify() {
        assertEquals(attributes, request.getAttributesToModify(), "The attributes should match the expected values.");
    }

    /**
     * Tests the setter for setId .
     * Ensures the request ID is correctly set.
     */
    @Test
    void testSetId() {
        request.setId(2);
        assertEquals(2, request.getId(), "The request ID should be updated to 2.");
    }

    /**
     * Tests the toString method.
     * Ensures that the string representation matches the expected format.
     */
    @Test
    void testToString() {
        String expected = """
                Request Type: [type=ModifyUser]
                RequestID: [id=1]
                """;
        assertEquals(expected, request.toString(), "The string representation of the request is incorrect.");
    }

    /**
     * Tests JSON serialization using toJSON .
     * Ensures that the serialized JSON matches the structure and values of the  CustomerRequest  object.
     */
    @Test
    void testToJSON() throws Exception {
        // Convert the request object to a JSON string
        String json = Request.toJSON(request);

        // Create the expected JSON string using the manually created object
        String expectedJson = """
    {"attributesToModify":{"Age":30, "Name":"John"},"id":1,"requestType":"ModifyUser"}
    """;

        // Assert that the serialized JSON matches the expected JSON string
        assertEquals(expectedJson.replaceAll("\\s+", ""), json.replaceAll("\\s+", ""),
                "The serialized JSON does not match the expected JSON.");
    }


    /**
     * Tests JSON deserialization using fromJSON .
     * Ensures that a JSON string is correctly deserialized into a CustomerRequest object.
     */
    @Test
    void testFromJSON() throws Exception {
        String json = """
                {
                    "requestType": "ModifyUser",
                    "id": 1,
                    "attributesToModify": {
                        "Name": "John",
                        "Age": 30
                    }
                }
                """;
        Request deserializedRequest = Request.fromJSON(json);
        assertNotNull(deserializedRequest, "The deserialized CustomerRequest object should not be null.");
        assertEquals("ModifyUser", deserializedRequest.getRequestType(), "The deserialized request type should be 'ModifyUser'.");
        assertEquals(1, deserializedRequest.getId(), "The deserialized request ID should be 1.");
        assertEquals(attributes, deserializedRequest.getAttributesToModify(), "The deserialized attributes should match the expected values.");
    }
}
