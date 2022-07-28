package com.example.debriserver.core.Post;


import com.example.debriserver.basicModels.BasicException;
import com.example.debriserver.basicModels.BasicResponse;
import com.example.debriserver.basicModels.BasicServerStatus;
import com.example.debriserver.core.Post.model.GetScrapRes;
import com.example.debriserver.core.Post.model.PatchPostsReq;
import com.example.debriserver.core.Post.model.PostPostsReq;
import com.example.debriserver.core.Post.model.PostPostsRes;
import com.example.debriserver.core.Post.model.*;
import com.example.debriserver.utility.jwtUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/post")
public class PostController {
    final jwtUtility jwt = new jwtUtility();

    @Autowired
    private final PostProvider postProvider;
    @Autowired
    private final PostService postService;

    public PostController(PostProvider postProvider, PostService postService) {
        this.postProvider = postProvider;
        this.postService = postService;
    }

    /**
     * 게시물 생성
     */
    @ResponseBody
    @PostMapping("/create")
    public BasicResponse<PostPostsRes> createPosts(@RequestBody PostPostsReq postPostsReq) {
        try{
            String jwtToken = jwt.getJwt();

            if(jwt.isJwtExpired(jwtToken)) throw new BasicException(BasicServerStatus.EXPIRED_TOKEN);

            if (postPostsReq.getPostContent().length() > 5000) {
                return new BasicResponse<>(BasicServerStatus.POST_TOO_LONG_CONTENTS);
            }

            /*if (postPostsReq.getPostImgUrls().size() < 1) {
                return new BasicResponse<>(BasicServerStatus.POST_EMPTY_IMG_URL);
            }*/

            PostPostsRes postPostsRes = postService.createPosts(postPostsReq);
            return new BasicResponse<>(postPostsRes);
        } catch(BasicException exception){
            return new BasicResponse<>((exception.getStatus()));
        }
    }

    /**
     * 게시물 수정
     */
    @ResponseBody
    @PatchMapping("/{postIdx}")
    public BasicResponse<String> modifyPost(@PathVariable ("postIdx") int postIdx, @RequestBody PatchPostsReq patchPostsReq) {
        try{
            String jwtToken = jwt.getJwt();

            if(jwt.isJwtExpired(jwtToken)) throw new BasicException(BasicServerStatus.EXPIRED_TOKEN);

            if (patchPostsReq.getPostContent().length() > 5000) {
                return new BasicResponse<>(BasicServerStatus.POST_TOO_LONG_CONTENTS);
            }

            postService.modifyPost(patchPostsReq.getUserIdx(), postIdx, patchPostsReq);
            String result = "게시물 정보 수정을 완료하였습니다.";
            return new BasicResponse<>(result);
        } catch(BasicException exception){
            return new BasicResponse<>((exception.getStatus()));
        }
    }

    /**
     * 게시물 삭제
     */
    @ResponseBody
    @PatchMapping("/{postIdx}/status")
    public BasicResponse<String> deletePost(@PathVariable ("postIdx") int postIdx) {
        try{
            String jwtToken = jwt.getJwt();

            if(jwt.isJwtExpired(jwtToken)) throw new BasicException(BasicServerStatus.EXPIRED_TOKEN);

            postService.deletePost(postIdx);
            String result = "삭제를 성공했습니다.";
            return new BasicResponse<>(result);
        } catch(BasicException exception){
            return new BasicResponse<>((exception.getStatus()));
        }
    }


    /**
     * Post 스크랩 설정 메서드
     * */
    @ResponseBody
    @PostMapping("/scrap/{postIdx}")
    public BasicResponse<String> scrapPost(@PathVariable ("postIdx") int postIdx) {
        try {
            String jwtToken = jwt.getJwt();
            if (jwt.isJwtExpired(jwtToken)) throw new BasicException(BasicServerStatus.EXPIRED_TOKEN);

            int userIdx = jwt.getUserIdx(jwtToken);

            String result = "스크랩이 설정되었습니다.";
            postService.scrapPost(postIdx, userIdx);

            return new BasicResponse<>(result);

        } catch (BasicException exception) {
            return new BasicResponse<>((exception.getStatus()));
        }
    }

    /**
     * 게시물 좋아요 생성
     */
    @ResponseBody
    @PostMapping("/like")
    public BasicResponse<String> createPostLike (@RequestBody PostPostLikeReq postPostLikeReq){
        try {

            String jwtToken = jwt.getJwt();

            if (jwt.isJwtExpired(jwtToken)) throw new BasicException(BasicServerStatus.EXPIRED_TOKEN);

            postService.createPostLike(postPostLikeReq.getUserIdx(), postPostLikeReq.getPostIdx(), postPostLikeReq);
            String result = "좋아요 또는 싫어요가 생성되었습니다.";
            return new BasicResponse<>(result);

        } catch (BasicException exception) {
            return new BasicResponse<>((exception.getStatus()));
        }
    }

