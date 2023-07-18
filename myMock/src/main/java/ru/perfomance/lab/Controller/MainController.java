package ru.perfomance.lab.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.perfomance.lab.Model.RequestDTO;
import ru.perfomance.lab.Model.ResponseDTO;

import java.math.BigDecimal;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@RestController
public class MainController {

    private Logger log = LoggerFactory.getLogger(MainController.class);

    ObjectMapper mapper = new ObjectMapper();

    public long start_time = 0L;

    @PostMapping(
            value = "/info/postBalances",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
            
    )
    public Object postBalances(@RequestBody RequestDTO requestDTO) {
        try {
            String clientId = requestDTO.getClientId();
            char firstDigit = clientId.charAt(0);
            BigDecimal maxLimit;
            String currency;

            if (firstDigit == '8') {
                maxLimit = new BigDecimal("2000.00");
                currency = "US";
            } else if (firstDigit == '9') {
                maxLimit = new BigDecimal("1000.00");
                currency = "EU";
            } else {
                maxLimit = new BigDecimal("10000.00");
                currency = "RUB";
            }

            String RqUID = requestDTO.getRqUID();
            ResponseDTO responseDTO = new ResponseDTO();

            responseDTO.setRqUID(RqUID);
            responseDTO.setClientId(clientId);
            responseDTO.setAccount(requestDTO.getAccount());
            responseDTO.setCurrency(currency);
            responseDTO.setBalance(generateRandomBalance(maxLimit));
            responseDTO.setMaxLimit(maxLimit);

            log.error("* Запрос *" + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(requestDTO));
            log.error("* Ответ *" + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(responseDTO));

            long pacing = ThreadLocalRandom.current().nextLong(100, 500);
            long end_time = System.currentTimeMillis();
            if (end_time - start_time < pacing)
                Thread.sleep(pacing - (end_time - start_time));

            return responseDTO;

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    private String generateRandomBalance(BigDecimal maxLimit) {
        Random random = new Random();
        double randomBalance = random.nextDouble() * maxLimit.doubleValue();
        return String.format("%.2f", randomBalance);
    }
}
