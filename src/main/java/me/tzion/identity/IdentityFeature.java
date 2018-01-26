package me.tzion.identity;

import org.glassfish.jersey.server.model.AnnotatedMethod;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Optional;

public class IdentityFeature implements DynamicFeature {
    private final IdentityFilter identityFilter;
    public IdentityFeature(IdentityFilter identityFilter) {
        this.identityFilter = identityFilter;
    }

    @Override
    public void configure(ResourceInfo resourceInfo, FeatureContext context) {
        AnnotatedMethod annotatedMethod = new AnnotatedMethod(resourceInfo.getResourceMethod());
        Annotation[][] parameterAnnotations = annotatedMethod.getParameterAnnotations();
        Class<?>[] parameterTypes = annotatedMethod.getParameterTypes();

        for (int i=0; i< parameterAnnotations.length; i++) {
            for (Annotation a : parameterAnnotations[i]) {
                if (a instanceof Current) {
                    Class<?> parameterType = parameterTypes[i];
                    // Optional need concrete class, because we need handle user not authorized as Optional.empty
                    if (parameterType.equals(Optional.class)) {
                        context.register(new ExceptionableFilter(identityFilter));
                        return;
                    } else {
                        context.register(identityFilter);
                        return;
                    }
                }
            }
        }
    }

    class ExceptionableFilter implements ContainerRequestFilter {
        private IdentityFilter identityFilter;

        public ExceptionableFilter(IdentityFilter identityFilter) {
            this.identityFilter = identityFilter;
        }

        @Override
        public void filter(ContainerRequestContext requestContext) throws IOException {
            try {
                identityFilter.filter(requestContext);
            } catch (Exception ignored) {
                // ignore
            }
        }
    }
}
