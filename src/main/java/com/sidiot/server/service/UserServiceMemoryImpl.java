package com.sidiot.server.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author sidiot
 */
public class UserServiceMemoryImpl implements UserService {

    @Override
    public boolean login(String username, String password) {
        final String pass = allUserMap.get(username);
        if (pass == null) {
            return false;
        }
        return pass.equals(password);
    }

    private Map<String, String> allUserMap = new ConcurrentHashMap<>();

    {
        allUserMap.put("sidiot", "123456");
        allUserMap.put("Tom", "56789");
        allUserMap.put("Lisa", "123");
        allUserMap.put("Oakley", "123");
        allUserMap.put("jake", "123");
        allUserMap.put("Root", "456");
    }
}
