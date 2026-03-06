package arqui.broker.controllers;

import arqui.broker.models.Log;
import arqui.broker.services.logService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/logs")
public class LogController {
    private final logService logService;

    public LogController(logService logService) {
        this.logService = logService;
    }

    @GetMapping("/{id}")
    public Optional<Log> getRoleById(@PathVariable Integer id) {
        return logService.getLogById(id);
    }

    @GetMapping("/all")
    public List<Log> getAllLogs() {
        return logService.findAll();
    }

    @PostMapping
    public ResponseEntity<Log> create(@RequestBody Log log) {
        return ResponseEntity.status(201).body(logService.createLog(log));
    }
}
