package com.cbrc.plato.core.basic.service;

import com.cbrc.plato.core.basic.model.QnyjYuqi90East;


import java.util.List;

public interface IQnyjYuqi90EastService {

    List<QnyjYuqi90East> selectByTime( String dataTime,String dataName);
}
