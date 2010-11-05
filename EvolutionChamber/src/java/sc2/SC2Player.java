package sc2;

import sc2.action.SC2Action;
import sc2.action.SC2ActionException;
import sc2.asset.SC2Asset;
import sc2.asset.SC2AssetType;
import static sc2.SC2World.Race;

import com.google.common.collect.Multimap;
import com.google.common.collect.HashMultimap;

import java.util.Iterator;
import java.util.Set;
import java.util.List;
import java.util.HashSet;
import java.util.ArrayList;

/**
** Represents a game state, from the point of view of one player.
**
** Currently this is mutable; it makes heavy use of SC2Action which is mutable.
*/
public class SC2Player {

	/** database of game statistics */
	final public SC2World star;
	/** player's race */
	final public Race race;

	/** current assets, stored by type */
	final protected HashMultimap<SC2AssetType, SC2Asset> assets = HashMultimap.create();
	/** completed research */
	final protected Set<SC2AssetType> research = new HashSet<SC2AssetType>();
	/** ongoing actions that don't belong to asset queues */
	final protected List<SC2Action> ongoing = new ArrayList<SC2Action>();

	/** mineral resources. representing as a double allows for better averaging. */
	public double res_m;
	/** vespene resources. representing as a double allows for better averaging. */
	public double res_v;
	/** food resources. representing as a double due to zergling 0.5 supply. */
	public double res_f;
	/** food capacity. */
	public int max_f;
	/** game ticks, currently measured in seconds */
	protected int time;

	public SC2Player(SC2World star, Race race) {
		if (star == null || race == null) { throw new NullPointerException(); }
		this.star = star;
		this.race = race;
		this.res_m = 50;
		this.res_v = 0;
		// TODO populate assets with 6 workers, 1 cc
	}

	public String timestamp() {
		int h = time / 60, m = time % 60;
		return ((h<10)?"0":"") + h + ((m<10)?":0":":") + m;
	}

	/**
	** Advance by 1 game second.
	*/
	public void advance() {
		// advance asset queues. these will automatically drop completed actions
		for (SC2Asset asset: assets.values()) { asset.advance(); }

		// advance ongoing actions, dropping actions if appropriate
		Iterator<SC2Action> it = ongoing.iterator();
		while (it.hasNext()) {
			SC2Action action = it.next();
			if (action.advance()) { it.remove(); }
		}

		// TODO
	}

	public void addAction(SC2Action action) throws SC2ActionException {
		// TODO
		action.init();
	}

	public void addAsset(SC2Asset asset) {
		// TODO
	}

	public Set<SC2Asset> getAssets(SC2AssetType type) {
		return assets.get(type);
	}

	public boolean hasTech(SC2AssetType type) {
		return research.contains(type);
	}

	//public Set<SC2Asset> getWorkers();
	//public Set<SC2Asset> getUnits();
	//public Set<SC2Asset> getStructures();
	//public Set<SC2Asset> getTechs();

}
