package com.example.debriserver.core.Auth;


import com.example.debriserver.utility.jwtUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthProvider {

    private final AuthDao authDao;
    private final jwtUtility jwtUtility;

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public AuthProvider(AuthDao authDao, jwtUtility jwtUtility)
    {
        this.authDao = authDao;
        this.jwtUtility = jwtUtility;
    }


}
