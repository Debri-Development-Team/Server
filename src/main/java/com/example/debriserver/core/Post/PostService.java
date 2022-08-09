package com.example.debriserver.core.Post;

import com.example.debriserver.basicModels.BasicException;
import com.example.debriserver.basicModels.BasicResponse;
import com.example.debriserver.core.Post.model.GetScrapRes;
import com.example.debriserver.core.Post.model.PatchPostsReq;
import com.example.debriserver.core.Post.model.PostPostsReq;
import com.example.debriserver.core.Post.model.PostPostsRes;
import com.example.debriserver.core.Post.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

            if (postProvider.checkPostExist(postIdx) == 0) {
                throw new BasicException(POSTS_EMPTY_POST_ID);
            }

            int result = postDao.deletePost(postIdx);
            if (result == 0) {
                throw new BasicException(DB_ERROR);
            }
        }
        catch (Exception exception) {
            throw new BasicException(DB_ERROR);
        }
    }

    public void createPostLike(int userIdx, int postIdx, PostPostLikeReq postPostLikeReq) throws BasicException {

        if (postProvider.checkUserExist(userIdx) == 0) {
            throw new BasicException(USERS_EMPTY_USER_ID);
        }

        if (postProvider.checkPostExist(postIdx) == 0) {
            throw new BasicException(POSTS_EMPTY_POST_ID);
        }

        try {
            postDao.insertPostLike(postPostLikeReq);
        } catch (Exception e) {
            throw new BasicException(DB_ERROR);
        }
    }

    public void cancelPostLike(int userIdx, int postIdx) throws BasicException {

        if (postProvider.checkUserExist(userIdx) == 0) {
            throw new BasicException(USERS_EMPTY_USER_ID);
        }

        if (postProvider.checkPostExist(postIdx) == 0) {
            throw new BasicException(POSTS_EMPTY_POST_ID);
        }

        try {
            int result = postDao.deletePostLike(postIdx, userIdx);
            if (result == 0) {
                throw new BasicException(DB_ERROR);
            }
        } catch (Exception e) {
            throw new BasicException(DB_ERROR);
        }
    }

    /**
     * 게시물 스크랩 설정
     * 게시물을 처음으로 스크랩 하는 경우 : insertPostMarked 함수 실행
     * 스크랩 해제했다가 다시 스크랩 하는 경우 : scrapPost 함수 실행
     */
    public void scrapPost(int postIdx, int userIdx) throws BasicException
    {
        // postIdx가 Post table에 존재하는지 확인
        if(postProvider.checkPostExist(postIdx) == 0)
        {
            throw new BasicException(POSTS_EMPTY_POST_ID);
        }

        // userIdx가 User table에 존재하는지 확인
        if(postProvider.checkUserExist(userIdx) == 0)
        {
            throw new BasicException(USERS_EMPTY_USER_ID);
        }

        // 데이터가 PostMarked table에 존재하지 않는 경우
        if(postProvider.checkPostMarkedExist(postIdx, userIdx) == 0)
        {
            // PostMarked에 데이터를 추가하는 함수
            int result = postDao.insertPostMarked(postIdx, userIdx);
        }
        // 데이터가 PostMarked table에 이미 존재하는 경우
        else
        {
            // status를 ACTIVE로 바꾸는 함수
            int result = postDao.scrapPost(postIdx, userIdx);
        }
    }

    /**
     * 게시물 스크랩 해제
     * 활성화 된 스크랩 클릭 : unScrapPost 함수 실행
     */
    public void unScrapPost(int postIdx, int userIdx) throws BasicException
    {
        // postIdx가 Post table에 존재하는지 확인
        if(postProvider.checkPostExist(postIdx) == 0)
        {
            throw new BasicException(POSTS_EMPTY_POST_ID);
        }

        // userIdx가 User table에 존재하는지 확인
        if(postProvider.checkUserExist(userIdx) == 0)
        {
            throw new BasicException(USERS_EMPTY_USER_ID);
        }

        // 데이터가 PostMarked table에 존재하지 않는 경우
        if(postProvider.checkPostMarkedExist(postIdx, userIdx) == 0)
        {
            // PostMarked에 데이터를 추가 -> status를 INACTIVE로 변경
            int result = postDao.insertPostMarked(postIdx, userIdx);
            int result2 = postDao.unScrapPost(postIdx, userIdx);
        }
        // 데이터가 PostMarked table에 이미 존재하는 경우
        else
        {
            // status를 INACTIVE로 바꾸는 함수
            int result = postDao.unScrapPost(postIdx, userIdx);
        }
    }


    public List<GetScrapRes> getScrapPosts(int userIdx) throws BasicException
    {
        // userIdx가 User table에 존재하는지 확인
        if(postProvider.checkUserExist(userIdx) == 0)
        {
            throw new BasicException(USERS_EMPTY_USER_ID);
        }

        List<GetScrapRes> getPosts = postDao.getScrapPosts(userIdx);
        return getPosts;
    }

    public List<GetPostListRes> getBoardPostList(String key, int boardIdx, int userIdx) throws BasicException{
        try{
            return postDao.getBoardPostList(key, boardIdx, userIdx);
        }catch (Exception exception){
            throw new BasicException(DB_ERROR);
        }
    }
}
