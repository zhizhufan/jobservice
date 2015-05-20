package esd.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import esd.bean.Area;
import esd.bean.Articles;
import esd.service.CookieHelper;
import esd.service.KitService;
import esd.service.ArticlesService;

/**
 * 工作流程controller
 * 
 * @author Administrator
 * 
 */
@Controller
@RequestMapping("/workflow")
public class WorkflowController {

	private static Logger log = Logger.getLogger(WorkflowController.class);

	@Autowired
	private ArticlesService newsService;

	@RequestMapping("/search")
	public ModelAndView work(HttpServletRequest request) {
		log.debug(request.getRequestURI());
		ModelAndView mav = new ModelAndView("workflow/workflow");
		return mav;
	}

	// 工作流程 分页查询
	@RequestMapping(value = "/search/{page}", method = RequestMethod.GET)
	public ModelAndView search(HttpServletRequest request,
			@PathVariable(value = "page") Integer page) {
		log.info("--- search ---");
		//从cookie读取acode
		String acode = CookieHelper.getCookieValue(request, Constants.AREACODE);
		Articles n = new Articles();
		n.setType(Constants.ARTICLETYPE.WORKFLOW.getValue());
		n.setArea(new Area(acode));
		String keyWord = request.getParameter("keyWord");
		if (keyWord != null && !"".equals(keyWord)) {
			n.setTitle(keyWord);
		}
		String releaseDateStr = request.getParameter("releaseDate");
	
		if (releaseDateStr != null && !"".equals(releaseDateStr)) {
			Integer releaseDate = Integer.valueOf(releaseDateStr);
			Date update_Date=KitService.getreleaseTime(releaseDate.longValue());
			n.setUpdateDate(update_Date);
			
		}
		Map<String, Object> entity = new HashMap<String, Object>();
		List<Articles> newsList = newsService.getListForShow(n, page,
				Constants.SIZE);
		Integer records = newsService.getTotalCount(n);
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		if (newsList != null && records != null && records > 0) {
			try {
				for (Iterator<Articles> iterator = newsList.iterator(); iterator
						.hasNext();) {
					Articles it = (Articles) iterator.next();
					log.debug(it.toString());
					Map<String, String> map = new HashMap<String, String>();
					map.put("id", String.valueOf(it.getId()));
					map.put("createDate", KitService.dateForShow(it.getCreateDate()));
					map.put("title", it.getTitle());
					map.put("areaName", it.getArea().getName());
					map.put("author", it.getAuthor());
					map.put("source", it.getSource());
					list.add(map);
				}
			} catch (Exception e) {
				log.error("error in list", e);
			}
		}
		while (list.size() < Constants.SIZE) {
			Map<String, String> map = new HashMap<String, String>();
			list.add(map);
		}

		entity.put("list", list);
		log.info("*******************************************************************");
		log.info("records = " + records);
		PaginationUtil pagination = new PaginationUtil(page, records);
		entity.put("pagination", pagination.getHandler());
		return new ModelAndView("workflow/workflow-json", "entity", entity);
	}

	// 根据id得到一个 工作流程 返回前台显示
	@RequestMapping("/getOneForShow")
	public String getOneForShow(HttpServletRequest req, RedirectAttributes ra) {
		log.info("--- getOneForShow ---");
		String id = req.getParameter("id");
		if (id == null ||"".equals(id)) {
			ra.addFlashAttribute("messageType", "0");
			ra.addFlashAttribute("message", "传递的参数有误!");
			return "/error";
		}
		// 将news放入到request中
		String acode = CookieHelper.getCookieValue(req, Constants.AREACODE);
		Articles news = newsService.getOneForShow(id);
		req.setAttribute("news", news);
		Articles newsparameter=new Articles();
		newsparameter.setType(news.getType());
		newsparameter.setArea(new Area(acode));
		
		List<Articles> list = newsService.getTitleList(newsparameter, 1, 15);
		req.setAttribute("newsList", list);
		return "workflow/workflow-detail";
	}

	
}