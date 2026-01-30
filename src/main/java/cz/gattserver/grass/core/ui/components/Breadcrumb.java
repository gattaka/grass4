package cz.gattserver.grass.core.ui.components;

import java.util.Arrays;
import java.util.List;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.router.internal.HasUrlParameterFormat;

public class Breadcrumb extends Div {

    private static final long serialVersionUID = 3874999284861747099L;

    public static class BreadcrumbElement {
        private String caption;
        private String url;

        private Class<? extends Component> routeTarget;
        private RouteParameters routeParameters;

        private RouterLink routerLink;

        public BreadcrumbElement(String caption, String url) {
            this.url = url;
            this.caption = caption;
        }

        public BreadcrumbElement(String caption, Class<? extends Component> routeTarget) {
            this(caption, routeTarget, null);
        }

        public BreadcrumbElement(RouterLink routerLink) {
            this.routerLink = routerLink;
        }

        public BreadcrumbElement(String caption, Class<? extends Component> routeTarget,
                                 RouteParameters routeParameters) {
            this.caption = caption;
            this.routeTarget = routeTarget;
            this.routeParameters = routeParameters;
        }

        public <T> BreadcrumbElement(String caption, Class<? extends Component> routeTarget, T parameter) {
            this(caption, routeTarget, HasUrlParameterFormat.getParameters(parameter));
        }

        public String getCaption() {
            return caption;
        }

        public void setCaption(String caption) {
            this.caption = caption;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public Breadcrumb() {
        setId("breadcrumb");
    }

    public Breadcrumb(List<BreadcrumbElement> breadcrumbElements) {
        this();
        initBreadcrumb(breadcrumbElements);
    }

    private Component createBreadcrumbElementLink(BreadcrumbElement element) {
        if (element.url != null) {
            return new Anchor(element.getUrl(), element.getCaption());
        } else if (element.routerLink != null) {
            return element.routerLink;
        } else {
            if (element.routeParameters != null) {
                return new RouterLink(element.getCaption(), element.routeTarget, element.routeParameters);
            } else {
                return new RouterLink(element.getCaption(), element.routeTarget);
            }
        }
    }

    private void initBreadcrumb(List<BreadcrumbElement> breadcrumbElements) {
        // konstrukce breadcrumb v opačném pořadí (správném)
        BreadcrumbElement element;
        for (int i = breadcrumbElements.size() - 1; i >= 0; i--) {
            element = breadcrumbElements.get(i);
            if (i != breadcrumbElements.size() - 1) {
                Image separator = new Image("img/bullet.png", "bullet");
                add(separator);
            }
            add(createBreadcrumbElementLink(element));
        }
    }

    public void resetBreadcrumb(BreadcrumbElement... breadcrumbElements) {
        resetBreadcrumb(Arrays.asList(breadcrumbElements));
    }

    public void resetBreadcrumb(List<BreadcrumbElement> breadcrumbElements) {
        removeAll();
        initBreadcrumb(breadcrumbElements);
    }

}
