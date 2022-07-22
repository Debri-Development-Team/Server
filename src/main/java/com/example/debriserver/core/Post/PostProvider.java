package com.example.debriserver.core.Post;

import com.example.debriserver.basicModels.BasicException;
import com.example.debriserver.core.Post.model.GetPostSearchListRes;
import com.example.debriserver.core.Post.model.GetScrapRes;
import com.example.debriserver.basicModels.BasicResponse;
import com.example.debriserver.core.Post.model.GetPostListRes;
import com.example.debriserver.core.Post.model.GetPostRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public int checkPostMarkedExist(int postIdx, int userIdx) throws BasicException {
        try {
            return postDao.checkPostMarkedExist(postIdx, userIdx);
        } catch (Exception exception) {
            throw new BasicException(DB_ERROR);
        }
    }

    public boolean checkBoardExist(int boardIdx) throws BasicException{
        try{
            return postDao.checkBoardExist(boardIdx);
        } catch (Exception exception){
            throw  new BasicException(DB_ERROR);
        }
    }

    public List<GetPostListRes> getPostList(int boardIdx) throws BasicException{
        try{
            return postDao.getPostList(boardIdx);
        }catch (Exception exception){
            System.out.println(exception.getMessage());
            throw new BasicException(DB_ERROR);
        }
    }

    public List<GetPostSearchListRes> getPostSearchList(String keyword) throws BasicException{
        try{

            return postDao.getPostSearchList(keyword);

        }catch (Exception exception){
            System.out.println(exception.getMessage());
            throw new BasicException(DB_ERROR);
        }
    }

    public GetPostRes getPost(int postIdx) throws BasicException {

        try{

            return postDao.getPost(postIdx);
        }catch(Exception exception){
            throw new BasicException(DB_ERROR);
        }
    }
}
