package arqui.broker.services;

import arqui.broker.models.Log;

import java.util.List;
import java.util.Optional;

public interface logService {
    List<Log> findAll();
    Optional<Log> getLogById(Integer id);
    Log createLog(Log log);
}
