package com.tcs.assignment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import aj.org.objectweb.asm.Attribute;

import java.time.LocalDate;
import java.util.*;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.*;



@Controller
public class HospitalManagementController {

	@Autowired
	private PatientService ps;
	
	
	@GetMapping("/")
    public String showloginForm(Model model) {
		model.addAttribute("login",new login());
        return "Login";
    }

	@RequestMapping(value="/checkLogin" ,method=RequestMethod.POST)
	public ModelAndView validateLoginInfo(ModelMap model,@ModelAttribute login loginForm,HttpServletRequest req) {
		loginForm=ps.checkLoginuser(loginForm);
		ModelAndView mv=new ModelAndView();
		System.out.println("verified login"+loginForm);
        if (loginForm==null) {
        	String message="Please, Enter correct username and password";
        	//atr.addFlashAttribute("ermsg",message);
        	//ses.setAttribute("ermsg", message);
        	mv.addObject("ermsg",message);
        	mv.setViewName("Login");
            return mv;
        }
        else {
        	HttpSession ses=req.getSession(true);
        	ses.setMaxInactiveInterval(5*60);
        ses.setAttribute("uname", loginForm.getUsername());
        ses.setAttribute("role", loginForm.getRole());}
        return new ModelAndView("redirect:/index");
	}
	
	@GetMapping("/index")
	public String home(HttpServletRequest req){
		HttpSession ses=req.getSession(true);
		if(ses.getAttribute("uname")!=null) {
		return "index";}
		else return "ErrorPage";
	}
	
	@GetMapping("/logout")
	public String logout(HttpServletRequest req) {
		HttpSession ses=req.getSession(true);
		ses.invalidate();
		return "redirect:/";
	}
	
	@GetMapping("/add-patient")
	public String showRegForm(Model model,HttpServletRequest req) {
		HttpSession ses=req.getSession(true);
		if(ses.getAttribute("uname")!=null && ses.getAttribute("role").equals("registration desk")) {
		model.addAttribute("patient" , new Patient());
		return "Registration";}
		else return "ErrorPage";
	}
	@RequestMapping(value="/addPatient" ,method=RequestMethod.POST)
	public ModelAndView addPatient(@ModelAttribute("patient") Patient pt) {		
		int k=ps.addPatient(pt);
		ModelAndView mv=new ModelAndView();
		System.out.println("got value is: "+k);
		if(k==0) { String ermsg="Patient Registered successfully";      mv.addObject("msg",ermsg);}
		if(k==1) { String ermsg="Adhar id matched,Try another adhar";      mv.addObject("msg",ermsg);}
		mv.setViewName("Registration");
		return mv;
	}
	
	@RequestMapping(value="/updatePatient" ,method=RequestMethod.POST)
	public ModelAndView updatePatient(@ModelAttribute("patient") Patient pt) {		
		int k=ps.updatePatient(pt);
		ModelAndView mv=new ModelAndView();
		System.out.println("got value is: "+k);
		if(k==1) { String ermsg="Patient Updated successfully";      mv.addObject("msg",ermsg);}
		//if(k==1) { String ermsg="Adhar id matched,Try another adhar";      mv.addObject("msg",ermsg);}
		mv.setViewName("updatePatient");
		return mv;
	}
	
	@RequestMapping(value="/get-patient" ,method=RequestMethod.GET)
	public String showPatient(Model model) {
		List<Patient> l1=ps.viewAllPatient();
		List<Integer> l3=new ArrayList<Integer>();
		for(Patient p:l1) {
			l3.add(p.getPatientId());
		}
		Collections.sort(l3);
		LinkedList<Patient> l2=new LinkedList<Patient>();
		for(Integer x:l3) {
		for(Patient p:l1) {
			if(p.getStatus().equalsIgnoreCase("Active")&& p.getPatientId()==x)l2.add(p);
		}
		}
		
		model.addAttribute("plist" , l2);
		//model.addAttribute("message","Patient added successfully");
		return "showPatient";
	}
	
