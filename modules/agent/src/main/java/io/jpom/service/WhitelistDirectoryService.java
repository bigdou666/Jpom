/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2019 Code Technology Studio
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package io.jpom.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSONObject;
import io.jpom.common.AgentConst;
import io.jpom.model.data.AgentWhitelist;
import io.jpom.util.JsonFileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 白名单服务
 *
 * @author jiangzeyin
 * @since 2019/2/28
 */
@Service
@Slf4j
public class WhitelistDirectoryService extends BaseDataService {

    /**
     * 获取白名单信息配置、如何没有配置或者配置错误将返回新对象
     *
     * @return AgentWhitelist
     */
    public AgentWhitelist getWhitelist() {
        try {
            JSONObject jsonObject = getJSONObject(AgentConst.WHITELIST_DIRECTORY);
            if (jsonObject == null) {
                return new AgentWhitelist();
            }
            return jsonObject.toJavaObject(AgentWhitelist.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return new AgentWhitelist();
    }

    /**
     * 单项添加白名单
     *
     * @param item 白名单
     */
    public void addProjectWhiteList(String item) {
        ArrayList<String> list = CollUtil.newArrayList(item);
        List<String> checkOk = AgentWhitelist.covertToArray(list, "项目路径白名单不能位于Jpom目录下");

        AgentWhitelist agentWhitelist = getWhitelist();
        List<String> project = agentWhitelist.getProject();
        project = ObjectUtil.defaultIfNull(project, Collections.emptyList());
        project = CollUtil.addAll(project, checkOk)
            .stream()
            .distinct()
            .collect(Collectors.toList());
        agentWhitelist.setProject(project);
        saveWhitelistDirectory(agentWhitelist);
    }

    public boolean checkProjectDirectory(String path) {
        AgentWhitelist agentWhitelist = getWhitelist();
        List<String> list = agentWhitelist.project();
        return AgentWhitelist.checkPath(list, path);
    }

    public boolean checkNgxDirectory(String path) {

        AgentWhitelist agentWhitelist = getWhitelist();
        List<String> list = agentWhitelist.nginx();
        return AgentWhitelist.checkPath(list, path);
    }


    public boolean checkCertificateDirectory(String path) {
        AgentWhitelist agentWhitelist = getWhitelist();

        List<String> list = agentWhitelist.certificate();
        if (list == null) {
            return false;
        }
        return AgentWhitelist.checkPath(list, path);
    }

    /**
     * 保存白名单
     *
     * @param jsonObject 实体
     */
    public void saveWhitelistDirectory(AgentWhitelist jsonObject) {
        String path = getDataFilePath(AgentConst.WHITELIST_DIRECTORY);
        JsonFileUtil.saveJson(path, jsonObject.toJson());
    }
}
