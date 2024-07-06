package Aspects;

import Kafka.RoleCheckRequest;
import Kafka.RoleCheckResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
@Aspect
public class PermissionAspect {

    @Autowired
    private KafkaTemplate<String, RoleCheckRequest> kafkaTemplate;

    private final ObjectMapper mapper = new ObjectMapper();

    private final ConcurrentHashMap<String, PendingResponse> pendingResponses = new ConcurrentHashMap<>();
    private static final int TIMEOUT = 10;

    @Around("@annotation(checkPermission)")
    public Mono<Object> checkPermission(ProceedingJoinPoint joinPoint, CheckPermission checkPermission) throws Throwable {
        String currentUser = getCurrentUser();
        String requiredRole = checkPermission.value();
        String correlationId = UUID.randomUUID().toString();

        RoleCheckRequest request = new RoleCheckRequest(currentUser, requiredRole, correlationId);

        CompletableFuture<RoleCheckResponse> responseFuture = new CompletableFuture<>();
        pendingResponses.put(correlationId, new PendingResponse(responseFuture, Instant.now()));

        kafkaTemplate.send("roleCheckTopic", correlationId, request);
        System.out.println("Sent RoleCheckRequest with correlationId: " + correlationId);


        responseFuture.orTimeout(TIMEOUT, TimeUnit.SECONDS)
                .exceptionally(ex -> {
                    if (ex != null) {
                        RoleCheckResponse timeoutResponse = new RoleCheckResponse();
                        timeoutResponse.setAllowed(false);
                        timeoutResponse.setError("Request timed out.");
                        pendingResponses.remove(correlationId);
                        responseFuture.complete(timeoutResponse);
                        System.out.println("Timeout response sent for correlationId: " + correlationId);
                    }
                    return null;
                });

        return Mono.defer(() -> Mono.fromFuture(responseFuture)
                .flatMap(response -> {
                    if (response == null) {
                        System.out.println("Response is null, service unavailable.");
                        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                                .body(new RoleCheckResponse("Сервис занят, попробуйте позже.", false)));
                    }
                    if (response.isAllowed()) {
                        try {
                            System.out.println("Permission granted for correlationId: " + correlationId);
                            return Mono.just(joinPoint.proceed());
                        } catch (Throwable throwable) {
                            return Mono.error(throwable);
                        }
                    } else {
                        System.out.println("Permission denied for correlationId: " + correlationId);
                        return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body(response));
                    }
                })
                .onErrorResume(e -> {
                    RoleCheckResponse errorResponse = new RoleCheckResponse();
                    errorResponse.setAllowed(false);
                    errorResponse.setError(e.getMessage());
                    System.out.println("Error during role check for correlationId: " + correlationId + ", error: " + e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(errorResponse));
                }));
    }

    @KafkaListener(topics = "roleCheckResponseTopic", groupId = "auth", containerFactory = "roleCheckResponseKafkaListenerContainerFactory")
    public void listen(ConsumerRecord<String, RoleCheckResponse> consumerRecord) {
        RoleCheckResponse response = consumerRecord.value();
        String correlationId = consumerRecord.key();
        System.out.println("Received RoleCheckResponse with correlationId: " + correlationId);

        PendingResponse pendingResponse = pendingResponses.remove(correlationId);
        if (pendingResponse != null) {
            pendingResponse.getFuture().complete(response);
        }
    }

    private String getCurrentUser() {
        return "currentUser";
    }

    private static class PendingResponse {
        private final CompletableFuture<RoleCheckResponse> future;
        private final Instant timestamp;

        public PendingResponse(CompletableFuture<RoleCheckResponse> future, Instant timestamp) {
            this.future = future;
            this.timestamp = timestamp;
        }

        public CompletableFuture<RoleCheckResponse> getFuture() {
            return future;
        }

        public Instant getTimestamp() {
            return timestamp;
        }
    }
}
