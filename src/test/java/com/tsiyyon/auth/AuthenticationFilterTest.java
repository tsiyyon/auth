package com.tsiyyon.auth;

import com.tsiyyon.auth.support.Resources;
import com.tsiyyon.auth.support.User;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.util.Objects;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class AuthenticationFilterTest extends JerseyTest {
    @Override
    protected Application configure() {
        ResourceConfig resourceConfig = new ResourceConfig();
        resourceConfig.register(Resources.class);
        resourceConfig.property(ServerProperties.RESPONSE_SET_STATUS_OVER_SEND_ERROR, false);
        resourceConfig.register(new AuthFeature(
                new AuthenticationFilter(
                        new Credentials<String>() {
                    @Override
                    public Optional<String> from(ContainerRequestContext context) {
                        return Optional.ofNullable(context.getHeaderString(HttpHeaders.AUTHORIZATION));
                    }
                }, (Authenticator<String, User>) credential -> {
                    if (Objects.equals(credential, "token_for_kayla")) {
                        return Optional.of(new User("kayla"));
                    }
                    return Optional.empty();
                }, (context) -> Response.status(401).build()
                )));

        resourceConfig.register(new AuthValueFactoryProvider.Binder<>(User.class));
        return resourceConfig;
    }

    @Override
    protected void configureClient(ClientConfig config) {
        config.register(new LoggingFilter());
        super.configureClient(config);
    }

    @Test
    public void should_able_to_return_401_when_unauthorized() throws Exception {
        Response response = target("/required").request().get();
        assertThat(response.getStatus(), is(Response.Status.UNAUTHORIZED.getStatusCode()));
    }

    @Test
    public void should_able_to_return_200_when_authorized() throws Exception {
        Response response = target("/required").request().header(HttpHeaders.AUTHORIZATION, "token_for_kayla").get();
        assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
    }

    @Test
    public void should_return_200_when_no_authorized_for_non_restricted_resource() throws Exception {
        Response response = target("/optional").request().get();
        assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
        String username = response.readEntity(String.class);
        assertThat(username, is("anonymous"));
    }

    @Test
    public void should_return_200_with_user_info_when_authorized_for_non_restricted_resource() throws Exception {
        Response response = target("/optional").request().header(HttpHeaders.AUTHORIZATION, "token_for_kayla").get();
        assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
        String username = response.readEntity(String.class);
        assertThat(username, is("kayla"));
    }


    @Test
    public void should_return_200_when_unauthorized_for_opened_resource() throws Exception {
        Response response = target("/opened").request().get();
        assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
    }
}
