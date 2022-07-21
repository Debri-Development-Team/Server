package com.example.debriserver.core.Comment;

import com.example.debriserver.basicModels.BasicException;
import com.example.debriserver.basicModels.BasicServerStatus;
import com.example.debriserver.core.Comment.Model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final CommentDao commentDao;

    public CommentService(CommentDao commentDao){
        this.commentDao = commentDao;
    }

    public PostReplyOnPostRes createReplyOnPost(PostReplyOnPostReq postReplyOnPostReq) throws BasicException{
        try{
            PostReplyOnPostRes postReplyOnPostRes = commentDao.createReplyOnPost(postReplyOnPostReq);

            return postReplyOnPostRes;

        }catch(Exception exception){
            System.out.println(exception.getMessage());
            throw new BasicException(BasicServerStatus.DB_ERROR);
        }
    }

    public PostReplyOnReplyRes createReplyOnReply(PostReplyOnReplyReq postReplyOnReplyReq) throws BasicException{
        try{
            PostReplyOnReplyRes postReplyOnReplyRes = commentDao.createReplyOnReply(postReplyOnReplyReq);

            return postReplyOnReplyRes;

        }catch(Exception exception){
            throw new BasicException(BasicServerStatus.DB_ERROR);
        }
    }

    public List<GetCommentRes> getComment(int postIdx) throws BasicException{
        try{
            List<GetCommentRes> getCommentRes = commentDao.getComment(postIdx);

            return getCommentRes;
        }catch (Exception exception){
            throw new BasicException(BasicServerStatus.DB_ERROR);
        }
    }

    public PatchCommentRes deleteComment(int commentIdx) throws BasicException{
        try{

            PatchCommentRes patchCommentRes = commentDao.deleteComment(commentIdx);

            return patchCommentRes;
        }catch(Exception exception){
            throw new BasicException(BasicServerStatus.DB_ERROR);
        }
    }

    public PatchModRes modifyComment(int commentIdx, PatchModReq patchModReq) throws BasicException{
        try{
            PatchModRes patchModRes = commentDao.modifyComment(commentIdx, patchModReq);

            return patchModRes;
        }catch (Exception exception){
            throw new BasicException(BasicServerStatus.DB_ERROR);
        }
    }
    public boolean checkCommentExist(int postIdx) throws BasicException{
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
