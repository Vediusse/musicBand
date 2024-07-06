package com.example.Service;

import Kafka.RoleCheckRequest;
import Kafka.RoleCheckResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private KafkaTemplate<String, RoleCheckResponse> roleCheckResponseKafkaTemplate;

    @KafkaListener(topics = "roleCheckTopic", groupId = "auth", containerFactory = "roleCheckRequestKafkaListenerContainerFactory")
    public void checkRoles(RoleCheckRequest request) {
        System.out.println("Received RoleCheckRequest with correlationId: " + request.getCorrelationId());
        RoleCheckResponse response;
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
            String userRole = userDetails.getAuthorities().iterator().next().getAuthority();
            boolean hasRequiredRole = userRole.equals(request.getRequiredRole());
            response = new RoleCheckResponse(request.getCorrelationId(), hasRequiredRole);
            System.out.println("Role check successful for user: " + request.getUsername());
        } catch (UsernameNotFoundException e) {
            response = new RoleCheckResponse();
            response.setCorrelationId(request.getCorrelationId());
            response.setAllowed(false);
            response.setError(e.getMessage());
            System.out.println("User not found: " + request.getUsername());
        }
        roleCheckResponseKafkaTemplate.send("roleCheckResponseTopic", response.getCorrelationId(), response);
        System.out.println("Sent RoleCheckResponse with correlationId: " + response.getCorrelationId());
    }
}
