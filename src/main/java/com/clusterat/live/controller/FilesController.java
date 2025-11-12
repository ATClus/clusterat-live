package com.clusterat.live.controller;

import com.clusterat.live.service.FilesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/files")
public class FilesController {
    private final FilesService filesService;

    @Autowired
    public FilesController(FilesService filesService) {
        this.filesService = filesService;
    }

    @PostMapping
    public Mono<ResponseEntity<String>> handleFileUpload(@RequestPart("file") Mono<FilePart> filePartMono) {
        return filePartMono
                .flatMap(filePart -> {
                    String filename = filePart.filename();

                    return filesService.store(filePart)
                            .then(Mono.just(
                                    ResponseEntity.ok("File uploaded successfully: " + filename)
                            ));
                })
                .onErrorResume(e -> Mono.just(
                        ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body("Failed to upload file: " + e.getMessage())
                ))
                .switchIfEmpty(Mono.just(
                        ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body("File part is required")
                ));
    }
}
