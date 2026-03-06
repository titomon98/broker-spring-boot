package arqui.broker.services;

import arqui.broker.models.Log;
import arqui.broker.repositories.logRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class logServiceImplementation implements logService {

    //Aqui va la logica de negocio
    private final logRepository logRepository;

    //Constructor de repositorio
    public logServiceImplementation(logRepository logRepository) {
        this.logRepository = logRepository;
    }

    @Override
    public List<Log> findAll() {
        return logRepository.findAll();
    }

    @Override
    public Optional<Log> getLogById(Integer id) {
        if (id == null) {
            return Optional.empty();
        }
        Optional<Log> logValidado = logRepository.findById(id);
        return logValidado;
    }

    @Override
    public Log createLog(Log log) {
        String API_BASE_PRODUCTOS = "http://localhost:3000";
        String url = API_BASE_PRODUCTOS + log.getRequest();
        String body = log.getBody();

        if (url == null || url.isBlank()) {
            throw new RuntimeException("El campo 'request' (url) es requerido");
        }

        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<String> responseEntity;

            if (body != null && !body.isBlank()) {
                // Si hay body → POST
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<String> entity = new HttpEntity<>(body, headers);
                responseEntity = restTemplate.postForEntity(url, entity, String.class);
            } else {
                // Si no hay body → GET
                responseEntity = restTemplate.getForEntity(url, String.class);
            }

            //Extraer el atributo "message" para guardarlo en rawResponse
            String message = null;

            try {
                JsonNode nodeResponse = new ObjectMapper()
                        .readTree(responseEntity.getBody());
                message = nodeResponse.get("tipoproducto").get("tipo").asText(); //message porque asi viene en la respuesta
            }
            catch (JsonProcessingException e) {
                message = e.getMessage();
            }

            log.setResponse(responseEntity.getBody());
            log.setCode(responseEntity.getStatusCode().value());
            log.setRawResponse(message);

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.setResponse(e.getResponseBodyAsString());
            log.setCode(e.getStatusCode().value());
        }

        return logRepository.save(log);
    }
}
