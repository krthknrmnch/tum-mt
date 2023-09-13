package com.tum.in.cm.platformservice.config.db;

import com.tum.in.cm.platformservice.model.connector.Connector;
import com.tum.in.cm.platformservice.model.user.User;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;

@Configuration
@DependsOn("primaryMongoTemplate")
public class IndexesConfig {
    @Autowired
    private MongoTemplate primaryMongoTemplate;

    @PostConstruct
    public void initIndexes() {
        primaryMongoTemplate.indexOps(User.class)
                .ensureIndex(
                        new Index().on("email", Sort.Direction.ASC).unique()
                );
        primaryMongoTemplate.indexOps(Connector.class)
                .ensureIndex(
                        new Index().on("ipPort", Sort.Direction.ASC).unique()
                );
        primaryMongoTemplate.indexOps(Connector.class)
                .ensureIndex(
                        new Index().on("updateTimestamp", Sort.Direction.ASC).expire(300)
                );
    }
}
