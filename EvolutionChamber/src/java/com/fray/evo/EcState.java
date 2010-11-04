package com.fray.evo;

import java.util.ArrayList;
import java.util.List;

import com.fray.evo.fitness.EcFitness;
import static java.lang.Math.*;

public class EcState
{
	
	public EcState() {
		fitness = EcSettings.getFitnessFunction();
		hatcheryTimes.add(new Integer(0));
	}
	
	private EcFitness				fitness = null;
	
	public double preTimeScore = 0.0;
	public double timeBonus = 0.0;
	
	public double					minerals			= 50;
	public double					gas					= 0;
	public double					supplyUsed			= 6;
	public int						evolvingHatcheries  = 0;
	public int						evolvingLairs		= 0;
	public int						evolvingHives		= 0;
	public int 						requiredBases	    = 1;
	public int						hatcheries			= 1;
	public int						lairs				= 0;
	public int						hives				= 0;
	public int						spawningPools		= 0;
	public int						evolutionChambers	= 0;
	public int						roachWarrens		= 0;
	public int						hydraliskDen		= 0;
	public int						banelingNest		= 0;
	public int						infestationPit		= 0;
	public int						greaterSpire		= 0;
	public int						ultraliskCavern		= 0;
	public int						gasExtractors		= 0;
	public int						spire				= 0;
	public int						spineCrawlers		= 0;
	public int						sporeCrawlers		= 0;
	public int						nydusNetwork		= 0;
	public int						nydusWorm			= 0;

	public int						drones				= 6;
	public int						overlords			= 1;
	public int						overseers			= 0;
	public int						zerglings			= 0;
	public int						banelings			= 0;
	public int						roaches				= 0;
	public int						mutalisks			= 0;
	public int						infestors			= 0;
	public int						queens				= 0;
	public int						hydralisks			= 0;
	public int						corruptors			= 0;
	public int						ultralisks			= 0;
	public int						broodlords			= 0;
	public int						scoutDrone			= 0;

	public boolean					metabolicBoost		= false;
	public boolean					adrenalGlands		= false;
	public boolean					glialReconstitution	= false;
	public boolean					tunnelingClaws		= false;
	public boolean					burrow				= false;
	public boolean					pneumatizedCarapace	= false;
	public boolean					ventralSacs			= false;
	public boolean					centrifugalHooks	= false;
	public boolean					melee1				= false;
	public boolean					melee2				= false;
	public boolean					melee3				= false;
	public boolean					missile1			= false;
	public boolean					missile2			= false;
	public boolean					missile3			= false;
	public boolean					armor1				= false;
	public boolean					armor2				= false;
	public boolean					armor3				= false;
	public boolean					groovedSpines		= false;
	public boolean					neuralParasite		= false;
	public boolean					pathogenGlands		= false;
	public boolean					flyerAttack1		= false;
	public boolean					flyerAttack2		= false;
	public boolean					flyerAttack3		= false;
	public boolean					flyerArmor1			= false;
	public boolean					flyerArmor2			= false;
	public boolean					flyerArmor3			= false;
	public boolean					chitinousPlating	= false;

	public int						seconds				= 0;
	public int						targetSeconds		= 0;
	public int						invalidActions		= 0;
	public double					actionLength		= 0;
	public int						waits;
	
	public int 						maxOverDrones 		= 50;
	public int						overDroneEfficiency = 80;
	
	public List<Integer>			hatcheryTimes		= new ArrayList<Integer>();

	public transient List<EcState>	waypoints			= new ArrayList<EcState>();

	@Override
	public Object clone() throws CloneNotSupportedException
	{
		EcState s = new EcState();
		assign(s);
		return s;
	}

