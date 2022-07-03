package com.example.debriserver.core.user;

import com.example.debriserver.basicModels.BasicException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.debriserver.basicModels.BasicServerStatus.DB_ERROR;

@Service
public class UserProvider {
    private final UserDao userDao;

    @Autowired
    public UserProvider(UserDao userDao) {
        this.userDao = userDao;
    }

    public int checkId(String id) throws BasicException {
        try{
            return userDao.checkId(id);
        } catch (Exception exception){
            throw new BasicException(DB_ERROR);
        }
    }
}
