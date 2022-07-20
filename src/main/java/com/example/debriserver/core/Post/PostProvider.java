package com.example.debriserver.core.Post;

import com.example.debriserver.basicModels.BasicException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.debriserver.basicModels.BasicServerStatus.DB_ERROR;

@Service
public class PostProvider {

    @Autowired
    private final PostDao postDao;

    public PostProvider(PostDao postDao) {
        this.postDao = postDao;
    }

    public int checkUserExist(int userIdx) throws BasicException{
        try{
            return postDao.checkUserExist(userIdx);
        } catch (Exception exception) {
            throw new BasicException(DB_ERROR);
        }
    }

    public int checkPostExist(int postIdx) throws BasicException{
        try{
            return postDao.checkPostExist(postIdx);
        } catch (Exception exception) {
            throw new BasicException(DB_ERROR);
        }
    }

    public int checkPostMarkedExist(int postIdx, int userIdx) throws BasicException
    {
        try{
            return postDao.checkPostMarkedExist(postIdx, userIdx);
        }catch (Exception exception) {
            throw new BasicException(DB_ERROR);
        }
    }

}
