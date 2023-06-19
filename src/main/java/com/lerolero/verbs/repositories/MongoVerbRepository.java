package com.lerolero.verbs.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Aggregation;

import reactor.core.publisher.Mono;

import com.lerolero.verbs.models.Verb;

public interface MongoVerbRepository extends ReactiveMongoRepository<Verb,String> {

	@Aggregation("{ $sample: { size: 1 } }")
	public Mono<Verb> pullRandom();

}