	@GetMapping("/get-patient-detail/{id}")
	public ModelAndView showPatientbyid(@PathVariable("id") int id,Model model,HttpServletRequest req,@ModelAttribute Patient pt) {
		HttpSession ses=req.getSession(true);
		ModelAndView mv=new ModelAndView();
		System.out.println("search input: "+id);
		String forward="";
		if(ses.getAttribute("uname")!=null) {
			System.out.println("value got in searcvh is:"+req.getParameter("searpatientId"));
			if(req.getParameter("searpatientId")!=null)pt.setPatientId(Integer.parseInt(req.getParameter("searpatientId")));
		pt=ps.getdetailbyPtId(pt);	
		if(pt!=null) {
			if(id==0) {
				System.out.println("update patient running.....");
				System.out.println("value in serach "+pt);
			mv.addObject("patient",pt);
			mv.setViewName("updatePatient");
		return mv;}
		
			 if(id==1) {
				
			mv.addObject("patient",pt);
			mv.setViewName("deletePatient");
			return mv;}
			 
			 if(id==2) {
				 
				 
					mv.addObject("patients",pt);
					System.out.println("from controller"+pt.getPatientId());
					List<PatientMedicine> p1=ps.getpatientMedicineById(pt.getPatientId());
					System.out.println("from controller p1"+p1);
					mv.addObject("plist1",p1);
					List<MedicineMaster> mmed=ps.getAllMedicine();
					MedicineMaster sno=null;
					//Gson gson = new GsonBuilder().setPrettyPrinting().create();
					//String sno=new Gson().toJson(mmed);
					String name=req.getParameter("getmed");
					if(name!=null) {
						for(MedicineMaster x:mmed) {
							if(name.equalsIgnoreCase(x.getMedicineName())) {
								sno=x;
							}
					}
					}
					if(name!=null && sno==null) {
						String ermsg="Medicine not found, Try again!";
						mv.addObject("msg",ermsg);
					}
					String name2=req.getParameter("quan");
					String name3=req.getParameter("amt");
					if(name2!=null && name3!=null) {
						PatientMedicine newpt=new PatientMedicine();
						newpt.setMedicineId(sno.getMedicineId());
						newpt.setPat_med_id(101);
						newpt.setMedName(sno.getMedicineName());
						newpt.setAmount(Double.parseDouble(name3));
						newpt.setPatientId(pt.getPatientId());
						newpt.setRateOfMed(sno.getRateOfMed());
						if(Integer.parseInt(name2)<=sno.getQuantity()) {
						newpt.setQuantity(Integer.parseInt(name2));
						System.out.println(newpt);
						ps.addPatientMedicine(newpt);
						MedicineMaster newmt=new MedicineMaster();
						newmt.setMedicineId(sno.getMedicineId());
						newmt.setMedicineName(sno.getMedicineName());
						newmt.setQuantity(sno.getQuantity()-Integer.parseInt(name2));
						newmt.setRateOfMed(sno.getRateOfMed());
						ps.updateMastermedicine(newmt);
						String sucmsg="Medicine added successfully,please click on refresh button";
						mv.addObject("msg",sucmsg);
						}
						else {
							String ermsg="Medicine is not enough choose quantity less than"+sno.getQuantity();
							mv.addObject("msg",ermsg);
						}
					}
					
					mv.addObject("medlist",sno);
					mv.setViewName("issueMedicine");
					
					
					return mv;} 
			 if(id==3) {
				 
				 
					mv.addObject("patients",pt);
					System.out.println("from controller"+pt.getPatientId());
					List<PatientDaignostic> p1=ps.getpatientDiagnosticById(pt.getPatientId());
					System.out.println("from controller p1"+p1);
					mv.addObject("plist1",p1);
					String name=req.getParameter("getmed");
					List<MasterDiagnostic> mmed=ps.getAllDiagnostic();
					MasterDiagnostic sno=null;
					if(name!=null) {
						for(MasterDiagnostic x:mmed) {
							if(name.equalsIgnoreCase(x.getNameOfTest())) {
								sno=x;
							}
					}
					}
					if(name!=null && sno==null) {
						String ermsg="Diagnostic not found, Try again!";
						mv.addObject("msg",ermsg);
					}
					
					PatientDaignostic pdi=new PatientDaignostic();
					if(name!=null) {
						pdi.setNameOfTest(sno.getNameOfTest());
						pdi.setPatientId(pt.getPatientId());
						pdi.setAmount(sno.getAmount());
						ps.addPatientDiagnose(pdi);
						
						
					}
					
					mv.addObject("diaglist",mmed);
					mv.addObject("diag",sno);
					mv.setViewName("diagnostic");
					
					
					return mv;}
			 if(id==4) {
				 
				 
					mv.addObject("patients",pt);
					System.out.println("from controller"+pt.getPatientId());
					List<PatientMedicine> p1=ps.getpatientMedicineById(pt.getPatientId());
					System.out.println("from controller p1"+p1);
					mv.addObject("plist12",p1);
					List<PatientDaignostic> p2=ps.getpatientDiagnosticById(pt.getPatientId());
					System.out.println("from controller p2"+p2);
					mv.addObject("plist2",p2);
					Date d=pt.getDateOfAdd();
					Date d1=java.sql.Date.valueOf(LocalDate.now());
					long diff=d1.getTime()-d.getTime();
					long noOfday= diff/(1000*60*60*24);
					long roomrent=0;
					if(pt.getBedtype().equalsIgnoreCase("semi sharing")) roomrent=roomrent+noOfday*4000;
					if(pt.getBedtype().equalsIgnoreCase("General Ward")) roomrent=roomrent+noOfday*2000;
					if(pt.getBedtype().equalsIgnoreCase("Single Room")) roomrent=roomrent+noOfday*8000;
					System.out.println(noOfday);
					System.out.println(d1);
					System.out.println("date is "+d);
					
					double pharmacybill=0;
					double diagbill=0;
					for(PatientMedicine x:p1) {
						pharmacybill=pharmacybill+x.getAmount();
					}
					for(PatientDaignostic x:p2) {
						diagbill=diagbill+x.getAmount();
					}
					double total=roomrent+pharmacybill+diagbill;
					mv.addObject("noOfday",noOfday);
					mv.addObject("roomrent",roomrent);
					mv.addObject("pharmaill",pharmacybill);
					mv.addObject("diagbill",diagbill);
					mv.addObject("total",total);
					mv.setViewName("patientBilling");
					
					
					return mv;} 
			 if(id==5) {
				 
				 
					mv.addObject("patient",pt);
					
					mv.setViewName("patientDetail");
					
					
					return mv;} 
		} else {
			
			if(id==0) {String ermsg="Patient ID not found, try Again";mv.addObject("msg1",ermsg);mv.setViewName("updatePatient");}
			if(id==1) {String ermsg="Patient ID not found, try Again";mv.addObject("msg1",ermsg); mv.setViewName("deletePatient");}
			if(id==2) { String ermsg="Patient ID not found, try Again";mv.addObject("msg1",ermsg);mv.setViewName("issueMedicine");}
			if(id==3) { String ermsg="Patient ID not found, try Again";mv.addObject("msg1",ermsg);mv.setViewName("diagnostic");}
			if(id==4) {String ermsg="Patient ID not found, try Again";mv.addObject("msg1",ermsg); mv.setViewName("patientBilling");}
			if(id==5) {String ermsg="Patient ID not found, try Again";mv.addObject("msg1",ermsg);mv.setViewName("patientDetail");}
		}	return mv;}				
		else {
			mv.setViewName("Login");
			return mv;}
	}
	@RequestMapping("/update-patient")
	public String updateprofile(Model model,@ModelAttribute Patient pt,HttpServletRequest req) {
		HttpSession ses=req.getSession(true);
		if(ses.getAttribute("uname")!=null && ses.getAttribute("role").equals("registration desk")) {
			System.out.println("update patient value:"+pt);
		model.addAttribute("patientx",pt);
	return "updatePatient";}
		else return "ErrorPage";
	}
	
