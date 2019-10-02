package ru.shemplo.conduit.appserver.web.controllers;

import ru.shemplo.conduit.appserver.ServerConstants.*
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import ru.shemplo.conduit.appserver.entities.wrappers.WUser
import org.springframework.web.servlet.ModelAndView
import ru.shemplo.conduit.appserver.entities.wrappers.IndentifiedUser
import ru.shemplo.conduit.appserver.*

@Controller
public class CourtController {
    
	@GetMapping (PAGE_OFFICE)
	fun handleIndexPage (
        @IndentifiedUser user : WUser
	) : ModelAndView {
		val mav = ModelAndView ("office/index");
		val ent  = user.getEntity ();
		mav.addObject ("user", ent);

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

		mav.addObject ("is_service_page", true);
		return mav;
	}
	
}
