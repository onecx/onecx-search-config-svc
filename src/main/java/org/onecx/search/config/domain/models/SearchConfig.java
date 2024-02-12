package org.onecx.search.config.domain.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import org.tkit.quarkus.jpa.models.TraceableEntity;

import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "search_config")
@SuppressWarnings("java:S2160")
public class SearchConfig extends TraceableEntity {

    private String name;
    private String page;
    private String application;
    private int fieldListVersion;
    private String values;
    @Column(name = "read_only")
    private boolean isReadOnly;
    @Column(name = "advanced")
    private boolean isAdvanced;
    @Column(name = "columns")
    private String columns;
    @Column(name = "api_version")
    private String apiVersion;
    private String tenant_id;

}
