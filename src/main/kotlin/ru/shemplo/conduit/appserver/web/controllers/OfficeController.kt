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
import ru.shemplo.conduit.appserver.entities.PeriodEntity
import ru.shemplo.conduit.appserver.entities.PeriodEntity.*

@Controller
public class OfficeController {
    
	@Autowired
	private var periodsService : PeriodsService? = null;
	
	@GetMapping (PAGE_OFFICE)
	fun handleIndexPage (
        @IndentifiedUser user : WUser
	) : ModelAndView {
		val mav = ModelAndView ("office/index");
		return setParameters (mav, user, null).first;
	}
	
	@GetMapping (PAGE_OFFICE_PERIODS)
	fun handlePeriodsPage (
        @IndentifiedUser user : WUser
	) : ModelAndView {
		val mav = ModelAndView ("office/periods");
		mav.addObject ("period_statuses", PeriodStatus.getValues ());
		val (_mav_, isSystemPeriod) = setParameters (mav, user, "management");
		
		if (!isSystemPeriod) { return handlePeriodsApplicationsPage (user); }
		return _mav_;
	}
	
	@GetMapping (PAGE_OFFICE_PERIODS_APPLICATIONS)
	fun handlePeriodsApplicationsPage (
        @IndentifiedUser user : WUser
	) : ModelAndView {
		val mav = ModelAndView ("office/periods-applications");
		val (_mav_, isSystemPeriod) = setParameters (mav, user, "applications");
		
		if (isSystemPeriod) { return handlePeriodsPage (user); }
		return _mav_;
	}
	
	private fun setParameters (mav : ModelAndView, user : WUser, tab : String?) : Pair <ModelAndView, Boolean> {
		mav.addObject ("user", user.getEntity ());
		
		val currentPeriod = periodsService?.getCurrentOfficePeriod (user);
		val isSystem = currentPeriod?.getId () == getSystemForKT ().getId ();
		mav.addObject ("is_system_period_selected", isSystem);
		mav.addObject ("current_period", currentPeriod);
		
		mav.addObject ("periods", periodsService?.getAllPeriods ());
		mav.addObject ("active_applications", 0);
		mav.addObject ("is_service_page", true);
		
		if (tab != null) { mav.addObject ("tab", tab); }
		
		return Pair (mav, isSystem);
	}
	
}
