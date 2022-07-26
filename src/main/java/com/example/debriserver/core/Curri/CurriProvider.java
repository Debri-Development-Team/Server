package com.example.debriserver.core.Curri;

import com.example.debriserver.core.Curri.CurriDao;
import com.example.debriserver.utility.jwtUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CurriProvider {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final jwtUtility jwt;

    @Autowired
    private final CurriDao curriDao;

    public CurriProvider(jwtUtility jwt, CurriDao curriDao){
        this.jwt = jwt;
        this.curriDao = curriDao;
    }


}
