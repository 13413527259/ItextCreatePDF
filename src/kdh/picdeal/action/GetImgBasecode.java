package kdh.picdeal.action;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.rt.BASE64Encoder;

import kdh.picdeal.dao.ibatis.KdhDaoSqlMap;
import netsky.cabp.util.ImageUtils;

public class GetImgBasecode {
	private static KdhDaoSqlMap dao;

	public static String getImgBase64Code(int p_id) {
		String imgeBase64Code = "";
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("itemId", p_id);
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap = dao.GetKdhImgList(param);
		if (resultMap != null) {
			Blob imgBlob = (Blob) resultMap.get("fileblob");
			try {
				byte[] imgData = imgBlob.getBytes(1, (int) imgBlob.length());
				imgData = ImageUtils.processImage(imgData, 1024, 1024, 100, true, true);
				BASE64Encoder encoder = new BASE64Encoder();
				imgeBase64Code = encoder.encode(imgData);
				if (imgeBase64Code != null && !imgeBase64Code.equals("")) {
					imgeBase64Code = "data:image/png;base64," + imgeBase64Code;
				} else {
					imgeBase64Code = "";
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return imgeBase64Code;
	}

	/**
	 * ÆóÒµµÄÕÂ
	 * 
	 * @param p_id
	 * @return
	 */
	public static String getQyImgBase64Code(int p_id) {
		String imgeBase64Code = "";
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("itemId", p_id);
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap = dao.GetQyImgList(param);
		if (resultMap != null) {
			Blob imgBlob = (Blob) resultMap.get("fileblob");
			imgeBase64Code="data:image/png;base64," +convertBlobToBase64String(imgBlob);
		}
		return imgeBase64Code;
	}

	public static String convertBlobToBase64String(Blob blob) {
		String result = "";
		if (null != blob) {
			try {
				InputStream msgContent = blob.getBinaryStream();
				ByteArrayOutputStream output = new ByteArrayOutputStream();
				byte[] buffer = new byte[100];
				int n = 0;
				while (-1 != (n = msgContent.read(buffer))) {
					output.write(buffer, 0, n);
				}
				result = new BASE64Encoder().encode(output.toByteArray());
				output.close();
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return result;
		} else {
			return null;
		}
	}

	public static KdhDaoSqlMap getDao() {
		return dao;
	}

	public static void setDao(KdhDaoSqlMap dao) {
		GetImgBasecode.dao = dao;
	}

}
