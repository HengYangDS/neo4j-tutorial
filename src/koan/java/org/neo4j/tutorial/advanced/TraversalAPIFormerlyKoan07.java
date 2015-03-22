package org.neo4j.tutorial.advanced;

import org.junit.ClassRule;
import org.junit.Test;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.Evaluator;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Uniqueness;
import org.neo4j.tutorial.DoctorWhoRelationships;
import org.neo4j.tutorial.DoctorWhoUniverseResource;

import static org.junit.Assert.assertThat;

import static org.neo4j.tutorial.matchers.ContainsOnlySpecificActors.containsOnlyActors;
import static org.neo4j.tutorial.matchers.ContainsSpecificNumberOfNodes.containsNumberOfNodes;

/**
 * In this Koan we start using the new traversal framework to find interesting
 * information from the graph about the Doctor's past life.
 */
public class TraversalAPIFormerlyKoan07
{
    @ClassRule
    static public DoctorWhoUniverseResource neo4jResource = new DoctorWhoUniverseResource();

    @Test
    public void shouldDiscoverHowManyDoctorActorsHaveParticipatedInARegeneration() throws Exception
    {
        Node theDoctor = neo4jResource.theDoctor();
        TraversalDescription regeneratedActors = null;

        GraphDatabaseService database = neo4jResource.getGraphDatabaseService();

        try ( Transaction tx = database.beginTx() )
        {
            // YOUR CODE GOES HERE
            // Note: every doctor has participated in a regeneration, including the first and last Doctors

            // SNIPPET_START

            regeneratedActors = database.traversalDescription()
                    .relationships( DoctorWhoRelationships.PLAYED, Direction.INCOMING )
                    .breadthFirst()
                    .evaluator( new Evaluator()
                    {
                        public Evaluation evaluate( Path path )
                        {
                            if ( path.endNode().hasRelationship( DoctorWhoRelationships.REGENERATED_TO ) )
                            {
                                return Evaluation.INCLUDE_AND_CONTINUE;
                            }
                            else
                            {
                                return Evaluation.EXCLUDE_AND_CONTINUE;
                            }
                        }
                    } );

            // SNIPPET_END

            assertThat( regeneratedActors.traverse( theDoctor ).nodes(), containsNumberOfNodes( 13 ) );
            tx.success();
        }
    }

    @Test
    public void shouldFindTheFirstDoctor()
    {
        Node theDoctor = neo4jResource.theDoctor();
        TraversalDescription firstDoctor = null;

        GraphDatabaseService database = neo4jResource.getGraphDatabaseService();

        try ( Transaction tx = database.beginTx() )
        {

            // YOUR CODE GOES HERE
            // SNIPPET_START

            firstDoctor = database.traversalDescription()
                    .relationships( DoctorWhoRelationships.PLAYED, Direction.INCOMING )
                    .depthFirst()
                    .uniqueness( Uniqueness.NODE_GLOBAL )
                    .evaluator( new Evaluator()
                    {
                        public Evaluation evaluate( Path path )
                        {
                            if ( path.endNode().hasRelationship( DoctorWhoRelationships.REGENERATED_TO,
                                    Direction.INCOMING ) )
                            {
                                return Evaluation.EXCLUDE_AND_CONTINUE;
                            }
                            else if ( !path.endNode().hasRelationship( DoctorWhoRelationships.REGENERATED_TO,
                                    Direction.OUTGOING ) )
                            {
                                // Catches Richard Hurndall who played the William
                                // Hartnell's Doctor in The Five Doctors (William
                                // Hartnell had died by then)
                                return Evaluation.EXCLUDE_AND_CONTINUE;
                            }
                            else
                            {
                                return Evaluation.INCLUDE_AND_PRUNE;
                            }
                        }
                    } );

            // SNIPPET_END

            assertThat( firstDoctor.traverse( theDoctor ).nodes(), containsOnlyActors( "William Hartnell" ) );
            tx.success();
        }
    }
}
