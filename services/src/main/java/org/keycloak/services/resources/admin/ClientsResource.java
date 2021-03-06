package org.keycloak.services.resources.admin;

import org.jboss.logging.Logger;
import org.jboss.resteasy.annotations.cache.NoCache;
import org.jboss.resteasy.spi.NotFoundException;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.keycloak.models.ClientModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.ModelDuplicateException;
import org.keycloak.models.RealmModel;
import org.keycloak.models.utils.ModelToRepresentation;
import org.keycloak.models.utils.RepresentationToModel;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.services.ErrorResponse;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.List;

/**
 * Base resource class for managing a realm's clients.
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ClientsResource {
    protected static final Logger logger = Logger.getLogger(RealmAdminResource.class);
    protected RealmModel realm;
    private RealmAuth auth;

    @Context
    protected KeycloakSession session;

    public ClientsResource(RealmModel realm, RealmAuth auth) {
        this.realm = realm;
        this.auth = auth;
        
        auth.init(RealmAuth.Resource.CLIENT);
    }

    /**
     * List of clients belonging to this realm.
     *
     * @return
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @NoCache
    public List<ClientRepresentation> getClients() {
        auth.requireAny();

        List<ClientRepresentation> rep = new ArrayList<>();
        List<ClientModel> clientModels = realm.getClients();

        boolean view = auth.hasView();
        for (ClientModel clientModel : clientModels) {
            if (view) {
                rep.add(ModelToRepresentation.toRepresentation(clientModel));
            } else {
                ClientRepresentation client = new ClientRepresentation();
                client.setId(clientModel.getId());
                client.setClientId(clientModel.getClientId());
                rep.add(client);
            }
        }

        return rep;
    }

    /**
     * Create a new client.  Client client_id must be unique!
     *
     * @param uriInfo
     * @param rep
     * @return
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createClient(final @Context UriInfo uriInfo, final ClientRepresentation rep) {
        auth.requireManage();

        try {
            ClientModel clientModel = RepresentationToModel.createClient(session, realm, rep, true);
            return Response.created(uriInfo.getAbsolutePathBuilder().path(getClientPath(clientModel)).build()).build();
        } catch (ModelDuplicateException e) {
            return ErrorResponse.exists("Client " + rep.getClientId() + " already exists");
        }
    }

    protected String getClientPath(ClientModel clientModel) {
        return clientModel.getClientId();
    }

    /**
     * Base path for managing a specific client.
     *
     * @param name
     * @return
     */
    @Path("{app-name}")
    public ClientResource getClient(final @PathParam("app-name") String name) {
        ClientModel clientModel = getClientByPathParam(name);
        if (clientModel == null) {
            throw new NotFoundException("Could not find client: " + name);
        }
        ClientResource clientResource = new ClientResource(realm, auth, clientModel, session);
        ResteasyProviderFactory.getInstance().injectProperties(clientResource);
        return clientResource;
    }

    protected ClientModel getClientByPathParam(String name) {
        return realm.getClientByClientId(name);
    }

}
