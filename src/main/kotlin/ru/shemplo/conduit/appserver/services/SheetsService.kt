package ru.shemplo.conduit.appserver.services;

import org.springframework.stereotype.Service
import ru.shemplo.conduit.appserver.entities.groups.events.SheetEntity

@Service
class SheetsService : AbsCachedService <SheetEntity> () {
	
	public override fun loadEntity (id : Long) : SheetEntity {
		null!!;
	}
	
	protected override fun getCacheSize () : Int = 32;
	
}