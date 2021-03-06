package esd.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.databind.util.JSONPObject;

import esd.bean.Area;
import esd.bean.Job;
import esd.bean.JobCategory;
import esd.common.PoiCreateExcel;
import esd.controller.Constants.Notice;
import esd.service.AreaService;
import esd.service.CookieHelper;
import esd.service.JobService;
import esd.service.KitService;

@Controller
@RequestMapping("/job")
public class JobController {

	private static Logger log = Logger.getLogger(JobController.class);
	@Autowired
	private JobService jobService;

	@Autowired
	private AreaService areaService;

	// 根据id得到一个职位返回前台显示
	@RequestMapping("/getOneForShow")
	public String getOneForShow(HttpServletRequest request,
			HttpServletResponse response, RedirectAttributes ra) {
		log.info("--- getOneForShow ---");

		/**
		 * 处理地区code和如何存放到cookie中的问题
		 */
		// ①先查看request中有没有传过来的acode,
		String acode = request.getParameter("acode");
		if (acode != null && !"".equals(acode)) {
			// ②不为空时, 则表示为从残联网站跳转过来的, 则清除原来可能存在的所有用户, 地区等cookie信息
			CookieHelper.killUserCookie(response, true);
		} else {
			// ③为空在则检查cookie是中没有地区信息, 如果cookie中也没有, 则说明是首次访问首页且不是从网站群跳转过来的,
			// 那么使用全国code
			String cookieAreaCode = CookieHelper.getCookieValue(request,
					Constants.AREACODE);
			if (cookieAreaCode == null || "".equals(cookieAreaCode)) {
				cookieAreaCode = Constants.AREACOUNTRY;
			}
			acode = cookieAreaCode;
		}
		// ④得到地区信息对象, 将地区名称放入到cookie中
		Area area = areaService.getByCode(acode);
		CookieHelper.setCookie(response, request, null, area);

		String idStr = request.getParameter("id");
		int id = KitService.getInt(idStr);
		if (id < 0) {
			ra.addFlashAttribute("messageType", "0");
			ra.addFlashAttribute("message", "传递的参数有误!");
			return "redirect:/index";
		}
		// 将job放入到request中
		Job job = jobService.getOneForShow(id);
		request.setAttribute("job", job);
		// 同时将该公司所发布的其他职位也放入到request中
		List<Job> jobList = jobService.getByCompanyForShow(job.getCompany()
				.getId(), 1, 99);
		request.setAttribute("jobList", jobList);
		return "work/work-detail";
	}

