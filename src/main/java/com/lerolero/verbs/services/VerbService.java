package com.lerolero.verbs.services;

import java.time.Duration;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import com.lerolero.verbs.repositories.MongoVerbRepository;
import com.lerolero.verbs.repositories.VerbCache;
import com.lerolero.verbs.models.Verb;

@Service
public class VerbService {

	@Autowired
	private MongoVerbRepository repo;

	@Autowired
	private VerbCache cache;

	private Mono<String> next() {
		return cache.next()
			.flatMap(v -> {
				if (v.getContinuous() == null) return repo.findById(v.getId());
				else return Mono.just(v);
			})
			.cast(Verb.class)
			.doOnNext(v -> cache.add(v))
			.map(v -> v.getContinuous());
	}

	public Mono<String> randomVerb() {
		return next()
			.subscribeOn(Schedulers.boundedElastic());
	}

	public Flux<String> randomVerbList(Integer size) {
		return Flux.range(1, size)
			.flatMap(i -> next())
			.subscribeOn(Schedulers.boundedElastic());
	}

	public Flux<String> randomVerbProducer(Integer interval) {
		return Flux.interval(Duration.ofMillis(interval))
			.flatMap(i -> next())
			.subscribeOn(Schedulers.boundedElastic());
	}

}