	protected void assign(EcState s)
	{
		for (EcState st : waypoints)
			try
			{
				s.waypoints.add((EcState) st.clone());
			}
			catch (CloneNotSupportedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		s.minerals = minerals;
		s.gas = gas;
		s.supplyUsed = supplyUsed;

		s.requiredBases = requiredBases;
		s.hatcheries = hatcheries;
		s.lairs = lairs;
		s.hives = hives;
		s.spawningPools = spawningPools;
		s.banelingNest = banelingNest;
		s.evolutionChambers = evolutionChambers;
		s.roachWarrens = roachWarrens;
		s.hydraliskDen = hydraliskDen;
		s.infestationPit = infestationPit;
		s.greaterSpire = greaterSpire;
		s.ultraliskCavern = ultraliskCavern;
		s.gasExtractors = gasExtractors;
		s.spire = spire;
		s.greaterSpire = greaterSpire;
		s.spineCrawlers = spineCrawlers;
		s.sporeCrawlers = sporeCrawlers;
		s.nydusNetwork = nydusNetwork;
		s.nydusWorm = nydusWorm;
		s.scoutDrone = scoutDrone;

		s.zerglings = zerglings;
		s.banelings = banelings;
		s.roaches = roaches;
		s.mutalisks = mutalisks;
		s.drones = drones;
		s.queens = queens;
		s.hydralisks = hydralisks;
		s.infestors = infestors;
		s.corruptors = corruptors;
		s.ultralisks = ultralisks;
		s.broodlords = broodlords;
		s.overlords = overlords;
		s.overseers = overseers;

		s.metabolicBoost = metabolicBoost;
		s.adrenalGlands = adrenalGlands;
		s.glialReconstitution = glialReconstitution;
		s.tunnelingClaws = tunnelingClaws;
		s.burrow = burrow;
		s.pneumatizedCarapace = pneumatizedCarapace;
		s.ventralSacs = ventralSacs;
		s.centrifugalHooks = centrifugalHooks;
		s.melee1 = melee1;
		s.melee2 = melee2;
		s.melee3 = melee3;
		s.missile1 = missile1;
		s.missile2 = missile2;
		s.missile3 = missile3;
		s.armor1 = armor1;
		s.armor2 = armor2;
		s.armor3 = armor3;
		s.groovedSpines = groovedSpines;
		s.neuralParasite = neuralParasite;
		s.pathogenGlands = pathogenGlands;
		s.flyerAttack1 = flyerAttack1;
		s.flyerAttack2 = flyerAttack2;
		s.flyerAttack3 = flyerAttack3;
		s.flyerArmor1 = flyerArmor1;
		s.flyerArmor2 = flyerArmor2;
		s.flyerArmor3 = flyerArmor3;
		s.chitinousPlating = chitinousPlating;

		s.seconds = seconds;
		s.targetSeconds = targetSeconds;
		s.invalidActions = invalidActions;
		s.actionLength = actionLength;
	}

	public int supply()
	{
		return Math.min((overlords + overseers) * 8 + 2 * bases(),200);
	}

	public static EcState defaultDestination()
	{
		EcState d = new EcState();

		d.drones = 0;
		d.overlords = 0;
		d.hatcheries = 0;
		d.targetSeconds = 60 * 120;

		return d;
	}

	public double score(EcState candidate)
	{
		return fitness.score(candidate, this);
	}
	
	public void union(EcState s)
	{
		if(s.requiredBases > requiredBases)
			requiredBases = s.requiredBases;
		
		if(s.lairs > lairs)
			lairs = s.lairs;
		
		if(s.hives > hives)
			hives = s.hives;
		
		if(s.spawningPools > spawningPools)
			spawningPools = s.spawningPools;
		
		if(s.banelingNest > banelingNest)
			banelingNest = s.banelingNest;
		
		if(s.evolutionChambers > evolutionChambers)
			evolutionChambers = s.evolutionChambers;
		
		if(s.roachWarrens > roachWarrens)
			roachWarrens = s.roachWarrens;
		
		if(s.hydraliskDen > hydraliskDen)
			hydraliskDen = s.hydraliskDen;
		
		if(s.infestationPit > infestationPit)
			infestationPit = s.infestationPit;
		
		if(s.greaterSpire > greaterSpire)
			greaterSpire = s.greaterSpire;
		
		if(s.ultraliskCavern > ultraliskCavern)
			ultraliskCavern = s.ultraliskCavern;
		
		if(s.gasExtractors > gasExtractors)
			gasExtractors = s.gasExtractors;
		
		if(s.spire > spire)
			spire = s.spire;
		
		if(s.spineCrawlers > spineCrawlers)
			spineCrawlers = s.spineCrawlers;
		
		if(s.sporeCrawlers > sporeCrawlers)
			sporeCrawlers = s.sporeCrawlers;
		
		if(s.nydusNetwork > nydusNetwork)
			nydusNetwork = s.nydusNetwork;
		
		if(s.nydusWorm > nydusWorm)
			nydusWorm = s.nydusWorm;
		
		if(s.zerglings > zerglings)
			zerglings = s.zerglings;
		
		if(s.banelings > banelings)
			banelings = s.banelings;
		
		if(s.roaches > roaches)
			roaches = s.roaches;
		
		if(s.mutalisks > mutalisks)
			mutalisks = s.mutalisks;
		
		if(s.drones > drones)
			drones = s.drones;
		
		if(s.nydusNetwork > nydusNetwork)
			nydusNetwork = s.nydusNetwork;
		
		if(s.queens > queens)
			queens = s.queens;
		
		if(s.hydralisks > hydralisks)
			hydralisks = s.hydralisks;
		
		if(s.infestors > infestors)
			infestors = s.infestors;
		
		if(s.corruptors > corruptors)
			corruptors = s.corruptors;
		
		if(s.ultralisks > ultralisks)
			ultralisks = s.ultralisks;
		
		if(s.broodlords > broodlords)
			broodlords = s.broodlords;
		
		if(s.overlords > overlords)
			overlords = s.overlords;
		
		if(s.overseers > overseers)
			overseers = s.overseers;

		metabolicBoost = s.metabolicBoost | metabolicBoost;
		adrenalGlands = s.adrenalGlands | adrenalGlands;
		glialReconstitution = s.glialReconstitution | glialReconstitution;
		tunnelingClaws = s.tunnelingClaws | tunnelingClaws;
		burrow = s.burrow | burrow;
		pneumatizedCarapace = s.pneumatizedCarapace | pneumatizedCarapace;
		ventralSacs = s.ventralSacs | ventralSacs;
		centrifugalHooks = s.centrifugalHooks | centrifugalHooks;
		melee1 = s.melee1 | melee1;
		melee2 = s.melee2 | melee2;
		melee3 = s.melee3 | melee3;
		missile1 = s.missile1 | missile1;
		missile2 = s.missile2 | missile2;
		missile3 = s.missile3 | missile3;
		armor1 = s.armor1 | armor1;
		armor2 = s.armor2 | armor2;
		armor3 = s.armor3 | armor3;
		groovedSpines = s.groovedSpines | groovedSpines;
		neuralParasite = s.neuralParasite | neuralParasite;
		pathogenGlands = s.pathogenGlands | pathogenGlands;
		flyerAttack1 = s.flyerAttack1 | flyerAttack1;
		flyerAttack2 = s.flyerAttack2 | flyerAttack2;
		flyerAttack3 = s.flyerAttack3 | flyerAttack3;
		flyerArmor1 = s.flyerArmor1 | flyerArmor1;
		flyerArmor2 = s.flyerArmor2 | flyerArmor2;
		flyerArmor3 = s.flyerArmor3 | flyerArmor3;
		chitinousPlating = s.chitinousPlating | chitinousPlating;

	}

	public boolean isSatisfied(EcState candidate)
	{		

		if (waypoints.size() > 0)
		{
			EcState state = defaultDestination();
			for (EcState s : waypoints)
			{
				state.union(s);
			}
			state.union(this);
			return state.isSatisfied(candidate);
		}
	
		
		if (candidate.drones < drones)
			return false;
		if (candidate.zerglings < zerglings)
			return false;
		if (candidate.banelings < banelings)
			return false;
		if (candidate.roaches < roaches)
			return false;
		if (candidate.mutalisks < mutalisks)
			return false;
		if (candidate.queens < queens)
			return false;
		if (candidate.hydralisks < hydralisks)
			return false;
		if (candidate.infestors < infestors)
			return false;
		if (candidate.corruptors < corruptors)
			return false;
		if (candidate.ultralisks < ultralisks)
			return false;
		if (candidate.broodlords < broodlords)
			return false;
		if (candidate.overlords < overlords)
			return false;
		if (candidate.overseers < overseers)
			return false;

		if (candidate.bases() < requiredBases)
			return false;
		if (candidate.lairs < lairs)
			return false;
		if (candidate.hives < hives)
			return false;
		if (candidate.gasExtractors < gasExtractors)
			return false;
		if (candidate.spawningPools < spawningPools)
			return false;
		if (candidate.banelingNest < banelingNest)
			return false;
		if (candidate.roachWarrens < roachWarrens)
			return false;
		if (candidate.hydraliskDen < hydraliskDen)
			return false;
		if (candidate.infestationPit < infestationPit)
			return false;
		if (candidate.spire < spire)
			return false;
		if (candidate.greaterSpire < greaterSpire)
			return false;
		if (candidate.ultraliskCavern < ultraliskCavern)
			return false;
		if (candidate.evolutionChambers < evolutionChambers)
			return false;
		if (candidate.spineCrawlers < spineCrawlers)
			return false;
		if (candidate.sporeCrawlers < sporeCrawlers)
			return false;
		if (candidate.nydusNetwork < nydusNetwork)
			return false;
		if (candidate.nydusWorm < nydusWorm)
			return false;

		if ((!candidate.metabolicBoost) & metabolicBoost)
			return false;
		if ((!candidate.adrenalGlands) & adrenalGlands)
			return false;
		if ((!candidate.glialReconstitution) & glialReconstitution)
			return false;
		if ((!candidate.tunnelingClaws) & tunnelingClaws)
			return false;
		if ((!candidate.burrow) & burrow)
			return false;
		if ((!candidate.pneumatizedCarapace) & pneumatizedCarapace)
			return false;
		if ((!candidate.ventralSacs) & ventralSacs)
			return false;
		if ((!candidate.centrifugalHooks) & centrifugalHooks)
			return false;
		if ((!candidate.melee1) & melee1)
			return false;
		if ((!candidate.melee2) & melee2)
			return false;
		if ((!candidate.melee3) & melee3)
			return false;
		if ((!candidate.missile1) & missile1)
			return false;
		if ((!candidate.missile2) & missile2)
			return false;
		if ((!candidate.missile3) & missile3)
			return false;
		if ((!candidate.armor1) & armor1)
			return false;
		if ((!candidate.armor2) & armor2)
			return false;
		if ((!candidate.armor3) & armor3)
			return false;
		if ((!candidate.groovedSpines) & groovedSpines)
			return false;
		if ((!candidate.neuralParasite) & neuralParasite)
			return false;
		if ((!candidate.pathogenGlands) & pathogenGlands)
			return false;
		if ((!candidate.flyerAttack1) & flyerAttack1)
			return false;
		if ((!candidate.flyerAttack2) & flyerAttack2)
			return false;
		if ((!candidate.flyerAttack3) & flyerAttack3)
			return false;
		if ((!candidate.flyerArmor1) & flyerArmor1)
			return false;
		if ((!candidate.flyerArmor2) & flyerArmor2)
			return false;
		if ((!candidate.flyerArmor3) & flyerArmor3)
			return false;
		if ((!candidate.chitinousPlating) & chitinousPlating)
			return false;
		
		
		if (EcSettings.overDrone || EcSettings.workerParity)
		{
			int overDrones = getOverDrones(candidate);
			
			if (EcSettings.overDrone && candidate.drones < overDrones) 
			{
				return false;
			}
			if (EcSettings.workerParity) 
			{
				int parityDrones  = getParityDrones(candidate);
				
				if (candidate.drones < parityDrones) 
				{
					return false;
				}
			}
		}
		
		return true;
	}

	public int getOverDrones(EcState s)
	{
		int overDrones = ((s.productionTime() / 17) + s.usedDrones()) * overDroneEfficiency / 100;
		
		overDrones = Math.min(overDrones, maxOverDrones);
		
		return overDrones;
	}
	
	public int getParityDrones(EcState s)
	{
		int optimalDrones = Math.min((s.bases() * 16) + (s.gasExtractors * 3), maxOverDrones);
		int parityDrones  = Math.min(s.getOverDrones(s), optimalDrones);
		
		return parityDrones;
	}
	
	public int bases()
	{
		return hatcheries + lairs + evolvingHatcheries + evolvingLairs + hives + evolvingHives;
	}
	
	public int productionTime()
	{
		int productionTime = 0;
		
		// Calculate raw hatchery production time
		for (int i = 0; i < hatcheryTimes.size(); i++) 
		{
		    productionTime += seconds - hatcheryTimes.get(i); // TODO: Change to constant
		}
		
		return productionTime;
	}
	
	public int usedDrones()
	{
		return (evolvingHatcheries + evolvingLairs + evolvingHives + (hatcheries - 1)
				+ lairs + hives + spawningPools + evolutionChambers + roachWarrens
				+ hydraliskDen + banelingNest + infestationPit 
				+ ultraliskCavern + gasExtractors + spire + spineCrawlers
				+ sporeCrawlers + nydusWorm);
	}

	public int getSumStuff()
	{
		if (waypoints.size() > 0)
		{
			EcState state = defaultDestination();
			for (EcState s : waypoints)
			{
				state.union(s);
			}
			state.union(this);
			return state.getSumStuff();
		}
		
		int i = hatcheries + lairs + hives + spawningPools + evolutionChambers + roachWarrens + hydraliskDen
				+ banelingNest + infestationPit + greaterSpire + ultraliskCavern + gasExtractors + spire
				+ spineCrawlers + sporeCrawlers + nydusNetwork + nydusWorm

				+ drones + overlords + overseers + zerglings + banelings * 2 + roaches + mutalisks + infestors + queens
				+ hydralisks + corruptors + ultralisks + broodlords * 2;

		if (metabolicBoost)
			i++;
		if (adrenalGlands)
			i++;
		if (glialReconstitution)
			i++;
		if (tunnelingClaws)
			i++;
		if (burrow)
			i++;
		if (pneumatizedCarapace)
			i++;
		if (ventralSacs)
			i++;
		if (centrifugalHooks)
			i++;
		if (melee1)
			i++;
		if (melee2)
			i++;
		if (melee3)
			i++;
		if (missile1)
			i++;
		if (missile2)
			i++;
		if (missile3)
			i++;
		if (armor1)
			i++;
		if (armor2)
			i++;
		if (armor3)
			i++;
		if (groovedSpines)
			i++;
		if (neuralParasite)
			i++;
		if (pathogenGlands)
			i++;
		if (flyerAttack1)
			i++;
		if (flyerAttack2)
			i++;
		if (flyerAttack3)
			i++;
		if (flyerArmor1)
			i++;
		if (flyerArmor2)
			i++;
		if (flyerArmor3)
			i++;
		if (chitinousPlating)
			i++;
		for (EcState s : waypoints)
			i += s.getSumStuff();
		return i;
	}

	public boolean waypointMissed(EcBuildOrder candidate)
	{
		for (EcState s : waypoints)
		{
			if (candidate.seconds < s.targetSeconds)
				continue;
			if (s.isSatisfied(candidate))
				continue;
			return true;
		}
		return false;
	}
}