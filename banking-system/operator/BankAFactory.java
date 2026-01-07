
public class BankAFactory implements OperatorFactory {

    @Override
    public AccountValidator createAccountValidator() {
        return accountId -> {
            System.out.println("Validation Banque A pour " + accountId);
            return true;
        };
    }

    @Override
    public RateCalculator createRateCalculator() {
        return () -> 2.5;
    }

    @Override
    public NotificationModule createNotificationModule() {
        return message ->
            System.out.println("Notification Banque A : " + message);
    }
}
