# Keycloak API examples

I've created a Spring Boot app which contains several examples of how to interact with Keycloak Java API.
The app contains a REST controller with basic users and roles operations. 

The purpose of it is to show how to use the Keycloak APIs, by providing basic examples as below.

## How to run the app

Run the Spring Boot app using Maven

```shell script
mvn spring-boot:run
```


## There are examples for:

* create, read, update and delete users

*Sample for get users*
```java
public List<UserRepresentation> getUsers() {
    Keycloak keycloak = getKeycloakInstance();
    List<UserRepresentation> userRepresentations = keycloak.realm(keycloakRealm).users().list();
    return userRepresentations;
}
```
* get the available roles

```java
public ResponseEntity<List<RoleRepresentation>> getRoles() {
    Keycloak keycloak = getKeycloakInstance();
    ClientRepresentation clientRepresentation = keycloak.realm(keycloakRealm).clients().findByClientId(keycloakClient).get(0);
    List<RoleRepresentation> roles = keycloak.realm(keycloakRealm).clients().get(clientRepresentation.getId()).roles().list();
    return ResponseEntity.ok(roles);
}
```

* get roles for user

```java
public ResponseEntity<List<RoleRepresentation>> getRolesByUser(@RequestParam String username) {
    Keycloak keycloak = getKeycloakInstance();
    Optional<UserRepresentation> user = keycloak.realm(keycloakRealm).users().search(username).stream()
            .filter(u -> u.getUsername().equals(username)).findFirst();
    if (user.isPresent()) {
        UserRepresentation userRepresentation = user.get();
        UserResource userResource = keycloak.realm(keycloakRealm).users().get(userRepresentation.getId());
        ClientRepresentation clientRepresentation = keycloak.realm(keycloakRealm).clients().findByClientId(keycloakClient).get(0);
        List<RoleRepresentation> roles = userResource.roles().clientLevel(clientRepresentation.getId()).listAll();
        return ResponseEntity.ok(roles);
    } else {
        return ResponseEntity.badRequest().build();
    }
}
```
