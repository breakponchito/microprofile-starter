/*
 * Copyright (c) 2017-2020 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.eclipse.microprofile.starter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * MicroProfile Starter runtimes API smoke tests.
 * <p>
 * Some rudimentary tests to make sure we ain't breaking the API.
 *
 * @author Michal Karm Babacek <karm@redhat.com>
 */
@RunWith(Arquillian.class)
public class APITest {

    public static final String URI = "api";

    private static final String WARNAME = "mp-starter-test.war";
    private Client client = ClientBuilder.newClient();

    private static final Logger logger = Logger.getLogger(APITest.class.getName());

    @Deployment(testable = true)
    public static WebArchive createDeployment() {
        WebArchive archive = ShrinkWrap.create(WebArchive.class, WARNAME)
                .addPackages(true, "org.eclipse.microprofile.starter")
                .addAsLibraries(Maven.resolver().resolve("org.thymeleaf:thymeleaf:3.0.10.RELEASE",
                                "org.apache.maven:maven-model:3.9.6", "org.apache.maven:maven-builder-support:3.9.6",
                                "org.apache.maven:maven-artifact:3.9.6", "org.apache.maven:maven-model-builder:3.9.6",
                                "com.google.guava:guava:11.0.2", "com.fasterxml.jackson.core:jackson-core:2.10.5",
                                "com.fasterxml.jackson.core:jackson-annotations:2.10.5", "com.fasterxml.jackson.core:jackson-databind:2.10.5",
                                "com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.10.5",
                                "org.apache.commons:commons-compress:1.20")
                        .withTransitivity().asFile());
        return archive;
    }

    @ArquillianResource
    private URL baseURL;

    private WebTarget target;
    private File v7Matrix;
    private File v7MatrixServers;
    private File v6Matrix;
    private File v6MatrixServers;
    private File v5Matrix;
    private File v5MatrixServers;
    private File v4Matrix;
    private File v4MatrixServers;
    private File v3Matrix;
    private File v3MatrixServers;

    @Before
    public void before() throws URISyntaxException {
        target = client.target(baseURL.toURI() + URI);
        v7Matrix = new File(getClass().getClassLoader().getResource("json_examples/v7/supportMatrix.json.segments").getFile());
        v7MatrixServers = new File(getClass().getClassLoader().getResource("json_examples/v7/supportMatrix_servers.json.segments").getFile());
        v6Matrix = new File(getClass().getClassLoader().getResource("json_examples/v6/supportMatrix.json.segments").getFile());
        v6MatrixServers = new File(getClass().getClassLoader().getResource("json_examples/v6/supportMatrix_servers.json.segments").getFile());
        v5Matrix = new File(getClass().getClassLoader().getResource("json_examples/v5/supportMatrix.json.segments").getFile());
        v5MatrixServers = new File(getClass().getClassLoader().getResource("json_examples/v5/supportMatrix_servers.json.segments").getFile());
        v4Matrix = new File(getClass().getClassLoader().getResource("json_examples/v4/supportMatrix.json.segments").getFile());
        v4MatrixServers = new File(getClass().getClassLoader().getResource("json_examples/v4/supportMatrix_servers.json.segments").getFile());
        v3Matrix = new File(getClass().getClassLoader().getResource("json_examples/v3/supportMatrix.json.segments").getFile());
        v3MatrixServers = new File(getClass().getClassLoader().getResource("json_examples/v3/supportMatrix_servers.json.segments").getFile());
    }

    public void test(File segments, String uri) throws IOException, URISyntaxException {
        String response = client.target(baseURL.toString() + URI + uri).request().get(String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        String expectedJson = objectMapper.writeValueAsString(objectMapper.readTree(segments));
        Assert.assertEquals("Not Equal failure", expectedJson, response);
    }

    @Test
    @RunAsClient
    public void supportMatrix() throws IOException, URISyntaxException {
        test(v7Matrix, "/7/supportMatrix");
        test(v7MatrixServers, "/7/supportMatrix/servers");
        test(v6Matrix, "/6/supportMatrix");
        test(v6MatrixServers, "/6/supportMatrix/servers");
        test(v5Matrix, "/5/supportMatrix");
        test(v5MatrixServers, "/5/supportMatrix/servers");
        test(v4Matrix, "/4/supportMatrix");
        test(v4MatrixServers, "/4/supportMatrix/servers");
        test(v3Matrix, "/3/supportMatrix");
        test(v3MatrixServers, "/3/supportMatrix/servers");
    }
}