package com.slingdelegation.core.export;

import com.adobe.cq.export.json.ContainerExporter;
import com.day.cq.wcm.foundation.model.responsivegrid.export.ResponsiveGridExporter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.osgi.annotation.versioning.ProviderType;

@JsonInclude(JsonInclude.Include.NON_NULL)
@ProviderType
public interface CustomContainerExporter extends ResponsiveGridExporter, ContainerExporter {
    @JsonProperty("myCustomProperty")
    String getCustomProperty();
}
