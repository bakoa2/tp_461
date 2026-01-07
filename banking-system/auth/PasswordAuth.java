
public class PasswordAuth implements AuthMethod {

    @Override
    public boolean authenticate(String userId) {
        System.out.println("Authentification par mot de passe pour " + userId);
        return true;
    }
}