	@GetMapping("/show-delete-patient")
	public String showdelteForm(Model model,HttpServletRequest req) {
		HttpSession ses=req.getSession(true);
		if(ses.getAttribute("uname")!=null && ses.getAttribute("role").equals("registration desk")) {
		
		model.addAttribute("patient",new Patient());
        return "deletePatient";}
		else return "ErrorPage";
    }
	/*
	@RequestMapping("/delete-patient")
	public String deleteProfile(Model model,@ModelAttribute Patient pt,HttpServletRequest req) {
		HttpSession ses=req.getSession(true);
		if(ses.getAttribute("uname")!=null) {
			model.addAttribute("patient",pt);
		return "deletePatient";}
		else return "Login";
	}
	*/
	@RequestMapping(value="/del-patient" ,method=RequestMethod.POST)
	public String deletePatient(@ModelAttribute("patient") Patient pt) {
		ps.deletePatient(pt);
		return "redirect:/get-patient";
	}
	
	@GetMapping("/show-medicine")
	public String showMedicine(Model model,HttpServletRequest req) {
		HttpSession ses=req.getSession(true);
		if(ses.getAttribute("uname")!=null && ses.getAttribute("role").equals("Pharmacist")) {
			model.addAttribute("patient" , new Patient());
		return "issueMedicine";
		}
		else return "ErrorPage";
	}
	
