public interface OperatorFactory {
    AccountValidator createValidator();
    RateCalculator createRateCalculator();
    NotificationModule createNotifier();
}
