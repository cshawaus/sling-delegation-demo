package com.slingdelegation.core.models;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.models.LayoutContainer;
import com.adobe.cq.wcm.core.components.models.ListItem;
import com.adobe.cq.wcm.core.components.models.datalayer.ComponentData;
import com.day.cq.wcm.foundation.model.export.AllowedComponentsExporter;
import com.day.cq.wcm.foundation.model.responsivegrid.ResponsiveGrid;
import com.day.cq.wcm.foundation.model.responsivegrid.export.ResponsiveGridExporter;
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.slingdelegation.core.export.CustomContainerExporter;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.via.ResourceSuperType;
import org.osgi.annotation.versioning.ProviderType;

import java.util.List;
import java.util.Map;

@Model(
        adaptables = SlingHttpServletRequest.class,
        adapters = {LayoutContainer.class, ComponentExporter.class},
        resourceType = CustomContainerModel.RESOURCE_TYPE,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
@Exporter(
        name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
        extensions = ExporterConstants.SLING_MODEL_EXTENSION
)
@JsonSerialize(
        as = CustomContainerExporter.class
)
@ProviderType
public class CustomContainerModel extends ResponsiveGrid implements LayoutContainer, CustomContainerExporter {
    public static final String RESOURCE_TYPE = "demo/components/container";

    @Self
    @Via(type = ResourceSuperType.class)
    private LayoutContainer layoutContainer;

    public String getCustomProperty() {
        return "foo";
    }

    @Override
    public @NotNull
    LayoutContainer.LayoutType getLayout() {
        return layoutContainer.getLayout();
    }

    @JsonIgnore
    @Override
    public @NotNull
    List<ListItem> getItems() {
        return layoutContainer.getItems();
    }

    @Override
    public @Nullable
    ComponentData getData() {
        return layoutContainer.getData();
    }

    @JsonIgnore
    @Override
    public @Nullable
    String getId() {
        return layoutContainer.getId();
    }

    @JsonIgnore
    @Override
    public @Nullable
    String getBackgroundStyle() {
        return layoutContainer.getBackgroundStyle();
    }

    @Override
    public @NotNull
    String getExportedType() {
        return RESOURCE_TYPE;
    }

    @NotNull
    @Override
    public Map<String, ? extends ComponentExporter> getExportedItems() {
        return layoutContainer.getExportedItems();
    }

    @NotNull
    @Override
    public String[] getExportedItemsOrder() {
        return layoutContainer.getExportedItemsOrder();
    }
}
