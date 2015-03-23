package org.neo4j.tutorial.server.rest;

import java.net.URI;

public final class FunctionalTestHelper
{
    private final URI baseUri;

    public FunctionalTestHelper( URI baseUri )
    {
        this.baseUri = baseUri;
    }

    public String dataUri()
    {
        return baseUri.toString() + "db/data/";
    }

    public String nodeUri()
    {
        return dataUri() + "node";
    }

    public String nodeUri( long id )
    {
        return nodeUri() + "/" + id;
    }

    public String relationshipUri()
    {
        return dataUri() + "relationship";
    }

    public String relationshipUri( long id )
    {
        return relationshipUri() + "/" + id;
    }

    /*public GraphDatabaseService getDatabase()
    {
        return server.getDatabase().getGraph();
    }*/

    public String nodeUri( String label, String key, String value )
    {
        return dataUri() + "label/" + label + "/nodes?" + key + "=%22" + convertAnySpaces( value ) + "%22";
    }

    private String convertAnySpaces( String value )
    {
        return value.replace( ' ', '+' );
    }
}
