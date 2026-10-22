
package com.alibou.customer.config;

import com.mongodb.ConnectionString;
import org.junit.jupiter.api.Test;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.mongodb.autoconfigure.MongoConnectionDetails;
import org.springframework.boot.mongodb.autoconfigure.MongoProperties;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MongoDebugConfigTest {

    @Test
    void mongoPropertiesDebugShouldPrintMongoConfiguration() throws Exception {
        MongoProperties properties = mock(MongoProperties.class);
        MongoConnectionDetails connectionDetails =
                mock(MongoConnectionDetails.class);

        when(properties.getUri())
                .thenReturn("mongodb://localhost:27017/testdb");
        when(properties.getHost()).thenReturn("localhost");
        when(properties.getPort()).thenReturn(27017);
        when(properties.getDatabase()).thenReturn("testdb");
        when(properties.getUsername()).thenReturn("testuser");

        when(connectionDetails.getConnectionString())
                .thenReturn(new ConnectionString(
                        "mongodb://localhost:27017/testdb"
                ));

        MongoDebugConfig config = new MongoDebugConfig();

        CommandLineRunner runner =
                config.mongoPropertiesDebug(properties, connectionDetails);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;

        try (PrintStream capture = new PrintStream(output, true, StandardCharsets.UTF_8)) {
            System.setOut(capture);

            runner.run(new String[]{"test"});
        } finally {
            System.setOut(originalOut);
        }

        String printedOutput = output.toString(StandardCharsets.UTF_8);

        assertTrue(printedOutput.contains("========== MONGO PROPERTIES =========="));
        assertTrue(printedOutput.contains("URI      : mongodb://localhost:27017/testdb"));
        assertTrue(printedOutput.contains("Host     : localhost"));
        assertTrue(printedOutput.contains("Port     : 27017"));
        assertTrue(printedOutput.contains("Database : testdb"));
        assertTrue(printedOutput.contains("Username : testuser"));
        assertTrue(printedOutput.contains("========== MONGO CONNECTION DETAILS =========="));
        assertTrue(printedOutput.contains("Connection String : mongodb://localhost:27017/testdb"));
        assertTrue(printedOutput.contains("=============================================="));
    }
}
