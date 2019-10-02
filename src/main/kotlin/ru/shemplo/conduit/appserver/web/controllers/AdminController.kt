package ru.shemplo.conduit.appserver.web.controllers

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.servlet.ModelAndView
import ru.shemplo.conduit.appserver.ServerConstants.PAGE_ADMIN
import ru.shemplo.conduit.appserver.entities.wrappers.IndentifiedUser
import ru.shemplo.conduit.appserver.entities.wrappers.WUser
import ru.shemplo.conduit.appserver.entities.UserEntity
import ru.shemplo.conduit.appserver.*
import java.lang.SuppressWarnings

@Controller
public class AdminController {
	
	@GetMapping (PAGE_ADMIN)
	fun handleIndexPage (
        @IndentifiedUser user : WUser
	) : ModelAndView {
		val mav = ModelAndView ("admin/index");
		val ent  = user.getEntity ();
		mav.addObject ("user", ent);

		mav.addObject ("is_service_page", true);
		return mav;
	}
	
}