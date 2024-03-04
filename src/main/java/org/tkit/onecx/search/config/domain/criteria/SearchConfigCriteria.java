package org.tkit.onecx.search.config.domain.criteria;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchConfigCriteria {

    private String configId;
    private String productName;
    private String name;
    private String appId;
    private String page;
    private Integer pageNumber;
    private Integer pageSize;
}
