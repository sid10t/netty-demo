package com.sidiot.server.service;

/**
 * @author sidiot
 */
public abstract class UserServiceFactory {

    private static UserService userService = new UserServiceMemoryImpl();

    public static UserService getUserService()
    {
        return userService;
    }

}
