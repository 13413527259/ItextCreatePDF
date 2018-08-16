package com.topdf.controller;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import netsky.cabp.org.SignerIntf;
import netsky.cabp.util.TypeUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Entities;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.BaseFont;

public class ConvertPDF implements Controller {

	private SignerIntf signer;
	private static final String FONT = "simhei.ttf";
	
	@Override
	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		//��֤�û���¼
		signer.checkSignedOn(request);		
		// ��ȡrequest��������Ĳ���
		int cvId = TypeUtil.ObjectToInt(request.getParameter("cvId"));
		int itemId = TypeUtil.ObjectToInt(request.getParameter("itemId"));
		String par_files = TypeUtil.ObjectToString(request.getParameter("par_files"));
		//��ȡ���̲����Ŀ¼
        String realPath = request.getSession().getServletContext().getRealPath("");
        //ͼƬ�ڹ����еĻ���·��
        String imgBasePath = new File(realPath+"/jsp/").toURI().toURL().toString(); 
		//������������磺http://localhost:8080/oseman
		String domainURL = request.getScheme() + "://" + request.getServerName()
				+ ":" + request.getServerPort() + request.getContextPath();
		//ƴ����Ҫ��ȡҳ�����ݵ�����URL
		String url = domainURL + "/ct/viewCtcontent.nx?ctAction=View&cvId=" + cvId 
							 + "&itemId=" + itemId + "&AddParam=par_files:" + par_files;
		//��ȡ��ǰ��¼jssessionId�����ڱ���������ȡҳ��ʱ��ʾ��¼
		Cookie[] cookies = request.getCookies();
		String jsessionid = "";
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals("JSESSIONID")) {
				jsessionid = cookie.getValue();
			}
		}		
		// ��ȡҳ����Ϣ
		String[] pageInfo = extractPageInfo(url, jsessionid);						      
		//ʹ��jsoup��htmlתΪxhtml����ȫ������ǩ����Ϊitext��html�����ϸ��Ҫ�󣬱����п�ʼ�ͽ����ɶԱ�ǩ
        org.jsoup.nodes.Document jdoc = Jsoup.parse(pageInfo[0]);        
		jdoc.outputSettings()
				.syntax(org.jsoup.nodes.Document.OutputSettings.Syntax.xml)
				.escapeMode(Entities.EscapeMode.xhtml);
        String content = jdoc.html();
        
        //�ļ�����
        String filename = "�渽��֤";
        String fullname = new String(filename.getBytes(), "iso-8859-1") + ".pdf";
        response.setContentType("multipart/form-data");
		response.setHeader("Content-disposition", "attachment;filename=" + fullname);// ����ͷ��Ϣ
        ServletOutputStream os = response.getOutputStream(); 
        //����PDF
        ConvertPDF.createPdf(content,os,imgBasePath);
        return null;
	}
	
	/**
	 * 
	 * ����PDF 
	 *
	 */
	public static void createPdf(String content,ServletOutputStream os,String imgBasePath) throws DocumentException, IOException,com.lowagie.text.DocumentException {
        //������Ⱦ��
        ITextRenderer renderer = new ITextRenderer();
        //�������������
        ITextFontResolver fontResolver = renderer.getFontResolver();
        //�����������
        fontResolver.addFont(FONT, BaseFont.IDENTITY_H,BaseFont.NOT_EMBEDDED);
        //����html����pdf
        renderer.setDocumentFromString(content);
        //���ͼƬ���·��������
        renderer.getSharedContext().setBaseURL(imgBasePath);
        renderer.layout();
        renderer.createPDF(os);
        os.flush();
		os.close();
	}

	/**
	 * 
	 * ����URL��ȡҳ�����Ϣ
	 * 
	 * 0815 cjh ���ܻ�������JSESSIONID(һ����Чһ����Ч)
	 * 
	 */
	public static String[] extractPageInfo(String url, String jsessionid)
			throws IOException {
		String[] pageInfo = new String[4];
		// ��ȡURL���ص�ҳ��
		org.jsoup.nodes.Document doc = Jsoup.connect(url)
				.cookie("JSESSIONID", jsessionid).get();
		// ��ȡclassΪwarp��div����
		org.jsoup.nodes.Element content = doc.select("div.warp").first();
		pageInfo[0] = content.html();
		return pageInfo;
	}

	public SignerIntf getSigner() {
		return signer;
	}

	public void setSigner(SignerIntf signer) {
		this.signer = signer;
	}

}
