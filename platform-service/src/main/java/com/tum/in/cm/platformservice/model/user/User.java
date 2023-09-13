package com.tum.in.cm.platformservice.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
@Getter
@Setter
public class User {
    @Id
    private String id;
    @Indexed(unique = true)
    private String email;
    private String password;
    @JsonIgnore
    private boolean isAdmin;
}
