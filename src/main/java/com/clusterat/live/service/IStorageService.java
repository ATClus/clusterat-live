package com.clusterat.live.service;

import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

public interface IStorageService {
    void init();
    Mono<Void> store(FilePart file);
//    void load();
//    void delete();
}