	@GetMapping("/show-diagnostic")
	public String showDiagnostic(Model model,HttpServletRequest req) {
		HttpSession ses=req.getSession(true);
		if(ses.getAttribute("uname")!=null && ses.getAttribute("role").equals("Diagnostic")) {
			model.addAttribute("patient" , new Patient());
		return "diagnostic";
		}
		else return "ErrorPage";
	}
	
	@GetMapping("/show-billing")
	public String showBilling(Model model,HttpServletRequest req) {
		HttpSession ses=req.getSession(true);
		if(ses.getAttribute("uname")!=null && ses.getAttribute("role").equals("registration desk")) {
			model.addAttribute("patient" , new Patient());
		return "patientBilling";
		}
		else return "ErrorPage";
	}
	@GetMapping("/show-patient-byId")
	public String showPatientbyId(Model model,HttpServletRequest req) {
		HttpSession ses=req.getSession(true);
		if(ses.getAttribute("uname")!=null) {
			model.addAttribute("patient" , new Patient());
		return "patientDetail";
		}
		else return "ErrorPage";
	}
	
	@RequestMapping(value="/issue-medicine" ,method=RequestMethod.POST)
	public String issueMedicines(@ModelAttribute("patient") Patient pt) {
		//ps.deletePatient(pt);
		return "redirect:/get-patient";
	}
	
	@GetMapping("/test")
	public String test() {
		return "test";
	}
	
	@RequestMapping(value="/test-medicine" ,method=RequestMethod.GET)
	public ModelAndView testMedicine(HttpServletRequest req) {
		String name=req.getParameter("search");
		String search=req.getParameter("med1");
		System.out.println("med1 called"+search);
		List<MedicineMaster> mmed=ps.getAllMedicine();
		
		String getSear=req.getParameter("med1");
		System.out.println("Value got "+getSear);
		ModelAndView mv=new ModelAndView();
		if(getSear!=null) {
		for(MedicineMaster x:mmed) {
			if(getSear.equalsIgnoreCase(x.getMedicineName())) {
				mv.addObject("val",x.getRateOfMed());
			}
		}}
		Gson gson = new Gson(); 
        JsonObject myObj = new JsonObject();
        JsonElement sno = gson.toJsonTree(mmed);
		mv.addObject("jsonform",sno);
		mv.setViewName("test");
		return mv;
	}
	
	@PostMapping("/serach-data")
	public String searchdata(HttpServletRequest req) {
		/*String getSear=req.getParameter("med1");
		System.out.println("Value got "+getSear);
		double sens=0;
		
		MedicineMaster send=new MedicineMaster();
		List<MedicineMaster> mmed=ps.getAllMedicine();
		for(MedicineMaster x:mmed) {
			if(getSear.equalsIgnoreCase(x.getMedicineName())) {
				sens=x.getRateOfMed();
			}
		}
	*/Gson gson = new Gson();
		double sens=4.5;
		return gson.toJson(sens);
	}
	@GetMapping("/del-patient-diag/{id}")
	public ModelAndView deletePatientdiagnostic(HttpServletRequest req,@PathVariable("id") int id) {
		System.out.println(id);
		PatientDaignostic pt=ps.getPatientDiagnosticBydiagId(id);
		ps.deletePatientDiagnostic(pt);
		ModelAndView mv=new ModelAndView();
		mv.setViewName("redirect:/get-patient-detail/3?searpatientId="+pt.getPatientId());
		return mv;
	}
	@GetMapping("/del-patient-med/{id}")
	public ModelAndView deletePatientMedicine(HttpServletRequest req,@PathVariable("id") int id) {
		System.out.println(id);
		PatientMedicine pt=ps.getpatientMedicineBymedId(id);
		MedicineMaster pt1=new MedicineMaster();
		pt1.setMedicineId(pt.getMedicineId());
		pt1.setQuantity(pt.getQuantity());pt1.setMedicineName(pt.getMedName());
		pt1.setRateOfMed(pt.getRateOfMed());
		ps.deletePatientMedicine(pt);
		ps.updateMastermedicine(pt1);
		ModelAndView mv=new ModelAndView();
		mv.setViewName("redirect:/get-patient-detail/2?searpatientId="+pt.getPatientId());
		return mv;
	}
	
	@GetMapping("/update-status-patient/{id}")
	public String setstatusofPatient(@PathVariable("id") int id) {
		Patient pt=new Patient();
		pt.setPatientId(id);
		pt=ps.getdetailbyPtId(pt);
		System.out.println(pt);
		pt.setStatus("Discharged");
		ps.addPatient(pt);
		return "patientBilling";
	}
}
