package com.example.debriserver.core.Board;

import com.example.debriserver.basicModels.BasicException;
import com.example.debriserver.core.Board.model.GetUnscrapBoardListRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.debriserver.basicModels.BasicServerStatus.DB_ERROR;

@Service
public class BoardProvider {

    @Autowired
    private final BoardDao boardDao;

    public BoardProvider(BoardDao boardDao) {
        this.boardDao = boardDao;
    }

    public int checkBoardExist(int boardIdx) throws BasicException {
        try {
            return boardDao.checkBoardExist(boardIdx);
        } catch (Exception exception) {
            throw new BasicException(DB_ERROR);
        }
    }

    public int checkUserExist(int userIdx) throws BasicException {
        try{
            return boardDao.checkUserExist(userIdx);
        } catch (Exception exception) {
            throw new BasicException(DB_ERROR);
        }
    }

    public int checkBoardSubsExist(int boardIdx, int userIdx) throws BasicException {
        try {
            return boardDao.checkBoardSubsExist(boardIdx, userIdx);
        } catch (Exception exception) {
            throw new BasicException(DB_ERROR);
        }
    }


    public List<GetUnscrapBoardListRes> getList(int userIdx) throws BasicException{

        try{
            return boardDao.getList(userIdx);

        }catch (Exception exception){
            throw new BasicException(DB_ERROR);
        }
    }

    public boolean checkUnscrapExist(int boardIdx, int userIdx) throws BasicException{

        try{
            return boardDao.checkUnscrapExist(boardIdx, userIdx);
        }catch (Exception exception){
            throw new BasicException(DB_ERROR);
        }
    }
}
