public class BiometricAuth implements AuthMethod {

    @Override
    public boolean authenticate(String userId) {
        System.out.println("Authentification biométrique pour " + userId);
        return true;
    }
}
