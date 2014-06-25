package org.keycloak.models.cache;

import org.keycloak.models.RealmModel;
import org.keycloak.models.cache.entities.CachedApplication;
import org.keycloak.models.cache.entities.CachedOAuthClient;
import org.keycloak.models.cache.entities.CachedRealm;
import org.keycloak.models.cache.entities.CachedRole;
import org.keycloak.models.cache.entities.CachedUser;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface KeycloakCache {
    void clear();

    CachedRealm getCachedRealm(String id);

    void invalidateCachedRealm(CachedRealm realm);

    void addCachedRealm(CachedRealm realm);

    CachedRealm getCachedRealmByName(String name);

    void invalidateCachedRealmById(String id);

    CachedApplication getApplication(String id);

    void invalidateApplication(CachedApplication app);

    void addCachedApplication(CachedApplication app);

    void invalidateCachedApplicationById(String id);

    CachedOAuthClient getOAuthClient(String id);

    void invalidateOAuthClient(CachedOAuthClient client);

    void addCachedOAuthClient(CachedOAuthClient client);

    void invalidateCachedOAuthClientById(String id);

    CachedRole getRole(String id);

    void invalidateRole(CachedRole role);

    void addCachedRole(CachedRole role);

    void invalidateCachedRoleById(String id);


    void invalidateRoleById(String id);

    CachedUser getCachedUser(String id);

    void invalidateCachedUser(CachedUser user);

    void addCachedUser(CachedUser user);

    CachedUser getCachedUserByUsername(String name, RealmModel realm);
    CachedUser getCachedUserByEmail(String name, RealmModel realm);

    void invalidedCachedUserById(String id);

    void invalidateCachedUserById(String id);
}