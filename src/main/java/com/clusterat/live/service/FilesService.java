package com.clusterat.live.service;

import jakarta.annotation.PostConstruct;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@Service
public class FilesService implements IStorageService {
    private final Path rootLocation = Paths.get("uploads");

    @Override
    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(this.rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("The directory can't be initialized: ", e);
        }
    }

    @Override
    public Mono<Void> store(FilePart file) {
        String filename = StringUtils.cleanPath(Objects.requireNonNull(file.filename()));
        Path destinationFile = this.rootLocation.resolve(filename);

        return file.transferTo(destinationFile);
    }
}
