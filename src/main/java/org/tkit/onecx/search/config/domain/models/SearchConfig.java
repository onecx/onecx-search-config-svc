package org.tkit.onecx.search.config.domain.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import org.hibernate.annotations.TenantId;
import org.tkit.quarkus.jpa.models.TraceableEntity;

import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "search_config", uniqueConstraints = {
        @UniqueConstraint(name = "ck_config", columnNames = { "name", "product_name", "page", "appId", "tenant_id" })
})
@SuppressWarnings("java:S2160")
public class SearchConfig extends TraceableEntity {

    @Column(name = "product_name")
    private String productName;
    @Column
    private String name;
    @Column
    private String page;
    @Column(name = "appId")
    private String appId;
    @Column
    private int fieldListVersion;
    @Column
    private String values;
    @Column(name = "read_only")
    private boolean readOnly;
    @Column(name = "advanced")
    private boolean advanced;
    @Column(name = "columns")
    private String columns;
    @Column(name = "api_version")
    private String apiVersion;
    @TenantId
    @Column(name = "tenant_id")
    private String tenantId;

}
