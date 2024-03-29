package be.kdg.backend.controller;

import be.kdg.backend.service.StreamingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/process")
public class ProcessController {
    private final StreamingService streamingService;

    public ProcessController(StreamingService streamingService) {
        this.streamingService = streamingService;
    }

    @DeleteMapping
    public ResponseEntity<String> stopAllProcesses() {
        streamingService.stopAllProcesses();
        return ResponseEntity.ok("All processes stopped");
    }
}
