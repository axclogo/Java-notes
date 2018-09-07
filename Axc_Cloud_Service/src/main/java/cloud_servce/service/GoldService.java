package cloud_servce.service;


import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cloud_servce.conf.ConstantConfig;
import cloud_servce.dao.IUserDao;
import cloud_servce.entity.User;
import cloud_servce.result.FailureResult;
import cloud_servce.result.ResultBody;
import cloud_servce.result.SuccessResult;
import cloud_servce.util.decision_making.Permissions;
import cloud_servce.util.HttpUtils;

@Service
public class GoldService {
	@Autowired
	private IUserDao userDao;

	public Object shgold(Integer userId ){
		User user = userDao.findByUserId(userId); // 查看User对象
		ResultBody result;
		if (user == null) {
			result = new FailureResult("401",ConstantConfig.EMPTY_USER_PROMPT);
			return result;
		}
		// 检查是否有权限调用该接口
		boolean is_permissions = Permissions.userPermissions(user.getService(), "3");
		if (is_permissions) {
			try {
				HashMap<String, String> parm = new HashMap<>();
				parm.put("key", ConstantConfig.JUHE_APIKEY);
				HttpResponse response = HttpUtils.doGet(
						ConstantConfig.JUHE_GOLD_URL, parm );
				// 得到返回数据
				String data = EntityUtils.toString(response.getEntity());
				// 字符转JsonObj
				JSONObject dataMap = JSON.parseObject(data);
				JSONArray resultArr = dataMap.getJSONArray("result");
				result = new SuccessResult("黄金数据接口请求成功", resultArr);
			} catch (Exception e) {
				// TODO: handle exception
				System.err.println(e);
				result = new FailureResult("黄金数据接口请求失败！");
			}
		}else {
			result = new FailureResult(ConstantConfig.USER_PERMISSIONS_PROMPT);
		}
		return result;
	}

}
