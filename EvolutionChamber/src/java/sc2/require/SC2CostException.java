package sc2.require;

/**
** Thrown when the required resources are not available.
*/
public class SC2CostException extends SC2RequireException {

	final public double need_m;
	final public double need_v;
	final public double need_e;

	public SC2CostException(double need_m, double need_v) {
		super("not enough resources; need " + (int)need_m + "m " + (int)need_v + "v.", true);
		assert need_m > 0 || need_v > 0;
		this.need_m = need_m;
		this.need_v = need_v;
		this.need_e = 0;
	}

	public SC2CostException(int cost_m, int cost_v, double curr_m, double curr_v) {
		this(cost_m-curr_m, cost_v-curr_v);
	}

	public SC2CostException(double need_e) {
		super("not enough energy; need " + (int)need_e, true);
		assert need_e > 0;
		this.need_m = 0;
		this.need_v = 0;
		this.need_e = need_e;
	}

	public SC2CostException(int cost_e, double curr_e) {
		this(cost_e-curr_e);
	}

}
