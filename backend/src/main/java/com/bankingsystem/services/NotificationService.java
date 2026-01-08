package com.bankingsystem.services;

import com.bankingsystem.interfaces.ISMSAdapter;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service de notifications globales (Pattern Singleton)
 */
@Service
public class NotificationService {
    private static volatile NotificationService instance;
    private static final Object lockObject = new Object();
    
    private Map<String, Object> configuration;
    private ISMSAdapter smsAdapter;
    
    private NotificationService() {
        this.configuration = new ConcurrentHashMap<>();
        this.smsAdapter = null;
        System.out.println("NotificationService: Instance unique créée");
    }
    
    public static NotificationService getInstance() {
        if (instance == null) {
            synchronized (lockObject) {
                if (instance == null) {
                    instance = new NotificationService();
                }
            }
        }
        return instance;
    }
    
    public void setSMSAdapter(ISMSAdapter adapter) {
        this.smsAdapter = adapter;
        System.out.println("NotificationService: Adaptateur SMS configuré");
    }
    
    public void setConfiguration(String key, Object value) {
        configuration.put(key, value);
        System.out.println("NotificationService: Configuration mise à jour - " + key + " = " + value);
    }
    
    public Object getConfiguration(String key) {
        return configuration.get(key);
    }
    
    public void envoyerNotification(String destination, String message) {
        if (smsAdapter != null) {
            try {
                smsAdapter.sendSMS(destination, message);
                System.out.println("NotificationService: Notification envoyée à " + destination);
            } catch (Exception e) {
                System.err.println("NotificationService: Erreur lors de l'envoi - " + e.getMessage());
            }
        } else {
            System.err.println("NotificationService: Adaptateur SMS non configuré");
        }
    }
}