package net.noyark.www.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.noyark.www.core.HandlerMapper;
import net.noyark.www.core.NoyarkApplicationContext;
import net.noyark.www.interf.HandlerBase;
import net.noyark.www.interf.Mapper;
import net.noyark.www.interf.NoyarkAbstractApplicationContext;


public class MainServlet extends HttpServlet {
	public static final String REDIRECT ="redirect:" ;
	private static final long serialVersionUID = 1L;
	private Mapper handlerMapper;
	private NoyarkAbstractApplicationContext naac;
	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String path = req.getRequestURI().substring(req.getContextPath().length()+1);
		HandlerBase handler = handlerMapper.getHandler(path);
		try {
			if(handler!=null) {
				Object file = handler.excuteThat(req);
				if(file instanceof String) {
					String theToPath = (String)file;
					if(theToPath.startsWith(REDIRECT)) {
						theToPath = theToPath.substring(REDIRECT.length());
						if(!theToPath.startsWith("http")) {
							theToPath = req.getContextPath()+theToPath;
						}
						resp.sendRedirect(theToPath);
					}else {
						req.getRequestDispatcher(theToPath).forward(req, resp);
					}
				}
			}else {
				resp.setStatus(404);
				resp.getWriter().println("<h1>404 NOTFOUND</h1>");
				resp.getWriter().println("Not found the "+path+" file");
			}
		} catch (Exception e) {
			throw new ServletException("<h1>505 error</h1>",e);
		}
	}
	@Override
	public void init() throws ServletException {
		String path = this.getInitParameter("contextConfigLocation");
		boolean isStream = false;
		if(path.startsWith("classpath:")) {
			path = path.substring("classpath:".length());
			isStream = true;
		}
		naac = new NoyarkApplicationContext(path,isStream);
		handlerMapper = new HandlerMapper();
		Collection<Object> beans = naac.getBeans();
		List<Object> allBeans = new ArrayList<Object>();
		for(Object o:beans) {
				allBeans.add(o);
		}
		try {
			handlerMapper.handleMap(allBeans);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	@Override
	public void destroy() {
		naac.close();
	}
}
