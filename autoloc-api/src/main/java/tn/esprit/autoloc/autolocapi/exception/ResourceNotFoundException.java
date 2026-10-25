package tn.esprit.autoloc.autolocapi.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resourceName, Long id) {
        super(resourceName + " introuvable avec l'id : " + id);
    }
}