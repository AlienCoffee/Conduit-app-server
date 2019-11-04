package ru.shemplo.conduit.appserver.web.controllers;

import ru.shemplo.conduit.appserver.ServerConstants.*
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import ru.shemplo.conduit.appserver.entities.wrappers.WUser
import org.springframework.web.servlet.ModelAndView
import ru.shemplo.conduit.appserver.entities.wrappers.IndentifiedUser
import ru.shemplo.conduit.appserver.*
import ru.shemplo.conduit.appserver.entities.PeriodStatus
import ru.shemplo.conduit.appserver.services.PeriodsService
import lombok.RequiredArgsConstructor
import org.springframework.beans.factory.annotation.Autowired

@Controller
public class OfficeController {
    
	@Autowired
	private var periodsService : PeriodsService? = null;
	
	@GetMapping (PAGE_OFFICE)
	fun handleIndexPage (
        @IndentifiedUser user : WUser
	) : ModelAndView {
		val mav = ModelAndView ("office/index");
		val ent  = user.getEntity ();
		mav.addObject ("user", ent);

		mav.addObject ("periods", periodsService?.getAllPeriods ());
		mav.addObject ("is_service_page", true);
		return mav;
	}
	
	@GetMapping (PAGE_OFFICE_PERIODS)
	fun handlePeriodsPage (
        @IndentifiedUser user : WUser
	) : ModelAndView {
		val mav = ModelAndView ("office/periods");
		val ent  = user.getEntity ();
		mav.addObject ("user", ent);

		mav.addObject ("period_statuses", PeriodStatus.getValues ());
		mav.addObject ("periods", periodsService?.getAllPeriods ());
		mav.addObject ("is_system_period_selected", true);
		mav.addObject ("active_applications", 4);
		mav.addObject ("is_service_page", true);
		mav.addObject ("tab", "management");
		return mav;
	}
	
	@GetMapping (PAGE_OFFICE_PERIODS_APPLICATIONS)
	fun handlePeriodsApplicationsPage (
        @IndentifiedUser user : WUser
	) : ModelAndView {
		val mav = ModelAndView ("office/periods-applications");
		val ent  = user.getEntity ();
		mav.addObject ("user", ent);

		mav.addObject ("periods", periodsService?.getAllPeriods ());
		mav.addObject ("is_system_period_selected", true);
		mav.addObject ("active_applications", 0);
		mav.addObject ("is_service_page", true);
		mav.addObject ("tab", "applications");
		return mav;
	}
	
}
