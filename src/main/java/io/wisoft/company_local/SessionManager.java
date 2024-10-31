package io.wisoft.company_local;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SessionManager {
  private final Map<String, SessionData> sessionStore = new ConcurrentHashMap<>();

  public void storeSession(String sessionId, String userEmail) {
    long expiryTime = System.currentTimeMillis() + 30 * 60 * 1000; // 30분 후 만료
    sessionStore.put(sessionId, new SessionData(userEmail, expiryTime));
  }

  public String validateSession(String sessionId) {
    SessionData sessionData = sessionStore.get(sessionId);
    if (sessionData != null && System.currentTimeMillis() < sessionData.expiryTime) {
      return sessionData.userEmail;
    }
    return null;
  }

  private static class SessionData {
    String userEmail;
    long expiryTime;

    SessionData(String userEmail, long expiryTime) {
      this.userEmail = userEmail;
      this.expiryTime = expiryTime;
    }
  }
}
