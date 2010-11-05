package com.fray.evo.action.build;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fray.evo.EcBuildOrder;
import com.fray.evo.EcEvolver;
import com.fray.evo.EcSettings;
import com.fray.evo.EcState;
import com.fray.evo.action.EcAction;

public class EcActionBuildSpawningPool extends EcAction implements Serializable
{

	@Override
	public void execute(final EcBuildOrder s, final EcEvolver e)
	{
		s.minerals -= 200;
		s.drones -= 1;
		s.dronesOnMinerals -= 1;
		s.supplyUsed -= 1;
		s.addFutureAction(65, new Runnable()
		{
			@Override
			public void run()
			{
				if (e.debug)
					e.obtained(s," Spawning Pool+1");
				s.spawningPools += 1;
			}
		});
	}

	@Override
	public boolean isInvalid(EcBuildOrder s)
	{
		if (s.spawningPools >= 1)
			return true;
		if(s.supplyUsed < s.settings.minimumPoolSupply)
			return true;
		return super.isInvalid(s);
	}

	@Override
	public boolean isPossible(EcBuildOrder s)
	{
		if (s.minerals < 200)
			return false;
		if (s.drones < 1)
			return false;
		return true;
	}

	@Override
	public List<EcAction> requirements(EcState destination)
	{
		ArrayList<EcAction> l = new ArrayList<EcAction>();
		return l;
	}
}
