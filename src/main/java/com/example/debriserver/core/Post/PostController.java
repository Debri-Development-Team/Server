package com.example.debriserver.core.Post;


import com.example.debriserver.basicModels.BasicException;
import com.example.debriserver.basicModels.BasicResponse;
import com.example.debriserver.basicModels.BasicServerStatus;
import com.example.debriserver.core.Post.model.PatchPostsReq;
import com.example.debriserver.core.Post.model.PostPostsReq;
import com.example.debriserver.core.Post.model.PostPostsRes;
import com.example.debriserver.utility.jwtUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

}
