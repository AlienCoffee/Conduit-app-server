package ru.shemplo.conduit.appserver.web.controllers;

import ru.shemplo.conduit.appserver.ServerConstants.*
import org.springframework.web.bind.annotation.RestController
import ru.shemplo.conduit.appserver.entities.wrappers.IndentifiedUser
import ru.shemplo.conduit.appserver.entities.wrappers.WUser
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import ru.shemplo.conduit.appserver.entities.PeriodEntity
import org.springframework.web.bind.annotation.PostMapping
import ru.shemplo.conduit.appserver.web.ResponseBox

@RestController
public class OfficeRestController {
    
	@PostMapping (API_OFFICE_UPDATE_PERIOD)
	fun handlePeriodsApplicationsPage (
        @IndentifiedUser user : WUser,
		@RequestBody period : PeriodEntity
	) : ResponseBox <Void> {
		println (period);
		return ResponseBox.ok ();
	}
	
}
