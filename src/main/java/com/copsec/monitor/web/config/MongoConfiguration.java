package com.copsec.monitor.web.config;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.ServerAddress;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoTypeMapper;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

@Configuration
public class MongoConfiguration extends AbstractMongoConfiguration {


    @Value("${mongo.db.url}")
    private String mongoDBUrl;

    @Value("${mongo.db.name}")
    private String dataBaseName;

    @Value("${mongo.db.log.url}")
	private String mongoLogUrl;

    @Value("${mongo.db.log.name}")
    private String logCollectionName;

    @Bean
    @Override
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongoDbFactory(), mappingMongoConverter());
    }

    @Bean
    public MongoTemplate logMongoTemplate() {

        return new MongoTemplate(logMongoDbFactory(), mappingMongoConverter());
    }

    @Bean
    @Override
    public MongoDbFactory mongoDbFactory() {
        return new SimpleMongoDbFactory(mongoClient(), getDatabaseName());
    }

    @Bean
    public MongoDbFactory logMongoDbFactory() {
        MongoDbFactory logMongoDbFactory = new SimpleMongoDbFactory(logMongoClient(), this.logCollectionName);
        return logMongoDbFactory;
    }

    @Override
    @Bean
    public MongoClient mongoClient() {

        return new MongoClient(new MongoClientURI(this.mongoDBUrl));
    }

    @Bean
    public MongoClient logMongoClient() {

        MongoClient logMongoClient = new MongoClient(new MongoClientURI(this.mongoLogUrl));
        return logMongoClient;
    }

    @Override
    protected String getDatabaseName() {
        return this.dataBaseName;
    }


    @Bean
    public MappingMongoConverter mappingMongoConverter() {

        MappingMongoConverter mmc = new MappingMongoConverter(dbRefResolver(), mongoMappingContext());
        mmc.setTypeMapper(getTypeMapper());
        return mmc;
    }

    @Bean
    public MongoTypeMapper getTypeMapper() {

        DefaultMongoTypeMapper mm = new DefaultMongoTypeMapper(null);
        return mm;
    }

    @Bean
    public MongoMappingContext mongoMappingContext() {

        return new MongoMappingContext();
    }

    @Bean
    public DbRefResolver dbRefResolver() {

        return new DefaultDbRefResolver(mongoDbFactory());
    }
}
