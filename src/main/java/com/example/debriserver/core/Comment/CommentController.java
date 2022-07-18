package com.example.debriserver.core.Comment;

import com.example.debriserver.basicModels.BasicException;
import com.example.debriserver.basicModels.BasicResponse;
import com.example.debriserver.basicModels.BasicServerStatus;
import com.example.debriserver.core.Comment.Model.*;
import com.example.debriserver.utility.jwtUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.persistence.Basic;
import java.util.List;

/**
 * 댓글 관련 컨트롤러
 * @author Rookie/이지호
 * @since 220712
 * */
@RestController
@RequestMapping("/api/comment")
public class CommentController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());
    final jwtUtility jwt = new jwtUtility();

    @Autowired
    private final CommentService commentService;
    @Autowired
    private final CommentDao commentDao;

    public CommentController(CommentService commentService, CommentDao commentDao){
        this.commentService = commentService;
        this.commentDao = commentDao;
    }

    /**
     * 댓글 생성 API
     * [POST]: localhost:8521/api/comment/replyOnPost/create
     * */
    @PostMapping("/replyOnPost/create")
    public BasicResponse<PostReplyOnPostRes> createReplyOnPost(@RequestBody PostReplyOnPostReq postReplyOnPostReq){

        try{

            String jwtToken = jwt.getJwt();

            if(jwt.isJwtExpired(jwtToken)) throw new BasicException(BasicServerStatus.EXPIRED_TOKEN);

            if(commentService.checkPostDeleted(postReplyOnPostReq.getPostIdx())){
                throw new BasicException(BasicServerStatus.COMMENT_POST_DELETED_ERROR);
            }
            PostReplyOnPostRes postReplyOnPostRes = commentService.createReplyOnPost(postReplyOnPostReq);

            if(postReplyOnPostReq.getContent().length() > 5000){
                throw new BasicException(BasicServerStatus.COMMENT_TOO_LONG_ERROR);
            }

            return new BasicResponse<>(postReplyOnPostRes);
        }catch (BasicException exception){
            return new BasicResponse<>((exception.getStatus()));
        }
    }

    /**
     * 대댓글 생성 API
     * [POST]" localhost:8521/api/comment/replyOnReply/create
     **/
    @PostMapping("/replyOnReply/create")
    public BasicResponse<PostReplyOnReplyRes> createReplyOnReply(@RequestBody PostReplyOnReplyReq postReplyOnReplyReq){

        try{
            String jwtToken = jwt.getJwt();

            if(jwt.isJwtExpired(jwtToken)) throw new BasicException(BasicServerStatus.EXPIRED_TOKEN);

            if(commentService.checkCommentDeleted(postReplyOnReplyReq.getRootCommentIdx())){
                throw new BasicException(BasicServerStatus.ROOT_COMMENT_DELETED_ERROR);
            }

            PostReplyOnReplyRes postReplyOnReplyRes = commentService.createReplyOnReply(postReplyOnReplyReq);

            if(postReplyOnReplyReq.getContent().length() > 5000){
                throw new BasicException(BasicServerStatus.COMMENT_TOO_LONG_ERROR);
            }

            return new BasicResponse<>(postReplyOnReplyRes);
        }catch (BasicException exception){
            return new BasicResponse<>((exception.getStatus()));
        }
    }

    /**
     * 댓글/대댓글 조회 API
     * [GET]: localhost:8521/api/comment/get/{postIdx}
     * */
    @GetMapping("/get/{postIdx}")
    public BasicResponse<List<GetCommentRes>> getComment (@PathVariable int postIdx){

        try{
            String jwtToken = jwt.getJwt();

            if(jwt.isJwtExpired(jwtToken)) throw new BasicException(BasicServerStatus.EXPIRED_TOKEN);

            if(!commentService.checkCommentExist(postIdx)){
                throw new BasicException(BasicServerStatus.COMMENT_NOT_EXIST_ERROR);
            }

            List<GetCommentRes> getCommentRes= commentService.getComment(postIdx);

            return new BasicResponse<>(getCommentRes);
        }catch(BasicException exception){
            return new BasicResponse<>((exception.getStatus()));
        }
    }

    /**
     * 댓글/대댓글 삭제 API
     * [PATCH]" localhost:8521/api/comment/delete
     * */
    @PatchMapping("/delete/{commentIdx}")
    public BasicResponse<PatchCommentRes> deleteComment (@PathVariable int commentIdx){

        try{
            String jwtToken = jwt.getJwt();

            if(jwt.isJwtExpired(jwtToken)) throw new BasicException(BasicServerStatus.EXPIRED_TOKEN);

            PatchCommentRes patchCommentRes = commentService.deleteComment(commentIdx);

            if(!patchCommentRes.getDeleteSuccess()){
                logger.info("Test", patchCommentRes.getDeleteSuccess());
                throw new BasicException(BasicServerStatus.COMMENT_NOT_EXIST_ERROR);
            }

            return new BasicResponse<>(patchCommentRes);
        }catch (BasicException exception){
            return new BasicResponse<>((exception.getStatus()));
        }
    }
}
