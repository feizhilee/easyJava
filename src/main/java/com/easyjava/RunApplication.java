package com.easyjava;

import com.easyjava.bean.TableInfo;
import com.easyjava.builder.*;

import java.util.List;

public class RunApplication {
    public static void main(String[] args) {
        List<TableInfo> tableinfoList = BuildTable.getTables();

        BuildBase.execute();

        for (TableInfo tableinfo : tableinfoList) {
            BuildPo.execute(tableinfo);

            BuildQuery.execute(tableinfo);

            BuildMapper.execute(tableinfo);

            BuildMapperXml.execute(tableinfo);

            BuildService.execute(tableinfo);

            BuildServImpl.execute(tableinfo);

            BuildController.execute(tableinfo);
        }
    }
}
