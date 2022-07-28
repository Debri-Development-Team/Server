package com.example.debriserver.core.Curri;

import com.example.debriserver.basicModels.BasicException;
import com.example.debriserver.basicModels.BasicServerStatus;
import com.example.debriserver.core.Curri.CurriDao;
import com.example.debriserver.core.Curri.CurriProvider;
import com.example.debriserver.core.Curri.Model.PostCurriCreateReq;
import com.example.debriserver.core.Curri.Model.PostCurriCreateRes;
import com.example.debriserver.utility.jwtUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CurriService {
    @Autowired
    private final jwtUtility jwt;

    @Autowired
    private final CurriProvider curriProvider;

    @Autowired
    private final CurriDao curriDao;

    public CurriService(jwtUtility jwt, CurriProvider curriProvider, CurriDao curriDao){
        this.jwt = jwt;
        this.curriProvider = curriProvider;
        this.curriDao = curriDao;
    }

    public PostCurriCreateRes createCurri(PostCurriCreateReq postCurriCreateReq) throws BasicException{
        try{
            PostCurriCreateRes postCurriCreateRes = curriDao.createCurri(postCurriCreateReq);

            return postCurriCreateRes;
        } catch(Exception exception){
            System.out.println(exception.getMessage());
            throw new BasicException(BasicServerStatus.DB_ERROR);
        }
    }

}
