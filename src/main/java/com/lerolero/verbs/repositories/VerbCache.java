package com.lerolero.verbs.repositories;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.api.RBucketReactive;

import reactor.core.publisher.Mono;

import com.lerolero.verbs.repositories.MongoVerbRepository;
import com.lerolero.verbs.models.Verb;

@Repository
public class VerbCache {

	@Autowired
	private RedissonReactiveClient redis;

	@Autowired
	private MongoVerbRepository repo;

	private List<String> ids;

	public Mono<Verb> next() {
		if (ids == null || ids.size() == 0) {
			ids = new ArrayList<>();
			repo.findAll().map(Verb::getId).doOnNext(i -> ids.add(i)).subscribe();
		}
		Verb defaultVerb = new Verb();
		return Mono.fromSupplier(() -> ids.get((int)(Math.random() * ids.size())))
			.doOnNext(id -> defaultVerb.setId(id))
			.flatMap(id -> redis.getBucket("/verb/" + id).get())
			.cast(Verb.class)
			.defaultIfEmpty(defaultVerb);
	}

	public void add(Verb verb) {
		RBucketReactive<Verb> bucket = redis.getBucket("/verb/" + verb.getId());
		bucket.set(verb).subscribe();
		ids.add(verb.getId());
	}

}
