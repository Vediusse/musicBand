package Kafka;


import com.fasterxml.jackson.annotation.JsonProperty;


import java.io.Serializable;

public class RoleCheckRequest  {

    @JsonProperty("correlationId")
    private String correlationId;
    @JsonProperty("username")
    private String username;

    @JsonProperty("requiredRole")
    private String requiredRole;

    public RoleCheckRequest() {}

    public RoleCheckRequest(String username, String requiredRole) {
        this.username = username;
        this.requiredRole = requiredRole;
    }

    public RoleCheckRequest(String currentUser, String requiredRole, String correlationId) {
        this(currentUser,requiredRole);
        this.correlationId = correlationId;

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRequiredRole() {
        return requiredRole;
    }

    public void setRequiredRole(String requiredRole) {
        this.requiredRole = requiredRole;
    }

    public String getCorrelationId() {
        return correlationId;
    }
}


