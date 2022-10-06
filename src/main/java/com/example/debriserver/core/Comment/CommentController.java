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
    private final CommentProvider commentProvider;
    @Autowired
    private final CommentService commentService;

    public CommentController(CommentProvider commentProvider, CommentService commentService){
        this.commentProvider = commentProvider;
        this.commentService = commentService;
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

            if(commentProvider.checkPostDeleted(postReplyOnPostReq.getPostIdx())){
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

            if(commentProvider.checkCommentDeleted(postReplyOnReplyReq.getRootCommentIdx())){
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

            int userIdx = jwt.getUserIdx(jwtToken);

            if(!commentProvider.checkCommentExistInPost(postIdx)){
                throw new BasicException(BasicServerStatus.COMMENT_NOT_EXIST_ERROR);
            }
            if(commentProvider.checkPostDeleted(postIdx)){
                throw new BasicException(BasicServerStatus.COMMENT_ROOT_POST_NOT_EXIST);
            }

            List<GetCommentRes> getCommentRes= commentService.getComment(postIdx, userIdx);

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
                throw new BasicException(BasicServerStatus.COMMENT_NOT_EXIST_ERROR);
            }

            return new BasicResponse<>(patchCommentRes);
        }catch (BasicException exception){
            return new BasicResponse<>((exception.getStatus()));
        }
    }

    /**
     * 댓글/대댓글 수정 API
     * [PATCH]: localhost:8521/api/comment/mod/{commentIdx}
     * */
    @PatchMapping("/mod/{commentIdx}")
    public BasicResponse<PatchModRes> modifyComment(@PathVariable int commentIdx, @RequestBody PatchModReq patchModReq){

        try{
            //Jwt 인증이 되었는지
            String jwtToken = jwt.getJwt();

            if(jwt.isJwtExpired(jwtToken)) throw new BasicException(BasicServerStatus.EXPIRED_TOKEN);
            //수정할 댓글의 길이가 5000자를 넘지는 않는지
            if(patchModReq.getModContent().length() > 5000) throw new BasicException(BasicServerStatus.COMMENT_TOO_LONG_ERROR);
            //일단 수정 시도하는 사람이 댓글 작성자인지
            if(commentProvider.isAuthor(patchModReq.getUserIdx(), commentIdx)) throw new BasicException(BasicServerStatus.MODIFY_IT_IS_NOT_AUTHOR_ERROR);
            //수정 시도하는 댓글이 삭제되어있지는 않은지
            if(commentProvider.isDeleted(commentIdx)) throw  new BasicException(BasicServerStatus.MODIFY_ALREADY_DELETED_COMMENT);

            PatchModRes patchModRes = commentService.modifyComment(commentIdx, patchModReq);

            return new BasicResponse<>(patchModRes);

        }catch(BasicException exception){
            return new BasicResponse<>((exception.getStatus()));
        }
    }

    /**
     * 댓글 좋아요 생성 api
     * [POST] localhost:8521/api/comment/like/create
     * */
    @PostMapping("/like/create/{commentIdx}")
    public BasicResponse<PostCommentLikeRes> createCommentLike(@PathVariable int commentIdx){

        try{
            String jwtToken = jwt.getJwt();

            if(jwt.isJwtExpired(jwtToken)) throw new BasicException(BasicServerStatus.EXPIRED_TOKEN);

            int userIdx = jwt.getUserIdx(jwtToken);
            //댓글이 존재하는지
            if(commentProvider.commentExist(commentIdx)) throw new BasicException(BasicServerStatus.COMMENT_NOT_EXIST_ERROR);
            //이미 좋아요를 눌렀는지
            //if(commentService.commentLikeExist(commentIdx, userIdx)) throw new BasicException(BasicServerStatus.ALREADY_COMMENT_LIKE);
            //본인이 작성한 댓글인지
            //if(commentService.checkCommentAuthor(commentIdx, userIdx))

            return new BasicResponse<>(commentService.createCommentLike(userIdx, commentIdx));

        }catch (BasicException exception){
            return new BasicResponse<>((exception.getStatus()));
        }
    }

    /**
     * 댓글 좋아요 삭제 api
     * [PATCH] localhost:8521/api/comment/like/delete
     * */

    @PatchMapping("/like/delete/{commentIdx}")
    public BasicResponse<PatchCommentLikeRes> deleteCommentLike(@PathVariable int commentIdx){

        try{
            String jwtToken = jwt.getJwt();

            if(jwt.isJwtExpired(jwtToken)) throw new BasicException(BasicServerStatus.EXPIRED_TOKEN);

            int userIdx = jwt.getUserIdx(jwtToken);

            if(commentProvider.commentExist(commentIdx)) throw new BasicException(BasicServerStatus.COMMENT_NOT_EXIST_ERROR);

            return new BasicResponse<>(commentService.deleteCommentLike(userIdx, commentIdx));
        }catch (BasicException exception){
            return new BasicResponse<>((exception.getStatus()));
        }
    }
}
