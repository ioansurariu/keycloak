package ro.surariu.ioan.controller;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/keycloak")
public class KeycloakController {

    @Value("${keycloak.resource}")
    private String keycloakClient;

    @Value("${keycloak.auth-server-url}")
    private String keycloakUrl;

    @Value("${keycloak.realm}")
    private String keycloakRealm;

    @Value("${is.keycloak.admin.user}")
    private String keycloakAdminUser;

    @Value("${is.keycloak.admin.password}")
    private String keycloakAdminPassword;

    private Keycloak getKeycloakInstance() {
        return Keycloak.getInstance(
                keycloakUrl,
                "master",
                keycloakAdminUser,
                keycloakAdminPassword,
                "admin-cli");
    }

    @PostMapping("/user")
    public ResponseEntity<String> createUser(String username, String password) {
        CredentialRepresentation credentials = new CredentialRepresentation();
        credentials.setType(CredentialRepresentation.PASSWORD);
        credentials.setValue(password);
        credentials.setTemporary(false);
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setUsername(username);
        userRepresentation.setCredentials(Arrays.asList(credentials));
        userRepresentation.setEnabled(true);
        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put("description", Arrays.asList("A test user"));
        userRepresentation.setAttributes(attributes);
        Keycloak keycloak = getKeycloakInstance();
        Response result = keycloak.realm(keycloakRealm).users().create(userRepresentation);
        return new ResponseEntity<>(HttpStatus.valueOf(result.getStatus()));
    }

    @GetMapping("/users")
    public List<UserRepresentation> getUsers() {
        Keycloak keycloak = getKeycloakInstance();
        List<UserRepresentation> userRepresentations = keycloak.realm(keycloakRealm).users().list();
        return userRepresentations;
    }

    @PutMapping("/user")
    public void updateUserDescriptionAttribute(String username, String description) {
        Keycloak keycloak = getKeycloakInstance();
        UserRepresentation userRepresentation = keycloak.realm(keycloakRealm).users().search(username).get(0);
        UserResource userResource = keycloak.realm(keycloakRealm).users().get(userRepresentation.getId());
        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put("description", Arrays.asList(description));
        userRepresentation.setAttributes(attributes);
        userResource.update(userRepresentation);
    }

    @DeleteMapping("/user")
    public void deleteUser(String username) {
        Keycloak keycloak = getKeycloakInstance();
        UsersResource users = keycloak.realm(keycloakRealm).users();
        List<UserRepresentation> userRepresentations = users.search(username);
        for (UserRepresentation userRepresentation : userRepresentations) {
            if (userRepresentation.getUsername().equals(username)) {
                keycloak.realm(keycloakRealm).users().delete(userRepresentation.getId());
                break;
            }
        }
    }

    @GetMapping("/roles")
    public List<RoleRepresentation> getRoles() {
        Keycloak keycloak = getKeycloakInstance();
        ClientRepresentation clientRepresentation = keycloak.realm(keycloakRealm).clients().findByClientId(keycloakClient).get(0);
        List<RoleRepresentation> roles = keycloak.realm(keycloakRealm).clients().get(clientRepresentation.getId()).roles().list();
        return roles;
    }

    @GetMapping("/roles-by-user")
    public List<RoleRepresentation> getRolesByUser(@RequestParam("username") String username) {
        Keycloak keycloak = getKeycloakInstance();
        UserRepresentation userRepresentation = keycloak.realm(keycloakRealm).users().search(username).get(0);
        UserResource userResource = keycloak.realm(keycloakRealm).users().get(userRepresentation.getId());
        ClientRepresentation clientRepresentation = keycloak.realm(keycloakRealm).clients().findByClientId(keycloakClient).get(0);

        List<RoleRepresentation> roles = userResource.roles().clientLevel(clientRepresentation.getId()).listAll();
        return roles;
    }
}
