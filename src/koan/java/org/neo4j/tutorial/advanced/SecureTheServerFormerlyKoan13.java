package org.neo4j.tutorial.advanced;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import org.neo4j.server.CommunityNeoServer;
import org.neo4j.tutorial.DoctorWhoUniverseGenerator;
import org.neo4j.tutorial.ServerDoctorWhoUniverse;
import org.neo4j.tutorial.security_rule.UserNameAndPasswordForSalariesSecurityRule;

import static javax.ws.rs.core.MediaType.TEXT_PLAIN;

import static junit.framework.Assert.assertEquals;

import static org.neo4j.tutorial.server.ServerBuilder.server;

/**
 * In this Koan we mix an unmanaged (JAX-RS) extension with security rules to provide
 * to provide authenticated domain-specific set of Doctor Who resources inside
 * the Neo4j server.
 */
public class SecureTheServerFormerlyKoan13
{

    private static ServerDoctorWhoUniverse universe;

    @BeforeClass
    public static void createDatabase() throws Exception
    {
        DoctorWhoUniverseGenerator doctorWhoUniverseGenerator = new DoctorWhoUniverseGenerator();

        CommunityNeoServer server =
                server()
                        .usingDatabaseDir( doctorWhoUniverseGenerator.generate() )
                        .withThirdPartyJaxRsPackage( "org.neo4j.tutorial.security_rule", "/security_rule" )
                        .withSecurityRules( UserNameAndPasswordForSalariesSecurityRule.class )
                        .build();

        universe = new ServerDoctorWhoUniverse( server );
    }

    @AfterClass
    public static void closeTheDatabase()
    {
        universe.stop();
    }

    @Test
    public void regularRESTAPIShouldNotBeSecured()
    {
        // This unit test provides the client side HTTP actions and assertions.
        // Your work happens in
        // org.neo4j.tutorial.security_rule.UserNameAndPasswordForSalariesSecurityRule
        // where you have to build the server-side infrastructure to make this Koan pass.

        ClientConfig config = new DefaultClientConfig();
        Client client = Client.create( config );

        ClientResponse response = client.resource( "http://localhost:7474/db/data" ).get( ClientResponse.class );

        assertEquals( 200, response.getStatus() );

    }

    @Test
    public void shouldNotAccessDavidTennantSalaryWithoutProperCredentials()
    {

        // This unit test provides the client side HTTP actions and assertions.
        // Your work happens in
        // org.neo4j.tutorial.security_rule.UserNameAndPasswordForSalariesSecurityRule
        // where you have to build the server-side infrastructure to make this Koan pass.

        ClientConfig config = new DefaultClientConfig();
        Client client = Client.create( config );

        ClientResponse response = client.resource( "http://localhost:7474/security_rule/David%20Tennant/salary" )
                .accept(
                        TEXT_PLAIN ).get( ClientResponse.class );

        assertEquals( 401, response.getStatus() );

    }

    @Test
    public void shouldAccessDavidTennantSalaryWithProperCredentials()
    {

        // This unit test provides the client side HTTP actions and assertions.
        // Your work happens in
        // org.neo4j.tutorial.security_rule.UserNameAndPasswordForSalariesSecurityRule
        // where you have to build the server-side infrastructure to make this Koan pass.

        // Note: this isn't a secure password scheme - this Koan is only to aid
        // understanding of how to register security rules with the server.

        ClientConfig config = new DefaultClientConfig();
        Client client = Client.create( config );

        ClientResponse response = client.resource( "http://localhost:7474/security_rule/David%20Tennant/salary" )
                .header(
                        "X-Username", "Alice" ).header( "X-Password", "1337" ).accept( TEXT_PLAIN )
                .get( ClientResponse.class );

        assertEquals( 200, response.getStatus() );
        assertEquals( "text/plain", response.getHeaders().get( "Content-Type" ).get( 0 ) );
        assertEquals( "1000000", response.getEntity( String.class ) );
    }
}
