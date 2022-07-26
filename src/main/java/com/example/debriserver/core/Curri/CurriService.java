package com.example.debriserver.core.Curri;

import com.example.debriserver.core.Curri.CurriDao;
import com.example.debriserver.core.Curri.CurriProvider;
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
}
