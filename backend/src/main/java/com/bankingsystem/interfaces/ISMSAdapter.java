package com.bankingsystem.interfaces;

/**
 * Interface pour les adaptateurs SMS (Pattern Adapter)
 */
public interface ISMSAdapter {
    void sendSMS(String destination, String message);
    String getProviderName();
    boolean isAvailable();
}