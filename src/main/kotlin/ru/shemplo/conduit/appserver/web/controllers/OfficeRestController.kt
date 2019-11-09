package ru.shemplo.conduit.appserver.web.controllers;

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.shemplo.conduit.appserver.ServerConstants.*
import ru.shemplo.conduit.appserver.entities.PeriodEntity
import ru.shemplo.conduit.appserver.entities.wrappers.IndentifiedUser
import ru.shemplo.conduit.appserver.entities.wrappers.WUser
import ru.shemplo.conduit.appserver.services.UserParametersService
import ru.shemplo.conduit.appserver.web.ResponseBox
import ru.shemplo.conduit.appserver.entities.UserParameterName

@RestController
public class OfficeRestController {
    
	@Autowired
	private var userParamtersService : UserParametersService? = null;
	
	@PostMapping (API_OFFICE_UPDATE_PERIOD)
	fun handleUpdatePeriod (
        @IndentifiedUser user : WUser,
		@RequestBody period : PeriodEntity
	) : ResponseBox <Void> {
		println (period);
		return ResponseBox.ok ();
	}
	
	@PostMapping (API_OFFICE_UPDATE_PERIOD_SELECTION)
	fun handlePeriodSelection (
        @IndentifiedUser user : WUser,
		@RequestParam ("periodId") periodId : Long
	) : ResponseBox <Void> {
		userParamtersService?.setParameterValue (user,
			UserParameterName.OFFICE_PERIOD,
			"" + periodId
        );
		return ResponseBox.ok ();
	}
	
}
