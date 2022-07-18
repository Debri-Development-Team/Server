package com.example.debriserver.core.Post;

import com.example.debriserver.core.Post.model.PostImgUrlReq;
import com.example.debriserver.core.Post.model.PostPostsReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class PostDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public int insertPosts(PostPostsReq postPostsReq){
        String insertPostQuery = "INSERT INTO Post(boardIdx, userIdx, postContent, postName) VALUES(?,?,?,?)";
        Object []insertPostParams = new Object[]{
                postPostsReq.getBoardIdx(),
                postPostsReq.getUserIdx(),
                postPostsReq.getPostContent(),
                postPostsReq.getPostName()
        };
        this.jdbcTemplate.update(insertPostQuery, insertPostParams);

        String lastInsertIdxQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdxQuery, int.class);

    }

    /*public int insertPostsImgs(int postIdx, PostImgUrlReq postImgUrlReq){
        String insertPostImgsQuery = "INSERT INTO PostImage(postIdx, url) VALUES(?,?)";
        Object []insertPostImgsParams = new Object[]{postIdx, postImgUrlReq.getUrl()};
        this.jdbcTemplate.update(insertPostImgsQuery, insertPostImgsParams);

        String lastInsertIdxQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdxQuery, int.class);

    }*/

    public int updatePost(int postIdx, String postContent){
        String updatePostQuery = "UPDATE Post SET postContent=? WHERE postIdx=?";
        Object []updatePostParams = new Object[]{postContent, postIdx};
        return this.jdbcTemplate.update(updatePostQuery, updatePostParams);
    }

    public int checkUserExist(int userIdx){
        String checkUserExistQuery = "select exists(select userIdx from User where userIdx = ?)";
        int checkUserExistParams = userIdx;
        return this.jdbcTemplate.queryForObject(checkUserExistQuery,
                int.class,
                checkUserExistParams);

    }

    public int checkPostExist(int postIdx){
        String checkPostExistQuery = "select exists(select postIdx from Post where postIdx = ?)";
        int checkPostExistParams = postIdx;
        return this.jdbcTemplate.queryForObject(checkPostExistQuery,
                int.class,
                checkPostExistParams);

    }

    public int deletePost(int postIdx){
        String deletePostQuery = "UPDATE Post SET status='DELETE' WHERE postIdx=?";
        Object [] deletePostParams = new Object[]{postIdx};
        return this.jdbcTemplate.update(deletePostQuery,
                deletePostParams);
    }
}
