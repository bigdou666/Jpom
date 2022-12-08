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
package io.jpom.controller.user;

import io.jpom.common.BaseServerController;
import io.jpom.common.JsonMessage;
import io.jpom.model.PageResultDto;
import io.jpom.model.log.UserOperateLogV1;
import io.jpom.permission.ClassFeature;
import io.jpom.permission.Feature;
import io.jpom.permission.MethodFeature;
import io.jpom.permission.SystemPermission;
import io.jpom.service.dblog.DbUserOperateLogService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户操作日志
 *
 * @author jiangzeyin
 * @since 2019/4/19
 */
@RestController
@RequestMapping(value = "/user/log")
@Feature(cls = ClassFeature.USER_LOG)
@SystemPermission
public class UserOptLogController extends BaseServerController {

	private final DbUserOperateLogService dbUserOperateLogService;

	public UserOptLogController(DbUserOperateLogService dbUserOperateLogService) {
		this.dbUserOperateLogService = dbUserOperateLogService;
	}

	/**
	 * 展示用户列表
	 *
	 * @return json
	 */
	@RequestMapping(value = "list_data.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Feature(method = MethodFeature.LIST)
	public String listData() {
		PageResultDto<UserOperateLogV1> pageResult = dbUserOperateLogService.listPage(getRequest());
		return JsonMessage.getString(200, "获取成功", pageResult);
	}
}
