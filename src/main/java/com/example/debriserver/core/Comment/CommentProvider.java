package com.example.debriserver.core.Comment;

import com.example.debriserver.basicModels.BasicException;
import com.example.debriserver.basicModels.BasicServerStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentProvider {

    @Autowired
    private final CommentDao commentDao;

    public CommentProvider(CommentDao commentDao)
    {
        this.commentDao = commentDao;
    }

    public boolean checkCommentExistInPost(int postIdx) throws BasicException {
        try{
            return commentDao.checkCommentExist(postIdx);
        }catch (Exception exception){
            throw new BasicException(BasicServerStatus.DB_ERROR);
        }
    }

    public boolean checkPostDeleted(int postIdx) throws BasicException {
        try{
            return commentDao.checkPostDeleted(postIdx);
        }catch (Exception exception){
            throw new BasicException(BasicServerStatus.DB_ERROR);
        }
    }

    public boolean checkCommentDeleted(int commentIdx) throws BasicException {
        try{
            return commentDao.checkCommentDeleted(commentIdx);
        }catch (Exception exception) {
            throw new BasicException(BasicServerStatus.DB_ERROR);
        }
    }

    public boolean commentExist(int commentIdx) throws BasicException{
        try{
            return commentDao.commentExist(commentIdx);
        }catch (Exception exception){
            throw new BasicException(BasicServerStatus.DB_ERROR);
        }
    }

    public boolean isAuthor(int userIdx, int commentIdx) throws BasicException{

        try{
            return commentDao.isAuthor(userIdx, commentIdx);
        }catch (Exception exception){
            throw new BasicException(BasicServerStatus.DB_ERROR);
        }
    }

    public boolean isDeleted(int commentIdx) throws BasicException{
        try{
            return commentDao.isDeleted(commentIdx);
        }catch (Exception exception){
            throw new BasicException(BasicServerStatus.DB_ERROR);
        }
    }


}
