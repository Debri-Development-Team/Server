package com.example.debriserver.core.Post;

import com.example.debriserver.basicModels.BasicException;
import com.example.debriserver.core.Post.model.PatchPostsReq;
import com.example.debriserver.core.Post.model.PostPostsReq;
import com.example.debriserver.core.Post.model.PostPostsRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.debriserver.basicModels.BasicServerStatus.*;

@Service
public class PostService {

    @Autowired
    private final PostDao postDao;

    @Autowired
    private final PostProvider postProvider;

    public PostService(PostDao postDao, PostProvider postProvider) {
        this.postDao = postDao;
        this.postProvider = postProvider;
    }

    public PostPostsRes createPosts(PostPostsReq postPostsReq) throws BasicException {

        try{

            int postIdx = postDao.insertPosts(postPostsReq);

           /* for (int i = 0; i < postPostsReq.getPostImgUrls().size(); i++) {
                postDao.insertPostsImgs(postIdx, postPostsReq.getPostImgUrls().get(i));
            }*/

            return new PostPostsRes(postIdx);
        }
        catch (Exception exception) {
            System.out.println(exception);
            throw new BasicException(DB_ERROR);
        }
    }

    public void modifyPost(int userIdx, int postIdx, PatchPostsReq patchPostsReq) throws BasicException {
        if (postProvider.checkUserExist(userIdx) == 0) {
            throw new BasicException(USERS_EMPTY_USER_ID);
        }

        if (postProvider.checkPostExist(postIdx) == 0) {
            throw new BasicException(POSTS_EMPTY_POST_ID);
        }

        try{

            int result = postDao.updatePost(postIdx, patchPostsReq.getPostContent());
            if (result == 0) {
                throw new BasicException(MODIFY_FAIL_POST);
            }
        }
        catch (Exception exception) {
            throw new BasicException(DB_ERROR);
        }
    }

    public void deletePost(int postIdx) throws BasicException {

        try{

            int result = postDao.deletePost(postIdx);
            if (result == 0) {
                throw new BasicException(DB_ERROR);
            }
        }
        catch (Exception exception) {
            System.out.println(exception);
            throw new BasicException(DB_ERROR);
        }
    }
}