    /**
     * 게시물 좋아요 취소
     */
    @ResponseBody
    @PatchMapping("/like/cancel")
    public BasicResponse<String> cancelPostLike(@RequestBody PatchPostLikeReq patchPostLikeReq){
        try {
            String jwtToken = jwt.getJwt();

            if (jwt.isJwtExpired(jwtToken)) throw new BasicException(BasicServerStatus.EXPIRED_TOKEN);

            postService.cancelPostLike(patchPostLikeReq.getUserIdx(), patchPostLikeReq.getPostIdx());
            String result = "좋아요 또는 싫어요가 취소되었습니다.";
            return new BasicResponse<>(result);

        } catch (BasicException exception) {
            return new BasicResponse<>((exception.getStatus()));
        }
    }
    
    /**
     * Post 스크랩 해제 메서드
     * */
    @ResponseBody
    @PostMapping("/unscrap/{postIdx}")
    public BasicResponse<String> unScrapPost(@PathVariable("postIdx") int postIdx) {
        try {
            String jwtToken = jwt.getJwt();
            if (jwt.isJwtExpired(jwtToken)) throw new BasicException(BasicServerStatus.EXPIRED_TOKEN);

            int userIdx = jwt.getUserIdx(jwtToken);

            String result = "스크랩이 해제되었습니다.";
            postService.unScrapPost(postIdx, userIdx);

            return new BasicResponse<>(result);
         } catch (BasicException exception) {
            return new BasicResponse<>((exception.getStatus()));
        }
    }
    
    /**
     * 전체 게시판에서 게시글 리스트를 조회하는 api
     * [GET] localhost/api/post/getSearchList
     * */
    @ResponseBody
    @PostMapping("/getSearchList")
    public BasicResponse<List<GetPostSearchListRes>> getPostSearchList(@RequestBody GetPostSearchListReq getPostSearchListReq){
        try{
            String jwtToken = jwt.getJwt();

            if(jwt.isJwtExpired(jwtToken)) throw new BasicException(BasicServerStatus.EXPIRED_TOKEN);

            String keyword = getPostSearchListReq.getKeyword();
            int userIdx = jwt.getUserIdx(jwtToken);
            List<GetPostSearchListRes> getPostSearchListRes = postProvider.getPostSearchList(userIdx, keyword);

            return  new BasicResponse<>(getPostSearchListRes);

        }catch (BasicException exception){
            return new BasicResponse<>((exception.getStatus()));
        }
    }
    

     /**
      * 특정 게시판의 게시글 리스트를 조회하는 api
      * [GET]localhost / api / post / getList / {boardIdx}
      */
     @GetMapping("/getList/{boardIdx}")
     public BasicResponse<List<GetPostListRes>> getPostList ( @PathVariable int boardIdx){

         try {
             String jwtToken = jwt.getJwt();

             if (jwt.isJwtExpired(jwtToken)) throw new BasicException(BasicServerStatus.EXPIRED_TOKEN);

             if (!postProvider.checkBoardExist(boardIdx))
                 return new BasicResponse<>(BasicServerStatus.BOARD_NOT_EXIST);

             int userIdx = jwt.getUserIdx(jwtToken);
             List<GetPostListRes> getPostListRes = postProvider.getPostList(userIdx, boardIdx);

             return new BasicResponse<>(getPostListRes);
         } catch (BasicException exception) {
             return new BasicResponse<>((exception.getStatus()));
         }
     }

     /**
      * 유저가 스크랩한 Posts
      * */
     @ResponseBody
     @GetMapping("/getMyScrap")
     public BasicResponse<List<GetScrapRes>> getScrapPosts ()
     {
         try {
             String jwtToken = jwt.getJwt();
             if (jwt.isJwtExpired(jwtToken)) throw new BasicException(BasicServerStatus.EXPIRED_TOKEN);

             int userIdx = jwt.getUserIdx(jwtToken);

             List<GetScrapRes> getPosts = postService.getScrapPosts(userIdx);
             return new BasicResponse<>(getPosts);

         } catch (BasicException exception) {
             return new BasicResponse<>((exception.getStatus()));
         }
     }

     /**
      * 특정 게시물의 내용을 조회하는 api
      * [GET]localhost / api / post / get / {postIdx}
      * */
     @GetMapping("/get/{postIdx}")
     public BasicResponse<GetPostRes> getPost ( @PathVariable int postIdx){

         try {
             String jwtToken = jwt.getJwt();

             if (jwt.isJwtExpired(jwtToken)) throw new BasicException(BasicServerStatus.EXPIRED_TOKEN);

             if (postProvider.checkPostExist(postIdx) == 0)
                 return new BasicResponse<>(BasicServerStatus.POSTS_EMPTY_POST_ID);

             GetPostRes getPostRes = postProvider.getPost(postIdx);

             return new BasicResponse<>(getPostRes);

         } catch (BasicException exception) {
             return new BasicResponse<>((exception.getStatus()));
         }
     }
}

