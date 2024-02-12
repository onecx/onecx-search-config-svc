package org.onecx.announcement.domain.models;

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

    //    private String name;
    //    private String page;
    //    private String application;
    //
    //    private int fieldListVersion;
    //
    //    private String values;
    //    @Column(name = "read_only")
    //    private boolean isReadOnly;
    //    @Column(name = "isAdvanced")
    //    private boolean advanced;
    //    @Column(name = "appId")
    //    private String appId;
    //
    //    api_version      varchar(255),
    //
    //    application      varchar(255),
    //
    //    page             varchar(255),
    //
    //    name             varchar(255),
    //    fieldListVersion int4,
    //    values           text,
    //    read_only        boolean,
    //    advanced         boolean,
    //    columns          text,
    //    default_config   boolean,
    //    global_config    boolean,
}
