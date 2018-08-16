package kdh.picdeal.dao.ibatis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import kdh.picdeal.dao.KdhDao;

public class KdhDaoSqlMap extends SqlMapClientDaoSupport implements KdhDao{

	@Override
	public Map<String, Object> GetKdhImgList(Map<String, Object> param) {
		// TODO Auto-generated method stub
		List<Map<String,Object>> ls=(List<Map<String, Object>>) (getSqlMapClientTemplate()
				.queryForList("GetKdhImgList", param));
		Map<String,Object> resultMap=new HashMap<String,Object>();
		if(ls!=null&&ls.size()>0){
			resultMap=ls.get(0);
		}
		return resultMap;
	}

	@Override
	public Map<String, Object> GetQyImgList(Map<String, Object> param) {
		// TODO Auto-generated method stub
		List<Map<String,Object>> ls=(List<Map<String, Object>>) (getSqlMapClientTemplate()
				.queryForList("GetQyImgList", param));
		Map<String,Object> resultMap=new HashMap<String,Object>();
		if(ls!=null&&ls.size()>0){
			resultMap=ls.get(0);
		}
		return resultMap;
	}
	
	

}
