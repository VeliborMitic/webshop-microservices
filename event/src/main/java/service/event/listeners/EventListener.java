package service.event.listeners;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import service.event.models.EventInfo;
import service.event.service.EventService;

import java.util.Objects;

@RabbitListener(queues = "spring-boot")
@Component
@Slf4j
public class EventListener {

    private final ObjectMapper objectMapper;

    private final EventService eventService;

    public EventListener(ObjectMapper objectMapper,
                         EventService eventService) {
        this.objectMapper = objectMapper;
        this.eventService = eventService;
    }

    @RabbitHandler
    @SuppressWarnings("unchecked")
    public void receiveMessage(String message) {
        try {
            EventInfo<String> in = objectMapper.readValue(message, EventInfo.class);
            eventService.add(in);
            log.info(Objects.requireNonNull(in).getMessage());
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
    }

}