	// 根据id得到一个职位返回前台数据以供显示
	@RequestMapping(value = "/getOneForShowData", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> getOneForShowData(HttpServletRequest req) {
		Job job = null;
		log.info("--- getOneForShow ---");
		String idStr = req.getParameter("id");
		int id = KitService.getInt(idStr);
		if (id < 0) {
			req.setAttribute("notice", "传递的参数有误!");
			return null;
		}

		try {
			job = jobService.getOneForShow(id);
		} catch (Exception e) {
			log.error("根据id得到一个职位返回前台数据。没有取到数据");
			return null;
		}
		log.info("job" + job);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("job", job);
		return map;
	}

	// 根据企业id, 得到该企业所发布的职位
	@RequestMapping("/getByCompany")
	@ResponseBody
	public Map<String, Object> getByCompany(HttpServletRequest req) {
		log.info("--- getByCompany ---");
		Map<String, Object> json = new HashMap<String, Object>();
		// 得到当前企业用户
		String idStr = req.getParameter("companyid");
		int id = KitService.getInt(idStr);
		if (id < 0) {
			json.put("notice", Notice.ERROR.toString());
			return json;
		}
		List<Job> jobList = jobService.getByCompanyForShow(id, 1,
				Constants.SIZE);
		log.info(" jobList.size() = " + jobList.size());
		json.put("notice", Notice.SUCCESS);
		json.put("jobList", jobList);
		return json;
	}

	// 根据职位种类code, 获得对应类型的51条数据, 以供前台显示
	@RequestMapping(value = "/getByCategory/{jobCategoryCode}", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> getByJobCategory(
			@PathVariable(value = "jobCategoryCode") String jobCategoryCode,
			HttpServletRequest request) {
		String acode = CookieHelper.getLocalArea(request);
		Map<String, Object> map = new HashMap<String, Object>();
		Job object = new Job();
		object.setArea(new Area(acode));
		object.setJobCategory(new JobCategory(jobCategoryCode));
		List<Job> jobByCategoryResult = jobService.getListForShow(object,
				Constants.START, 51);
		List<Map<String, Object>> jobByCategoryList = new ArrayList<Map<String, Object>>();
		for (Job job : jobByCategoryResult) {
			Map<String, Object> j = new HashMap<String, Object>();
			j.put("jobId", job.getId());
			j.put("jobName", job.getName());
			j.put("companyId", job.getCompany().getId());
			j.put("companyName", job.getCompany().getName());
			jobByCategoryList.add(j);
		}
		map.put("jobByCategoryList", jobByCategoryList);
		return map;
	}

	// 多条件查询职位--给opencms框架使用
	@RequestMapping(value = "/searchForOpenCms", method = RequestMethod.GET)
	@ResponseBody
	public JSONPObject searchForOpenCms(
			@RequestParam(value = "callback") String callback,
			HttpServletRequest req) {
		log.info("--- searchForOpenCms ---");
		// 接收从网站群接收来的地区code, 根据他查找所属地区的职位
		String acode = req.getParameter("acode");
		String pageSizeStr = req.getParameter("pageSize");
		// 初始化为10
		Integer pageSize = 10;
		if (pageSizeStr != null && !"".equals(pageSizeStr)) {
			pageSize = Integer.parseInt(pageSizeStr);
		}
		ModelMap map = new ModelMap();
		List<Job> jobList = jobService.getByNew(acode, pageSize);
		map.put("jobList", jobList);
		return new JSONPObject(callback, map);
	}

	// 导出所选信息 --不许删
	@RequestMapping(value = "/jobExportSelected", method = RequestMethod.POST)
	@ResponseBody
	public String ExportSelected(
			@RequestParam(value = "params[]") int params[],
			HttpServletRequest req) {
		log.info("--------  down_multi ----------" + params.toString());
		boolean b = true;
		List<Job> job = new ArrayList<Job>();
		for (int i = 0; i < params.length; i++) {
			job.add(jobService.getOneForShow(params[i]));
		}
		String url = req.getRealPath("/");
		// 创建导出文件夹
		File uploadPath = new File(url + "upload");
		// 导出文件夹
		String exportFolder = uploadPath + File.separator + "job";
		File jobPath = new File(exportFolder);
		if (!(uploadPath.exists())) {
			uploadPath.mkdir();
		}
		if (!(jobPath.exists())) {
			jobPath.mkdir();
		}
		// 创建文件唯一名称
		String uuid = UUID.randomUUID().toString();
		String exportPath = exportFolder + File.separator + uuid + ".xls";
		String FileDownloadPath = "null";
		// 导出文件
		b = PoiCreateExcel.createJobExcel(exportPath, job);
		if (b) {
			String destPath = req.getLocalAddr() + ":" + req.getLocalPort()
					+ req.getContextPath();
			FileDownloadPath = "http://" + destPath + "/upload/job/" + uuid
					+ ".xls";
		}
		return FileDownloadPath;

	}

	// 批量导出信息 --不许删
	@RequestMapping(value = "/jobExportAll", method = RequestMethod.POST)
	@ResponseBody
	public String ExportAll(Job param, HttpServletRequest req) {
		boolean b = true;
		Job paramEntity = new Job();
		String targetName = param.getTargetName();
		String checkStatus = param.getCheckStatus();
		if (checkStatus == null || "".equals(checkStatus)) {
			checkStatus = Constants.CheckStatus.DAISHEN.getValue();
		}
		paramEntity.setName(targetName);
		paramEntity.setCheckStatus(checkStatus);
		Integer total = jobService.getTotalCount(paramEntity, Boolean.TRUE);
		Integer page = 1;
		List<Job> job = jobService.getListForManage(paramEntity, page, total);
		String url = req.getRealPath("/");

		// 创建导出文件夹
		File uploadPath = new File(url + "upload");
		// 导出文件夹
		String exportFolder = uploadPath + File.separator + "job";
		File jobPath = new File(exportFolder);
		if (!(uploadPath.exists())) {
			uploadPath.mkdir();
		}
		if (!(jobPath.exists())) {
			jobPath.mkdir();
		}
		// 创建文件唯一名称
		String uuid = UUID.randomUUID().toString();
		String exportPath = exportFolder + File.separator + uuid + ".xls";
		String FileDownloadPath = "null";
		// 导出文件
		b = PoiCreateExcel.createJobExcel(exportPath, job);
		if (b) {
			String destPath = req.getLocalAddr() + ":" + req.getLocalPort()
					+ req.getContextPath();
			FileDownloadPath = "http://" + destPath + "/upload/job/" + uuid
					+ ".xls";
		}
		return FileDownloadPath;
	}

}
