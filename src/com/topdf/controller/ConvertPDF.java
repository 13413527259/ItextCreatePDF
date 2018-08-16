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

		//验证用户登录
		signer.checkSignedOn(request);		
		// 获取request请求过来的参数
		int cvId = TypeUtil.ObjectToInt(request.getParameter("cvId"));
		int itemId = TypeUtil.ObjectToInt(request.getParameter("itemId"));
		String par_files = TypeUtil.ObjectToString(request.getParameter("par_files"));
		//获取工程部署的目录
        String realPath = request.getSession().getServletContext().getRealPath("");
        //图片在工程中的基础路径
        String imgBasePath = new File(realPath+"/jsp/").toURI().toURL().toString(); 
		//请求的域名，如：http://localhost:8080/oseman
		String domainURL = request.getScheme() + "://" + request.getServerName()
				+ ":" + request.getServerPort() + request.getContextPath();
		//拼接需要提取页面内容的请求URL
		String url = domainURL + "/ct/viewCtcontent.nx?ctAction=View&cvId=" + cvId 
							 + "&itemId=" + itemId + "&AddParam=par_files:" + par_files;
		//获取当前登录jssessionId，用于避免下面爬取页面时提示登录
		Cookie[] cookies = request.getCookies();
		String jsessionid = "";
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals("JSESSIONID")) {
				jsessionid = cookie.getValue();
			}
		}		
		// 提取页面信息
		String[] pageInfo = extractPageInfo(url, jsessionid);						      
		//使用jsoup把html转为xhtml，补全结束标签，因为itext对html有着严格的要求，必须有开始和结束成对标签
        org.jsoup.nodes.Document jdoc = Jsoup.parse(pageInfo[0]);        
		jdoc.outputSettings()
				.syntax(org.jsoup.nodes.Document.OutputSettings.Syntax.xml)
				.escapeMode(Entities.EscapeMode.xhtml);
        String content = jdoc.html();
        
        //文件名称
        String filename = "随附单证";
        String fullname = new String(filename.getBytes(), "iso-8859-1") + ".pdf";
        response.setContentType("multipart/form-data");
		response.setHeader("Content-disposition", "attachment;filename=" + fullname);// 设置头信息
        ServletOutputStream os = response.getOutputStream(); 
        //创建PDF
        ConvertPDF.createPdf(content,os,imgBasePath);
        return null;
	}
	
	/**
	 * 
	 * 创建PDF 
	 *
	 */
	public static void createPdf(String content,ServletOutputStream os,String imgBasePath) throws DocumentException, IOException,com.lowagie.text.DocumentException {
        //创建渲染器
        ITextRenderer renderer = new ITextRenderer();
        //创建字体解析器
        ITextFontResolver fontResolver = renderer.getFontResolver();
        //添加中文字体
        fontResolver.addFont(FONT, BaseFont.IDENTITY_H,BaseFont.NOT_EMBEDDED);
        //解析html生成pdf
        renderer.setDocumentFromString(content);
        //解决图片相对路径的问题
        renderer.getSharedContext().setBaseURL(imgBasePath);
        renderer.layout();
        renderer.createPDF(os);
        os.flush();
		os.close();
	}

	/**
	 * 
	 * 根据URL提取页面的信息
	 * 
	 * 0815 cjh 可能会有两个JSESSIONID(一个无效一个有效)
	 * 
	 */
	public static String[] extractPageInfo(String url, String jsessionid)
			throws IOException {
		String[] pageInfo = new String[4];
		// 获取URL返回的页面
		org.jsoup.nodes.Document doc = Jsoup.connect(url)
				.cookie("JSESSIONID", jsessionid).get();
		// 提取class为warp的div部分
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
