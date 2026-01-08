package com.bankingsystem.adapters;

import com.bankingsystem.interfaces.ISMSAdapter;
import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * Adaptateur SMS pour MTN (Pattern Adapter)
 */
@Component("mtnSMSAdapter")
public class MtnSMSAdapter implements ISMSAdapter {
    
    private Random random = new Random();
    private boolean isAvailable = true;
    
    @Override
    public void sendSMS(String destination, String message) {
        if (!isAvailable()) {
            throw new RuntimeException("Service SMS MTN indisponible");
        }
        
        // Simuler l'envoi de SMS via l'API MTN
        System.out.println("📱 [MTN SMS] Envoi vers " + destination + ": " + message);
        
        // Simuler une latence réseau (plus lente qu'Orange)
        try {
            Thread.sleep(150 + random.nextInt(300));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Simuler un taux d'échec de 3%
        if (random.nextDouble() < 0.03) {
            throw new RuntimeException("Échec de l'envoi SMS via MTN");
        }
        
        System.out.println("✅ [MTN SMS] Message envoyé avec succès à " + destination);
    }
    
    @Override
    public String getProviderName() {
        return "MTN SMS";
    }
    
    @Override
    public boolean isAvailable() {
        // Simuler une disponibilité de 95%
        isAvailable = random.nextDouble() < 0.95;
        return isAvailable;
    }
    
    /**
     * Obtenir des informations sur l'adaptateur
     */
    public String getAdapterInfo() {
        return "MTN SMS Adapter - Version 1.0\n" +
               "Fournisseur: MTN Cameroun\n" +
               "Capacité: 80 SMS/minute\n" +
               "Coût: 45 XAF/SMS";
    }
}