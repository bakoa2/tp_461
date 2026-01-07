public class BankBFactory implements OperatorFactory {

    @Override
    public AccountValidator createAccountValidator() {
        return accountId -> {
            System.out.println("Validation Banque B pour " + accountId);
            return true;
        };
    }

    @Override
    public RateCalculator createRateCalculator() {
        return () -> 3.1;
    }

    @Override
    public NotificationModule createNotificationModule() {
        return message ->
            System.out.println("Notification Banque B : " + message);
    }
}
