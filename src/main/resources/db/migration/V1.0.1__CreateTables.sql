create table search_config
(
    GUID             varchar(255) not null,
    OPTLOCK          int4         not null,
    creationDate     timestamp,
    creationUser     varchar(255),
    modificationDate timestamp,
    modificationUser varchar(255),
    api_version      varchar(255),
    application      varchar(255),
    page             varchar(255),
    name             varchar(255),
    fieldListVersion int4,
    values           text,
    read_only        boolean,
    advanced         boolean,
    columns          text,
    user   varchar(255),
    global_config    boolean,
    primary key (GUID)
);



alter table if exists search_config
    add constraint ck_config unique (name, page, application);