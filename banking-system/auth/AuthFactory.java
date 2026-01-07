public class AuthFactory {

    public static AuthMethod create(String type) {

        switch (type.toUpperCase()) {
            case "PASSWORD":
                return new PasswordAuth();
            case "OTP":
                return new OTPAuth();
            case "BIOMETRIC":
                return new BiometricAuth();
            default:
                throw new IllegalArgumentException("Méthode d'authentification inconnue");
        }
    }
}
