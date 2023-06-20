package com.sidiot.server.session;

/**
 * @author sidiot
 */
public abstract class SessionFactory {

    private static Session session = new SessionMemoryImpl();

    public static Session getSession() {
        return session;
    }
}
