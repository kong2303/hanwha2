package com.hanwha.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.hanwha.model.DeptDAO_Mybatis;
import com.hanwha.model.DeptDTO;
import com.hanwha.model.EmpService;
import com.hanwha.model.EmpVO;

//<bean id="myController" class="com.hanwha.controller.MyController">
@Controller
public class MyController {

	// <bean id="dao" class="com.hanwha.model.DeptDAO"/>
	// <bean id="myController" class="com.hanwha.controller.MyController">
	// <property name="dao" ref="dao">
	// </bean>
	// 를 @Autoiwired 한방에 끝냄

	@Autowired
	DeptDAO_Mybatis dao;
	// DeptDAO dao;

	@Autowired
	EmpService service;

	@RequestMapping("/dept/deptdownload")
	public void deptdownload(String folder, String file, HttpServletRequest request, HttpServletResponse response) throws IOException{
		response.setHeader("Content-Disposition", "attachment;filename=" + file);
		String fullPath = request.getSession().getServletContext().getRealPath(folder + "/" + file);
		FileInputStream fi = new FileInputStream(fullPath);
		ServletOutputStream sout = response.getOutputStream();
		byte[] buf = new byte[1024];
		int size = 0;
		while ((size = fi.read(buf, 0, 1024)) != -1) {
			sout.write(buf, 0, size);
		}
		fi.close();
		sout.close();
	}

	@RequestMapping("/404")
	public String error404(Model model) {
		model.addAttribute("company", "한화ICT");
		model.addAttribute("manager", "공선빈");
		return "error404";
	}

	@ExceptionHandler(Exception.class)
	public String error500(Exception ex, Model model) {
		model.addAttribute("company", "한화ICT");
		model.addAttribute("manager", "공선빈");
		model.addAttribute("phone", "000-0000-0000");
		model.addAttribute("errormessage", ex.getMessage());
		return "error500";
	}

	/*
	 * @RequestMapping("/500") public String error500(Model model) {
	 * model.addAttribute("company", "한화ICT"); model.addAttribute("manager", "공선빈");
	 * model.addAttribute("phone", "000-0000-0000"); return "error500"; }
	 */

	@RequestMapping("/dept/deptlist")
	public String deptlist(Model model) {
		model.addAttribute("deptlist", dao.selectAll());

		return "dept/deptlist";
	}

	@RequestMapping(value = "/dept/deptdetail", method = RequestMethod.GET)
	public String deptdatail(int deptid, Model model) {
		model.addAttribute("dept", dao.selectById(deptid));
		return "dept/deptdetail";
	}

	@RequestMapping(value = "/dept/deptdetail", method = RequestMethod.POST)
	public String deptdatailPost(DeptDTO dept) {
		dao.updateDept(dept);
		return "redirect:deptlist";
	}

	@RequestMapping(value = "/dept/deptinsert", method = RequestMethod.GET)
	public String deptinsert() {

		return "dept/deptinsert";
	}

	@RequestMapping(value = "/dept/deptinsert", method = RequestMethod.POST)
	public String deptinsertPost(DeptDTO dept, HttpServletRequest request) {

		MultipartFile uploadfile = dept.getUploadfile();
		if (uploadfile == null)
			return "redirect:deptinsert";
		String path = request.getSession().getServletContext().getRealPath("/resources");
		System.out.println("웹서버의 실제경로" + path);
		String fileName = uploadfile.getOriginalFilename();
		String fpath = path + "\\" + fileName;
		dept.setFileName(fileName); // 파일 경로를 저장하기 위함
		try {

			// 2. File 사용
			File file = new File(fpath);
			uploadfile.transferTo(file);

		} catch (IOException e) {
			e.printStackTrace();
		}

		dao.insertDept(dept);
		return "redirect:deptlist";
	}

	@RequestMapping("/dept/deptdelete")
	public String deptdelete(int deptid) {
		dao.deleteDept(deptid);
		return "redirect:deptlist";
	}

	@RequestMapping("/emp/emplist")
	public ModelAndView emplist() {
		ModelAndView mv = new ModelAndView();
		mv.addObject("emplist", service.selectAll());
		mv.setViewName("emp/emplist");
		return mv;
	}

	@RequestMapping(value = "/emp/empinsert", method = RequestMethod.GET)
	public String empinsert(Model model) {
		model.addAttribute("joblist", service.selectAllJob());
		model.addAttribute("mlist", service.selectAllManager());
		model.addAttribute("deptlist", dao.selectAll());
		return "emp/empinsert";
	}

	@RequestMapping(value = "/emp/empinsert", method = RequestMethod.POST)
	public String empinsertPost(EmpVO vo) {
		service.insertEmp(vo);
		return "redirect:emplist";
	}

	@RequestMapping(value = "emp/empdetail", method = RequestMethod.GET)
	public ModelAndView empdetail(int empid) {
		ModelAndView mv = new ModelAndView("emp/empdetail");
		mv.addObject("emp", service.selectById(empid));
		mv.addObject("joblist", service.selectAllJob());
		mv.addObject("mlist", service.selectAllManager());
		mv.addObject("deptlist", dao.selectAll());

		return mv;
	}

	@RequestMapping(value = "emp/empdetail", method = RequestMethod.POST)
	public String empdetailPost(EmpVO vo) {
		service.updateEmp(vo);

		return "redirect:emplist";
	}

	@RequestMapping("emp/empdelete")
	public String empdelete(int empid) {
		service.deleteEmp(empid);

		return "redirect:emplist";
	}
}
