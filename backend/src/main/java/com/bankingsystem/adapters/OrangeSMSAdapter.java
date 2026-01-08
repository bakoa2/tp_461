package com.bankingsystem.adapters;

import com.bankingsystem.interfaces.ISMSAdapter;
import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * Adaptateur SMS pour Orange (Pattern Adapter)
 */
@Component("orangeSMSAdapter")
public class OrangeSMSAdapter implements ISMSAdapter {
    
    private Random random = new Random();
    private boolean isAvailable = true;
    
    @Override
    public void sendSMS(String destination, String message) {
        if (!isAvailable()) {
            throw new RuntimeException("Service SMS Orange indisponible");
        }
        
        // Simuler l'envoi de SMS via l'API Orange
        System.out.println("📱 [ORANGE SMS] Envoi vers " + destination + ": " + message);
        
        // Simuler une latence réseau
        try {
            Thread.sleep(100 + random.nextInt(200));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Simuler un taux d'échec de 2%
        if (random.nextDouble() < 0.02) {
            throw new RuntimeException("Échec de l'envoi SMS via Orange");
        }
        
        System.out.println("✅ [ORANGE SMS] Message envoyé avec succès à " + destination);
    }
    
    @Override
    public String getProviderName() {
        return "Orange SMS";
    }
    
    @Override
    public boolean isAvailable() {
        // Simuler une disponibilité de 98%
        isAvailable = random.nextDouble() < 0.98;
        return isAvailable;
    }
    
    /**
     * Obtenir des informations sur l'adaptateur
     */
    public String getAdapterInfo() {
        return "Orange SMS Adapter - Version 1.0\n" +
               "Fournisseur: Orange Cameroun\n" +
               "Capacité: 100 SMS/minute\n" +
               "Coût: 50 XAF/SMS";
    }
}