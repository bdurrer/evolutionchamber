package com.fray.evo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.jgap.Chromosome;
import org.jgap.Configuration;
import org.jgap.Gene;
import org.jgap.Genotype;
import org.jgap.IChromosome;
import org.jgap.InvalidConfigurationException;
import org.jgap.event.GeneticEvent;
import org.jgap.event.GeneticEventListener;
import org.jgap.impl.DefaultConfiguration;
import org.jgap.impl.IntegerGene;

import com.fray.evo.action.EcAction;
import com.fray.evo.util.EcFileSystem;

public class EvolutionChamber
{
	// The seeds files. (one and a backup, in case execution stops while the
	// file is half written)
	private static File			SEEDS_EVO			= null;
	private static File			SEEDS_EVO_2			= null;
	
	public int					CHROMOSOME_LENGTH		= 120;
	int							NUM_THREADS				= Runtime.getRuntime().availableProcessors();
	int							MAX_NUM_THREADS			= Runtime.getRuntime().availableProcessors()*4;
	public int					POPULATION_SIZE	 		= 200;
	public double				BASE_MUTATION_RATE 		= 5;
	int							STAGNATION_LIMIT_MIN	= 50;

	public Double				bestScore				= new Double(0);
	public Integer				stagnationLimit 		= new Integer(0);
	public Double				waterMark				= new Double(0);

	static List<EcBuildOrder>	seeds					= new ArrayList<EcBuildOrder>();
	private EcState			destination				= EcState.defaultDestination();
	public EcReportable			reportInterface;
	public List<Thread>			threads					= new ArrayList<Thread>();
	private boolean			killThreads				= false;
	public static Double[]		bestScores;
	public static Integer[]	evolutionsSinceDiscovery;
	private boolean 			firstrun 				= true;
	private boolean 			newbestscore 			= false;

