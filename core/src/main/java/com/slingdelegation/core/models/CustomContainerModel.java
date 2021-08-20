package com.slingdelegation.core.models;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.models.LayoutContainer;
import com.adobe.cq.wcm.core.components.models.ListItem;
import com.adobe.cq.wcm.core.components.models.datalayer.ComponentData;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.components.ComponentContext;
import com.day.cq.wcm.foundation.model.responsivegrid.ResponsiveGrid;
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.slingdelegation.core.export.CustomContainerExporter;
import com.slingdelegation.core.support.HierarchyComponentContextWrapper;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.wrappers.SlingHttpServletRequestWrapper;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.via.ResourceSuperType;
import org.apache.sling.models.factory.ModelFactory;
import org.osgi.annotation.versioning.ProviderType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
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
    private static final Logger logger = LoggerFactory.getLogger(CustomContainerModel.class);

    protected static final String RESOURCE_TYPE = "demo/components/container";

    /**
     * Name of the request attribute that defines whether the page is an entry point of the request.
     */
    public static final String ATTR_HIERARCHY_ENTRY_POINT_PAGE = "com.adobe.aem.spa.project.core.models.Page.entryPointPage";

    /**
     * Request attribute key of the component context
     */
    public static final String ATTR_COMPONENT_CONTEXT = "com.day.cq.wcm.componentcontext";

    /**
     * Request attribute key of the current page
     */
    public static final String ATTR_CURRENT_PAGE = "currentPage";

    @Inject
    private ModelFactory modelFactory;

    @Self
    private SlingHttpServletRequest request;

    @Self
    @Via(type = ResourceSuperType.class)
    private LayoutContainer layoutContainer;

    @PostConstruct
    protected void postInit() {
        try {
            Resource resource = request.getResource();
            Page currentPage = resource.adaptTo(Page.class);

            // If the 'LayoutContainer' instance is either 'null' or it has no items available then we can
            // attempt to get a new instance using a wrapped request and adapting it to 'LayoutContainer'.
            if (layoutContainer == null || layoutContainer.getItems().size() == 0) {
                layoutContainer = modelFactory.getModelFromWrappedRequest(
                        createHierarchyServletRequest(request, currentPage, currentPage),
                        resource,
                        LayoutContainer.class);
            }
        } catch (Exception e) {
            logger.info("Unable to bind the current page request to 'LayoutContainer'", e);
        }
    }

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

    private SlingHttpServletRequest createHierarchyServletRequest(
            @NotNull SlingHttpServletRequest request,
            @NotNull com.day.cq.wcm.api.Page page, @Nullable com.day.cq.wcm.api.Page entryPage) {
        SlingHttpServletRequest wrapperRequest = new SlingHttpServletRequestWrapper(request);

        ComponentContext componentContext = (ComponentContext) request.getAttribute(ATTR_COMPONENT_CONTEXT);

        // When traversing child pages, the currentPage must be updated
        HierarchyComponentContextWrapper componentContextWrapper = new HierarchyComponentContextWrapper(componentContext, page);
        wrapperRequest.setAttribute(ATTR_COMPONENT_CONTEXT, componentContextWrapper);
        wrapperRequest.setAttribute(ATTR_CURRENT_PAGE, page);
        wrapperRequest.setAttribute(ATTR_HIERARCHY_ENTRY_POINT_PAGE, entryPage);

        return wrapperRequest;
    }
}
