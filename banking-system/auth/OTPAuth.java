public class OTPAuth implements AuthMethod {

    @Override
    public boolean authenticate(String userId) {
        System.out.println("Authentification par OTP pour " + userId);
        return true;
    }
}