	static 
	{
		try
		{
			SEEDS_EVO = new File(EcFileSystem.getTempPath(), "seeds.evo");
			SEEDS_EVO.getParentFile().mkdirs();
			SEEDS_EVO_2 = new File(EcFileSystem.getTempPath(), "seeds2.evo");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws InvalidConfigurationException
	{
		new EvolutionChamber().go();
	}

	public void go() throws InvalidConfigurationException
	{
		killThreads = false;
		firstrun = true;
		EcState s = importSource();
		EcState d = getInternalDestination();
		EcAction.setup(d);
		CHROMOSOME_LENGTH = d.getSumStuff() + 70;
		bestScore = new Double(0);
		bestScores = new Double[NUM_THREADS];
		evolutionsSinceDiscovery = new Integer[NUM_THREADS];

		// We are using the 'many small villages' vs 'one large city' method of
		// evolution.
		for (int threadIndex = 0; threadIndex < NUM_THREADS; threadIndex++)
		{
			spawnEvolutionaryChamber(s, d, threadIndex);
		}

		if (reportInterface == null)
			while (true)
				try
				{
					Thread.sleep(10000);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
	}

	public void stop()
	{
		killThreads = true;
		for (Thread t : threads)
			try
			{
				t.join();
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		threads.clear();

	}

	private void spawnEvolutionaryChamber(final EcState source, final EcState destination, final int threadIndex)
			throws InvalidConfigurationException
	{
		bestScores[threadIndex] = new Double(0);
		evolutionsSinceDiscovery[threadIndex] = new Integer(0);
		DefaultConfiguration.reset(threadIndex + " thread.");
		
		final EcEvolver myFunc = new EcEvolver(source, destination);
		
		final Configuration conf = constructConfiguration(threadIndex, myFunc);

		final Genotype population = Genotype.randomInitialGenotype(conf);

		if(!firstrun)
		{
			int totalevoSinceDiscoveryOnBest = 0;
			int numBestThreads = 0;
			synchronized(bestScores)	{
				for(int i = 0; i < bestScores.length; i++ ) {
					if(bestScores[i] >= bestScore) {
						numBestThreads++;
						totalevoSinceDiscoveryOnBest += evolutionsSinceDiscovery[i];
					}
				}
			}
			
			if(!(totalevoSinceDiscoveryOnBest > Math.max(stagnationLimit, STAGNATION_LIMIT_MIN) * numBestThreads) && numBestThreads < Math.max(Math.ceil(NUM_THREADS / 3), 1)) {
				loadOldBuildOrders(population, conf,myFunc);
			}
		}
		else if (firstrun && threadIndex == 0) {
			loadOldBuildOrders(population, conf,myFunc);
		}
		else if (firstrun && threadIndex == NUM_THREADS - 1) {
			firstrun = false;
		}
		
		final Thread thread = new Thread(population);
		conf.getEventManager().addEventListener(GeneticEvent.GENOTYPE_EVOLVED_EVENT, new GeneticEventListener()
		{
			@Override
			public void geneticEventFired(GeneticEvent a_firedEvent)
			{
				Collections.shuffle(conf.getGeneticOperators());
				BASE_MUTATION_RATE += .001;
				if (BASE_MUTATION_RATE >= CHROMOSOME_LENGTH / 2)
					BASE_MUTATION_RATE = 1;
				IChromosome fittestChromosome = population.getFittestChromosome();
				if (killThreads)
					thread.interrupt();
				double fitnessValue = fittestChromosome.getFitnessValue();
				if (fitnessValue > bestScores[threadIndex])
				{
					bestScores[threadIndex] = fitnessValue;
					evolutionsSinceDiscovery[threadIndex] = 0;
					BASE_MUTATION_RATE = 1;
					if (reportInterface != null)
						reportInterface.threadScore(threadIndex,getOutput(myFunc,fittestChromosome,fitnessValue));
				}
				else
					evolutionsSinceDiscovery[threadIndex]++;

				int highestevosSinceDiscovery = 0;
				for(int i = 0; i < bestScores.length; i++ )
				{
					if(bestScores[i] >= bestScore)
					{
						if(evolutionsSinceDiscovery[i] > highestevosSinceDiscovery)
							highestevosSinceDiscovery = evolutionsSinceDiscovery[i];
					}
				}
				
				stagnationLimit = (int)Math.ceil(highestevosSinceDiscovery * (.5));
				
				if(fitnessValue < bestScore)
				{
					if (evolutionsSinceDiscovery[threadIndex] > Math.max(stagnationLimit, STAGNATION_LIMIT_MIN) && fitnessValue < waterMark)
					{
						//Stagnation. Suicide village and try again.
						System.out.println("Restarting thread " + threadIndex);
						try
						{
							spawnEvolutionaryChamber(source, destination, threadIndex);
						}
						catch (InvalidConfigurationException e)
						{
							e.printStackTrace();
						}
						thread.interrupt();
					}
					else if(evolutionsSinceDiscovery[threadIndex] > Math.max(stagnationLimit, STAGNATION_LIMIT_MIN) * 3)
					{
						//Stagnation. Suicide village and try again.
						System.out.println("Restarting thread " + threadIndex);
						try
						{
							spawnEvolutionaryChamber(source, destination, threadIndex);
						}
						catch (InvalidConfigurationException e)
						{
							e.printStackTrace();
						}
						thread.interrupt();
					}
				}
				else if (evolutionsSinceDiscovery[threadIndex] > Math.max(stagnationLimit, STAGNATION_LIMIT_MIN))
				{
					if(newbestscore)
					{
						waterMark = fitnessValue;
					}
					
					int totalevoSinceDiscoveryOnBest = 0;
					int numBestThreads = 0;
					
					for(int i = 0; i < bestScores.length; i++ )
					{
						if(bestScores[i] >= bestScore)
						{
							numBestThreads++;
							totalevoSinceDiscoveryOnBest += evolutionsSinceDiscovery[i];
						}
					}
					
					if(totalevoSinceDiscoveryOnBest > Math.max(stagnationLimit, STAGNATION_LIMIT_MIN) * 3 * numBestThreads)
					{
						// Deadlock, open this thread.
						System.out.println("Restarting thread " + threadIndex);
						try
						{
							spawnEvolutionaryChamber(source, destination, threadIndex);
						}
						catch (InvalidConfigurationException e)
						{
							e.printStackTrace();
						}
						thread.interrupt();
					}
				}

				synchronized (bestScore)
				{
					if (fitnessValue > bestScore)
					{
						BASE_MUTATION_RATE = 1;
						bestScore = fitnessValue;
						newbestscore = true;

						String exactBuildOrder = getOutput(myFunc, fittestChromosome, fitnessValue);
						String buildOrder = getBuildOrder(myFunc, fittestChromosome);
						String yabotBuildOrder = getYabotBuildOrder(myFunc, fittestChromosome);
						
						if (reportInterface != null)
							reportInterface.bestScore(myFunc.evaluateGetBuildOrder(fittestChromosome),
									bestScore.intValue(), exactBuildOrder, buildOrder, yabotBuildOrder);

						displayChromosome(fittestChromosome);
						saveSeeds(fittestChromosome);
					}
				}
			}

		});
		thread.setPriority(Thread.MIN_PRIORITY);
		thread.start();
		threads.add(thread);
	}

	private String getOutput(final EcEvolver myFunc, IChromosome fittestChromosome, double fitnessValue)
	{
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(byteArrayOutputStream);
		if (reportInterface != null)
			myFunc.log = ps;

		displayBuildOrder(myFunc, fittestChromosome);
		myFunc.log.println(new Date() + ": " + fitnessValue);
		String results = new String(byteArrayOutputStream.toByteArray());
		return results;
	}
	
	public String getBuildOrder(final EcEvolver myFunc, IChromosome fittestChromosome)
	{
		String results = myFunc.getBuildOrder(fittestChromosome);
		return results;
	}
	
	public String getYabotBuildOrder(final EcEvolver myFunc, IChromosome fittestChromosome)
	{
		String results = myFunc.getYabotBuildOrder(fittestChromosome);
		return results;
	}
	
	private void restart(final EcState source, final EcState destination, final int threadIndex, final Thread t1)
	{
		// Stagnation. Suicide village and try again.
		System.out.println("Restarting thread " + threadIndex);
		try
		{
			spawnEvolutionaryChamber(source, destination, threadIndex);
		}
		catch (InvalidConfigurationException e)
		{
			e.printStackTrace();
		}
		t1.interrupt();
	}
	
	private Configuration constructConfiguration(final int threadIndex, final EcEvolver myFunc)
			throws InvalidConfigurationException
	{
		final Configuration conf = new DefaultConfiguration(threadIndex + " thread.", threadIndex + " thread.");
		conf.setFitnessFunction(myFunc);
		conf.addGeneticOperator(EcGeneticUtil.getCleansingOperator(this));
		conf.addGeneticOperator(EcGeneticUtil.getOverlordingOperator(this));
		conf.addGeneticOperator(EcGeneticUtil.getInsertionOperator(this));
		conf.addGeneticOperator(EcGeneticUtil.getDeletionOperator(this));
		conf.addGeneticOperator(EcGeneticUtil.getTwiddleOperator(this));
		conf.addGeneticOperator(EcGeneticUtil.getSwapOperator(this));
		conf.setPopulationSize(POPULATION_SIZE);
		conf.setSelectFromPrevGen(1);
		conf.setPreservFittestIndividual(false);
		conf.setAlwaysCaculateFitness(false);
		conf.setKeepPopulationSizeConstant(false);
		Gene[] initialGenes = importInitialGenes(conf);
		Chromosome c = new Chromosome(conf, initialGenes);
		conf.setSampleChromosome(c);
		return conf;
	}

	public static void displayChromosome(IChromosome fittestChromosome)
	{
		int i = 0;
		for (Gene g : fittestChromosome.getGenes())
		{
			if (i++ == 100)
				break;
			if (((Integer) g.getAllele()).intValue() >= 10)
				System.out.print(((char) ((int) 'a' + (Integer) g.getAllele() - 10)));
			else
				System.out.print(g.getAllele().toString());
		}
		System.out.println();
	}

	private void displayBuildOrder(final EcEvolver myFunc, IChromosome fittestChromosome)
	{
		myFunc.debug = true;
		myFunc.evaluateGetBuildOrder(fittestChromosome);
		myFunc.debug = false;
	}

	private synchronized void loadOldBuildOrders(Genotype population, final Configuration conf, final EcEvolver myFunc)
	{
		loadSeeds();

		int cindex = 0;

		Collections.sort(seeds, new Comparator<EcBuildOrder>()
		{

			@Override
			public int compare(EcBuildOrder arg0, EcBuildOrder arg1)
			{
				double score = 0;
				try
				{
					score = myFunc.getFitnessValue(buildChromosome(conf, arg1))
							- myFunc.getFitnessValue(buildChromosome(conf, arg0));
				}
				catch (InvalidConfigurationException e)
				{
					e.printStackTrace();
				}
				return (int) score;
			}
		});
		for (EcBuildOrder bo : seeds)
		{
			try
			{
				Chromosome c = buildChromosome(conf, bo);
				System.out.println(myFunc.getFitnessValue(c));
				population.getPopulation().setChromosome(cindex++, c);
			}
			catch (InvalidConfigurationException e)
			{
				e.printStackTrace();
			}
		}
	}

	private static void loadSeeds()
	{
		try
		{
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(SEEDS_EVO));
			seeds = (List<EcBuildOrder>) ois.readObject();
			ois.close();
		}
		catch (FileNotFoundException e)
		{
			System.out.println("Seeds file not found.");
		}
		catch (InvalidClassException ex)
		{
			System.out.println("Seeds file is in old format. Starting over. :-(");
		}
		catch (IOException e)
		{
			try
			{
				ObjectInputStream ois;
				ois = new ObjectInputStream(new FileInputStream(SEEDS_EVO_2));
				seeds = (List<EcBuildOrder>) ois.readObject();
				ois.close();
			}
			catch (FileNotFoundException e1)
			{
				System.out.println("Seeds file not found.");
			}
			catch (InvalidClassException ex)
			{
				System.out.println("Seeds 2 file is in old format. Starting over. :-(");
			}
			catch (IOException e1)
			{
				e1.printStackTrace();
			}
			catch (ClassNotFoundException ex)
			{
				System.out.println("Seeds 2 file is in old format. Starting over. :-(");
			}

		}
		catch (ClassNotFoundException e)
		{
			System.out.println("Seeds file is in old format. Starting over. :-(");
		}

	}

	static boolean	haveSavedBefore	= false;

	protected synchronized void saveSeeds(IChromosome fittestChromosome)
	{
		EcBuildOrder bo = importSource();
		try
		{
			bo = EcEvolver.populateBuildOrder(bo, fittestChromosome);
			if (haveSavedBefore)
				seeds.remove(seeds.size() - 1);
			haveSavedBefore = true;
			seeds.add(bo);
		}
		catch (CloneNotSupportedException e)
		{
			e.printStackTrace();
		}

		try
		{
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SEEDS_EVO, false));
			oos.writeObject(seeds);
			oos.close();
			oos = new ObjectOutputStream(new FileOutputStream(SEEDS_EVO_2, false));
			oos.writeObject(seeds);
			oos.close();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

	}

	private Gene[] importInitialGenes(Configuration conf)
	{
		ArrayList<Gene> genes = new ArrayList<Gene>();
		for (int i = 0; i < CHROMOSOME_LENGTH; i++)
			try
			{
				IntegerGene g = new IntegerGene(conf, 0, EcAction.actions.size() - 1);
				g.setAllele(0);
				genes.add(g);
			}
			catch (InvalidConfigurationException e)
			{
				e.printStackTrace();
			}
		return genes.toArray(new Gene[genes.size()]);
	}

	private EcBuildOrder importSource()
	{
		EcBuildOrder ecBuildOrder = new EcBuildOrder();
		ecBuildOrder.targetSeconds = importDestination().targetSeconds;
		return ecBuildOrder;
	}

	public EcState importDestination()
	{
		try
		{
			return (EcState) getInternalDestination().clone();
		}
		catch (CloneNotSupportedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	Chromosome buildChromosome(Configuration conf, EcBuildOrder bo) throws InvalidConfigurationException
	{
		ArrayList<Gene> genes = new ArrayList<Gene>();
		int CC = 0;
		for (EcAction a : bo.actions)
		{
			if (++CC > CHROMOSOME_LENGTH)
				continue;
			IntegerGene g = new IntegerGene(conf, 0, EcAction.actions.size() - 1);
			Integer allele = EcAction.findAllele(a);
			if (allele == null)
				break;
			g.setAllele(allele);
			genes.add(g);

		}
		while (genes.size() < CHROMOSOME_LENGTH)
		{
			IntegerGene g = new IntegerGene(conf, 0, EcAction.actions.size() - 1);
			g.setAllele(0);
			genes.add(g);
		}
		Chromosome c = new Chromosome(conf);
		c.setGenes(genes.toArray(new Gene[genes.size()]));
		c.setIsSelectedForNextGeneration(true);
		return c;
	}

	public void setThreads(int digit)
	{
		int availableProcessors = MAX_NUM_THREADS;
		NUM_THREADS = digit;
		if (NUM_THREADS > availableProcessors || NUM_THREADS < 1)
			NUM_THREADS = availableProcessors;
	}

	public void setDestination(EcState destination)
	{
		this.destination = destination;
	}

	public EcState getInternalDestination()
	{
		return destination;
	}

	public int getThreads()
	{
		return NUM_THREADS;
	}
}