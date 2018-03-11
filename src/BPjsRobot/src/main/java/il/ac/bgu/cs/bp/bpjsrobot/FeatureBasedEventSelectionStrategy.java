package il.ac.bgu.cs.bp.bpjsrobot;

import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static java.util.stream.Collectors.toSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.mozilla.javascript.Context;

import il.ac.bgu.cs.bp.bpjs.bprogram.runtimeengine.BSyncStatement;
import il.ac.bgu.cs.bp.bpjs.events.BEvent;
import il.ac.bgu.cs.bp.bpjs.eventselection.AbstractEventSelectionStrategy;
import il.ac.bgu.cs.bp.bpjs.eventselection.EventSelectionResult;
import il.ac.bgu.cs.bp.bpjs.eventsets.ComposableEventSet;
import il.ac.bgu.cs.bp.bpjs.eventsets.EventSet;
import il.ac.bgu.cs.bp.bpjs.eventsets.EventSets;

public class FeatureBasedEventSelectionStrategy extends AbstractEventSelectionStrategy{
	private String featureBasedPolicy;

	public FeatureBasedEventSelectionStrategy(String featureBasedPolicy) {
		this.featureBasedPolicy = featureBasedPolicy;
	}

	@Override
	public Set<BEvent> selectableEvents(Set<BSyncStatement> statements, List<BEvent> externalEvents) {
		if ( statements.isEmpty() ) {
            // Corner case, not sure this is even possible.
            return externalEvents.isEmpty() ? emptySet() : singleton(externalEvents.get(0));
        }
        
        EventSet blocked = ComposableEventSet.anyOf(statements.stream()
                .filter( stmt -> stmt!=null )
                .map(BSyncStatement::getBlock )
                .filter(r -> r != EventSets.none )
                .collect( Collectors.toSet() ) );
        
        Set<BEvent> requested = statements.stream()
                .filter( stmt -> stmt!=null )
                .flatMap( stmt -> stmt.getRequest().stream() )
                .collect( Collectors.toSet() );
        
        // Let's see what internal events are requested and not blocked (if any).
        try {
            Context.enter();
            Set<BEvent> requestedAndNotBlocked = requested.stream()
                    .filter( req -> !blocked.contains(req) )
                    .collect( toSet() );

            return requestedAndNotBlocked.isEmpty() ?
                    externalEvents.stream().filter( e->!blocked.contains(e) ) // No internal events requested, defer to externals.
                                  .findFirst().map( e->singleton(e) ).orElse(emptySet())
                    : requestedAndNotBlocked;
        } finally {
            Context.exit();
        }
    }

    @Override
    public Optional<EventSelectionResult> select(Set<BSyncStatement> statements, List<BEvent> externalEvents, Set<BEvent> selectableEvents) {
        if ( selectableEvents.isEmpty() ) {
            return Optional.empty();
        }
        
        BEvent chosen = new ArrayList<>(selectableEvents).get(rnd.nextInt(selectableEvents.size()));
        
        Set<BEvent> requested = statements.stream()
                .filter( stmt -> stmt!=null )
                .flatMap( stmt -> stmt.getRequest().stream() )
                .collect( Collectors.toSet() );
        
        if (requested.contains(chosen)) {
            return Optional.of(new EventSelectionResult(chosen));
        } else {
            // that was an internal event, need to find the first index 
            return Optional.of(new EventSelectionResult(chosen, singleton(externalEvents.indexOf(chosen))));
        }
    }

}
