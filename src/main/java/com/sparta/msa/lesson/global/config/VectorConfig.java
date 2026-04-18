package com.sparta.msa.lesson.global.config;

import javax.sql.DataSource;
import lombok.Setter;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore.PgDistanceType;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore.PgIndexType;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Setter
@Configuration
@ConfigurationProperties(prefix = "spring.ai.vectorstore.pgvector")
public class VectorConfig {

  private int dimensions;

  @Bean
  public VectorStore vectorStore(DataSource dataSource, EmbeddingModel embeddingModel) {

    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

    return PgVectorStore.builder(jdbcTemplate, embeddingModel)
        .dimensions(dimensions)
        .distanceType(PgDistanceType.COSINE_DISTANCE)
        .indexType(PgIndexType.HNSW)
        .initializeSchema(false)
        .removeExistingVectorStoreTable(false)
        .vectorTableValidationsEnabled(true)
        .schemaName("public")
        .vectorTableName("vector_store")
        .build();
  }

}
