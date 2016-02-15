package com.xebia.shopmanager.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xebia.shopmanager.Config;
import com.xebia.shopmanager.rest.TimeoutPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ShopManager {

    @Autowired
    TimeoutPolicy timeoutPolicy;

    @Autowired
    RabbitTemplate rabbitTemplate;

    private ObjectMapper mapper = new ObjectMapper();

    private static Logger LOG = LoggerFactory.getLogger(ShopManager.class);

    private final List<Session> sessions = new ArrayList<>();

    public ShopManager() {
        sessionCleaner.start();
    }

    public void registerClerk(Clerk clerk) {
        Session session = new Session(System.currentTimeMillis() + timeoutPolicy.getTimeout(), clerk);
        sessions.add(session);
        LOG.info("Adding " + session + " to list of sessions " + System.currentTimeMillis());
        sessions.sort((session1, session2) -> {
            if (session1.getEta() < session2.getEta()) {
                return -1;
            } else {
                return 1;
            }
        });
        LOG.info("Sessions: " + sessions);
    }

    protected List<Session> expiredSessions = new ArrayList<>();

    Thread sessionCleaner = new Thread(new Runnable() {
        @Override
        public void run() {
            while (true) {
                for (Session session : sessions) {
                    if (session.getEta() < System.currentTimeMillis()) {
                        try {
                            String sessionAsJson = mapper.writeValueAsString(session);
                            rabbitTemplate.convertAndSend(Config.shopExchange, Config.sessionExpired, sessionAsJson);
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        } finally {
                            LOG.info("adding " + session + " to expired sessions");
                            expiredSessions.add(session);
                        }
                    } else {
                        break;
                    }
                }
                sessions.removeAll(expiredSessions);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    );

    public List<Session> getSessions() {
        List<Session> newSessions = new ArrayList(sessions.size());
        newSessions.addAll(sessions);
        return newSessions;
    }

    public List<Session> getExpiredSessions() {
        List<Session> newSessions = new ArrayList(expiredSessions.size());
        newSessions.addAll(expiredSessions);
        return newSessions;
    }

    public Session findSessionByClerk(Clerk clerk) throws InvalidStatusException {
        List<Session> result = new ArrayList<>();
        for (Session session : sessions) {
            if (session.getClerk().getUuid().equals(clerk.getUuid())) {
                result.add(session);
                break;
            }
        }
        if (result.size()==0) {
            throw new InvalidStatusException("No session for clerk " + clerk);
        }
        return result.get(0);
    }
}
