package kdh.picdeal.dao;

import java.util.List;
import java.util.Map;

public interface KdhDao {
   public Map<String,Object> GetKdhImgList(Map<String,Object> param);
   
   public Map<String,Object> GetQyImgList(Map<String,Object> param);
}
